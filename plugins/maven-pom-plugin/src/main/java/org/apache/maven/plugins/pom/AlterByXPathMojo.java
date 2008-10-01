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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.pom.util.XMLException;
import org.apache.maven.plugins.pom.util.XMLTool;
import org.dom4j.Element;

/**
 * Alter a project file based on an xpath location
 * 
 * @author <a href="mailto:jmcconnell@apache.org">Jesse McConnell</a>
 * @version $Id:$
 * 
 * @phase process-resources
 * @goal alter-by-xpath
 */
public class AlterByXPathMojo extends AbstractSingleAlterationMojo
{

	/**
	 * @parameter expression="${xpath}"
	 * @required
	 * 
	 */
    private String xpath;

    /**
     * @parameter expression="${newValue}"
     * @required
     * 
     */
    private String newValue;
    
    /**
     * setting this boolean to false will allow the mojo to skip altering a field that might not exist in the 
     * target project file.
     * 
     * @parameter default-value="true"
     */
    private boolean failFast;
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( xpath == null || newValue == null )
        {
            getLog().info( "missing required information" );
            return;
        }

        try
        {
            XMLTool xmlTool = new XMLTool( "project", new File( projectFile ) );

            xmlTool.removeNamespaces();

            if ( xmlTool.hasElement( xpath ) )
            {
            	Element node = xmlTool.getElement( xpath );
            	
            	node.setText( newValue );            	            
            } 
            else if ( failFast )
            {
            	throw new MojoFailureException( "unable to locate required element in project file: " + projectFile + " with xpath: " + xpath );
            }           
            
            xmlTool.writeDocument( projectFile );
        }
        catch ( XMLException e )
        {
            throw new MojoExecutionException( "error processing: " + projectFile, e );
        }
    }
}
