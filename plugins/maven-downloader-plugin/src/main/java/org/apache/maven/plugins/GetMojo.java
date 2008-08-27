package org.apache.maven.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Goal which downloads an artifact
 *
 * @goal get
 * @requiresProject false
 * 
 */
public class GetMojo
    extends AbstractMojo
{
    
    /**
     * @component
     */
    private ArtifactFactory artifactFactory;
    
    /**
     * @component
     */
    private ArtifactResolver artifactResolver;
    
    /**
     * @component
     */
    private ArtifactRepositoryFactory artifactRepositoryFactory;
    
    /**
     * @component
     */
    private ArtifactMetadataSource source;
    
    /**
     * ArtifactRepository of the localRepository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter expression="${groupId}"
     * @required
     */
    private String groupId;

    /**
     * @parameter expression="${artifactId}"
     * @required
     */
    private String artifactId;

    /**
     * @parameter expression="${version}"
     * @required
     */
    private String version;

    /**
     * @parameter expression="${packaging}" default-value="jar"
     */
    private String packaging = "jar";

    /**
     * @parameter expression="${repoId}" default-value="temp"
     */
    private String repoId = "temp";

    /**
     * @parameter expression="${repoUrl}"
     * @required
     */
    private String repoUrl;

    /**
     * @parameter expression="${remoteRepositories}"
     */
    private String remoteRepositories;
    
    /**
     * The remote repositories available for discovering dependencies and extensions as indicated
     * by the POM.
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     */
    private List pomRemoteRepositories;
    
    public void execute()
        throws MojoExecutionException
    {        
        
        Artifact toDownload = artifactFactory.createBuildArtifact( groupId, artifactId, version, packaging );
        Artifact dummyOriginatingArtifact =
            artifactFactory.createBuildArtifact( "org.apache.maven.plugins", "maven-downloader-plugin", "1.0", "jar" );
        
        ArtifactRepositoryLayout repositoryLayout = new DefaultRepositoryLayout();
        ArtifactRepositoryPolicy always =
            new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                                          ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN );
        ArtifactRepository remoteRepo =
            artifactRepositoryFactory.createArtifactRepository( repoId, repoUrl, repositoryLayout, always, always );
        

        if (pomRemoteRepositories == null) pomRemoteRepositories = new ArrayList();
        
        List repoList = new ArrayList(pomRemoteRepositories);
        if (remoteRepositories != null)
        {
     
            repoList.addAll( Arrays.asList( StringUtils.split( remoteRepositories, "," ) ) );
        
        }
        
        repoList.add( remoteRepo );
        
        try
        {
            artifactResolver.resolveTransitively( Collections.singleton( toDownload ), dummyOriginatingArtifact,
                                                  repoList, localRepository, source );
        }
        catch ( AbstractArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Couldn't download artifact: " + e.getMessage(), e );
        }
    }
}
