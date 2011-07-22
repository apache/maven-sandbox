/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.mae.boot.services;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.mae.MAEExecutionRequest;
import org.apache.maven.mae.boot.embed.MAEEmbeddingException;
import org.apache.maven.mae.conf.MAELibrary;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;

/**
 * Service manager used as an aid in constructing complex objects correctly for use in Maven, such as
 * {@link RepositorySystemSession} instances, which are used to resolve artifacts, or {@link ProjectBuildingRequest}
 * instances, used to build maven projects from their corresponding POM files or artifact coordinates.
 * 
 * @author John Casey
 */
public interface MAEServiceManager
{

    /**
     * Retrieve the Maven {@link ProjectBuilder} component.
     */
    ProjectBuilder projectBuilder()
        throws MAEEmbeddingException;

    /**
     * Create a new {@link ProjectBuildingRequest} to be used in building project instances from POM files or artifact
     * coordinates.
     */
    DefaultProjectBuildingRequest createProjectBuildingRequest()
        throws MAEEmbeddingException;

    /**
     * Create a new {@link ProjectBuildingRequest} to be used in building project instances from POM files or artifact
     * coordinates.
     */
    DefaultProjectBuildingRequest createProjectBuildingRequest( ProjectBuildingRequest templateProjectBuildingRequest )
        throws MAEEmbeddingException;

    /**
     * Create a new {@link ProjectBuildingRequest} to be used in building project instances from POM files or artifact
     * coordinates.
     */
    DefaultProjectBuildingRequest createProjectBuildingRequest( MAEExecutionRequest executionRequest )
        throws MAEEmbeddingException;

    /**
     * Create a new {@link ProjectBuildingRequest} to be used in building project instances from POM files or artifact
     * coordinates.
     */
    DefaultProjectBuildingRequest createProjectBuildingRequest( MavenExecutionRequest executionRequest )
        throws MAEEmbeddingException;

    /**
     * Retrieve the Maven {@link RepositorySystem} component.
     */
    RepositorySystem mavenRepositorySystem()
        throws MAEEmbeddingException;

    /**
     * Retrieve the aether {@link org.sonatype.aether.RepositorySystem} component.
     */
    org.sonatype.aether.RepositorySystem aetherRepositorySystem()
        throws MAEEmbeddingException;

    /**
     * Create a new {@link RepositorySystemSession} for resolving artifacts, using default configurations.
     */
    RepositorySystemSession createAetherRepositorySystemSession()
        throws MAEEmbeddingException;

    /**
     * Create a new {@link RepositorySystemSession} for resolving artifacts, based on the configuration of a
     * {@link MavenExecutionRequest}.
     */
    RepositorySystemSession createAetherRepositorySystemSession( MavenExecutionRequest request )
        throws MAEEmbeddingException;

    /**
     * Retrieve a component from the Maven component environment, based on its class. <br/>
     * <b>NOTE:</b> This method requires that some {@link MAELibrary} present in the current environment has granted
     * permission to lookup the component in question.
     */
    <T> T service( Class<T> type )
        throws MAEEmbeddingException;

    /**
     * Retrieve a component from the Maven component environment, based on its class and an implementation hint. <br/>
     * <b>NOTE:</b> This method requires that some {@link MAELibrary} present in the current environment has granted
     * permission to lookup the component in question.
     */
    <T> T service( Class<T> type, String hint )
        throws MAEEmbeddingException;

    /**
     * Retrieve an {@link ArtifactRepository} instance configured for the default location of the Maven local
     * repository.
     */
    ArtifactRepository defaultLocalRepository()
        throws MAEEmbeddingException;

}
