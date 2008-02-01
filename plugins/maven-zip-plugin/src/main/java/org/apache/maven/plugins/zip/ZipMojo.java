/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugins.zip;

import java.io.File;

import org.apache.maven.archiver.PomPropertiesUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 12 janv. 08
 * @version $Id$
 * 
 * @goal zip
 * @phase package
 */
public class ZipMojo
    extends AbstractMojo
{

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;    
    
   
    /**
     * Directory containing the classes.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File contentDirectory;

    /**
     * Directory containing the generated ZIP.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;    
    
    /**
     * Name of the generated ZIP.
     *
     * @parameter alias="zipName" expression="${zip.finalName}" default-value="${project.build.finalName}"
     * @required
     */
    private String finalName;    
    
    /**
     * Classifier to add to the artifact generated. If given, the artifact will be an attachment instead.
     *
     * @parameter
     */
    private String classifier;  
    
    /**
     * The Jar archiver.
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#zip}"
     * @required
     */
    private ZipArchiver zipArchiver;
    
    /**
     * Include or not empty directories
     * 
     * @parameter expression="${zip.includeEmptyDirs}" default-value="false"
     */
    private boolean includeEmptyDirs;
    
    /**
     * Whether creating the archive should be forced.
     *
     * @parameter expression="${zip.forceCreation}" default-value="false"
     */
    private boolean forceCreation; 
    
    /**
     * Adding pom.xml and pom.properties to the archive.
     *
     * @parameter expression="${addMavenDescriptor}" default-value="true"
     */    
    private boolean addMavenDescriptor;
    
    
    protected File getZipFile( File basedir, String finalName, String classifier )
    {
        if ( classifier == null )
        {
            classifier = "";
        }
        else if ( classifier.trim().length() > 0 && !classifier.startsWith( "-" ) )
        {
            classifier = "-" + classifier;
        }

        return new File( basedir, finalName + classifier + ".zip" );
    }    
    
    /** 
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {

            File zipFile = getZipFile( outputDirectory, finalName, classifier );

            zipArchiver.setDestFile( zipFile );
            zipArchiver.setIncludeEmptyDirs( includeEmptyDirs );
            zipArchiver.setCompress( true );
            zipArchiver.setForced( forceCreation );
            
            if ( addMavenDescriptor )
            {
                if ( project.getArtifact().isSnapshot() )
                {
                    project.setVersion( project.getArtifact().getVersion() );
                }

                String groupId = project.getGroupId();

                String artifactId = project.getArtifactId();

                zipArchiver.addFile( project.getFile(), "META-INF/maven/" + groupId + "/" + artifactId + "/pom.xml" );
                PomPropertiesUtil pomPropertiesUtil = new PomPropertiesUtil();
                File dir = new File( project.getBuild().getDirectory(), "maven-zip-plugin" );
                File pomPropertiesFile = new File( dir, "pom.properties" );
                pomPropertiesUtil.createPomProperties( project, zipArchiver, pomPropertiesFile, forceCreation );
            }
            zipArchiver.addDirectory( contentDirectory );
            zipArchiver.createArchive();
            project.getArtifact().setFile( zipFile );

        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error assembling ZIP", e );
        }

    }

}
