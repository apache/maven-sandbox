/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.apache.maven.plugins.digest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.help.DescribeMojo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper Mojo that extends the standard Maven help plugin describe goal. This is needed because the generated help mojo
 * does not handle annotation property names at present.
 */
@Mojo( name = "help", requiresProject = false )
public class HelperMojo
    extends DescribeMojo
{
    // Where to find plugin config
    private static final String PLUGIN_PATH = "/META-INF/maven/plugin.xml";

    // ----------------------------------------------------------------------
    // Mojo components
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Mojo options
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute()
        throws MojoExecutionException
    {
        final String pluginId = getpluginGA();
        Field f = null;
        boolean isAccessible = true; // assume accessible
        try
        {
            // Unfortunately the plugin field is private
            f = DescribeMojo.class.getDeclaredField( "plugin" );
            isAccessible = f.isAccessible();
            if ( !isAccessible )
            {
                f.setAccessible( true );
            }
            f.set( this, pluginId );
            super.execute();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Could not set up plugin details" );
        }
        finally
        {
            if ( f != null && !isAccessible )
            {
                f.setAccessible( isAccessible ); // reset accessibility (prob not needed)
            }
        }
    }
    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------
    private String getpluginGA() throws MojoExecutionException {
        Document doc = build();
        Node plugin = getSingleChild( doc, "plugin" );
        String id = getValue( plugin, "groupId" ) + ":" + getValue( plugin, "artifactId" );
        return id;
    }

    private Document build()
        throws MojoExecutionException
    {
        getLog().debug( "load plugin-help.xml: " + PLUGIN_PATH );
        InputStream is = getClass().getResourceAsStream( PLUGIN_PATH );
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse( is );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( ParserConfigurationException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( SAXException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        finally
        {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    private List<Node> findNamedChild( Node node, String elementName )
    {
        List<Node> result = new ArrayList<Node>();
        NodeList childNodes = node.getChildNodes();
        for ( int i = 0; i < childNodes.getLength(); i++ )
        {
            Node item = childNodes.item( i );
            if ( elementName.equals( item.getNodeName() ) )
            {
                result.add( item );
            }
        }
        return result;
    }

    private Node getSingleChild( Node node, String elementName )
        throws MojoExecutionException
    {
        List<Node> namedChild = findNamedChild( node, elementName );
        if ( namedChild.isEmpty() )
        {
            throw new MojoExecutionException( "Could not find " + elementName + " in plugin-help.xml" );
        }
        if ( namedChild.size() > 1 )
        {
            throw new MojoExecutionException( "Multiple " + elementName + " in plugin-help.xml" );
        }
        return namedChild.get( 0 );
    }

    private String getValue( Node node, String elementName )
        throws MojoExecutionException
    {
        return getSingleChild( node, elementName ).getTextContent();
    }


    // ----------------------------------------------------------------------
    // Static methods
    // ----------------------------------------------------------------------
}
