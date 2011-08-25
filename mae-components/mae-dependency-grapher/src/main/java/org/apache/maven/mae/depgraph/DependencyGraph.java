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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.mae.graph.DirectedGraph;
import org.apache.maven.mae.graph.DirectionalEdge;
import org.apache.maven.mae.graph.SimpleDirectedGraph;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.resolution.ArtifactResult;

public class DependencyGraph
    implements Iterable<DepGraphNode>
{

    private final Set<DepGraphRootNode> roots = new LinkedHashSet<DepGraphRootNode>();

    private final DepGraph graph = new DepGraph();

    public DepGraphRootNode addRoot( final DependencyNode root )
    {
        return addRoot( root, null );
    }

    public DepGraphRootNode addRoot( final DependencyNode root, final MavenProject project )
    {
        final DepGraphRootNode newRoot = new DepGraphRootNode( root, project );
        final DepGraphRootNode rootNode = (DepGraphRootNode) findOrAdd( newRoot );

        if ( rootNode != newRoot )
        {
            rootNode.merge( root );
        }

        roots.add( rootNode );

        return rootNode;
    }

    public DepGraphNode addNodeResult( final Artifact artifact, final ArtifactResult result )
    {
        final DepGraphNode node = findOrAdd( new DepGraphNode( artifact, false ) );
        node.merge( result );

        return node;
    }

    private DepGraphNode findOrAdd( final DepGraphNode node )
    {
        DepGraphNode result = find( node );

        if ( result == null )
        {
            graph.addVertex( node );
            result = node;
        }

        return result;
    }

    private DepGraphNode find( final DepGraphNode node )
    {
        final List<DepGraphNode> nodes = new ArrayList<DepGraphNode>( graph.vertices() );
        final int idx = nodes.indexOf( node );
        if ( idx > -1 )
        {
            return nodes.get( idx );
        }

        return null;
    }

    public DepGraphNode[] addDependency( final DepGraphNode parentNode, final DependencyNode child )
    {
        final DepGraphNode newChildNode = new DepGraphNode( child );
        final DepGraphNode childNode = findOrAdd( newChildNode );

        // if we're reusing an existing node, merge the new info from the child.
        if ( childNode != newChildNode )
        {
            childNode.merge( child );
        }

        if ( parentNode != null )
        {
            graph.connect( parentNode, childNode );
        }

        return new DepGraphNode[] { parentNode, childNode };
    }

    /**
     * Add a dependency edge between the nodes representing the two given {@link DependencyNode} instances.
     * 
     * @param parent The parent, which has the dependency on the child.
     * @param child The child, which is depended upon by the parent.
     * @return An array of graph nodes, with the parent node in index 0, and the child node in index 1. <br/>
     *         <b>NOTE:</b> If the parent parameter is null, the node at index 0 will be null as well.
     */
    public DepGraphNode[] addDependency( final DependencyNode parent, final DependencyNode child )
    {
        DepGraphNode parentNode = null;
        if ( parent != null )
        {
            parentNode = findOrAdd( new DepGraphNode( parent ) );
        }

        return addDependency( parentNode, child );
    }

    public DepGraphNode[] addDependency( final Artifact parent, final Artifact child,
                                         final boolean parentPreResolved,
                                         final boolean childPreResolved )
    {
        final DepGraphNode from = findOrAdd( new DepGraphNode( parent, parentPreResolved ) );

        final DepGraphNode newTo = new DepGraphNode( child, childPreResolved );
        final DepGraphNode to = findOrAdd( newTo );

        // if we're reusing an existing node, merge the new info from the child.
        if ( to != newTo )
        {
            to.merge( child );
        }

        graph.connect( from, to );

        return new DepGraphNode[] { from, to };
    }

    public Set<DepGraphRootNode> getRoots()
    {
        return new LinkedHashSet<DepGraphRootNode>( roots );
    }

    @Override
    public Iterator<DepGraphNode> iterator()
    {
        return new LinkedHashSet<DepGraphNode>( graph.vertices() ).iterator();
    }

    public int size()
    {
        return graph.vertices().size();
    }

    public DirectedGraph<DepGraphNode, DirectionalEdge<DepGraphNode>> getGraph()
    {
        return graph;
    }

    public boolean contains( final DependencyNode dep )
    {
        return find( new DepGraphNode( dep ) ) != null;
    }

    private static final class DepGraph
        extends SimpleDirectedGraph<DepGraphNode>
    {

        public void addVertex( final DepGraphNode node )
        {
            getNakedGraph().addVertex( node );
        }

        public Collection<? extends DepGraphNode> vertices()
        {
            return getNakedGraph().getVertices();
        }

    }

    public static DependencyGraph constructFromRoot( final DependencyNode rootNode,
                                                     final MavenProject rootProject )
    {
        final DependencyGraph graph = new DependencyGraph();
        graph.addRoot( rootNode, rootProject );
        constructChildren( graph, rootNode );

        return graph;
    }

    private static void constructChildren( final DependencyGraph graph, final DependencyNode node )
    {
        for ( final DependencyNode child : node.getChildren() )
        {
            graph.addDependency( node, child );
            constructChildren( graph, child );
        }
    }

}
