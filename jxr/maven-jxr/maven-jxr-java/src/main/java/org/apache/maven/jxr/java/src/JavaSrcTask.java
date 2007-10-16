package org.apache.maven.jxr.java.src;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.SourceFileScanner;
import org.codehaus.plexus.util.IOUtil;

/**
 * Runs the javasrc converter as a task inside
 * the well known build tool "ant" (see ant.apache.org).
 *
 * @see <a href="http://ant.apache.org">http://ant.apache.org</a>
 * @version $Id$
 */
public class JavaSrcTask
    extends MatchingTask
{
    /**
     * Default location for css
     */
    private static final String DEFAULT_CSS_NAME = "styles.css";

    private static final String RESOURCE_CSS_DIR = "org/apache/maven/jxr/java/src/css";

    /** Field srcDir */
    private File srcDir;

    /** Field destDir */
    private File destDir;

    /** Field recurse */
    private boolean recurse = true;

    /** Field title */
    private String title = "JavaSrc";

    /** Field verbose */
    private boolean verbose = false;

    /**
     * Constructor for JavaSrcTask.
     */
    public JavaSrcTask()
    {
        super();
    }

    /**
     * Returns the directory where the Java sources are stored.
     *
     * @return String directory name
     */
    public File getSrcDir()
    {
        return srcDir;
    }

    /**
     * Sets the directory where the Java sources are stored.
     *
     * @param javaDir directory name
     */
    public void setSrcDir( File javaDir )
    {
        this.srcDir = javaDir;
    }

    /**
     * Returns the directory where the HTML output is written.
     *
     * @return String directory name
     */
    public File getDestDir()
    {
        return destDir;
    }

    /**
     * Sets the directory where the HTML output is written.
     *
     * @param htmlDir directory name
     */
    public void setDestDir( File htmlDir )
    {
        this.destDir = htmlDir;
    }

    /**
     * @throws BuildException
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute()
        throws BuildException
    {
        if ( getDestDir() == null )
        {
            throw new BuildException( "Missing mandatory attribute 'dest'.", getLocation() );
        }
        if ( getDestDir().exists() && !getDestDir().isDirectory() )
        {
            throw new BuildException( "Dest directory is a file.", getLocation() );
        }
        if ( !getDestDir().exists() && !getDestDir().mkdirs() )
        {
            throw new BuildException( "Cannot create the dest directory.", getLocation() );
        }

        if ( srcDir == null )
        {
            // We directly change the user variable, because it
            // shouldn't lead to problems
            srcDir = this.getProject().resolveFile( "." );
            log( "No src dir specified, using " + srcDir.getAbsolutePath() + " instead of" );
        }

        // find the files/directories
        DirectoryScanner dirScanner = getDirectoryScanner( srcDir );

        // get a list of files to work on
        String[] allSourceFiles = dirScanner.getIncludedFiles();
        SourceFileScanner sourceScanner = new SourceFileScanner( this );
        FileNameMapper sourceToOutMapper = new GlobPatternMapper();

        sourceToOutMapper.setFrom( "*" );
        sourceToOutMapper.setTo( "*.java" );

        String[] sourceFilesToProcess = sourceScanner.restrict( allSourceFiles, srcDir, destDir, sourceToOutMapper );

        if ( sourceFilesToProcess.length > 0 )
        {
            String files = ( ( sourceFilesToProcess.length == 1 ) ? " file" : " files" );

            log( "Converting " + sourceFilesToProcess.length + files, Project.MSG_INFO );
        }

        for ( int i = 0; i < sourceFilesToProcess.length; ++i )
        {
            sourceFilesToProcess[i] = new File( srcDir, sourceFilesToProcess[i] ).getAbsolutePath();
        }

        Pass1 p1 = new Pass1();

        p1.initializeDefaults();
        p1.setOutDir( destDir.getAbsolutePath() );
        p1.setRecurse( recurse );
        p1.setTitle( title );
        p1.setVerbose( verbose );
        p1.run( sourceFilesToProcess );

        Pass2 p2 = new Pass2();

        p2.initializeDefaults();
        p2.setOutDir( destDir.getAbsolutePath() );
        p2.setTitle( title );
        p2.setVerbose( verbose );

        try
        {
            p2.run( new String[] {} );
            copyDefaultStylesheet( getDestDir() );
        }
        catch ( IOException ioe )
        {
            throw new BuildException( ioe );
        }
    }

    /**
     * Method getRecurse
     *
     * @return
     */
    public boolean getRecurse()
    {
        return recurse;
    }

    /**
     * Method setRecurse
     *
     * @param recurse
     */
    public void setRecurse( boolean recurse )
    {
        this.recurse = recurse;
    }

    /**
     * Method getTitle
     *
     * @return
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Method setTitle
     *
     * @param title
     */
    public void setTitle( String title )
    {
        this.title = title;
    }

    /**
     * Method getVerbose
     *
     * @return
     */
    public boolean getVerbose()
    {
        return verbose;
    }

    /**
     * Method setVerbose
     *
     * @param verbose
     */
    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
    }

    /**
     * Method that copy the <code>DEFAULT_STYLESHEET_NAME</code> file from the current class
     * loader to the <code>outputDirectory</code>.
     *
     * @param outputDirectory the output directory
     * @throws java.io.IOException if any
     * @see #DEFAULT_CSS_NAME
     */
    private void copyDefaultStylesheet( File outputDirectory )
        throws IOException
    {
        if ( outputDirectory == null || !outputDirectory.exists() )
        {
            throw new IOException( "The outputDirectory " + outputDirectory + " doesn't exists." );
        }

        InputStream is = getStream( RESOURCE_CSS_DIR + "/" + DEFAULT_CSS_NAME );

        if ( is == null )
        {
            throw new IOException( "The resource " + DEFAULT_CSS_NAME + " doesn't exists." );
        }

        File outputFile = new File( outputDirectory, DEFAULT_CSS_NAME );

        if ( !outputFile.getParentFile().exists() )
        {
            outputFile.getParentFile().mkdirs();
        }

        FileOutputStream w = new FileOutputStream( outputFile );

        IOUtil.copy( is, w );

        IOUtil.close( is );

        IOUtil.close( w );
    }

    /**
     * Returns an input stream for reading the specified resource from the
     * current class loader.
     *
     * @param resource the resource
     * @return InputStream An input stream for reading the resource, or <tt>null</tt>
     *         if the resource could not be found
     */
    private InputStream getStream( String resource )
    {
        return getClass().getClassLoader().getResourceAsStream( resource );
    }
}
