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

package org.apache.maven.plugin.gpg;

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
 * Help Mojo that extends the standard Maven help plugin describe goal.
 * This is needed because the generated help mojo 
 * does not handle annotation property names at present.
 * Nor does it handle default values
 */
@Mojo (name = "help", requiresProject=false )
public class HelperMojo extends DescribeMojo 
{

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
        final String pluginId = "@GAV@"; // replaced by POM
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

    // ----------------------------------------------------------------------
    // Static methods
    // ----------------------------------------------------------------------
}
