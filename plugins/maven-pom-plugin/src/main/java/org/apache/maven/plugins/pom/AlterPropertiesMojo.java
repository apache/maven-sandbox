package org.apache.maven.plugins.pom;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.pom.util.XMLException;
import org.apache.maven.plugins.pom.util.XMLTool;
import org.dom4j.Element;

/**
 * Alter the parent of a project file
 * 
 * @author <a href="mailto:jmcconnell@apache.org">Jesse McConnell</a>
 * @version $Id:$
 * 
 * @phase process-resources
 * @goal alter-properties
 */
public class AlterPropertiesMojo extends AbstractSingleAlterationMojo
{

    /**
     * @parameter
     */
    Map alteredProperties;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( alteredProperties == null || alteredProperties.isEmpty() )
        {
            getLog().info( "no properties declared to alter" );
            return;
        }

        try
        {
            XMLTool xmlTool = new XMLTool( "project", new File( projectFile ) );

            xmlTool.removeNamespaces();

            Element properties = null;

            if ( xmlTool.hasElement( "//project/properties" ) )
            {
                properties = xmlTool.getElement( "//project/properties" );
            }
            else
            {
                Element project = xmlTool.getElement( "//project" );

                properties = project.addElement( "properties" );
            }

            for ( Iterator i = alteredProperties.keySet().iterator(); i.hasNext(); )
            {
                String propertyName = (String) i.next();

                if ( xmlTool.hasElement( "//project/properties/" + propertyName ) )
                {
                    xmlTool.updateElement( "//project/properties/" + propertyName,
                                           (String) alteredProperties.get( propertyName ) );
                }
                else
                {
                    Element newProperty = properties.addElement( propertyName );
                    newProperty.setText( (String) alteredProperties.get( propertyName ) );
                }
            }

            xmlTool.writeDocument( projectFile );
        }
        catch ( XMLException e )
        {
            throw new MojoExecutionException( "error processing: " + projectFile, e );
        }
    }
}
