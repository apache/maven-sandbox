/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.tools.maven2.bundleplugin;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import aQute.lib.osgi.Analyzer;
import aQute.lib.osgi.Jar;

/**
 * 
 * @goal bundleall
 * @phase package
 * @requiresDependencyResolution runtime
 * @description build an OSGi bundle jar for all transitive dependencies
 */
public class BundleAllPlugin
    extends ManifestPlugin
{

    private static final Pattern SNAPSHOT_VERSION_PATTERN = Pattern.compile( "[0-9]{8}_[0-9]{6}_[0-9]+" );

    /**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Local Repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * Remote repositories
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readonly
     */
    private List remoteRepositories;

    /**
     * @component
     */
    private ArtifactFactory factory;

    /**
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * @component
     */
    private ArtifactCollector collector;

    /**
     * Artifact resolver, needed to download jars.
     * 
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * @component
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * @component
     */
    private MavenProjectBuilder mavenProjectBuilder;

    /**
     * {@link Map} &lt; {@link String}, {@link List} &lt; {@link Artifact} > >
     * Used to check for duplicated exports. Key is package name and value list of artifacts where it's exported.
     */
    private Map exportedPackages;

    public void execute()
        throws MojoExecutionException
    {
        exportedPackages = new HashMap();
        bundleAll( project );

        for ( Iterator it = exportedPackages.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();
            List artifacts = (List) entry.getValue();
            if ( artifacts.size() > 1 )
            {
                /* remove warnings caused by different versions of same artifact */
                Set artifactKeys = new HashSet();

                String packageName = (String) entry.getKey();
                for ( Iterator it2 = artifacts.iterator(); it2.hasNext(); )
                {
                    Artifact artifact = (Artifact) it2.next();
                    artifactKeys.add( artifact.getGroupId() + "." + artifact.getArtifactId() );
                }

                if ( artifactKeys.size() > 1 )
                {
                    getLog().warn( "Package " + packageName + " is exported in more than a bundle: " );
                    for ( Iterator it2 = artifacts.iterator(); it2.hasNext(); )
                    {
                        Artifact artifact = (Artifact) it2.next();
                        getLog().warn( "  " + artifact );
                    }
                }
            }
        }
    }

    /**
     * Bundle a project and all its dependencies
     * 
     * @param project
     * @throws MojoExecutionException
     */
    private void bundleAll( MavenProject project )
        throws MojoExecutionException
    {

        if ( alreadyBundled( project.getArtifact() ) )
        {
            getLog().debug( "Ignoring project already processed " + project.getArtifact() );
            return;
        }

        DependencyTree dependencyTree;

        try
        {
            dependencyTree = dependencyTreeBuilder.buildDependencyTree( project, localRepository, factory,
                                                                        artifactMetadataSource, collector );
        }
        catch ( DependencyTreeBuilderException e )
        {
            throw new MojoExecutionException( "Unable to build dependency tree", e );
        }

        getLog().debug( "Will bundle the following dependency tree\n" + dependencyTree );

        for ( Iterator it = dependencyTree.inverseIterator(); it.hasNext(); )
        {
            DependencyNode node = (DependencyNode) it.next();
            if ( !it.hasNext() )
            {
                /* this is the root, current project */
                break;
            }
            Artifact artifact = resolveArtifact( node.getArtifact() );
            MavenProject childProject;
            try
            {
                childProject = mavenProjectBuilder.buildFromRepository( artifact, remoteRepositories, localRepository,
                                                                        true );
            }
            catch ( ProjectBuildingException e )
            {
                throw new MojoExecutionException( "Unable to build project object for artifact " + artifact, e );
            }
            childProject.setArtifact( artifact );
            getLog().debug( "Child project artifact location: " + childProject.getArtifact().getFile() );

            if ( ( artifact.getScope().equals( Artifact.SCOPE_COMPILE ) )
                || ( artifact.getScope().equals( Artifact.SCOPE_RUNTIME ) ) )
            {
                bundleAll( childProject );
            }
            else
            {
                getLog().debug(
                                "Not processing due to scope (" + childProject.getArtifact().getScope() + "): "
                                    + childProject.getArtifact() );
            }
        }

        if ( this.project != project )
        {
            getLog().debug( "Project artifact location: " + project.getArtifact().getFile() );
            bundle( project );
        }
    }

    /**
     * Bundle one project only without building its childre
     * 
     * @param project
     * @throws MojoExecutionException
     */
    void bundle( MavenProject project )
        throws MojoExecutionException
    {
        Artifact artifact = project.getArtifact();
        getLog().info( "Bundling " + artifact );

        try
        {
            Map instructions = new HashMap();
            instructions.put( Analyzer.EXPORT_PACKAGE, "*" );

            project.setFile( getFile( artifact ) );
            File outputFile = getOutputFile( artifact );

            if ( project.getFile().equals( outputFile ) )
            {
                /* TODO find the cause why it's getting here */
                return;
                //                getLog().error(
                //                                "Trying to read and write " + artifact + " to the same file, try cleaning: "
                //                                    + outputFile );
                //                throw new IllegalStateException( "Trying to read and write " + artifact
                //                    + " to the same file, try cleaning: " + outputFile );
            }

            Analyzer analyzer = getAnalyzer( project, getClasspath( project ) );
            checkDuplicatedPackages( project, analyzer.getExports().keySet() );
            Jar osgiJar = new Jar( project.getArtifactId(), project.getFile() );
            Manifest manifest = analyzer.getJar().getManifest();
            osgiJar.setManifest( manifest );
            outputFile.getParentFile().mkdirs();
            osgiJar.write( outputFile );
        }
        /* too bad Jar.write throws Exception */
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error generating OSGi bundle for project "
                + getArtifactKey( project.getArtifact() ), e );
        }
    }

    private void checkDuplicatedPackages( MavenProject project, Collection packages )
    {
        for ( Iterator it = packages.iterator(); it.hasNext(); )
        {
            String packageName = (String) it.next();
            List artifactsWithPackage = (List) exportedPackages.get( packageName );
            if ( artifactsWithPackage == null )
            {
                artifactsWithPackage = new ArrayList();
                exportedPackages.put( packageName, artifactsWithPackage );
            }
            artifactsWithPackage.add( project.getArtifact() );
        }
    }

    private String getArtifactKey( Artifact artifact )
    {
        return artifact.getGroupId() + ":" + artifact.getArtifactId();
    }

    protected String getBundleName( MavenProject project )
    {
        return getBundleName( project.getArtifact() );
    }

    private String getBundleNameFirstPart( Artifact artifact )
    {
        return artifact.getGroupId() + "." + artifact.getArtifactId();
    }

    private String getBundleName( Artifact artifact )
    {
        return getBundleNameFirstPart( artifact ) + "_" + convertVersionToOsgi( artifact.getVersion() ) + ".jar";
    }

    private boolean alreadyBundled( Artifact artifact )
    {
        return getBuiltFile( artifact ) != null;
    }

    /**
     * Use previously built bundles when available.
     * 
     * @param artifact
     */
    protected File getFile( final Artifact artifact )
    {
        File bundle = getBuiltFile( artifact );

        if ( bundle != null )
        {
            getLog().debug( "Using previously built OSGi bundle for " + artifact + " in " + bundle );
            return bundle;
        }
        return super.getFile( artifact );
    }

    private File getBuiltFile( final Artifact artifact )
    {
        File bundle = null;

        /* if bundle was already built use it instead of jar from repo */
        File outputFile = getOutputFile( artifact );
        if ( outputFile.exists() )
        {
            bundle = outputFile;
        }

        /*
         * Find snapshots in output folder, eg. 2.1-SNAPSHOT will match 2.1.0.20070207_193904_2
         * TODO there has to be another way to do this using Maven libs 
         */
        if ( ( bundle == null ) && artifact.isSnapshot() )
        {
            final File buildDirectory = new File( getBuildDirectory() );
            if ( !buildDirectory.exists() )
            {
                buildDirectory.mkdirs();
            }
            File[] files = buildDirectory.listFiles( new FilenameFilter()
            {
                public boolean accept( File dir, String name )
                {
                    if ( dir.equals( buildDirectory ) && snapshotMatch( artifact, name ) )
                    {
                        return true;
                    }
                    return false;
                }
            } );
            if ( files.length > 1 )
            {
                throw new RuntimeException( "More than one previously built bundle matches for artifact " + artifact
                    + " : " + Arrays.asList( files ) );
            }
            if ( files.length == 1 )
            {
                bundle = files[0];
            }
        }

        return bundle;
    }

    /**
     * Check that the bundleName provided correspond to the artifact provided.
     * Used to determine when the bundle name is a timestamped snapshot and the artifact is a snapshot not timestamped.
     * 
     * @param artifact artifact with snapshot version
     * @param bundleName bundle file name 
     * @return if both represent the same artifact and version, forgetting about the snapshot timestamp
     */
    boolean snapshotMatch( Artifact artifact, String bundleName )
    {
        String artifactBundleName = getBundleName( artifact );
        int i = artifactBundleName.indexOf( "SNAPSHOT" );
        if ( i < 0 )
        {
            return false;
        }
        artifactBundleName = artifactBundleName.substring( 0, i );

        if ( bundleName.startsWith( artifactBundleName ) )
        {
            /* it's the same artifact groupId and artifactId */
            String timestamp = bundleName.substring( artifactBundleName.length(), bundleName.lastIndexOf( ".jar" ) );
            Matcher m = SNAPSHOT_VERSION_PATTERN.matcher( timestamp );
            return m.matches();
        }
        return false;
    }

    protected File getOutputFile( Artifact artifact )
    {
        return new File( getBuildDirectory(), getBundleName( artifact ) );
    }

    private Artifact resolveArtifact( Artifact artifact )
        throws MojoExecutionException
    {
        Artifact resolvedArtifact = factory.createArtifact( artifact.getGroupId(), artifact.getArtifactId(), artifact
            .getVersion(), artifact.getScope(), artifact.getType() );

        try
        {
            artifactResolver.resolve( resolvedArtifact, remoteRepositories, localRepository );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Artifact was not found in the repo" + resolvedArtifact, e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Error resolving artifact " + resolvedArtifact, e );
        }

        return resolvedArtifact;
    }
}
