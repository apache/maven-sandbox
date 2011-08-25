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

import edu.uci.ics.jung.graph.Graph;

public final class CompoundGraphVisitor<V, E>
    implements GraphVisitor<V, E>
{
    private final GraphVisitor<V, E>[] visitors;

    public CompoundGraphVisitor( final GraphVisitor<V, E>... visitors )
    {
        this.visitors = visitors;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.graph.traverse.GraphVisitor#traversedEdge(edu.uci.ics.jung.graph.Graph, java.lang.Object)
     */
    @Override
    public boolean traversedEdge( final Graph<V, E> graph, final E edge )
    {
        boolean doContinue = true;
        for ( final GraphVisitor<V, E> visitor : visitors )
        {
            doContinue = doContinue && visitor.traversedEdge( graph, edge );

            if ( !doContinue )
            {
                break;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.graph.traverse.GraphVisitor#startedVertexVisit(edu.uci.ics.jung.graph.Graph,
     *      java.lang.Object)
     */
    @Override
    public boolean startedVertexVisit( final Graph<V, E> graph, final V vertex )
    {
        boolean doContinue = true;
        for ( final GraphVisitor<V, E> visitor : visitors )
        {
            doContinue = doContinue && visitor.startedVertexVisit( graph, vertex );

            if ( !doContinue )
            {
                break;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.graph.traverse.GraphVisitor#finishedVertexVisit(edu.uci.ics.jung.graph.Graph,
     *      java.lang.Object)
     */
    @Override
    public boolean finishedVertexVisit( final Graph<V, E> graph, final V vertex )
    {
        boolean doContinue = true;
        for ( final GraphVisitor<V, E> visitor : visitors )
        {
            doContinue = doContinue && visitor.finishedVertexVisit( graph, vertex );

            if ( !doContinue )
            {
                break;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.graph.traverse.GraphVisitor#skippedVertexVisit(edu.uci.ics.jung.graph.Graph,
     *      java.lang.Object)
     */
    @Override
    public void skippedVertexVisit( final Graph<V, E> graph, final V vertex )
    {
        for ( final GraphVisitor<V, E> visitor : visitors )
        {
            visitor.skippedVertexVisit( graph, vertex );
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.graph.traverse.GraphVisitor#skippedEdgeTraversal(edu.uci.ics.jung.graph.Graph,
     *      java.lang.Object)
     */
    @Override
    public void skippedEdgeTraversal( final Graph<V, E> graph, final E edge )
    {
        for ( final GraphVisitor<V, E> visitor : visitors )
        {
            visitor.skippedEdgeTraversal( graph, edge );
        }
    }

}