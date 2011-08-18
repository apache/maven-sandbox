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

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.mae.DefaultMAEExecutionRequest;
import org.apache.maven.mae.MAEExecutionRequest;
import org.apache.maven.mae.boot.embed.MAEEmbeddingException;
import org.apache.maven.mae.internal.container.ServiceAuthorizer;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.util.DefaultRepositoryCache;

@Component( role = MAEServiceManager.class )
public class DefaultMAEServiceManager
    implements MAEServiceManager
{

    @Requirement
    private ProjectBuilder projectBuilder;

    @Requirement
    private RepositorySystem repositorySystem;

    @Requirement
    private org.sonatype.aether.RepositorySystem aetherRepositorySystem;

    @Requirement
    private ServiceAuthorizer authorizer;

    // @Requirement( role = Maven.class, hint = "default_" )
    @Requirement( role = Maven.class )
    private DefaultMaven defaultMaven;

    @Requirement
    private MavenExecutionRequestPopulator requestPopulator;

    private transient ArtifactRepository defaultLocalRepo;

    @Requirement
    private PlexusContainer container;

    // @Inject
    // public DefaultEMBServiceManager( final ProjectBuilder projectBuilder, final RepositorySystem repositorySystem,
    // final org.sonatype.aether.RepositorySystem aetherRepositorySystem,
    // final ServiceAuthorizer authorizer,
    // @Named( "default_" ) final DefaultMaven defaultMaven,
    // final MavenExecutionRequestPopulator requestPopulator,
    // final PlexusContainer container )
    // {
    // this.projectBuilder = projectBuilder;
    // this.repositorySystem = repositorySystem;
    // this.aetherRepositorySystem = aetherRepositorySystem;
    // this.authorizer = authorizer;
    // this.defaultMaven = defaultMaven;
    // this.requestPopulator = requestPopulator;
    // this.container = container;
    // }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectBuilder projectBuilder()
    {
        return projectBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultProjectBuildingRequest createProjectBuildingRequest()
        throws MAEEmbeddingException
    {
        return createProjectBuildingRequest( (ProjectBuildingRequest) null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultProjectBuildingRequest createProjectBuildingRequest( final MAEExecutionRequest executionRequest )
        throws MAEEmbeddingException
    {
        return createProjectBuildingRequest( executionRequest == null ? null
                        : executionRequest.getProjectBuildingRequest() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultProjectBuildingRequest createProjectBuildingRequest( final MavenExecutionRequest executionRequest )
        throws MAEEmbeddingException
    {
        return createProjectBuildingRequest( executionRequest == null ? null
                        : executionRequest.getProjectBuildingRequest() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultProjectBuildingRequest createProjectBuildingRequest( final ProjectBuildingRequest templateProjectBuildingRequest )
        throws MAEEmbeddingException
    {
        final DefaultProjectBuildingRequest req;
        if ( templateProjectBuildingRequest != null )
        {
            req = new DefaultProjectBuildingRequest( templateProjectBuildingRequest );
        }
        else
        {
            req = new DefaultProjectBuildingRequest();
        }

        if ( req.getLocalRepository() == null )
        {
            req.setLocalRepository( defaultLocalRepository() );
        }

        req.setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL );
        req.setProcessPlugins( false );
        req.setResolveDependencies( false );

        if ( req.getRepositorySession() == null )
        {
            req.setRepositorySession( createAetherRepositorySystemSession() );
        }

        return req;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepositorySystem mavenRepositorySystem()
    {
        return repositorySystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.sonatype.aether.RepositorySystem aetherRepositorySystem()
    {
        return aetherRepositorySystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepositorySystemSession createAetherRepositorySystemSession()
        throws MAEEmbeddingException
    {
        try
        {
            final MavenExecutionRequest req =
                requestPopulator.populateDefaults( new DefaultMAEExecutionRequest().asMavenExecutionRequest() );

            return defaultMaven.newRepositorySession( req );
        }
        catch ( final MavenExecutionRequestPopulationException e )
        {
            throw new MAEEmbeddingException( "Failed to populate default Maven execution request, "
                + " for use in constructing a repository system session." + "\nReason: %s", e, e.getMessage() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepositorySystemSession createAetherRepositorySystemSession( MavenExecutionRequest request )
        throws MAEEmbeddingException
    {
        if ( request == null )
        {
            return createAetherRepositorySystemSession();
        }
        else
        {
            try
            {
                request = requestPopulator.populateDefaults( request );
                if ( request.getRepositoryCache() == null )
                {
                    request.setRepositoryCache( new DefaultRepositoryCache() );
                }
            }
            catch ( final MavenExecutionRequestPopulationException e )
            {
                throw new MAEEmbeddingException( "Failed to populate default Maven execution request, "
                    + " for use in constructing a repository system session." + "\nReason: %s", e, e.getMessage() );
            }

            return defaultMaven.newRepositorySession( request );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ArtifactRepository defaultLocalRepository()
        throws MAEEmbeddingException
    {
        if ( defaultLocalRepo == null )
        {
            try
            {
                defaultLocalRepo = mavenRepositorySystem().createDefaultLocalRepository();
            }
            catch ( final InvalidRepositoryException e )
            {
                throw new MAEEmbeddingException( "Failed to create default local-repository instance: {0}", e,
                                                 e.getMessage() );
            }
        }

        return defaultLocalRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T service( final Class<T> type )
        throws MAEEmbeddingException
    {
        if ( type == null )
        {
            throw new MAEEmbeddingException( "Invalid service: null" );
        }

        if ( !authorizer.isAvailable( type ) )
        {
            throw new UnauthorizedServiceException( type );
        }

        try
        {
            return type.cast( container.lookup( type ) );
        }
        catch ( final ComponentLookupException e )
        {
            throw new MAEEmbeddingException( "Failed to retrieve service: %s. Reason: %s", e, type.getName(),
                                             e.getMessage() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T service( final Class<T> type, final String hint )
        throws MAEEmbeddingException
    {
        if ( type == null )
        {
            throw new MAEEmbeddingException( "Invalid service: null" );
        }

        if ( !authorizer.isAvailable( type, hint ) )
        {
            throw new UnauthorizedServiceException( type, hint == null ? "" : hint );
        }

        try
        {
            return type.cast( container.lookup( type, hint ) );
        }
        catch ( final ComponentLookupException e )
        {
            throw new MAEEmbeddingException( "Failed to retrieve service: %s with hint: %s. Reason: %s", e,
                                             type.getName(), hint, e.getMessage() );
        }
    }

}
