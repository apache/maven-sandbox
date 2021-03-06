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

package org.apache.maven.mae.project.session;

import static org.apache.maven.artifact.ArtifactUtils.key;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.mae.project.event.EventDispatcher;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

public class SimpleProjectToolsSession
    implements ProjectToolsSession
{

    private transient List<ArtifactRepository> remoteArtifactRepositories;

    private transient List<RemoteRepository> remoteRepositories;

    private transient ProjectBuildingRequest projectBuildingRequest;

    private transient RepositorySystemSession repositorySystemSession;

    private transient LinkedHashMap<String, MavenProject> reactorProjects = new LinkedHashMap<String, MavenProject>();

    private Repository[] resolveRepositories;

    private File localRepositoryDirectory;

    private MavenExecutionRequest executionRequest;

    private int resolveThreads = 4;

    private boolean resolvePlugins;

    private DependencySelector dependencySelector;

    private DependencyFilter filter;

    private int pomValidationLevel = ModelBuildingRequest.VALIDATION_LEVEL_MAVEN_2_0;

    private ProjectBuildingRequest templateProjectBuildingRequest;

    private transient Map<Class<?>, Object> states = new HashMap<Class<?>, Object>();

    private MavenExecutionResult executionResult;

    public SimpleProjectToolsSession()
    {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getArtifactRepositoriesForResolution()
     */
    @Override
    public List<ArtifactRepository> getArtifactRepositoriesForResolution()
    {
        return remoteArtifactRepositories;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#setArtifactRepositoriesForResolution(java.util.List)
     */
    @Override
    public ProjectToolsSession setArtifactRepositoriesForResolution( final List<ArtifactRepository> remoteArtifactRepositories )
    {
        this.remoteArtifactRepositories = remoteArtifactRepositories;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getRepositoryDefinitionsForResolution()
     */
    @Override
    public Repository[] getRepositoryDefinitionsForResolution()
    {
        return resolveRepositories;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getRemoteRepositoriesForResolution()
     */
    @Override
    public List<RemoteRepository> getRemoteRepositoriesForResolution()
    {
        return remoteRepositories;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getRemoteRepositoriesArray()
     */
    @Override
    public RemoteRepository[] getRemoteRepositoriesArray()
    {
        return remoteRepositories == null ? new RemoteRepository[] {}
                        : remoteRepositories.toArray( new RemoteRepository[] {} );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#setRemoteRepositoriesForResolution(java.util.List)
     */
    @Override
    public ProjectToolsSession setRemoteRepositoriesForResolution( final List<RemoteRepository> remoteRepositories )
    {
        this.remoteRepositories = remoteRepositories;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getProjectBuildingRequest()
     */
    @Override
    public ProjectBuildingRequest getProjectBuildingRequest()
    {
        return projectBuildingRequest;
    }

    @Override
    public ProjectToolsSession setProjectBuildingRequest( final ProjectBuildingRequest pbr )
    {
        this.projectBuildingRequest = pbr;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getRepositorySystemSession()
     */
    @Override
    public RepositorySystemSession getRepositorySystemSession()
    {
        return repositorySystemSession;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#addReactorProject(org.apache.maven.project.MavenProject)
     */
    @Override
    public synchronized ProjectToolsSession addReactorProject( final MavenProject project )
    {
        final String id = key( project.getGroupId(), project.getArtifactId(), project.getVersion() );
        if ( !reactorProjects.containsKey( id ) )
        {
            reactorProjects.put( id, project );
        }

        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#setReactorProjects(org.apache.maven.project.MavenProject)
     */
    @Override
    public synchronized ProjectToolsSession setReactorProjects( final MavenProject... projects )
    {
        reactorProjects.clear();
        for ( final MavenProject project : projects )
        {
            addReactorProject( project );
        }

        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#setReactorProjects(java.lang.Iterable)
     */
    @Override
    public synchronized ProjectToolsSession setReactorProjects( final Iterable<MavenProject> projects )
    {
        reactorProjects.clear();
        for ( final MavenProject project : projects )
        {
            addReactorProject( project );
        }

        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getReactorProjects()
     */
    @Override
    public Collection<MavenProject> getReactorProjects()
    {
        return new ArrayList<MavenProject>( reactorProjects.values() );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getReactorPom(org.sonatype.aether.artifact.Artifact)
     */
    @Override
    public File getReactorPom( final Artifact artifact )
    {
        final MavenProject project = getReactorProject( artifact );
        return project == null ? null : project.getFile();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getReactorProject(org.sonatype.aether.artifact.Artifact)
     */
    @Override
    public MavenProject getReactorProject( final Artifact artifact )
    {
        final String id = key( artifact.getGroupId(), artifact.getArtifactId(), artifact.getBaseVersion() );
        return reactorProjects.get( id );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#copyOf(org.apache.maven.mae.project.session.ProjectToolsSession)
     */
    @Override
    public ProjectToolsSession copy()
    {
        final SimpleProjectToolsSession copy = new SimpleProjectToolsSession();
        copy.resolveRepositories = resolveRepositories;

        copy.localRepositoryDirectory = localRepositoryDirectory;

        copy.projectBuildingRequest =
            projectBuildingRequest == null ? null : new DefaultProjectBuildingRequest( projectBuildingRequest );

        copy.reactorProjects = new LinkedHashMap<String, MavenProject>( reactorProjects );

        copy.remoteArtifactRepositories =
            remoteArtifactRepositories == null ? null : new ArrayList<ArtifactRepository>( remoteArtifactRepositories );

        copy.remoteRepositories =
            remoteRepositories == null ? null : new ArrayList<RemoteRepository>( remoteRepositories );

        copy.repositorySystemSession =
            repositorySystemSession == null ? null : new DefaultRepositorySystemSession( repositorySystemSession );

        return copy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getLocalRepositoryDirectory()
     */
    @Override
    public File getLocalRepositoryDirectory()
    {
        return localRepositoryDirectory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#setExecutionRequest(org.apache.maven.execution.MavenExecutionRequest)
     */
    @Override
    public ProjectToolsSession setExecutionRequest( final MavenExecutionRequest executionRequest )
    {
        this.executionRequest = executionRequest;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getExecutionRequest()
     */
    @Override
    public synchronized MavenExecutionRequest getExecutionRequest()
    {
        if ( executionRequest == null )
        {
            executionRequest = new DefaultMavenExecutionRequest();
        }

        return executionRequest;
    }

    @Override
    public ProjectToolsSession setExecutionResult( final MavenExecutionResult executionResult )
    {
        this.executionResult = executionResult;
        return this;
    }

    @Override
    public synchronized MavenExecutionResult getExecutionResult()
    {
        if ( executionResult == null )
        {
            this.executionResult = new DefaultMavenExecutionResult();
        }

        return executionResult;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#setResolveThreads(int)
     */
    @Override
    public ProjectToolsSession setResolveThreads( final int threads )
    {
        resolveThreads = threads;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getResolveThreads()
     */
    @Override
    public int getResolveThreads()
    {
        return resolveThreads;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#isProcessPomPlugins()
     */
    @Override
    public boolean isProcessPomPlugins()
    {
        return resolvePlugins;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#setProcessPomPlugins(boolean)
     */
    @Override
    public ProjectToolsSession setProcessPomPlugins( final boolean resolvePlugins )
    {
        this.resolvePlugins = resolvePlugins;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getDependencySelector()
     */
    @Override
    public DependencySelector getDependencySelector()
    {
        return dependencySelector;
    }

    @Override
    public ProjectToolsSession setDependencySelector( final DependencySelector dependencySelector )
    {
        this.dependencySelector = dependencySelector;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getDependencyFilter()
     */
    @Override
    public DependencyFilter getDependencyFilter()
    {
        return filter;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#setDependencyFilter(org.sonatype.aether.graph.DependencyFilter)
     */
    @Override
    public ProjectToolsSession setDependencyFilter( final DependencyFilter filter )
    {
        this.filter = filter;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#connectProjectHierarchy(org.sonatype.aether.artifact.Artifact,
     *      boolean, org.sonatype.aether.artifact.Artifact, boolean)
     */
    @Override
    public ProjectToolsSession connectProjectHierarchy( final Artifact parent, final boolean parentPreResolved,
                                                        final Artifact child, final boolean childPreResolved )
    {
        // NOP.
        return this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> T setState( final T state )
    {
        if ( state != null )
        {
            return (T) states.put( state.getClass(), state );
        }

        return null;
    }

    @Override
    public void clearStates()
    {
        states.clear();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> T clearState( final Class<T> type )
    {
        return (T) states.remove( type );
    }

    @Override
    public <T> T getState( final Class<T> type )
    {
        final Object state = states.get( type );
        return state == null ? null : type.cast( state );
    }

    public void setResolveRepositories( final Repository... resolveRepositories )
    {
        this.resolveRepositories = resolveRepositories;
    }

    public void setLocalRepositoryDirectory( final File localRepositoryDirectory )
    {
        this.localRepositoryDirectory = localRepositoryDirectory;
    }

    @Override
    public int getPomValidationLevel()
    {
        return pomValidationLevel;
    }

    @Override
    public ProjectToolsSession setPomValidationLevel( final int pomValidationLevel )
    {
        this.pomValidationLevel = pomValidationLevel;
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.project.session.ProjectToolsSession#getTemplateProjectBuildingRequest()
     */
    @Override
    public ProjectBuildingRequest getTemplateProjectBuildingRequest()
    {
        return templateProjectBuildingRequest;
    }

    public ProjectToolsSession setTemplProjectBuildingRequest( final ProjectBuildingRequest templateProjectBuildingRequest )
    {
        this.templateProjectBuildingRequest = templateProjectBuildingRequest;
        return this;
    }

    private final Map<Class<?>, EventDispatcher<?>> eventDispatchers = new HashMap<Class<?>, EventDispatcher<?>>();

    private boolean initialized;

    @SuppressWarnings( "unchecked" )
    @Override
    public <E> EventDispatcher<E> getEventDispatcher( final Class<E> eventType )
    {
        return (EventDispatcher<E>) eventDispatchers.get( eventType );
    }

    @Override
    public <E> ProjectToolsSession setEventDispatcher( final Class<E> eventType, final EventDispatcher<E> dispatcher )
    {
        eventDispatchers.put( eventType, dispatcher );
        return this;
    }

    @Override
    public void initialize( final RepositorySystemSession rss, final ProjectBuildingRequest pbr,
                            final List<ArtifactRepository> artifactRepos, final List<RemoteRepository> remoteRepos )
    {
        this.repositorySystemSession = rss;
        this.projectBuildingRequest = pbr;

        setArtifactRepositoriesForResolution( artifactRepos );
        setRemoteRepositoriesForResolution( remoteRepos );

        this.initialized = true;
    }

    @Override
    public boolean isInitialized()
    {
        return initialized;
    }

}
