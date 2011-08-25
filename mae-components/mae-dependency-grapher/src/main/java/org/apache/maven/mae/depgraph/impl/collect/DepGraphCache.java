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

import org.sonatype.aether.RepositoryCache;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.DependencyManager;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.collection.DependencyTraverser;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.ArtifactRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.version.Version;
import org.sonatype.aether.version.VersionConstraint;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Based on DataPool in aether-impl, this cache provides the ability to clear the cache in the
 * {@link RepositorySystemSession}, along with tracking for {@link RemoteRepository} instances, and NOT including
 * tracking for nodes.
 * 
 * @author Benjamin Bentmann
 * @author John Casey
 */
public final class DepGraphCache
{

    private static final String ARTIFACT_POOL = DepGraphCache.class.getName() + "$Artifact";

    private static final String DEPENDENCY_POOL = DepGraphCache.class.getName() + "$Dependency";

    private static final String REPOSITORY_POOL = DepGraphCache.class.getName() + "$Repository";

    private static final String DESCRIPTORS = DepGraphCache.class.getName() + "$Descriptors";

    private ObjectPool<String, Artifact> artifacts;

    private ObjectPool<Dependency, Dependency> dependencies;

    private ObjectPool<RemoteRepository, RemoteRepository> repositories;

    private Map<Object, Descriptor> descriptors;

    private final Map<Object, Constraint> constraints = new WeakHashMap<Object, Constraint>();

    @SuppressWarnings( "unchecked" )
    DepGraphCache( final RepositorySystemSession session )
    {
        final RepositoryCache cache = session.getCache();

        if ( cache != null )
        {
            artifacts = (ObjectPool<String, Artifact>) cache.get( session, ARTIFACT_POOL );
            dependencies = (ObjectPool<Dependency, Dependency>) cache.get( session, DEPENDENCY_POOL );
            repositories = (ObjectPool<RemoteRepository, RemoteRepository>) cache.get( session, REPOSITORY_POOL );
            descriptors = (Map<Object, Descriptor>) cache.get( session, DESCRIPTORS );
        }

        if ( artifacts == null )
        {
            artifacts = new ObjectPool<String, Artifact>();
            if ( cache != null )
            {
                cache.put( session, ARTIFACT_POOL, artifacts );
            }
        }

        if ( dependencies == null )
        {
            dependencies = new ObjectPool<Dependency, Dependency>();
            if ( cache != null )
            {
                cache.put( session, DEPENDENCY_POOL, dependencies );
            }
        }

        if ( repositories == null )
        {
            repositories = new ObjectPool<RemoteRepository, RemoteRepository>();
            if ( cache != null )
            {
                cache.put( session, DEPENDENCY_POOL, repositories );
            }
        }

        if ( descriptors == null )
        {
            descriptors = Collections.synchronizedMap( new WeakHashMap<Object, Descriptor>( 256 ) );
            if ( cache != null )
            {
                cache.put( session, DESCRIPTORS, descriptors );
            }
        }
    }

    public static void clear( final RepositorySystemSession session )
    {
        synchronized ( session )
        {
            final RepositoryCache cache = session.getCache();

            if ( cache != null )
            {
                cache.put( session, ARTIFACT_POOL, null );
                cache.put( session, DEPENDENCY_POOL, null );
                cache.put( session, REPOSITORY_POOL, null );
                cache.put( session, DESCRIPTORS, null );
            }
        }
    }

    synchronized void setArtifact( final Artifact artifact )
    {
        final String id = toId( artifact );
        artifacts.set( id, artifact );
    }

    Artifact getArtifact( final String key )
    {
        return artifacts.get( key );
    }

    Artifact intern( final Artifact artifact )
    {
        return artifacts.intern( toId( artifact ), artifact );
    }

    Dependency intern( final Dependency dependency )
    {
        return dependencies.intern( dependency, dependency );
    }

    RemoteRepository intern( final RemoteRepository repo )
    {
        return repositories.intern( repo, repo );
    }

    Object toKey( final ArtifactDescriptorRequest request )
    {
        return request.getArtifact();
    }

