/*
 * Copyright 2011 Red Hat, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.maven.mae.project.session;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.boot.embed.MAEEmbedder;
import org.apache.maven.mae.project.ProjectToolsException;
import org.apache.maven.model.Repository;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.impl.internal.EnhancedLocalRepositoryManager;
import org.sonatype.aether.repository.AuthenticationSelector;
import org.sonatype.aether.repository.ProxySelector;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component( role = SessionInjector.class )
public class DefaultSessionInjector
    implements SessionInjector
{

    private static final Logger LOGGER = Logger.getLogger( DefaultSessionInjector.class );

    @Requirement
    private MAEEmbedder embedder;

    @Requirement
    private RepositorySystem mavenRepositorySystem;

    @Override
    public synchronized ProjectBuildingRequest getProjectBuildingRequest( final ProjectToolsSession session )
        throws ProjectToolsException
    {
        ProjectBuildingRequest pbr = session.getProjectBuildingRequest();
        try
        {
            if ( pbr == null )
            {
                pbr = embedder.serviceManager().createProjectBuildingRequest();

                pbr.setValidationLevel( session.getPomValidationLevel() );
                pbr.setProcessPlugins( session.isProcessPomPlugins() );
                pbr.setResolveDependencies( false );
                pbr.setSystemProperties( System.getProperties() );
                pbr.setInactiveProfileIds( new ArrayList<String>() );
                pbr.setRepositoryMerging( ProjectBuildingRequest.RepositoryMerging.REQUEST_DOMINANT );

                final RepositorySystemSession rss = getRepositorySystemSession( session );
                pbr.setRepositorySession( rss );
                pbr.setLocalRepository( mavenRepositorySystem.createLocalRepository( rss.getLocalRepository()
                                                                                        .getBasedir() ) );
                pbr.setRemoteRepositories( getArtifactRepositories( session ) );

                session.setProjectBuildingRequest( pbr );
            }
            else
            {
                pbr = new DefaultProjectBuildingRequest( pbr );
                pbr.setRepositorySession( getRepositorySystemSession( session ) );
            }
        }
        catch ( final MAEException e )
        {
            throw new ProjectToolsException( "Failed to create project-building request: %s", e, e.getMessage() );
        }
        catch ( final InvalidRepositoryException e )
        {
            throw new ProjectToolsException( "Failed to create local-repository instance. Reason: %s",
                                             e,
                                             e.getMessage() );
        }

        return pbr;
    }

    @Override
    public RepositorySystemSession getRepositorySystemSession( final ProjectToolsSession session )
        throws MAEException
    {
        final File localRepo = session.getLocalRepositoryDirectory();

        RepositorySystemSession sess = session.getRepositorySystemSession();
        if ( sess == null )
        {
            final DefaultRepositorySystemSession rss =
                new DefaultRepositorySystemSession( embedder.serviceManager()
                                                            .createAetherRepositorySystemSession( session.getExecutionRequest() ) );

            // session.setWorkspaceReader( new ImportWorkspaceReader( workspace ) );
            rss.setConfigProperty( ProjectToolsSession.SESSION_KEY, session );

            if ( localRepo != null )
            {
                localRepo.mkdirs();
                rss.setLocalRepositoryManager( new EnhancedLocalRepositoryManager( localRepo ) );
            }

            rss.setWorkspaceReader( new SessionWorkspaceReader( session ) );

            sess = rss;

            session.setRepositorySystemSession( sess );
        }

        sess.getData().set( ProjectToolsSession.SESSION_KEY, session );

        return sess;
    }

    @Override
    public synchronized List<RemoteRepository> getRemoteRepositories( final ProjectToolsSession session )
        throws ProjectToolsException
    {
        List<RemoteRepository> result = session.getRemoteRepositories();

        if ( result == null )
        {
            result = new ArrayList<RemoteRepository>();

            boolean selectorsEnabled = false;
            AuthenticationSelector authSelector = null;
            ProxySelector proxySelector = null;
            if ( session.getRepositorySystemSession() != null )
            {
                selectorsEnabled = true;
                authSelector = session.getRepositorySystemSession().getAuthenticationSelector();
                proxySelector = session.getRepositorySystemSession().getProxySelector();
            }
            else
            {
                LOGGER.warn( "Cannot set proxy or authentication information on new RemoteRepositories; "
                    + "RepositorySystemSession is not available in ProjectToolsSession instance." );
            }

            for ( final ArtifactRepository repo : getArtifactRepositories( session ) )
            {
                RemoteRepository r = null;
                if ( repo instanceof RemoteRepository )
                {
                    r = (RemoteRepository) repo;
                }
                else if ( repo instanceof MavenArtifactRepository )
                {
                    r = new RemoteRepository( repo.getId(), "default", repo.getUrl() );
                }

                if ( r != null )
                {
                    if ( selectorsEnabled )
                    {
                        r.setAuthentication( authSelector.getAuthentication( r ) );
                        r.setProxy( proxySelector.getProxy( r ) );
                    }

                    result.add( r );
                }
            }

            session.setRemoteRepositories( result );
        }

        return result;
    }

    @Override
    public synchronized List<ArtifactRepository> getArtifactRepositories( final ProjectToolsSession session )
        throws ProjectToolsException
    {
        List<ArtifactRepository> repos = session.getRemoteArtifactRepositories();
        if ( repos == null )
        {
            final Repository[] remoteRepositories = session.getResolveRepositories();

            repos = new ArrayList<ArtifactRepository>( remoteRepositories == null ? 0 : remoteRepositories.length );

            if ( remoteRepositories != null )
            {
                for ( final Repository repo : remoteRepositories )
                {
                    try
                    {
                        repos.add( mavenRepositorySystem.buildArtifactRepository( repo ) );
                    }
                    catch ( final InvalidRepositoryException e )
                    {
                        throw new ProjectToolsException( "Failed to create remote artifact repository instance from: %s\nReason: %s",
                                                         e,
                                                         repo,
                                                         e.getMessage() );
                    }
                }
            }

            try
            {
                repos.add( mavenRepositorySystem.createDefaultRemoteRepository() );
            }
            catch ( final InvalidRepositoryException e )
            {
                throw new ProjectToolsException( "Failed to create default (central) repository instance: %s",
                                                 e,
                                                 e.getMessage() );
            }

            session.setRemoteArtifactRepositories( repos );
        }

        return repos;
    }

}
