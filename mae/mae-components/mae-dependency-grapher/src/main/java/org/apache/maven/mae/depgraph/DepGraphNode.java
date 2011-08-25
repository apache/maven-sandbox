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

package org.apache.maven.mae.depgraph;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.mae.depgraph.impl.ArtifactOnlyDependencyNode;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactResult;

public class DepGraphNode
    implements Iterable<Throwable>
{

    private Artifact latestArtifact;

    private ArtifactResult latestResult;

    private final LinkedHashSet<RemoteRepository> remoteRepositories =
        new LinkedHashSet<RemoteRepository>();

    private String key;

    private final boolean preResolved;

    private final Set<Throwable> errors = new HashSet<Throwable>();

    private DependencyNode latestDependencyNode;

    public DepGraphNode( final DependencyNode node )
    {
        this( node, null, false );
    }

    protected DepGraphNode( final DependencyNode node, final String key, final boolean preResolved )
    {
        merge( node );

        if ( key == null )
        {
            if ( latestArtifact != null )
            {
                this.key = key( latestArtifact );
            }
            else
            {
                throw new NullPointerException(
                                                "Cannot calculate node key. DependencyNode parameter does not contain a valid artifact!" );
            }
        }
        else
        {
            this.key = key;
        }

        this.preResolved = preResolved;
    }

    public DepGraphNode( final Artifact artifact, final boolean preResolved )
    {
        key = key( artifact );
        latestArtifact = artifact;
        this.preResolved = preResolved;
    }

    static String key( final Artifact a )
    {
        return ArtifactUtils.key( a.getGroupId(), a.getArtifactId(), a.getBaseVersion() );
    }

    public boolean isPreResolved()
    {
        return preResolved;
    }

    public synchronized void merge( final DependencyNode node )
    {
        latestDependencyNode = node;

        if ( node.getRepositories() != null )
        {
            remoteRepositories.addAll( node.getRepositories() );
        }

        if ( latestArtifact == null && node.getDependency() != null
            && node.getDependency().getArtifact() != null )
        {
            latestArtifact = node.getDependency().getArtifact();
        }
    }

    public synchronized void merge( final ArtifactResult result )
    {
        if ( result.getArtifact() != null && result.getArtifact().getFile() != null )
        {
            result.getExceptions().clear();
        }

        latestResult = result;
    }

    public DependencyNode getLatestDependencyNode()
    {
        return latestDependencyNode;
    }

    public ArtifactResult getLatestResult()
    {
        return latestResult;
    }

    public Artifact getLatestArtifact()
    {
        return latestArtifact;
    }

    public LinkedHashSet<RemoteRepository> getRemoteRepositories()
    {
        return remoteRepositories;
    }

    public String getKey()
    {
        return key;
    }

    public synchronized boolean hasErrors()
    {
        return !errors.isEmpty();
    }

    private String renderErrors()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append( "Failed to resolve: " ).append( getKey() );
        sb.append( "\n\n" ).append( errors.size() ).append( " Resolution errors:\n" );

        for ( final Throwable error : errors )
        {
            final StringWriter sWriter = new StringWriter();
            error.printStackTrace( new PrintWriter( sWriter ) );

            sb.append( "\n\n" ).append( sWriter.toString() );
        }

        return sb.toString();
    }

    @Override
    public Iterator<Throwable> iterator()
    {
        return getErrors().iterator();
    }

    public List<Throwable> getErrors()
    {
        return new ArrayList<Throwable>( errors );
    }

    public void logErrors( final PrintWriter writer )
    {
        writer.println( renderErrors() );
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "DependencyTrackingState (\n    latestArtifact=" );
        builder.append( latestArtifact );
        builder.append( "\n    latestResult=" );
        builder.append( latestResult );
        builder.append( "\n    projectId=" );
        builder.append( key );
        builder.append( "\n)" );
        return builder.toString();
    }

    public void removeLatestResult()
    {
        latestResult = null;
    }

    public void merge( final Artifact child )
    {
        merge( new ArtifactOnlyDependencyNode( child ) );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( key == null ) ? 0 : key.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final DepGraphNode other = (DepGraphNode) obj;
        if ( key == null )
        {
            if ( other.key != null )
            {
                return false;
            }
        }
        else if ( !key.equals( other.key ) )
        {
            return false;
        }
        return true;
    }
}
