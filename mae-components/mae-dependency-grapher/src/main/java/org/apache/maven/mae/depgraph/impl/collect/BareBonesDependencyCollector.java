package org.apache.maven.mae.depgraph.impl.collect;

/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/

import static org.sonatype.aether.util.artifact.ArtifacIdUtils.toId;

import org.apache.log4j.Logger;
import org.apache.maven.mae.project.session.ProjectToolsSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositoryException;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.RequestTrace;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactType;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.collection.DependencyCollectionContext;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.collection.DependencyGraphTransformationContext;
import org.sonatype.aether.collection.DependencyGraphTransformer;
import org.sonatype.aether.collection.DependencyManagement;
import org.sonatype.aether.collection.DependencyManager;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.collection.DependencyTraverser;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.impl.ArtifactDescriptorReader;
import org.sonatype.aether.impl.DependencyCollector;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.impl.VersionRangeResolver;
import org.sonatype.aether.impl.internal.DefaultDependencyCollector;
import org.sonatype.aether.repository.ArtifactRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResolutionException;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.spi.locator.Service;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.DefaultRequestTrace;
import org.sonatype.aether.util.artifact.ArtifactProperties;
import org.sonatype.aether.version.Version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * {@link DependencyCollector} implementation based on {@link DefaultDependencyCollector}, but which uses a more
 * aggressively graph-oriented approach. This centralizes and de-dupes references to {@link Artifact} and
 * {@link RemoteRepository} instances, in order to reduce the memory consumption.
 * 
 * @author Benjamin Bentmann
 * @author John Casey
 */
