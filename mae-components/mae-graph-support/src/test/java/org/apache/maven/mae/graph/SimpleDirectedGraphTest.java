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
import java.io.StringWriter;

import org.apache.maven.mae.graph.DirectionalEdge;
import org.apache.maven.mae.graph.SimpleDirectedGraph;
import org.apache.maven.mae.graph.DirectedGraph.Printer;
import org.apache.maven.mae.graph.output.EdgePrinter;
import org.apache.maven.mae.graph.traverse.GraphWalker;
import org.junit.Test;

public class SimpleDirectedGraphTest
{

    @Test
    public void printSimpleStringDiGraph()
    {
        final SimpleDirectedGraph<String> graph = new SimpleDirectedGraph<String>();
        graph.connect( "from", "to" ).connect( "to", "onward" );

        final StringWriter sw = new StringWriter();

        final Printer<String> printer =
            new Printer<String>( new EdgePrinter.ToStringPrinter<DirectionalEdge<String>>(),
                                 new PrintWriter( sw ) );

        GraphWalker.walkDepthFirst( graph, "from", printer );

        System.out.println( sw );
    }

}
