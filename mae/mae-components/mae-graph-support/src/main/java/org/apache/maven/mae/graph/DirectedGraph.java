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

import java.io.PrintWriter;

import org.apache.maven.mae.graph.DirectionalEdge.DirectionalEdgeFactory;
import org.apache.maven.mae.graph.output.EdgePrinter;
import org.apache.maven.mae.graph.output.GraphPrinter;
import org.apache.maven.mae.graph.output.VertexPrinter;
import org.apache.maven.mae.graph.traverse.GraphVisitor;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Graphs;

public class DirectedGraph<V, E extends DirectionalEdge<V>>
    implements GraphManager<V, E>
{

    private final DirectedSparseGraph<V, E> graph = new DirectedSparseGraph<V, E>();

    private final DirectionalEdgeFactory<V, E> edgeFactory;

    public DirectedGraph( final DirectionalEdgeFactory<V, E> edgeFactory )
    {
        this.edgeFactory = edgeFactory;
    }

    public DirectedGraph<V, E> connect( final V from, final V to )
    {
        final E edge = edgeFactory.createEdge( from, to );

        if ( graph.containsEdge( edge ) )
        {
            return this;
        }

        if ( !graph.containsVertex( from ) )
        {
            graph.addVertex( from );
        }

        if ( !graph.containsVertex( to ) )
        {
            graph.addVertex( to );
        }

        graph.addEdge( edge, from, to );

        return this;
    }

    public abstract static class Visitor<T>
        implements GraphVisitor<T, DirectionalEdge<T>>
    {

    }

    public static final class Printer<T>
        extends GraphPrinter<T, DirectionalEdge<T>>
    {

        public Printer( final PrintWriter printWriter )
        {
            super( printWriter );
        }

        public Printer( final boolean printEdges, final PrintWriter printWriter )
        {
            super( printEdges, printWriter );
        }

        public Printer( final VertexPrinter<T> vPrinter, final PrintWriter printWriter )
        {
            super( vPrinter, printWriter );
        }

        public Printer( final EdgePrinter<DirectionalEdge<T>> ePrinter,
                        final PrintWriter printWriter )
        {
            super( ePrinter, printWriter );
        }

        public Printer( final String indent, final VertexPrinter<T> vPrinter,
                        final EdgePrinter<DirectionalEdge<T>> ePrinter,
                        final PrintWriter printWriter )
        {
            super( indent, vPrinter, ePrinter, printWriter );
        }

    }

    @Override
    public Graph<V, E> getManagedGraph()
    {
        return Graphs.unmodifiableDirectedGraph( graph );
    }

    protected Graph<V, E> getNakedGraph()
    {
        return graph;
    }

}
