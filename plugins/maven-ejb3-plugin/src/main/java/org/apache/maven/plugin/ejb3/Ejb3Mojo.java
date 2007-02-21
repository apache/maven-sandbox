package org.apache.maven.plugin.ejb3;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Builds J2EE EJB3 archive.
 *
 * @author <a href="piotr@bzdyl.net">Piotr Bzdyl</a>
 * @version $Id$
 * @goal ejb3
 * @phase package
 * @description build an ejb3
 *
 * @todo Add deployment descriptor file handling
 */
public class Ejb3Mojo
    extends AbstractMojo
{
    private static final String[] DEFAULT_EXCLUDES = new String[]{"**/package.html"};

    private static final String[] DEFAULT_INCLUDES = new String[]{"**/**"};
    
    private static final String[] DEFAULT_CLIENT_INCLUDES = new String[]{"**/**"};

    private static final String[] DEFAULT_CLIENT_EXCLUDES = new String[]{"**/*Bean.class",
         "**/*CMP.class", "**/*Session.class", "**/package.html"};

    /**
     * Directory containing the generated EJB3.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * Name of the generated EJB3.
     *
     * @parameter alias="parName" expression="${project.build.finalName}"
     * @required
     */
    private String jarName;

    /**
     * Directory containing the classes.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private File outputDirectory;

    /**
     * Single directory for extra files to include in the ejb3.
     *
     * @parameter expression="${basedir}/src/main/ejb3"
     * @required
     */
    private File ejb3SourceDirectory;

    /**
     * Whether the ejb client jar should be generated or not. Default
     * is false.
     *
     * @parameter
     * @todo boolean instead
     */
    private String generateClient = Boolean.FALSE.toString();

    /**
     * Excludes.
     *
     * <br/>Usage:
     * <pre>
     * &lt;clientIncludes&gt;
     *   &lt;clientInclude&gt;**&#47;*Ejb.class&lt;&#47;clientInclude&gt;
     *   &lt;clientInclude&gt;**&#47;*Bean.class&lt;&#47;clientInclude&gt;
     * &lt;&#47;clientIncludes&gt;
     * </pre>
     * <br/>Attribute is used only if client jar is generated.
     * <br/>Default exclusions: **&#47;*Bean.class, **&#47;*CMP.class, **&#47;*Session.class, **&#47;package.html
     * @parameter
     */
    private List clientExcludes;

    /**
     * Includes.
     *
     * <br/>Usage:
     * <pre>
     * &lt;clientIncludes&gt;
     *   &lt;clientInclude&gt;**&#47;*&lt;&#47;clientInclude&gt;
     * &lt;&#47;clientIncludes&gt;
     * </pre>
     * <br/>Attribute is used only if client jar is generated.
     * <br/>Default value: **&#47;**
     * @parameter
     */
    private List clientIncludes;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The maven archiver to use.
     *
     * @parameter
     */
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * The Jar archiver.
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
     * @required
     */
     private JarArchiver jarArchiver;

    /**
     * The client Jar archiver.
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
     * @required
     */
    private JarArchiver clientJarArchiver;

    /**
     * The maven project's helper.
     *
     * @parameter expression="${component.org.apache.maven.project.MavenProjectHelper}"
     * @required
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    /**
     * Generates the EJB3.
     *
     * @todo Add license files in META-INF directory.
     */
    public void execute()
        throws MojoExecutionException
    {
        try
        {
            copyResources();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error copying EJB3 resources", e );
        }

        try
        {
            generateEJB3Archive();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error assembling EJB3", e );
        }
    
        try
        {
            generateClient( jarName );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error creating client archive", e );
        }
    }

    private void copyResources()
        throws IOException
    {
        if ( ejb3SourceDirectory != null
                && ejb3SourceDirectory.exists() )
        {
            getLog().info( "Copy ejb3 resources to " + outputDirectory.getAbsolutePath() );
            FileUtils.copyDirectoryStructure( ejb3SourceDirectory, outputDirectory );
        }
    }


    private void generateEJB3Archive()
        throws ArchiverException, ManifestException, IOException, DependencyResolutionRequiredException
    {
        File ejb3File = new File( basedir, jarName + ".ejb3" );

        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver( jarArchiver );

        archiver.setOutputFile( ejb3File );

        if ( outputDirectory == null || !outputDirectory.exists() )
        {
            getLog().warn( "EJB3 will be empty - no content was marked for inclusion!" );
        }
        else
        {
            archiver.getArchiver().addDirectory( outputDirectory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES );
        }

        archiver.createArchive( project, archive );

        project.getArtifact().setFile( ejb3File );
    }

    private void generateClient( String jarName )
        throws ArchiverException, ManifestException, IOException, DependencyResolutionRequiredException
    {
        if ( new Boolean( generateClient ).booleanValue() )
        {
            getLog().info( "Building ejb client " + jarName + "-client" );

            String[] includes = DEFAULT_CLIENT_INCLUDES;
            String[] excludes = DEFAULT_CLIENT_EXCLUDES;

            if ( clientIncludes != null && !clientIncludes.isEmpty() )
            {
                includes = (String[]) clientIncludes.toArray( new String[clientIncludes.size()] );
            }

            if ( clientExcludes != null && !clientExcludes.isEmpty() )
            {
                excludes = (String[]) clientExcludes.toArray( new String[clientExcludes.size()] );
            }

            File clientJarFile = new File( basedir, jarName + "-client.jar" );

            MavenArchiver clientArchiver = new MavenArchiver();

            clientArchiver.setArchiver( clientJarArchiver );

            clientArchiver.setOutputFile( clientJarFile );

            clientArchiver.getArchiver().addDirectory( outputDirectory, includes, excludes );

            // create archive
            clientArchiver.createArchive( project, archive );

            projectHelper.attachArtifact( project, "jar", "client", clientJarFile );
        }

    }

}
