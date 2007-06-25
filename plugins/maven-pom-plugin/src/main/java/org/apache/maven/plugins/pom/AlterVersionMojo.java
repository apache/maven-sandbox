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
 * Alter the version of a project and its subprojects
 * 
 * @author <a href="mailto:jmcconnell@apache.org">Jesse McConnell</a>
 * @author <a href="tcue@exist.com">Teody Cu Jr.</a>
 * @version $Id:$
 * 
 * @phase process-resources
 * @goal alter-version
 */
public class AlterVersionMojo extends AbstractMultipleAlterationMojo
{
    /**
     * @parameter default-value="${project.version}"
     * @required
     */
    private String newVersion;

    private Set managedGroupIds = new HashSet();
    private Set managedArtifactIds = new HashSet();
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        List targetProjects = getProjects( target );

        for ( Iterator i = targetProjects.iterator(); i.hasNext(); )
        {
            File file = (File) i.next();
        }

        loadProjectTools( targetProjects );
        
        gatherManagedInfo();
        
        fixProjectVersions();
        fixProjectReferences();
        
        closeProjectTools();
    }

    private void gatherManagedInfo() throws MojoExecutionException
    {
        for ( Iterator i = projectTools.iterator(); i.hasNext(); )
        {
            XMLTool projectTool = (XMLTool)i.next();
            
            if ( projectTool.hasElement( "//project/groupId" ) )
            {
                Element groupIdElement = projectTool.getElement( "//project/groupId" );
                managedGroupIds.add( groupIdElement.getTextTrim() );                
            }
            else
            {
                Element parentGroupIdElement = projectTool.getElement( "//project/parent/groupId" );
                managedGroupIds.add( parentGroupIdElement.getTextTrim() );
            }
            
            Element artifactId = projectTool.getElement( "//project/artifactId" );
            managedArtifactIds.add( artifactId.getTextTrim() );
        }
    }
    
    private void fixProjectVersions() throws MojoExecutionException
    {
        for ( Iterator i = projectTools.iterator(); i.hasNext(); )
        {            
            try
            {               
                XMLTool xmlTool = (XMLTool) i.next();

                if ( xmlTool.hasElement( "//project/parent/version" ) )
                {
                    xmlTool.updateElement( "//project/parent/version", newVersion );
                }
                if ( xmlTool.hasElement( "//project/version" ) )
                {
                    xmlTool.updateElement( "//project/version", newVersion );
                }
            }
            catch ( XMLException e )
            {
                throw new MojoExecutionException( "unable to fix versions for a document", e );
            }
        }
    }

    private void fixProjectReferences() throws MojoExecutionException
    {
       
        for ( Iterator i = projectTools.iterator(); i.hasNext(); )
        {
            try
            {
                XMLTool xmlTool = (XMLTool) i.next();
                
                // resolve managed dependencies
                List dependencies = xmlTool.getElementList( "//project/dependencies/dependency" );               
                
                for ( Iterator j = dependencies.iterator(); j.hasNext(); )
                {
                    Element dependency = (Element)j.next();
                                    
                    if ( managedGroupIds.contains( getGroupId( dependency ).getTextTrim() ) )
                    {
                        if ( managedArtifactIds.contains( getArtifactId( dependency ).getTextTrim() ) )
                        {
                            if ( xmlTool.hasElement( dependency.getUniquePath() + "/version" ) )
                            {
                                xmlTool.updateElement(  dependency.getUniquePath() + "/version", newVersion );
                            }
                        }
                    }                                       
                }              
                
                dependencies = xmlTool.getElementList( "//project/dependencyManagement/dependencies/dependency" );

                // resolve managed dependencies from dependencyManagement
                for ( Iterator j = dependencies.iterator(); j.hasNext(); )
                {
                    Element dependency = (Element)j.next();
                    
                    if ( managedGroupIds.contains( getGroupId( dependency ).getTextTrim() ) )
                    {
                        if ( managedArtifactIds.contains( getArtifactId( dependency ).getTextTrim() ) )
                        {                            
                            if ( xmlTool.hasElement( dependency.getUniquePath() + "/version" ) )
                            {
                                xmlTool.updateElement(  dependency.getUniquePath() + "/version", newVersion );
                            }
                        }
                    }                                       
                }              
            }
            catch ( XMLException e )
            {
                e.printStackTrace();
            }
        }
    }
}
