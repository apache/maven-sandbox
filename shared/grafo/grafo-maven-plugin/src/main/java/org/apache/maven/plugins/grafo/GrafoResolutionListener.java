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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * Send resolution events to the debug log.
 *
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class GrafoResolutionListener
    implements ResolutionListener
{

    private Map nodes = new HashMap();
    
    private Set edges = new HashSet();

    private Stack stack = new Stack();

    private String indent = "";
    
    public void setNodes( Map nodes )
    {
        this.nodes = nodes;
    }

    public Map getNodes()
    {
        return nodes;
    }

    public void setEdges( Set edges )
    {
        this.edges = edges;
    }

    public Set getEdges()
    {
        return edges;
    }

    private Object getKey( Artifact artifact )
    {
        return artifact.getGroupId() + ":" + artifact.getArtifactId();   
    }

    public void testArtifact( Artifact artifact )
    {
        getNodes().put( getKey( artifact ), artifact );
    }

    public void startProcessChildren( Artifact artifact )
    {
        stack.push( artifact );
    }

    public void endProcessChildren( Artifact artifact )
    {
        stack.pop();
    }

    public void includeArtifact( Artifact artifact )
    {
        if ( !stack.isEmpty() )
        {
            getEdges().add( new ArtifactDependency( (Artifact) stack.peek(), artifact ) );
        } else 
        {
            System.out.println( "includeArtifact with empty stack " + artifact );
        }
    }

    public void omitForNearer( Artifact omitted, Artifact kept )
    {
        System.out.println( "---" + indent + omitted + " (removed - nearer found: " + kept.getVersion() + ")" );
    }

    public void omitForCycle( Artifact omitted )
    {
        System.out.println( "---" + indent + omitted + " (removed - causes a cycle in the graph)" );
    }

    public void updateScopeCurrentPom( Artifact artifact, String scope )
    {
        System.out.println( "---" + indent + artifact + " (not setting scope to: " + scope + "; local scope "
            + artifact.getScope() + " wins)" );
    }

    public void updateScope( Artifact artifact, String scope )
    {
        System.out.println( "---" + indent + artifact + " (setting scope to: " + scope + ")" );
    }

    public void selectVersionFromRange( Artifact artifact )
    {
        System.out.println( "---" + indent + artifact + " (setting version to: " + artifact.getVersion()
            + " from range: " + artifact.getVersionRange() + ")" );
    }

    public void restrictRange( Artifact artifact, Artifact replacement, VersionRange newRange )
    {
        System.out.println( "---" + indent + artifact + " (range restricted from: " + artifact.getVersionRange()
            + " and: " + replacement.getVersionRange() + " to: " + newRange + " )" );
    }

    public void manageArtifact( Artifact artifact, Artifact replacement )
    {
        String msg = indent + artifact;
        msg += " (";
        if ( replacement.getVersion() != null )
        {
            msg += "applying version: " + replacement.getVersion() + ";";
        }
        if ( replacement.getScope() != null )
        {
            msg += "applying scope: " + replacement.getScope();
        }
        msg += ")";
        System.out.println( "---" + msg );
    }
}
