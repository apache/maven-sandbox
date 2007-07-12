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
package org.apache.maven.plugins.pom;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.pom.util.XMLException;
import org.apache.maven.plugins.pom.util.XMLTool;
import org.dom4j.Element;

/**
 * Alter a project's SCM elements
 * 
 * @author eredmond
 *
 * @phase process-resources
 * @goal alter-scm
 */
public class AlterScmMojo extends AbstractSingleAlterationMojo
{
    /**
     * @parameter expression="${connection}"
     */
    String connection;

    /**
     * @parameter expression="${developerConnection}"
     */
    String developerConnection;

    /**
     * @parameter expression="${url}"
     */
    String url;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            XMLTool xmlTool = new XMLTool( "project", new File( projectFile ) );

            xmlTool.removeNamespaces();

            Element scm = null;

            if ( xmlTool.hasElement( "//project/scm" ) )
            {
                scm = xmlTool.getElement( "//project/scm" );
            }
            else
            {
                Element project = xmlTool.getElement( "//project" );

                scm = project.addElement( "scm" );
            }

            getScmElement( xmlTool, scm, "connection", connection );

            getScmElement( xmlTool, scm, "developerConnection", developerConnection );

            getScmElement( xmlTool, scm, "url", url );

            xmlTool.writeDocument( projectFile );
        }
        catch ( XMLException e )
        {
            throw new MojoExecutionException( "error processing: " + projectFile, e );
        }
    }

    private void getScmElement( XMLTool xmlTool, Element scm, String elementName, String value )
        throws XMLException
    {
        if ( value == null || value.length() == 0 )
        {
            return;
        }

        if ( xmlTool.hasElement( "//project/scm/" + elementName ) )
        {
            xmlTool.updateElement( "//project/scm/" + elementName, connection );
        }
        else
        {
            Element scmElement = scm.addElement( elementName );
            scmElement.setText( connection );
        }
    }
}