@Component( role = DependencyCollector.class, hint = BareBonesDependencyCollector.HINT )
public class BareBonesDependencyCollector
    implements DependencyCollector, Service
{

    public static final String HINT = "bare-bones";

    private static final Logger logger = Logger.getLogger( BareBonesDependencyCollector.class );

    @Requirement
    private RemoteRepositoryManager remoteRepositoryManager;

    @Requirement
    private ArtifactDescriptorReader descriptorReader;

    @Requirement
    private VersionRangeResolver versionRangeResolver;

    public BareBonesDependencyCollector()
    {
        // enables default constructor
    }

    public BareBonesDependencyCollector( final Logger logger, final RemoteRepositoryManager remoteRepositoryManager,
                                         final ArtifactDescriptorReader artifactDescriptorReader,
                                         final VersionRangeResolver versionRangeResolver )
    {
        setRemoteRepositoryManager( remoteRepositoryManager );
        setArtifactDescriptorReader( artifactDescriptorReader );
        setVersionRangeResolver( versionRangeResolver );
    }

    @Override
    public void initService( final ServiceLocator locator )
    {
        setRemoteRepositoryManager( locator.getService( RemoteRepositoryManager.class ) );
        setArtifactDescriptorReader( locator.getService( ArtifactDescriptorReader.class ) );
        setVersionRangeResolver( locator.getService( VersionRangeResolver.class ) );
    }

    public BareBonesDependencyCollector setRemoteRepositoryManager( final RemoteRepositoryManager remoteRepositoryManager )
    {
        if ( remoteRepositoryManager == null )
        {
            throw new IllegalArgumentException( "remote repository manager has not been specified" );
        }
        this.remoteRepositoryManager = remoteRepositoryManager;
        return this;
    }

    public BareBonesDependencyCollector setArtifactDescriptorReader( final ArtifactDescriptorReader artifactDescriptorReader )
    {
        if ( artifactDescriptorReader == null )
        {
            throw new IllegalArgumentException( "artifact descriptor reader has not been specified" );
        }
        descriptorReader = artifactDescriptorReader;
        return this;
    }

    public BareBonesDependencyCollector setVersionRangeResolver( final VersionRangeResolver versionRangeResolver )
    {
        if ( versionRangeResolver == null )
        {
            throw new IllegalArgumentException( "version range resolver has not been specified" );
        }
        this.versionRangeResolver = versionRangeResolver;
        return this;
    }

    @Override
    public CollectResult collectDependencies( RepositorySystemSession session, final CollectRequest request )
        throws DependencyCollectionException
    {
        session = optimizeSession( session );

        final RequestTrace trace = DefaultRequestTrace.newChild( request.getTrace(), request );

        final CollectResult result = new CollectResult( request );

        final ProjectToolsSession toolsSession =
            (ProjectToolsSession) session.getData().get( ProjectToolsSession.SESSION_KEY );
        
        DependencySelector depSelector = toolsSession == null ? null : toolsSession.getDependencySelector();
        if ( depSelector == null )
        {
            depSelector = session.getDependencySelector();
        }
        
        final DependencyManager depManager = session.getDependencyManager();
        final DependencyTraverser depTraverser = session.getDependencyTraverser();

        Dependency root = request.getRoot();
        List<RemoteRepository> repositories = request.getRepositories();
        List<Dependency> dependencies = request.getDependencies();
        List<Dependency> managedDependencies = request.getManagedDependencies();

        final SlimDepGraph graph = new SlimDepGraph( session );
        SlimDependencyEdge edge = null;
        if ( root != null )
        {
            VersionRangeResult rangeResult;
            try
            {
                final VersionRangeRequest rangeRequest =
                    new VersionRangeRequest( root.getArtifact(), request.getRepositories(), request.getRequestContext() );
                rangeRequest.setTrace( trace );
                rangeResult = versionRangeResolver.resolveVersionRange( session, rangeRequest );

                if ( rangeResult.getVersions().isEmpty() )
                {
                    throw new VersionRangeResolutionException( rangeResult, "No versions available for "
                        + root.getArtifact() + " within specified range" );
                }
            }
            catch ( final VersionRangeResolutionException e )
            {
                result.addException( e );
                throw new DependencyCollectionException( result );
            }

            final Version version = rangeResult.getVersions().get( rangeResult.getVersions().size() - 1 );
            root = root.setArtifact( root.getArtifact().setVersion( version.toString() ) );

            ArtifactDescriptorResult descriptorResult;
            try
            {
                final ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
                descriptorRequest.setArtifact( root.getArtifact() );
                descriptorRequest.setRepositories( request.getRepositories() );
                descriptorRequest.setRequestContext( request.getRequestContext() );
                descriptorRequest.setTrace( trace );
                if ( isLackingDescriptor( root.getArtifact() ) )
                {
                    descriptorResult = new ArtifactDescriptorResult( descriptorRequest );
                }
                else
                {
                    descriptorResult = descriptorReader.readArtifactDescriptor( session, descriptorRequest );
                }
            }
            catch ( final ArtifactDescriptorException e )
            {
                result.addException( e );
                throw new DependencyCollectionException( result );
            }

            root = root.setArtifact( descriptorResult.getArtifact() );

            repositories =
                remoteRepositoryManager.aggregateRepositories( session, repositories,
                                                               descriptorResult.getRepositories(), true );
            dependencies = mergeDeps( dependencies, descriptorResult.getDependencies() );
            managedDependencies = mergeDeps( managedDependencies, descriptorResult.getManagedDependencies() );

            final SlimDependencyNode node = new SlimDependencyNode( toId( descriptorResult.getArtifact() ), graph );
            node.setAliases( descriptorResult.getAliases() );
            node.setRepositories( request.getRepositories() );

            edge = new SlimDependencyEdge( node, graph );
            edge.setDependency( root );
            edge.setRequestContext( request.getRequestContext() );
            edge.setRelocations( descriptorResult.getRelocations() );
            edge.setVersionConstraint( rangeResult.getVersionConstraint() );
            edge.setVersion( version );
        }
        else
        {
            edge = new SlimDependencyEdge( new SlimDependencyNode( SlimDependencyNode.UNKNOWN_ROOT_ID, graph ), graph );
        }

        result.setRoot( edge );

        final boolean traverse = ( root == null ) || depTraverser.traverseDependency( root );

        if ( traverse && !dependencies.isEmpty() )
        {
            final LinkedList<SlimDependencyEdge> edges = new LinkedList<SlimDependencyEdge>();
            edges.addFirst( edge );

            final DependencyCollectionContext context = new CollectionContext( session, root, managedDependencies );

            final DepGraphCache pool = new DepGraphCache( session );
            process( session, toolsSession, trace, result, edges, dependencies, repositories,
                     depSelector.deriveChildSelector( context ), depManager.deriveChildManager( context ),
                     depTraverser.deriveChildTraverser( context ), pool, graph );
        }

        final DependencyGraphTransformer transformer = session.getDependencyGraphTransformer();
        try
        {
            final DependencyGraphTransformationContext context = new GraphTransformationContext( session );
            result.setRoot( transformer.transformGraph( edge, context ) );
        }
        catch ( final RepositoryException e )
        {
            result.addException( e );
        }

        if ( !result.getExceptions().isEmpty() )
        {
            throw new DependencyCollectionException( result );
        }

        return result;
    }

    private RepositorySystemSession optimizeSession( final RepositorySystemSession session )
    {
        final DefaultRepositorySystemSession optimized = new DefaultRepositorySystemSession( session );
        optimized.setArtifactTypeRegistry( new TypeRegistry( session ) );
        return optimized;
    }

    private List<Dependency> mergeDeps( final List<Dependency> dominant, final List<Dependency> recessive )
    {
        List<Dependency> result;
        if ( dominant == null || dominant.isEmpty() )
        {
            result = recessive;
        }
        else if ( recessive == null || recessive.isEmpty() )
        {
            result = dominant;
        }
        else
        {
            result = new ArrayList<Dependency>( dominant.size() + recessive.size() );
            final Collection<String> ids = new HashSet<String>();
            for ( final Dependency dependency : dominant )
            {
                ids.add( getId( dependency.getArtifact() ) );
                result.add( dependency );
            }
            for ( final Dependency dependency : recessive )
            {
                if ( !ids.contains( getId( dependency.getArtifact() ) ) )
                {
                    result.add( dependency );
                }
            }
        }
        return result;
    }

    private String getId( final Artifact a )
    {
        return a.getGroupId() + ':' + a.getArtifactId() + ':' + a.getClassifier() + ':' + a.getExtension();
    }

    private boolean process( final RepositorySystemSession session, final ProjectToolsSession toolsSession,
                             final RequestTrace trace, final CollectResult result,
                             final LinkedList<SlimDependencyEdge> edges, final List<Dependency> dependencies,
                             final List<RemoteRepository> repositories, final DependencySelector depSelector,
                             final DependencyManager depManager, final DependencyTraverser depTraverser,
                             final DepGraphCache pool, final SlimDepGraph graph )
        throws DependencyCollectionException
    {
        boolean cycle = false;

        final DependencyFilter filter = toolsSession == null ? null : toolsSession.getDependencyFilter();

        nextDependency: for ( Dependency dependency : dependencies )
        {
            boolean disableVersionManagement = false;

            List<Artifact> relocations = Collections.emptyList();

            thisDependency: while ( true )
            {
                if ( !depSelector.selectDependency( dependency ) )
                {
                    logger.warn( "SELECT - Excluding from dependency graph: " + dependency.getArtifact() );
                    continue nextDependency;
                }

                final DependencyManagement depMngt = depManager.manageDependency( dependency );
                String premanagedVersion = null;
                String premanagedScope = null;

                if ( depMngt != null )
                {
                    if ( depMngt.getVersion() != null && !disableVersionManagement )
                    {
                        final Artifact artifact = dependency.getArtifact();
                        premanagedVersion = artifact.getVersion();
                        dependency = dependency.setArtifact( artifact.setVersion( depMngt.getVersion() ) );
                    }
                    if ( depMngt.getProperties() != null )
                    {
                        final Artifact artifact = dependency.getArtifact();
                        dependency = dependency.setArtifact( artifact.setProperties( depMngt.getProperties() ) );
                    }
                    if ( depMngt.getScope() != null )
                    {
                        premanagedScope = dependency.getScope();
                        dependency = dependency.setScope( depMngt.getScope() );
                    }
                    if ( depMngt.getExclusions() != null )
                    {
                        dependency = dependency.setExclusions( depMngt.getExclusions() );
                    }
                }
                disableVersionManagement = false;

                final boolean noDescriptor = isLackingDescriptor( dependency.getArtifact() );

                final boolean traverse = !noDescriptor && depTraverser.traverseDependency( dependency );

                VersionRangeResult rangeResult;
                try
                {
                    final VersionRangeRequest rangeRequest = new VersionRangeRequest();
                    rangeRequest.setArtifact( dependency.getArtifact() );
                    rangeRequest.setRepositories( repositories );
                    rangeRequest.setRequestContext( result.getRequest().getRequestContext() );
                    rangeRequest.setTrace( trace );

                    final Object key = pool.toKey( rangeRequest );
                    rangeResult = pool.getConstraint( key, rangeRequest );
                    if ( rangeResult == null )
                    {
                        rangeResult = versionRangeResolver.resolveVersionRange( session, rangeRequest );
                        pool.putConstraint( key, rangeResult );
                    }

                    if ( rangeResult.getVersions().isEmpty() )
                    {
                        throw new VersionRangeResolutionException( rangeResult, "No versions available for "
                            + dependency.getArtifact() + " within specified range" );
                    }
                }
                catch ( final VersionRangeResolutionException e )
                {
                    result.addException( e );
                    continue nextDependency;
                }

                final List<Version> versions = new ArrayList<Version>( rangeResult.getVersions() );
                Collections.reverse( versions );

                for ( final Version version : versions )
                {
                    final Artifact originalArtifact = dependency.getArtifact().setVersion( version.toString() );
                    Dependency d = dependency.setArtifact( originalArtifact );

                    ArtifactDescriptorResult descriptorResult;
                    try
                    {
                        final ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
                        descriptorRequest.setArtifact( d.getArtifact() );
                        descriptorRequest.setRepositories( repositories );
                        descriptorRequest.setRequestContext( result.getRequest().getRequestContext() );
                        descriptorRequest.setTrace( trace );

                        if ( noDescriptor )
                        {
                            descriptorResult = new ArtifactDescriptorResult( descriptorRequest );
                        }
                        else
                        {
                            final Object key = pool.toKey( descriptorRequest );
                            descriptorResult = pool.getDescriptor( key, descriptorRequest );
                            if ( descriptorResult == null )
                            {
                                descriptorResult = descriptorReader.readArtifactDescriptor( session, descriptorRequest );
                                pool.putDescriptor( key, descriptorResult );
                            }
                        }
                    }
                    catch ( final ArtifactDescriptorException e )
                    {
                        result.addException( e );
                        continue;
                    }

                    d = d.setArtifact( descriptorResult.getArtifact() );

                    if ( findDuplicate( edges, d.getArtifact() ) != null )
                    {
                        cycle = true;
                        continue nextDependency;
                    }

                    if ( !descriptorResult.getRelocations().isEmpty() )
                    {
                        relocations = descriptorResult.getRelocations();

                        disableVersionManagement =
                            originalArtifact.getGroupId().equals( d.getArtifact().getGroupId() )
                                && originalArtifact.getArtifactId().equals( d.getArtifact().getArtifactId() );

                        dependency = d;
                        continue thisDependency;
                    }

                    d = pool.intern( d.setArtifact( graph.intern( d.getArtifact() ) ) );

                    DependencySelector childSelector = null;
                    DependencyManager childManager = null;
                    DependencyTraverser childTraverser = null;
                    List<RemoteRepository> childRepos = null;
                    final String key = toId( d.getArtifact() );

                    boolean recurse = traverse && !descriptorResult.getDependencies().isEmpty();
                    if ( recurse )
                    {
                        final DependencyCollectionContext context =
                            new CollectionContext( session, d, descriptorResult.getManagedDependencies() );

                        childSelector = depSelector.deriveChildSelector( context );
                        childManager = depManager.deriveChildManager( context );
                        childTraverser = depTraverser.deriveChildTraverser( context );

                        childRepos =
                            remoteRepositoryManager.aggregateRepositories( session, repositories,
                                                                           descriptorResult.getRepositories(), true );
                    }

                    List<RemoteRepository> repos;
                    final ArtifactRepository repo = rangeResult.getRepository( version );
                    if ( repo instanceof RemoteRepository )
                    {
                        repos = Collections.singletonList( (RemoteRepository) repo );
                    }
                    else if ( repo == null )
                    {
                        repos = repositories;
                    }
                    else
                    {
                        repos = Collections.emptyList();
                    }

                    SlimDependencyNode child = graph.getNode( key );
                    if ( child == null )
                    {
                        child = new SlimDependencyNode( key, graph );
                        child.setAliases( descriptorResult.getAliases() );
                        child.setRepositories( repos );
                    }
                    else
                    {
                        recurse = false;

                        if ( repos.size() < child.getRepositories().size() )
                        {
                            child.setRepositories( repos );
                        }
                    }

                    final SlimDependencyNode node = edges.getFirst().getTo();

                    final SlimDependencyEdge edge = new SlimDependencyEdge( node, child, graph );
                    edge.setDependency( d );
                    edge.setScope( d.getScope() );
                    edge.setPremanagedScope( premanagedScope );
                    edge.setPremanagedVersion( premanagedVersion );
                    edge.setRelocations( relocations );
                    edge.setVersionConstraint( rangeResult.getVersionConstraint() );
                    edge.setVersion( version );
                    edge.setRequestContext( result.getRequest().getRequestContext() );

                    final List<DependencyNode> parents = new ArrayList<DependencyNode>();
                    parents.add( edges.getFirst() );

                    if ( filter != null && !filter.accept( edge, parents ) )
                    {
                        logger.warn( "FILTER - Excluding from dependency graph: " + edge.getDependency().getArtifact() );
                        graph.removeEdge( edge );
                        continue nextDependency;
                    }

                    if ( recurse )
                    {
                        edges.addFirst( edge );

                        if ( process( session, toolsSession, trace, result, edges, descriptorResult.getDependencies(), childRepos,
                                      childSelector, childManager, childTraverser, pool, graph ) )
                        {
                            cycle = true;
                            continue nextDependency;
                        }

                        edges.removeFirst();
                    }
                }

                break;
            }
        }

        return cycle;
    }

    private SlimDependencyEdge findDuplicate( final List<SlimDependencyEdge> edges, final Artifact artifact )
    {
        for ( final SlimDependencyEdge edge : edges )
        {
            final Dependency dependency = edge.getDependency();
            if ( dependency == null )
            {
                break;
            }

            final Artifact a = dependency.getArtifact();
            if ( !a.getArtifactId().equals( artifact.getArtifactId() ) )
            {
                continue;
            }
            if ( !a.getGroupId().equals( artifact.getGroupId() ) )
            {
                continue;
            }
            if ( !a.getBaseVersion().equals( artifact.getBaseVersion() ) )
            {
                continue;
            }
            if ( !a.getExtension().equals( artifact.getExtension() ) )
            {
                continue;
            }
            if ( !a.getClassifier().equals( artifact.getClassifier() ) )
            {
                continue;
            }

            return edge;
        }

        return null;
    }

    private boolean isLackingDescriptor( final Artifact artifact )
    {
        return artifact.getProperty( ArtifactProperties.LOCAL_PATH, null ) != null;
    }

    private static final class CollectionContext
        implements DependencyCollectionContext
    {

        private final RepositorySystemSession session;

        private final Dependency dependency;

        private final List<Dependency> managedDependencies;

        CollectionContext( final RepositorySystemSession session, final Dependency dependency,
                           final List<Dependency> managedDependencies )
        {
            this.session = session;
            this.dependency = dependency;
            this.managedDependencies = managedDependencies;
        }

        @Override
        public RepositorySystemSession getSession()
        {
            return session;
        }

        @Override
        public Dependency getDependency()
        {
            return dependency;
        }

        @Override
        public List<Dependency> getManagedDependencies()
        {
            return managedDependencies;
        }

    }

    private static final class GraphTransformationContext
        implements DependencyGraphTransformationContext
    {

        private final RepositorySystemSession session;

        private final Map<Object, Object> ctx = new HashMap<Object, Object>();

        GraphTransformationContext( final RepositorySystemSession session )
        {
            this.session = session;
        }

        @Override
        public RepositorySystemSession getSession()
        {
            return session;
        }

        @Override
        public Object get( final Object key )
        {
            return ctx.get( key );
        }

        @Override
        public Object put( final Object key, final Object value )
        {
            return ctx.put( key, value );
        }

    }

    private static final class TypeRegistry
        implements ArtifactTypeRegistry
    {
        private final ArtifactTypeRegistry delegate;

        private final Map<String, ArtifactType> types = new HashMap<String, ArtifactType>();

        TypeRegistry( final RepositorySystemSession session )
        {
            delegate = session.getArtifactTypeRegistry();
        }

        @Override
        public ArtifactType get( final String typeId )
        {
            ArtifactType type = types.get( typeId );

            if ( type == null )
            {
                type = delegate.get( typeId );
                types.put( typeId, type );
            }

            return type;
        }
    }

}
