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

package org.apache.maven.mae.graph.output;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.mae.graph.traverse.GraphVisitor;

import edu.uci.ics.jung.graph.Graph;

public class GraphPrinter<V, E>
    implements GraphVisitor<V, E>
{
    private PrintWriter printWriter;

    private final String indent;

    private int indentCounter = 0;

    private final List<Object> lines = new ArrayList<Object>();

    private final VertexPrinter<V> vPrinter;

    private final EdgePrinter<E> ePrinter;

    public GraphPrinter( final PrintWriter printWriter )
    {
        this( null, new VertexPrinter.ToStringPrinter<V>(), null, printWriter );
    }

    public GraphPrinter( final boolean printEdges, final PrintWriter printWriter )
    {
        this( null, new VertexPrinter.ToStringPrinter<V>(),
              printEdges ? new EdgePrinter.ToStringPrinter<E>() : null, printWriter );
    }

    public GraphPrinter( final VertexPrinter<V> vPrinter, final PrintWriter printWriter )
    {
        this( null, vPrinter, null, printWriter );
    }

    public GraphPrinter( final EdgePrinter<E> ePrinter, final PrintWriter printWriter )
    {
        this( null, null, ePrinter, printWriter );
    }

    public GraphPrinter( final String indent, final VertexPrinter<V> vPrinter,
                         final EdgePrinter<E> ePrinter, final PrintWriter printWriter )
    {
        this.vPrinter = vPrinter;
        this.ePrinter = ePrinter;
        this.printWriter = printWriter;
        this.indent = indent == null ? "  " : indent;
    }

    public GraphPrinter<V, E> reset( final PrintWriter printWriter )
    {
        this.printWriter = printWriter;
        indentCounter = 0;
        lines.clear();

        return this;
    }

    private void indentLine()
    {
        for ( int i = 0; i < indentCounter; i++ )
        {
            printWriter.append( indent );
        }
    }

    private void newLine()
    {
        printWriter.append( '\n' );

        final int sz = lines.size() + 1;
        printWriter.append( Integer.toString( sz ) ).append( ":" );
        if ( sz < 100 )
        {
            printWriter.append( ' ' );
            if ( sz < 10 )
            {
                printWriter.append( ' ' );
            }
        }
        printWriter.append( ">" ).append( Integer.toString( indentCounter ) ).append( ' ' );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.graph.traverse.GraphVisitor#traversedEdge(edu.uci.ics.jung.graph.Graph, java.lang.Object)
     */
    @Override
    public boolean traversedEdge( final Graph<V, E> graph, final E edge )
    {
        if ( ePrinter != null )
        {
            newLine();
            indentLine();

            printWriter.append( '(' ).append( ePrinter.printEdge( edge ) ).append( ')' );
            lines.add( edge );
        }

        return true;
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
        if ( vPrinter != null )
        {
            newLine();
            indentLine();

            printWriter.append( vPrinter.vertexStarted( vertex ) );
            lines.add( vertex );
        }

        indentCounter++;

        return true;
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
        if ( vPrinter != null )
        {
            final String ending = vPrinter.vertexFinished( vertex );
            if ( ending != null )
            {
                newLine();
                indentLine();

                printWriter.append( ending );
                lines.add( "end" );
            }
        }

        indentCounter--;

        return true;
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
        newLine();
        indentLine();

        final int idx = lines.indexOf( vertex );

        // builder.append( vertex );
        if ( vPrinter != null )
        {
            final String skip = vPrinter.vertexSkipped( vertex );
            if ( skip != null )
            {
                printWriter.append( skip ).append( ' ' );
            }
        }
        printWriter.append( "-DUPLICATE- (see line: " ).append( Integer.toString( idx + 1 ) ).append( ")" );

        // we need some placeholder here, without screwing up indexOf() operations for the vertex...
        lines.add( idx );
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
        // NOP.
    }
}
