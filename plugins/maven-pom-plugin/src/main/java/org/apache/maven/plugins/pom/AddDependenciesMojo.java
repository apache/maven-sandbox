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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.pom.util.XMLException;
import org.apache.maven.plugins.pom.util.XMLTool;
import org.dom4j.Element;


/**
 * add dependencies to a project file
 * 
 * @author <a href="mailto:jmcconnell@apache.org">Jesse McConnell</a>
 * @version $Id:$
 * 
 * @phase process-resources
 * @goal add-dependencies
 */
public class AddDependenciesMojo extends AbstractSingleAlterationMojo
{
    /**
     * @parameter
     */
    ArrayList dependencies;

    private Set knownGroupIds = new HashSet();
    private Set knownArtifactIds = new HashSet();
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( dependencies == null || dependencies.isEmpty() )
        {
            getLog().info( "skipping add dependencies, no dependencies to add declared" );
            return;
        }
        
        try
        {
            XMLTool xmlTool = new XMLTool( "project", new File( projectFile ) );

            xmlTool.removeNamespaces();

            Element dependenciesElement;
            
            if ( xmlTool.hasElement( "//project/dependencies" ) )
            {   
                dependenciesElement = xmlTool.getElement( "//project/dependencies" );                 
            }
            else
            {   
                Element projectElement = xmlTool.getElement( "//project" );
                dependenciesElement = projectElement.addElement( "dependencies" );                           
            }
            
            gatherExistingDependencies( xmlTool );
            
            for ( Iterator i = dependencies.iterator(); i.hasNext(); )
            {         
                Dependency dep = (Dependency)i.next();
                
                if ( knownGroupIds.contains( dep.getGroupId() ) )
                {
                    if ( knownArtifactIds.contains( dep.getArtifactId() ) )
                    {
                        getLog().info("Dependency already exists in target pom: " + dep.toString() );
                        continue;
                    }
                }
                
                Element newDepElement = dependenciesElement.addElement( "dependency" );
                Element newGroupId = newDepElement.addElement( "groupId" );
                newGroupId.setText( dep.getGroupId() );
                Element newArtifactId = newDepElement.addElement( "artifactId" );
                newArtifactId.setText( dep.getArtifactId() );
                Element newVersion = newDepElement.addElement( "version" );
                newVersion.setText( dep.getVersion() );
                
                if ( dep.getScope() != null )
                {
                	Element newScope = newDepElement.addElement( "scope" );
                	newScope.setText( dep.getScope() );
                }
            }     
            
            xmlTool.writeDocument( projectFile );
        }
        catch ( XMLException e )
        {
            throw new MojoExecutionException( "error processing: " + projectFile, e );
        }        
    }
    
    private void gatherExistingDependencies( XMLTool xmlTool )
        throws MojoExecutionException
    {
        List dependencies = xmlTool.getElementList( "//project/dependencies/dependency" );               
        
        for ( Iterator j = dependencies.iterator(); j.hasNext(); )
        {
            Element dependency = (Element)j.next();
            
            knownGroupIds.add( getGroupId( dependency ) );
            knownArtifactIds.add( getArtifactId( dependency ) );                            
        }   
    }

}
