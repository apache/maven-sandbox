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

import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;

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
     * @parameter expression="${project}"
     */
    private MavenProject project;

    /**
     * @component
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * @component
     */
    private ArtifactCollector collector;

    /**
     * @component
     */
    private MavenProjectBuilder mavenProjectBuilder;

    public void execute()
        throws MojoExecutionException
    {
        if ( artifactResolver == null )
        {
            throw new NullPointerException( "artifactResolver can not be null" );
        }
        if ( artifactFactory == null )
        {
            throw new NullPointerException( "artifactFactory can not be null" );
        }

        Grafo grafo = new Grafo( artifactResolver, artifactFactory, artifactMetadataSource, dependencyTreeBuilder,
                                 collector, mavenProjectBuilder );
        grafo.execute( project, localRepository, pomRemoteRepositories );
    }
}
