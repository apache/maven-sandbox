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

package org.apache.maven.mae.graph;

public class DirectionalEdge<V>
{

    private final V from;

    private final V to;

    public DirectionalEdge( final V from, final V to )
    {
        this.from = from;
        this.to = to;
    }

    public V getFrom()
    {
        return from;
    }

    public V getTo()
    {
        return to;
    }

    public interface DirectionalEdgeFactory<V, E extends DirectionalEdge<V>>
    {
        E createEdge( V from, V to );
    }

    public static final class SimpleDirectionalEdgeFactory<V>
        implements DirectionalEdgeFactory<V, DirectionalEdge<V>>
    {
        @Override
        public DirectionalEdge<V> createEdge( final V from, final V to )
        {
            return new DirectionalEdge<V>( from, to );
        }

    }

    @Override
    public int hashCode()
    {
        final int prime = 37;
        int result = 1;
        result = prime * result + ( ( from == null ) ? 0 : from.hashCode() );
        result = prime * result + ( ( to == null ) ? 0 : to.hashCode() );
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
        final DirectionalEdge<?> other = (DirectionalEdge<?>) obj;
        if ( from == null )
        {
            if ( other.from != null )
            {
                return false;
            }
        }
        else if ( !from.equals( other.from ) )
        {
            return false;
        }
        if ( to == null )
        {
            if ( other.to != null )
            {
                return false;
            }
        }
        else if ( !to.equals( other.to ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "DirectionalEdge [" ).append( from ).append( " --> " ).append( to ).append( "]" );
        return builder.toString();
    }

}
