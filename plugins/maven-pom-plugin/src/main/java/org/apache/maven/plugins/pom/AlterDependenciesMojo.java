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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * @goal alter-dependencies
 */
public class AlterDependenciesMojo extends AbstractMultipleAlterationMojo
{
    
    /**
     * @parameter
     */
    ArrayList dependencies;

    private Set managedGroupIds = new HashSet();
    private Set managedArtifactIds = new HashSet();
    private Map managedDependencyMap = new HashMap();
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( dependencies == null || dependencies.isEmpty() )
        {
            getLog().info( "skipping alter dependencies, no dependencies to alter declared" );
            return;
        }
        
        List targetProjects = getProjects( target );

        loadProjectTools( targetProjects );
        
        gatherDependencyInfo();
        
        alterDependencies();
        
        closeProjectTools();  
    }
    
    private void alterDependencies() throws MojoExecutionException
    {
        for ( Iterator i = projectTools.iterator(); i.hasNext(); )
        {            
            try
            {               
                XMLTool xmlTool = (XMLTool) i.next();
                
                // resolve managed dependencies
                List dependencies = xmlTool.getElementList( "//project/dependencies/dependency" );               
                
                processDependencyList(xmlTool, dependencies);
                
                dependencies = xmlTool.getElementList( "//project/dependencyManagement/dependencies/dependency" );

                processDependencyList(xmlTool, dependencies);
                
            }
            catch ( XMLException e )
            {
                throw new MojoExecutionException( "unable to fix versions for a document", e );
            }
        }
    }

    
    private void processDependencyList( XMLTool xmlTool, List dependencies )
    	throws MojoExecutionException
    {
    	// resolve managed dependencies from dependencyManagement
        for ( Iterator j = dependencies.iterator(); j.hasNext(); )
        {
            Element dependency = (Element)j.next();
            
            if ( managedGroupIds.contains( getGroupId( dependency ).getTextTrim() ) )
            {
                if ( managedArtifactIds.contains( getArtifactId( dependency ).getTextTrim() ) )
                {                            
                	String dependencyKey = getGroupId( dependency ).getTextTrim() + ":" + getArtifactId( dependency ).getTextTrim();
                	Dependency relevantDependency = (Dependency)managedDependencyMap.get( dependencyKey );
                	
                    if ( xmlTool.hasElement( dependency.getUniquePath() + "/version" ) )
                    {                            	
                        xmlTool.updateElement(  dependency.getUniquePath() + "/version", relevantDependency.getVersion() );
                    }
                    
                    if ( relevantDependency.getScope() != null && xmlTool.hasElement( dependency.getUniquePath() + "/scope" ) )
                    {                            	
                        xmlTool.updateElement(  dependency.getUniquePath() + "/scope", relevantDependency.getScope() );
                    }
                    else if ( relevantDependency.getScope() != null )
                    {
                    	Element scope = dependency.addElement( "scope" );
                    	scope.setText( relevantDependency.getScope() );
                    } 
                }
            }                                       
        } 
    }
    
    private void gatherDependencyInfo()
    {
        for ( Iterator i = dependencies.iterator(); i.hasNext(); )
        {
            Dependency dep = (Dependency)i.next();
            
            managedGroupIds.add( dep.getGroupId() );
            managedArtifactIds.add( dep.getArtifactId() );
            managedDependencyMap.put( dep.getGroupId() + ":" + dep.getArtifactId(), dep );          
        }
    }
    
}
