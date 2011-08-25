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

package org.apache.maven.mae.depgraph.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.maven.RepositoryUtils;
import org.apache.maven.mae.depgraph.DepGraphNode;
import org.apache.maven.mae.depgraph.DepGraphRootNode;
import org.apache.maven.mae.depgraph.DependencyGraph;
import org.apache.maven.mae.project.session.ProjectToolsSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.artifact.JavaScopes;

@Component( role = DependencyGraphResolver.class )
public class DependencyGraphResolver
{

    private static final Logger LOGGER = Logger.getLogger( DependencyGraphResolver.class );

    @Requirement
    private RepositorySystem repositorySystem;

    public DependencyGraph accumulateGraph( final Collection<MavenProject> rootProjects,
                                            RepositorySystemSession rss,
                                            final ProjectToolsSession session )
    {
        // if ( LOGGER.isDebugEnabled() )
        {
            if ( LOGGER.isDebugEnabled() )
            {
                LOGGER.debug( "Preparing for dependency-graph accumulation..." );
            }
        }
        rss = prepareForGraphResolution( rss, session );

        // if ( LOGGER.isDebugEnabled() )
        {
            if ( LOGGER.isDebugEnabled() )
            {
                LOGGER.debug( "Accumulating dependency graph..." );
            }
        }

        return accumulate( session, rss, rootProjects, session.getRemoteRepositoriesArray() );
    }

    public DependencyGraph resolveGraph( final DependencyGraph depGraph,
                                         final Collection<MavenProject> rootProjects,
                                         final RepositorySystemSession rss,
                                         final ProjectToolsSession session )
    {

        // if ( LOGGER.isDebugEnabled() )
        {
            if ( LOGGER.isDebugEnabled() )
            {
                LOGGER.debug( "Resolving dependencies in graph..." );
            }
        }
        resolve( rss, rootProjects, depGraph, session );

        // if ( LOGGER.isDebugEnabled() )
        {
            if ( LOGGER.isDebugEnabled() )
            {
                LOGGER.debug( "Graph state contains: " + depGraph.size() + " nodes." );
            }
        }

        return depGraph;
    }

    // TODO: Allow fine-tuning of scopes resolved...
    private RepositorySystemSession prepareForGraphResolution( final RepositorySystemSession s,
                                                               final ProjectToolsSession session )
    {
        final DefaultRepositorySystemSession result = new DefaultRepositorySystemSession( s );
        result.setDependencySelector( session.getDependencySelector() );

        return result;
    }

