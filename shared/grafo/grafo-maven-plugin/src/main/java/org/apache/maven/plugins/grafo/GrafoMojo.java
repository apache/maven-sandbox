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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

/**
 * <p>
 * Grafo mojo
 * </p>
 * 
 * @goal grafo
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class GrafoMojo
  extends AbstractMojo
{

    /**
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * @component
     */
    private ArtifactFactory artifactFactory;
    
    /**
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * @component
     */
    private ArtifactRepositoryFactory artifactRepositoryFactory;

    /**
     * @component role="org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout" roleHint="default"
     */
    private ArtifactRepositoryLayout defaultArtifactRepositoryLayout;


    /**
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter expression="${groupId}"
     */
    private String groupId;

    /**
     * @parameter expression="${artifactId}"
     */
    private String artifactId;

    /**
     * @parameter expression="${version}" default-value="1.0-SNAPSHOT"
     * @required
     */
    private String version;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     */
    private List pomRemoteRepositories;

    /**
     * @parameter expression="${remoteRepositories}"
     */
    private String remoteRepositories;

    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;
    
    public void execute()
        throws MojoExecutionException
    {
        // TODO: prompt for missing values
        // TODO: configurable license

        // ----------------------------------------------------------------------
        // archetypeGroupId
        // archetypeArtifactId
        // archetypeVersion
        //
        // localRepository
        // remoteRepository
        // parameters
        // ----------------------------------------------------------------------

        if ( project.getFile() != null && groupId == null )
        {
            groupId = project.getGroupId();
        }

        if ( project.getFile() != null && artifactId == null )
        {
            artifactId = project.getArtifactId();
        }

        String basedir = System.getProperty( "user.dir" );

        List archetypeRemoteRepositories = new ArrayList( pomRemoteRepositories );

        if ( remoteRepositories != null )
        {
            getLog().info( "We are using command line specified remote repositories: " + remoteRepositories );

            archetypeRemoteRepositories = new ArrayList();

            String[] s = StringUtils.split( remoteRepositories, "," );

            for ( int i = 0; i < s.length; i++ )
            {
                archetypeRemoteRepositories.add( createRepository( s[i], "id" + i ) );
            }
        }

        if ( artifactResolver == null )
        {
            throw new NullPointerException( "artifactResolver can not be null" );
        }
        if ( artifactFactory == null )
        {
            throw new NullPointerException( "artifactFactory can not be null" );
        }
        
        Grafo grafo = new Grafo( artifactResolver, artifactFactory, artifactMetadataSource );
        try
        {
            grafo.execute( groupId, artifactId, version,
                           localRepository, archetypeRemoteRepositories );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Error during artifact resolution", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Artifact not found", e );
        }
    }

    //TODO: this should be put in John's artifact utils and used from there instead of being repeated here. Creating
    // artifact repositories is someowhat cumbersome atm.
    public ArtifactRepository createRepository( String url, String repositoryId )
    {
        // snapshots vs releases
        // offline = to turning the update policy off

        //TODO: we'll need to allow finer grained creation of repositories but this will do for now

        String updatePolicyFlag = ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;

        String checksumPolicyFlag = ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN;

        ArtifactRepositoryPolicy snapshotsPolicy =
            new ArtifactRepositoryPolicy( true, updatePolicyFlag, checksumPolicyFlag );

        ArtifactRepositoryPolicy releasesPolicy =
            new ArtifactRepositoryPolicy( true, updatePolicyFlag, checksumPolicyFlag );

        return artifactRepositoryFactory.createArtifactRepository( repositoryId, url, defaultArtifactRepositoryLayout,
                                                                   snapshotsPolicy, releasesPolicy );
    }
    
}

