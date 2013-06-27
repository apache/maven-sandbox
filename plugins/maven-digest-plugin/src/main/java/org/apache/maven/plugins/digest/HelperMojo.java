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

import java.lang.reflect.Field;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.help.DescribeMojo;

/**
 * Helper Mojo that extends the standard Maven help plugin describe goal. This is needed because the generated help mojo
 * does not handle annotation property names at present.
 */
@Mojo( name = "help", requiresProject = false )
public class HelperMojo
    extends DescribeMojo
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
            // N.B. Customise for each plugin
            // We used to use project component to get the info, but that forces plugin to be used in a project
            // Plugin should be able to be used standalone
            final String plugin = "org.apache.maven.plugins:maven-digest-plugin";
            f.set( this, plugin );
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