    private void resolve( final RepositorySystemSession session,
                          final Collection<MavenProject> rootProjects,
                          final DependencyGraph depGraph, final ProjectToolsSession toolsSession )
    {
        final Set<DependencyResolveWorker> workers = new HashSet<DependencyResolveWorker>();
        for ( final DepGraphNode node : depGraph )
        {
            if ( node == null || node.hasErrors() || node.isPreResolved() )
            {
                continue;
            }

            // if ( LOGGER.isDebugEnabled() )
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( "Resolving: " + node.getLatestArtifact() );
                }
            }
            workers.add( new DependencyResolveWorker( node, session, repositorySystem ) );
        }

        runResolve( workers, toolsSession );
        // for ( final DependencyResolveWorker worker : workers )
        // {
        // worker.run();
        // }

        // if ( LOGGER.isDebugEnabled() )
        {
            if ( LOGGER.isDebugEnabled() )
            {
                LOGGER.debug( "Dependency-graph resolution complete." );
            }
        }
    }

    private void runResolve( final Set<DependencyResolveWorker> workers,
                             final ProjectToolsSession session )
    {
        final ExecutorService executorService =
            Executors.newFixedThreadPool( session.getResolveThreads() );

        final CountDownLatch latch = new CountDownLatch( workers.size() );
        for ( final DependencyResolveWorker worker : workers )
        {
            worker.setLatch( latch );
            executorService.execute( worker );
        }

        synchronized ( latch )
        {
            long count = 0;
            while ( ( count = latch.getCount() ) > 0 )
            {
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( count + " resolution workers remaining. Waiting 3s..." );
                    }
                }
                try
                {
                    latch.await( 3, TimeUnit.SECONDS );
                }
                catch ( final InterruptedException e )
                {
                    break;
                }
            }
        }

        boolean terminated = false;
        int count = 1;
        while ( !terminated )
        {
            try
            {
                executorService.shutdown();
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Attempt " + count
                            + " to shutdown graph-resolver. Waiting 3s..." );
                    }
                }

                count++;
                terminated = executorService.awaitTermination( 3, TimeUnit.SECONDS );
            }
            catch ( final InterruptedException e )
            {
                break;
            }
        }
    }

    private DependencyGraph accumulate( final ProjectToolsSession session,
                                        final RepositorySystemSession rss,
                                        final Collection<MavenProject> projects,
                                        final RemoteRepository... remoteRepositories )
    {
        final ArtifactTypeRegistry stereotypes = rss.getArtifactTypeRegistry();

        DependencyGraph depGraph;
        synchronized ( session )
        {
            depGraph = session.getState( DependencyGraph.class );
            if ( depGraph == null )
            {
                depGraph = new DependencyGraph();
            }
        }

        final GraphAccumulator accumulator =
            new GraphAccumulator( depGraph, session.getDependencyFilter() );

        for ( final MavenProject project : projects )
        {
            // if ( LOGGER.isDebugEnabled() )
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( "Collecting dependencies for: " + project );
                }
            }
            final CollectRequest request = new CollectRequest();
            request.setRequestContext( "project" );
            request.setRepositories( Arrays.asList( remoteRepositories ) );

            if ( project.getDependencyArtifacts() == null )
            {
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Adding dependencies to collection request..." );
                    }
                }
                for ( final Dependency dependency : project.getDependencies() )
                {
                    request.addDependency( RepositoryUtils.toDependency( dependency, stereotypes ) );
                }
            }
            else
            {
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Mapping project dependencies by management key..." );
                    }
                }
                final Map<String, Dependency> dependencies = new HashMap<String, Dependency>();
                for ( final Dependency dependency : project.getDependencies() )
                {
                    final String key = dependency.getManagementKey();
                    dependencies.put( key, dependency );
                }

                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Adding dependencies to collection request..." );
                    }
                }
                for ( final org.apache.maven.artifact.Artifact artifact : project.getDependencyArtifacts() )
                {
                    final String key = artifact.getDependencyConflictId();
                    final Dependency dependency = dependencies.get( key );
                    final Collection<org.apache.maven.model.Exclusion> exclusions =
                        dependency != null ? dependency.getExclusions() : null;

                    org.sonatype.aether.graph.Dependency dep =
                        RepositoryUtils.toDependency( artifact, exclusions );
                    if ( !JavaScopes.SYSTEM.equals( dep.getScope() )
                        && dep.getArtifact().getFile() != null )
                    {
                        // enable re-resolution
                        org.sonatype.aether.artifact.Artifact art = dep.getArtifact();
                        art = art.setFile( null ).setVersion( art.getBaseVersion() );
                        dep = dep.setArtifact( art );
                    }
                    request.addDependency( dep );
                }
            }

            final DependencyManagement depMngt = project.getDependencyManagement();
            if ( depMngt != null )
            {
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Adding managed dependencies to collection request..." );
                    }
                }
                for ( final Dependency dependency : depMngt.getDependencies() )
                {
                    request.addManagedDependency( RepositoryUtils.toDependency( dependency,
                                                                                stereotypes ) );
                }
            }

            // if ( LOGGER.isDebugEnabled() )
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( "Collecting dependencies..." );
                }
            }
            CollectResult result;
            final Object old = rss.getData().get( ProjectToolsSession.SESSION_KEY );
            try
            {
                rss.getData().set( ProjectToolsSession.SESSION_KEY, session );
                result = repositorySystem.collectDependencies( rss, request );
            }
            catch ( final DependencyCollectionException e )
            {
                // TODO: Handle problem resolving POMs...
                result = e.getResult();

                // result.setDependencyGraph( e.getResult().getRoot() );
                // result.setCollectionErrors( e.getResult().getExceptions() );
                //
                // throw new DependencyResolutionException( result, "Could not resolve dependencies for project "
                // + project.getId() + ": " + e.getMessage(), e );
            }
            finally
            {
                rss.getData().set( ProjectToolsSession.SESSION_KEY, old );
            }

            final DependencyNode root = result.getRoot();
            final DepGraphRootNode rootNode = depGraph.addRoot( root, project );

            accumulator.resetForNextRun( root, rootNode );

            // if ( LOGGER.isDebugEnabled() )
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( "Adding collected dependencies to consolidated dependency graph..." );
                }
            }
            result.getRoot().accept( accumulator );

        }

        return depGraph;
    }

    private static final class GraphAccumulator
        implements DependencyVisitor
    {
        private final LinkedList<DependencyNode> parents = new LinkedList<DependencyNode>();

        private final Set<Exclusion> exclusions = new HashSet<Exclusion>();

        private final Set<Exclusion> lastExclusions = new HashSet<Exclusion>();

        private final DependencyGraph depGraph;

        private DependencyNode root;

        private DepGraphRootNode rootNode;

        private final DependencyFilter filter;

        GraphAccumulator( final DependencyGraph depGraph, final DependencyFilter filter )
        {
            this.depGraph = depGraph;
            this.filter = filter;
        }

        void resetForNextRun( final DependencyNode root, final DepGraphRootNode rootNode )
        {
            parents.clear();
            this.root = root;
            this.rootNode = rootNode;
            exclusions.clear();
            lastExclusions.clear();
        }

        @Override
        public boolean visitEnter( final DependencyNode node )
        {
            if ( filter != null && !filter.accept( node, parents ) )
            {
                return false;
            }

            if ( node == root )
            {
                parents.addFirst( root );
                return true;
            }
            else if ( node == null || node.getDependency() == null
                || node.getDependency().getArtifact() == null )
            {
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Invalid node: " + node );
                    }
                }
                return true;
            }

            // if ( LOGGER.isDebugEnabled() )
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( "START: dependency-processing for: " + node );
                }
            }

            boolean result = false;
            final Artifact artifact = node.getDependency().getArtifact();
            if ( !excluded( artifact ) )
            {
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Enabling resolution for: " + node );
                    }
                }

                final DependencyNode parent = parents.getFirst();
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Adding dependency from: " + parent + " to: " + node );
                    }
                }

                // TODO: don't traverse beyond this node if it's already been considered...though we still need to
                // connect it
                // to the parent node (see below).
                result = !depGraph.contains( node );

                // result = true;

                if ( parent == root )
                {
                    depGraph.addDependency( rootNode, node );
                }
                else
                {
                    depGraph.addDependency( parent, node );
                }

                if ( node.getDependency().getExclusions() != null )
                {
                    for ( final Exclusion exclusion : node.getDependency().getExclusions() )
                    {
                        if ( exclusions.add( exclusion ) )
                        {
                            lastExclusions.add( exclusion );
                        }
                    }
                }

                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Pushing node: " + node + " onto parents stack." );
                    }
                }
                parents.addFirst( node );

                final StringBuilder builder = new StringBuilder();
                for ( int i = 0; i < parents.size(); i++ )
                {
                    builder.append( "  " );
                }
                builder.append( ">>>" );
                builder.append( node );
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( builder.toString() );
                    }
                }
            }
            else
            {
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "DISABLING resolution for: " + node );
                    }
                }
            }

            if ( node != null && !node.getRelocations().isEmpty() )
            {
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "The artifact " + node.getRelocations().get( 0 )
                            + " has been relocated to " + node.getDependency().getArtifact() );
                    }
                }
            }

            return result;
        }

        private boolean excluded( final Artifact artifact )
        {
            for ( final Exclusion exclusion : exclusions )
            {
                if ( match( exclusion.getGroupId(), artifact.getGroupId() )
                    && match( exclusion.getArtifactId(), artifact.getArtifactId() )
                    && match( exclusion.getExtension(), artifact.getExtension() )
                    && match( exclusion.getClassifier(), artifact.getClassifier() ) )
                {
                    // if ( LOGGER.isDebugEnabled() )
                    {
                        if ( LOGGER.isDebugEnabled() )
                        {
                            LOGGER.debug( "EXCLUDED: " + artifact );
                        }
                    }
                    return true;
                }
            }

            return false;
        }

        private boolean match( final String excluded, final String check )
        {
            return "*".equals( excluded ) || excluded.equals( check );
        }

        @Override
        public boolean visitLeave( final DependencyNode node )
        {
            if ( node == null || parents.isEmpty() )
            {
                return true;
            }

            if ( node == parents.getFirst() )
            {
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "Removing exclusions from last node: " + node );
                    }
                }

                for ( final Exclusion exclusion : lastExclusions )
                {
                    exclusions.remove( exclusion );
                }

                lastExclusions.clear();

                final StringBuilder builder = new StringBuilder();
                for ( int i = 0; i < parents.size(); i++ )
                {
                    builder.append( "  " );
                }
                builder.append( "<<<" );
                builder.append( node );
                // if ( LOGGER.isDebugEnabled() )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( builder.toString() );
                    }
                }

                parents.removeFirst();
            }
            else
            {
                final int idx = parents.indexOf( node );
                if ( idx > -1 )
                {
                    // if ( LOGGER.isDebugEnabled() )
                    {
                        if ( LOGGER.isDebugEnabled() )
                        {
                            LOGGER.debug( "TRAVERSAL LEAK. Removing " + ( idx + 1 )
                                + " unaccounted-for parents that have finished traversal." );
                        }
                    }

                    for ( int i = 0; i <= idx; i++ )
                    {
                        parents.removeFirst();
                    }
                }
            }

            // if ( LOGGER.isDebugEnabled() )
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( "END: dependency-processing for: " + node );
                }
            }

            return true;
        }

    }

    private static final class DependencyResolveWorker
        implements Runnable
    {

        private final DepGraphNode depState;

        private final RepositorySystemSession session;

        private final RepositorySystem repositorySystem;

        private ArtifactResult result;

        private CountDownLatch latch;

        DependencyResolveWorker( final DepGraphNode depState,
                                 final RepositorySystemSession session,
                                 final RepositorySystem repositorySystem )
        {
            this.depState = depState;
            this.session = session;
            this.repositorySystem = repositorySystem;
        }

        void setLatch( final CountDownLatch latch )
        {
            this.latch = latch;
        }

        @Override
        public void run()
        {
            final Artifact artifact = depState.getLatestArtifact();

            try
            {
                final ArtifactRequest request =
                    new ArtifactRequest(
                                         artifact,
                                         new ArrayList<RemoteRepository>(
                                                                          depState.getRemoteRepositories() ),
                                         "project" );

                result = new ArtifactResult( request );
                if ( validateForResolution() )
                {
                    try
                    {
                        // if ( LOGGER.isDebugEnabled() )
                        {
                            if ( LOGGER.isDebugEnabled() )
                            {
                                LOGGER.debug( "RESOLVE: " + artifact );
                            }
                        }

                        result = repositorySystem.resolveArtifact( session, request );
                    }
                    catch ( final ArtifactResolutionException e )
                    {
                        result.addException( e );
                    }
                }
            }
            finally
            {
                // final Runtime r = Runtime.getRuntime();
                //
                // final long MB = 1024 * 1024;
                //
                // System.out.println( "Memory status: " + ( r.totalMemory() - r.freeMemory() ) / MB + "M/"
                // + r.totalMemory() / MB + "M" );

                // FIXME: Do we need to detect whether resolution already happened for this artifact before we try to
                // resolve it
                // again??
                depState.merge( result );
                if ( latch != null )
                {
                    latch.countDown();
                }
            }
        }

        private boolean validateForResolution()
        {
            boolean valid = true;
            if ( session == null )
            {
                result.addException( new IllegalArgumentException( "Cannot resolve dependency: "
                    + depState.getLatestArtifact() + ", RepositorySystemSession has not been set!" ) );

                valid = false;
            }

            if ( repositorySystem == null )
            {
                result.addException( new IllegalArgumentException( "Cannot resolve dependency: "
                    + depState.getLatestArtifact() + ", RepositorySystem has not been set!" ) );

                valid = false;
            }

            return valid;
        }
    }

}