    ArtifactDescriptorResult getDescriptor( final Object key, final ArtifactDescriptorRequest request )
    {
        final Descriptor descriptor = descriptors.get( key );
        if ( descriptor != null )
        {
            return descriptor.toResult( request );
        }
        return null;
    }

    void putDescriptor( final Object key, final ArtifactDescriptorResult result )
    {
        descriptors.put( key, new Descriptor( result ) );
    }

    Object toKey( final VersionRangeRequest request )
    {
        return new ConstraintKey( request );
    }

    VersionRangeResult getConstraint( final Object key, final VersionRangeRequest request )
    {
        final Constraint constraint = constraints.get( key );
        if ( constraint != null )
        {
            return constraint.toResult( request );
        }
        return null;
    }

    void putConstraint( final Object key, final VersionRangeResult result )
    {
        constraints.put( key, new Constraint( result ) );
    }

    Object toKey( final Artifact artifact, final List<RemoteRepository> repositories )
    {
        return new NodeKey( artifact, repositories );
    }

    Object toKey( final Artifact artifact, final List<RemoteRepository> repositories,
                  final DependencySelector selector, final DependencyManager manager,
                  final DependencyTraverser traverser )
    {
        return new GraphKey( artifact, repositories, selector, manager, traverser );
    }

    static class Descriptor
    {

        final Artifact artifact;

        final Map<String, Object> properties;

        final List<Artifact> relocations;

        final List<RemoteRepository> repositories;

        final List<Dependency> dependencies;

        final List<Dependency> managedDependencies;

        public Descriptor( final ArtifactDescriptorResult result )
        {
            artifact = result.getArtifact();
            properties = result.getProperties();
            relocations = result.getRelocations();
            dependencies = result.getDependencies();
            managedDependencies = result.getManagedDependencies();
            repositories = clone( result.getRepositories() );
        }

        public ArtifactDescriptorResult toResult( final ArtifactDescriptorRequest request )
        {
            final ArtifactDescriptorResult result = new ArtifactDescriptorResult( request );
            result.setArtifact( artifact );
            result.setProperties( properties );
            result.setRelocations( relocations );
            result.setDependencies( dependencies );
            result.setManagedDependencies( dependencies );
            result.setRepositories( clone( repositories ) );
            return result;
        }

        private static List<RemoteRepository> clone( final List<RemoteRepository> repositories )
        {
            final List<RemoteRepository> clones = new ArrayList<RemoteRepository>( repositories.size() );
            for ( final RemoteRepository repository : repositories )
            {
                final RemoteRepository clone = new RemoteRepository( repository );
                clone.setMirroredRepositories( new ArrayList<RemoteRepository>( repository.getMirroredRepositories() ) );
                clones.add( clone );
            }
            return clones;
        }

    }

    static class Constraint
    {

        final Map<Version, ArtifactRepository> repositories;

        final VersionConstraint versionConstraint;

        public Constraint( final VersionRangeResult result )
        {
            versionConstraint = result.getVersionConstraint();
            repositories = new LinkedHashMap<Version, ArtifactRepository>();
            for ( final Version version : result.getVersions() )
            {
                repositories.put( version, result.getRepository( version ) );
            }
        }

        public VersionRangeResult toResult( final VersionRangeRequest request )
        {
            final VersionRangeResult result = new VersionRangeResult( request );
            for ( final Map.Entry<Version, ArtifactRepository> entry : repositories.entrySet() )
            {
                result.addVersion( entry.getKey() );
                result.setRepository( entry.getKey(), entry.getValue() );
            }
            result.setVersionConstraint( versionConstraint );
            return result;
        }

    }

    static class ConstraintKey
    {

        private final Artifact artifact;

        private final List<RemoteRepository> repositories;

        private final int hashCode;

        public ConstraintKey( final VersionRangeRequest request )
        {
            artifact = request.getArtifact();
            repositories = request.getRepositories();
            hashCode = artifact.hashCode();
        }

        @Override
        public boolean equals( final Object obj )
        {
            if ( obj == this )
            {
                return true;
            }
            else if ( !( obj instanceof ConstraintKey ) )
            {
                return false;
            }
            final ConstraintKey that = (ConstraintKey) obj;
            return artifact.equals( that.artifact ) && equals( repositories, that.repositories );
        }

