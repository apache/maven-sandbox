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

package org.apache.maven.repository.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.mae.project.session.ProjectToolsSession;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositoryEvent.EventType;
import org.sonatype.aether.RepositoryException;
import org.sonatype.aether.RepositoryListener;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.RequestTrace;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactType;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.impl.ArtifactDescriptorReader;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.impl.VersionResolver;
import org.sonatype.aether.repository.WorkspaceRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.VersionRequest;
import org.sonatype.aether.resolution.VersionResolutionException;
import org.sonatype.aether.resolution.VersionResult;
import org.sonatype.aether.spi.locator.Service;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.aether.spi.log.Logger;
import org.sonatype.aether.spi.log.NullLogger;
import org.sonatype.aether.transfer.ArtifactNotFoundException;
import org.sonatype.aether.util.DefaultRequestTrace;
import org.sonatype.aether.util.artifact.ArtifactProperties;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.DefaultArtifactType;
import org.sonatype.aether.util.listener.DefaultRepositoryEvent;

/**
 * @author Benjamin Bentmann
 */
@Component( role = ArtifactDescriptorReader.class )
public class ConfigurableArtifactDescriptorReader
    implements ArtifactDescriptorReader, Service
{

    @SuppressWarnings( "unused" )
    @Requirement
    private Logger logger = NullLogger.INSTANCE;

    @Requirement
    private RemoteRepositoryManager remoteRepositoryManager;

    @Requirement
    private VersionResolver versionResolver;

    @Requirement
    private ArtifactResolver artifactResolver;

    @Requirement
    private ModelBuilder modelBuilder;

    @Override
    public void initService( final ServiceLocator locator )
    {
        setLogger( locator.getService( Logger.class ) );
        setRemoteRepositoryManager( locator.getService( RemoteRepositoryManager.class ) );
        setVersionResolver( locator.getService( VersionResolver.class ) );
        setArtifactResolver( locator.getService( ArtifactResolver.class ) );
        modelBuilder = locator.getService( ModelBuilder.class );
        if ( modelBuilder == null )
        {
            setModelBuilder( new DefaultModelBuilderFactory().newInstance() );
        }
    }

    public ConfigurableArtifactDescriptorReader setLogger( final Logger logger )
    {
        this.logger = ( logger != null ) ? logger : NullLogger.INSTANCE;
        return this;
    }

    public ConfigurableArtifactDescriptorReader setRemoteRepositoryManager( final RemoteRepositoryManager remoteRepositoryManager )
    {
        if ( remoteRepositoryManager == null )
        {
            throw new IllegalArgumentException( "remote repository manager has not been specified" );
        }
        this.remoteRepositoryManager = remoteRepositoryManager;
        return this;
    }

    public ConfigurableArtifactDescriptorReader setVersionResolver( final VersionResolver versionResolver )
    {
        if ( versionResolver == null )
        {
            throw new IllegalArgumentException( "version resolver has not been specified" );
        }
        this.versionResolver = versionResolver;
        return this;
    }

    public ConfigurableArtifactDescriptorReader setArtifactResolver( final ArtifactResolver artifactResolver )
    {
        if ( artifactResolver == null )
        {
            throw new IllegalArgumentException( "artifact resolver has not been specified" );
        }
        this.artifactResolver = artifactResolver;
        return this;
    }

    public ConfigurableArtifactDescriptorReader setModelBuilder( final ModelBuilder modelBuilder )
    {
        if ( modelBuilder == null )
        {
            throw new IllegalArgumentException( "model builder has not been specified" );
        }
        this.modelBuilder = modelBuilder;
        return this;
    }

    @Override
    public ArtifactDescriptorResult readArtifactDescriptor( final RepositorySystemSession session,
                                                            final ArtifactDescriptorRequest request )
        throws ArtifactDescriptorException
    {
        final ArtifactDescriptorResult result = new ArtifactDescriptorResult( request );

        final Model model = loadPom( session, request, result );

        if ( model != null )
        {
            final ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

            for ( final Repository r : model.getRepositories() )
            {
                result.addRepository( ArtifactDescriptorUtils.toRemoteRepository( r ) );
            }

            for ( final org.apache.maven.model.Dependency dependency : model.getDependencies() )
            {
                result.addDependency( convert( dependency, stereotypes ) );
            }

            final DependencyManagement mngt = model.getDependencyManagement();
            if ( mngt != null )
            {
                for ( final org.apache.maven.model.Dependency dependency : mngt.getDependencies() )
                {
                    result.addManagedDependency( convert( dependency, stereotypes ) );
                }
            }

            final Map<String, Object> properties = new LinkedHashMap<String, Object>();

            final Prerequisites prerequisites = model.getPrerequisites();
            if ( prerequisites != null )
            {
                properties.put( "prerequisites.maven", prerequisites.getMaven() );
            }

            final List<License> licenses = model.getLicenses();
            properties.put( "license.count", Integer.valueOf( licenses.size() ) );
            for ( int i = 0; i < licenses.size(); i++ )
            {
                final License license = licenses.get( i );
                properties.put( "license." + i + ".name", license.getName() );
                properties.put( "license." + i + ".url", license.getUrl() );
                properties.put( "license." + i + ".comments", license.getComments() );
                properties.put( "license." + i + ".distribution", license.getDistribution() );
            }

            result.setProperties( properties );
        }

        return result;
    }

    private Model loadPom( final RepositorySystemSession session, final ArtifactDescriptorRequest request,
                           final ArtifactDescriptorResult result )
        throws ArtifactDescriptorException
    {
        ProjectToolsSession pts = (ProjectToolsSession) session.getData().get( ProjectToolsSession.SESSION_KEY );
        final RequestTrace trace = DefaultRequestTrace.newChild( request.getTrace(), request );

        final Set<String> visited = new LinkedHashSet<String>();
        for ( Artifact artifact = request.getArtifact();; )
        {
            try
            {
                final VersionRequest versionRequest =
                    new VersionRequest( artifact, request.getRepositories(), request.getRequestContext() );
                versionRequest.setTrace( trace );
                final VersionResult versionResult = versionResolver.resolveVersion( session, versionRequest );

                artifact = artifact.setVersion( versionResult.getVersion() );
            }
            catch ( final VersionResolutionException e )
            {
                result.addException( e );
                throw new ArtifactDescriptorException( result );
            }

            if ( !visited.add( artifact.getGroupId() + ':' + artifact.getArtifactId() + ':' + artifact.getBaseVersion() ) )
            {
                final RepositoryException exception =
                    new RepositoryException( "Artifact relocations form a cycle: " + visited );
                invalidDescriptor( session, trace, artifact, exception );
                if ( session.isIgnoreInvalidArtifactDescriptor() )
                {
                    return null;
                }
                result.addException( exception );
                throw new ArtifactDescriptorException( result );
            }

            Artifact pomArtifact = ArtifactDescriptorUtils.toPomArtifact( artifact );

            ArtifactResult resolveResult;
            try
            {
                final ArtifactRequest resolveRequest =
                    new ArtifactRequest( pomArtifact, request.getRepositories(), request.getRequestContext() );
                resolveRequest.setTrace( trace );
                resolveResult = artifactResolver.resolveArtifact( session, resolveRequest );
                pomArtifact = resolveResult.getArtifact();
                result.setRepository( resolveResult.getRepository() );
            }
            catch ( final ArtifactResolutionException e )
            {
                if ( e.getCause() instanceof ArtifactNotFoundException )
                {
                    missingDescriptor( session, trace, artifact, (Exception) e.getCause() );
                    if ( session.isIgnoreMissingArtifactDescriptor() )
                    {
                        return null;
                    }
                }
                result.addException( e );
                throw new ArtifactDescriptorException( result );
            }

            Model model;
            try
            {
                final ModelBuildingRequest modelRequest = new DefaultModelBuildingRequest();
                modelRequest.setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL );
                modelRequest.setProcessPlugins( pts == null ? false : pts.isProcessPomPlugins() );
                modelRequest.setTwoPhaseBuilding( false );
                modelRequest.setSystemProperties( toProperties( session.getUserProperties(),
                                                                session.getSystemProperties() ) );
                modelRequest.setModelCache( DefaultModelCache.newInstance( session ) );
                modelRequest.setModelResolver( new DefaultModelResolver( session, trace.newChild( modelRequest ),
                                                                         request.getRequestContext(), artifactResolver,
                                                                         remoteRepositoryManager,
                                                                         request.getRepositories() ) );
                if ( resolveResult.getRepository() instanceof WorkspaceRepository )
                {
                    modelRequest.setPomFile( pomArtifact.getFile() );
                }
                else
                {
                    modelRequest.setModelSource( new FileModelSource( pomArtifact.getFile() ) );
                }

                model = modelBuilder.build( modelRequest ).getEffectiveModel();
            }
            catch ( final ModelBuildingException e )
            {
                for ( final ModelProblem problem : e.getProblems() )
                {
                    if ( problem.getException() instanceof UnresolvableModelException )
                    {
                        result.addException( problem.getException() );
                        throw new ArtifactDescriptorException( result );
                    }
                }
                invalidDescriptor( session, trace, artifact, e );
                if ( session.isIgnoreInvalidArtifactDescriptor() )
                {
                    return null;
                }
                result.addException( e );
                throw new ArtifactDescriptorException( result );
            }

            final Relocation relocation = getRelocation( model );

            if ( relocation != null )
            {
                result.addRelocation( artifact );
                artifact =
                    new RelocatedArtifact( artifact, relocation.getGroupId(), relocation.getArtifactId(),
                                           relocation.getVersion() );
                result.setArtifact( artifact );
            }
            else
            {
                return model;
            }
        }
    }

    private Properties toProperties( final Map<String, String> dominant, final Map<String, String> recessive )
    {
        final Properties props = new Properties();
        if ( recessive != null )
        {
            props.putAll( recessive );
        }
        if ( dominant != null )
        {
            props.putAll( dominant );
        }
        return props;
    }

    private Relocation getRelocation( final Model model )
    {
        Relocation relocation = null;
        final DistributionManagement distMngt = model.getDistributionManagement();
        if ( distMngt != null )
        {
            relocation = distMngt.getRelocation();
        }
        return relocation;
    }

    private Dependency convert( final org.apache.maven.model.Dependency dependency,
                                final ArtifactTypeRegistry stereotypes )
    {
        ArtifactType stereotype = stereotypes.get( dependency.getType() );
        if ( stereotype == null )
        {
            stereotype = new DefaultArtifactType( dependency.getType() );
        }

        final boolean system = dependency.getSystemPath() != null && dependency.getSystemPath().length() > 0;

        Map<String, String> props = null;
        if ( system )
        {
            props = Collections.singletonMap( ArtifactProperties.LOCAL_PATH, dependency.getSystemPath() );
        }

        final Artifact artifact =
            new DefaultArtifact( dependency.getGroupId(), dependency.getArtifactId(), dependency.getClassifier(), null,
                                 dependency.getVersion(), props, stereotype );

        final List<Exclusion> exclusions = new ArrayList<Exclusion>( dependency.getExclusions().size() );
        for ( final org.apache.maven.model.Exclusion exclusion : dependency.getExclusions() )
        {
            exclusions.add( convert( exclusion ) );
        }

        final Dependency result = new Dependency( artifact, dependency.getScope(), dependency.isOptional(), exclusions );

        return result;
    }

    private Exclusion convert( final org.apache.maven.model.Exclusion exclusion )
    {
        return new Exclusion( exclusion.getGroupId(), exclusion.getArtifactId(), "*", "*" );
    }

    private void missingDescriptor( final RepositorySystemSession session, final RequestTrace trace,
                                    final Artifact artifact, final Exception exception )
    {
        final RepositoryListener listener = session.getRepositoryListener();
        if ( listener != null )
        {
            final DefaultRepositoryEvent event =
                new DefaultRepositoryEvent( EventType.ARTIFACT_DESCRIPTOR_MISSING, session, trace );
            event.setArtifact( artifact );
            event.setException( exception );
            listener.artifactDescriptorMissing( event );
        }
    }

    private void invalidDescriptor( final RepositorySystemSession session, final RequestTrace trace,
                                    final Artifact artifact, final Exception exception )
    {
        final RepositoryListener listener = session.getRepositoryListener();
        if ( listener != null )
        {
            final DefaultRepositoryEvent event =
                new DefaultRepositoryEvent( EventType.ARTIFACT_DESCRIPTOR_INVALID, session, trace );
            event.setArtifact( artifact );
            event.setException( exception );
            listener.artifactDescriptorInvalid( event );
        }
    }

}
