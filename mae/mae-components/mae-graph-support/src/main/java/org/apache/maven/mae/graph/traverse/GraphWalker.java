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

package org.apache.maven.mae.graph.traverse;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.mae.graph.GraphManager;

import edu.uci.ics.jung.graph.Graph;

public final class GraphWalker
{

    public static <V, E> List<V> walkDepthFirst( final GraphManager<V, E> graphManager,
                                                 final V start, final GraphVisitor<V, E> visitor )
    {
        return walkDepthFirst( graphManager.getManagedGraph(), start, visitor );
    }

    public static <V, E> List<V> walkDepthFirst( final Graph<V, E> graph, final V start,
                                                 final GraphVisitor<V, E> visitor )
    {
        final LinkedHashSet<V> encounters = new LinkedHashSet<V>();
        encounters.add( start );

        walkDepthFirst( graph, start, visitor, encounters );

        return new ArrayList<V>( encounters );
    }

    private static <V, E> void walkDepthFirst( final Graph<V, E> graph, final V from,
                                               final GraphVisitor<V, E> visitor,
                                               final Set<V> progress )
    {
        if ( !visitor.startedVertexVisit( graph, from ) )
        {
            return;
        }

        for ( final E edge : graph.getOutEdges( from ) )
        {
            final V to = graph.getOpposite( from, edge );

            if ( to.equals( from ) )
            {
                visitor.skippedVertexVisit( graph, to );
                continue;
            }

            if ( !visitor.traversedEdge( graph, edge ) )
            {
                visitor.skippedEdgeTraversal( graph, edge );
                continue;
            }

            if ( !progress.contains( to ) )
            {
                progress.add( to );
                walkDepthFirst( graph, to, visitor, progress );
            }
            else
            {
                visitor.skippedVertexVisit( graph, to );
            }
        }

        visitor.finishedVertexVisit( graph, from );
    }
}
