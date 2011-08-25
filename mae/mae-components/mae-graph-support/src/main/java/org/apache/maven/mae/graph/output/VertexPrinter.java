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

public interface VertexPrinter<V>
{

    public static final class ToStringPrinter<T>
        extends AbstractVertexPrinter<T>
    {
        @Override
        public String vertexStarted( final T vertex )
        {
            return vertex == null ? "-null-" : vertex.toString();
        }
    };

    public abstract static class AbstractVertexPrinter<T>
        implements VertexPrinter<T>
    {
        @Override
        public String vertexFinished( final T vertex )
        {
            return null;
        }

        @Override
        public String vertexSkipped( final T vertex )
        {
            return null;
        }
    }

    /**
     * Write the header to signal the start of a new vertex traversal. <b>NEVER</b> <code>null</code>.
     */
    String vertexStarted( V vertex );

    String vertexFinished( V vertex );

    String vertexSkipped( V vertex );

}
