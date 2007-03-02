package org.apache.maven.plugins.grafo;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

/**
 * <p>
 * Generates an xml file representing the graph of dependencies.
 * </p>
 * <p>
 * Artifacts are nodes and dependencies between them make the unions
 * </p> 
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class Grafo
{

    private ArtifactResolver artifactResolver;

    private ArtifactFactory artifactFactory;

    private ArtifactMetadataSource artifactMetadataSource;

    private DependencyTreeBuilder dependencyTreeBuilder;

    private ArtifactCollector collector;

    private MavenProjectBuilder mavenProjectBuilder;

    private Xpp3Dom report;

    public Grafo( ArtifactResolver artifactResolver, ArtifactFactory artifactFactory,
                  ArtifactMetadataSource artifactMetadataSource, DependencyTreeBuilder dependencyTreeBuilder,
                  ArtifactCollector collector, MavenProjectBuilder mavenProjectBuilder )
    {
        this.artifactResolver = artifactResolver;
        this.artifactFactory = artifactFactory;
        this.artifactMetadataSource = artifactMetadataSource;
        this.dependencyTreeBuilder = dependencyTreeBuilder;
        this.collector = collector;
        this.mavenProjectBuilder = mavenProjectBuilder;
    }

    public void execute( String groupId, String artifactId, String version, ArtifactRepository localRepository,
                         List remoteRepositories )
        throws ArtifactResolutionException, ArtifactNotFoundException
    {
        // TODO
    }

    public void execute( MavenProject project, ArtifactRepository localRepository, List remoteRepositories )
        throws MojoExecutionException
    {
        DependencyTree dependencyTree;

        try
        {
            dependencyTree = dependencyTreeBuilder.buildDependencyTree( project, localRepository, artifactFactory,
                                                                        artifactMetadataSource, collector );
        }
        catch ( DependencyTreeBuilderException e )
        {
            throw new MojoExecutionException( "Unable to build dependency tree", e );
        }

        report = new Xpp3Dom( "graphml" );
        report.setAttribute( "xmlns", "http://graphml.graphdrawing.org/xmlns" );
        Xpp3Dom graph = new Xpp3Dom( "graph" );
        graph.setAttribute( "edgedefault", "directed" );
        report.addChild( graph );

        addKeys( graph );

        for ( Iterator it = dependencyTree.iterator(); it.hasNext(); )
        {
            DependencyNode node = (DependencyNode) it.next();

            Artifact artifact = resolveArtifact( node.getArtifact(), remoteRepositories, localRepository );

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

            processNode( report, artifact );
            processEdges( report, node, node.getChildren() );
        }

        System.out.println( report );

        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( "graph.xml" ),
                                                                                  "UTF-8" ) ) );
            Xpp3DomWriter.write( writer, report );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
        catch ( FileNotFoundException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            if ( writer != null )
            {
                IOUtil.close( writer );
            }
        }

    }

    private String getId( Artifact artifact )
    {
        return artifact.getId();
    }

    private void processNode( Xpp3Dom dom, Artifact artifact )
    {
        System.out.println( artifact.getArtifactId() );

        Xpp3Dom node = new Xpp3Dom( "node" );
        node.setAttribute( "id", getId( artifact ) );

        Xpp3Dom data = new Xpp3Dom( "data" );
        data.setAttribute( "key", "artifactId" );
        data.setValue( artifact.getArtifactId() );
        node.addChild( data );

        data = new Xpp3Dom( "data" );
        data.setAttribute( "key", "groupId" );
        data.setValue( artifact.getGroupId() );
        node.addChild( data );

        data = new Xpp3Dom( "data" );
        data.setAttribute( "key", "version" );
        data.setValue( artifact.getVersion() );
        node.addChild( data );

        data = new Xpp3Dom( "data" );
        data.setAttribute( "key", "scope" );
        data.setValue( artifact.getScope() );
        node.addChild( data );

        data = new Xpp3Dom( "data" );
        data.setAttribute( "key", "label" );
        data.setValue( artifact.getArtifactId() + ":" + artifact.getVersion() );
        node.addChild( data );

        dom.addChild( node );
    }

    private void processEdges( Xpp3Dom dom, DependencyNode parent, Collection children )
    {
        Iterator it = children.iterator();
        while ( it.hasNext() )
        {
            DependencyNode child = (DependencyNode) it.next();

            Xpp3Dom edge = new Xpp3Dom( "edge" );
            edge.setAttribute( "source", getId( parent.getArtifact() ) );
            edge.setAttribute( "target", getId( child.getArtifact() ) );
            dom.addChild( edge );
        }
    }

    private void addKeys( Xpp3Dom graph )
    {
        Xpp3Dom key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "artifactId" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "artifactId" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );

        key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "groupId" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "groupId" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );

        key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "version" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "version" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );

        key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "scope" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "scope" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );

        key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "label" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "label" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );
    }

    private Artifact resolveArtifact( Artifact artifact, List remoteRepositories, ArtifactRepository localRepository )
        throws MojoExecutionException
    {
        Artifact resolvedArtifact = artifactFactory.createArtifact( artifact.getGroupId(), artifact.getArtifactId(),
                                                                    artifact.getVersion(), artifact.getScope(),
                                                                    artifact.getType() );

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