        private static boolean equals( final Collection<RemoteRepository> repos1,
                                       final Collection<RemoteRepository> repos2 )
        {
            if ( repos1.size() != repos2.size() )
            {
                return false;
            }
            for ( Iterator<RemoteRepository> it1 = repos1.iterator(), it2 = repos2.iterator(); it1.hasNext(); )
            {
                final RemoteRepository repo1 = it1.next();
                final RemoteRepository repo2 = it2.next();
                if ( repo1.isRepositoryManager() != repo2.isRepositoryManager() )
                {
                    return false;
                }
                if ( repo1.isRepositoryManager() )
                {
                    if ( !equals( repo1.getMirroredRepositories(), repo2.getMirroredRepositories() ) )
                    {
                        return false;
                    }
                }
                else if ( !repo1.getUrl().equals( repo2.getUrl() ) )
                {
                    return false;
                }
                else if ( repo1.getPolicy( true ).isEnabled() != repo2.getPolicy( true ).isEnabled() )
                {
                    return false;
                }
                else if ( repo1.getPolicy( false ).isEnabled() != repo2.getPolicy( false ).isEnabled() )
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }

    }

    static class NodeKey
    {

        private final Artifact artifact;

        private final List<RemoteRepository> repositories;

        private final int hashCode;

        public NodeKey( final Artifact artifact, final List<RemoteRepository> repositories )
        {
            this.artifact = artifact;
            this.repositories = repositories;

            int hash = 17;
            hash = hash * 31 + artifact.hashCode();
            hash = hash * 31 + repositories.hashCode();
            hashCode = hash;
        }

        @Override
        public boolean equals( final Object obj )
        {
            if ( obj == this )
            {
                return true;
            }
            else if ( !( obj instanceof NodeKey ) )
            {
                return false;
            }
            final NodeKey that = (NodeKey) obj;
            return artifact.equals( that.artifact ) && repositories.equals( that.repositories );
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }

    }

    static class GraphKey
    {

        private final Artifact artifact;

        private final List<RemoteRepository> repositories;

        private final DependencySelector selector;

        private final DependencyManager manager;

        private final DependencyTraverser traverser;

        private final int hashCode;

        public GraphKey( final Artifact artifact, final List<RemoteRepository> repositories,
                         final DependencySelector selector, final DependencyManager manager,
                         final DependencyTraverser traverser )
        {
            this.artifact = artifact;
            this.repositories = repositories;
            this.selector = selector;
            this.manager = manager;
            this.traverser = traverser;

            int hash = 17;
            hash = hash * 31 + artifact.hashCode();
            hash = hash * 31 + repositories.hashCode();
            hash = hash * 31 + selector.hashCode();
            hash = hash * 31 + manager.hashCode();
            hash = hash * 31 + traverser.hashCode();
            hashCode = hash;
        }

        @Override
        public boolean equals( final Object obj )
        {
            if ( obj == this )
            {
                return true;
            }
            else if ( !( obj instanceof GraphKey ) )
            {
                return false;
            }
            final GraphKey that = (GraphKey) obj;
            return artifact.equals( that.artifact ) && repositories.equals( that.repositories )
                && selector.equals( that.selector ) && manager.equals( that.manager )
                && traverser.equals( that.traverser );
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }

    }

    static final class ObjectPool<K, V>
    {

        private final Map<K, WeakReference<V>> objects = new WeakHashMap<K, WeakReference<V>>( 256 );

        synchronized V intern( final K id, final V object )
        {
            final WeakReference<V> pooledRef = objects.get( id );
            if ( pooledRef != null )
            {
                final V pooled = pooledRef.get();
                if ( pooled != null )
                {
                    return pooled;
                }
            }

            objects.put( id, new WeakReference<V>( object ) );
            return object;
        }

        void set( final K id, final V object )
        {
            objects.put( id, new WeakReference<V>( object ) );
        }

        V get( final K id )
        {
            final WeakReference<V> ref = objects.get( id );
            return ref == null ? null : ref.get();
        }

    }

}
