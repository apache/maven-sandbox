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

package org.apache.maven.mae.depgraph.impl.collect;

import java.util.Collection;
import java.util.List;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;

class SlimDependencyNode
{

    public static final String UNKNOWN_ROOT_ID = "anonymous-root".intern();

    private final SlimDepGraph graph;

    private final String key;

    SlimDependencyNode( final String key, final SlimDepGraph graph )
    {
        this.key = key;
        this.graph = graph;
    }

    Collection<Artifact> getAliases()
    {
        return graph.getAliases( key );
    }

    void setAliases( final Collection<Artifact> aliases )
    {
        graph.setAliases( key, aliases );
    }

    void addAlias( final Artifact artifact )
    {
        graph.addAlias( key, artifact );
    }

    List<RemoteRepository> getRepositories()
    {
        return graph.getRepositories( key );
    }

    void setRepositories( final List<RemoteRepository> repositories )
    {
        graph.setRepositories( key, repositories );
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
        final SlimDependencyNode other = (SlimDependencyNode) obj;
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

    @Override
    public String toString()
    {
        return key;
    }
}
