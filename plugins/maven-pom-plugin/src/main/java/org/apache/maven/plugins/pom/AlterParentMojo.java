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
 * Alter the parent of a project file
 * 
 * @author <a href="mailto:jmcconnell@apache.org">Jesse McConnell</a>
 * @version $Id:$
 * 
 * @phase process-resources
 * @goal alter-parent
 */
public class AlterParentMojo extends AbstractSingleAlterationMojo
{
    /**
     * @parameter
     * @required
     */
    String newParentGroupId;

    /**
     * @parameter
     * @required
     */
    String newParentArtifactId;

    /**
     * @parameter
     * @required
     */
    String newParentVersion;

    /**
     * @parameter
     * @parameter
     */
    String relativePath;

    /**
     * @parameter default-value="false"
     */
    boolean suppressRelativePath;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            XMLTool xmlTool = new XMLTool( "project", new File( projectFile ) );

            xmlTool.removeNamespaces();

            if ( xmlTool.hasElement( "//project/parent/version" ) )
            {

                xmlTool.updateElement( "//project/parent/version", newParentVersion );    
                xmlTool.updateElement( "//project/parent/groupId", newParentGroupId ); 
                xmlTool.updateElement( "//project/parent/artifactId", newParentArtifactId );

                // smoke the relative path if it exists

                if ( suppressRelativePath )
                {
                    if ( xmlTool.hasElement( "//project/parent/relativePath" ) )
                    {
                        Element relativePathElement = xmlTool.getElement( "//project/parent/relativePath" );
                        relativePathElement.detach();
                    }
                }
                else
                {
                    if ( xmlTool.hasElement( "//project/parent/relativePath" ) )
                    {
                        xmlTool.updateElement( "//project/parent/relativePath", relativePath );
                    }
                }

                xmlTool.writeDocument( projectFile );
            }
            else
            {
                getLog().info( "Project does not have a parent to alter!" );                
            }
        }
        catch ( XMLException e )
        {
            throw new MojoExecutionException( "error processing: " + projectFile, e );
        }        
    }
}
