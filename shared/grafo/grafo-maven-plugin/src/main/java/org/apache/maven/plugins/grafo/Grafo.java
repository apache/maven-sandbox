package org.apache.maven.plugins.grafo;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

/**
 * <p>
 * Generates an xml file representing the graph of dependencies.
 * </p>
 * <p>
 * Artifacts are nodes and dependencies between them make the unions
 * </p> 
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class Grafo
{

    private ArtifactResolver artifactResolver;

    private ArtifactFactory artifactFactory;

    private ArtifactMetadataSource artifactMetadataSource;

    private Xpp3Dom report;

    public Grafo( ArtifactResolver artifactResolver, ArtifactFactory artifactFactory,
                 ArtifactMetadataSource artifactMetadataSource )
    {
        this.artifactResolver = artifactResolver;
        this.artifactFactory = artifactFactory;
        this.artifactMetadataSource = artifactMetadataSource;
    }

    public void execute( String groupId, String artifactId, String version, ArtifactRepository localRepository,
                        List remoteRepositories )
        throws ArtifactResolutionException, ArtifactNotFoundException
    {
        // ----------------------------------------------------------------------
        // Download the archetype
        // ----------------------------------------------------------------------

        Artifact dummyArtifact = artifactFactory.createArtifact( "dummy", "dummy", "dummy", Artifact.SCOPE_RUNTIME,
                                                                 "jar" );

        Artifact artifact = artifactFactory
            .createArtifact( groupId, artifactId, version, Artifact.SCOPE_RUNTIME, "jar" );

        artifactResolver.resolve( artifact, remoteRepositories, localRepository );

        GrafoResolutionListener grafoResolutionListener = new GrafoResolutionListener();
        List listeners = Collections.singletonList( grafoResolutionListener );
        Set artifacts = Collections.singleton( artifact );

        //System.out.println( artifact.getDependencyTrail() );

        ArtifactResolutionResult result = artifactResolver
            .resolveTransitively( artifacts, dummyArtifact, new HashMap(), localRepository, remoteRepositories,
                                  artifactMetadataSource, null, listeners );

        report = new Xpp3Dom( "graphml" );
        report.setAttribute( "xmlns", "http://graphml.graphdrawing.org/xmlns" );
        Xpp3Dom graph = new Xpp3Dom( "graph" );
        graph.setAttribute( "edgedefault", "directed" );
        report.addChild( graph );

        addKeys( graph );

        processNodes( graph, grafoResolutionListener.getNodes() );
        System.out.println();
        processEdges( graph, grafoResolutionListener.getEdges() );
        System.out.println( report );

        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( "graph.xml" ),
                                                                                  "UTF-8" ) ) );
            Xpp3DomWriter.write( writer, report );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
        catch ( FileNotFoundException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            if ( writer != null )
            {
                IOUtil.close( writer );
            }
        }

    }
    
    private String getId( Artifact artifact )
    {
        return artifact.getId();
    }

    private void processNodes( Xpp3Dom dom, Map nodes )
    {
        Iterator it = nodes.values().iterator();
        while ( it.hasNext() )
        {
            Artifact artifact = (Artifact) it.next();
            System.out.println( artifact.getArtifactId() );

            Xpp3Dom node = new Xpp3Dom( "node" );
            node.setAttribute( "id", getId( artifact ) );

            Xpp3Dom data = new Xpp3Dom( "data" );
            data.setAttribute( "key", "artifactId" );
            data.setValue( artifact.getArtifactId() );
            node.addChild( data );

            data = new Xpp3Dom( "data" );
            data.setAttribute( "key", "groupId" );
            data.setValue( artifact.getGroupId() );
            node.addChild( data );

            data = new Xpp3Dom( "data" );
            data.setAttribute( "key", "version" );
            data.setValue( artifact.getVersion() );
            node.addChild( data );

            data = new Xpp3Dom( "data" );
            data.setAttribute( "key", "scope" );
            data.setValue( artifact.getScope() );
            node.addChild( data );

            data = new Xpp3Dom( "data" );
            data.setAttribute( "key", "label" );
            data.setValue( artifact.getArtifactId() + ":" + artifact.getVersion() );
            node.addChild( data );

            dom.addChild( node );
        }
    }

    private void processEdges( Xpp3Dom dom, Set links )
    {
        Iterator it = links.iterator();
        while ( it.hasNext() )
        {
            ArtifactDependency artifactDependency = (ArtifactDependency) it.next();
            System.out.println( artifactDependency.getOrigin().getArtifactId() + " -> "
                + artifactDependency.getDestination().getArtifactId() );

            Xpp3Dom edge = new Xpp3Dom( "edge" );
            edge.setAttribute( "source", getId( artifactDependency.getOrigin() ) );
            edge.setAttribute( "target", getId( artifactDependency.getDestination() ) );
            dom.addChild( edge );
        }
    }

    private void addKeys( Xpp3Dom graph )
    {
        Xpp3Dom key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "artifactId" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "artifactId" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );

        key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "groupId" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "groupId" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );

        key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "version" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "version" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );

        key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "scope" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "scope" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );

        key = new Xpp3Dom( "key" );
        key.setAttribute( "id", "label" );
        key.setAttribute( "for", "node" );
        key.setAttribute( "attr.name", "label" );
        key.setAttribute( "attr.type", "string" );
        graph.addChild( key );
    }

}
