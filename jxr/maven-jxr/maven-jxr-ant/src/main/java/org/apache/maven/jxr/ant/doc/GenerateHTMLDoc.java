package org.apache.maven.jxr.ant.doc;

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
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.maven.jxr.ant.doc.vizant.Vizant;
import org.apache.maven.jxr.util.DotTask;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * Generate HTML documentation for <a href="http://ant.apache.org/">Ant</a> file.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class GenerateHTMLDoc
{
    /** An ant file */
    private File antFile;

    /** Destination directory */
    private File destDir;

    /** Temp xsl file */
    private File xml2dot;

    /** Temp xsl file */
    private File xml2html;

    /** Temp xsl file */
    private File xml2tg;

    /** Temp Vizant build graph */
    private File buildGraph;

    /** Temp generated dot file */
    private File dot;

    /** Temp generated touch graph */
    private File buildtg;

    /**
     * @param antFile
     * @param destDir
     * @throws IllegalArgumentException
     */
    public GenerateHTMLDoc( File antFile, File destDir )
        throws IllegalArgumentException
    {
        if ( antFile == null )
        {
            throw new IllegalArgumentException( "Missing mandatory attribute 'antFile'." );
        }
        if ( !antFile.exists() || !antFile.isFile() )
        {
            throw new IllegalArgumentException( "Input '" + getAntFile() + "' not found or not a file." );
        }

        if ( destDir == null )
        {
            throw new IllegalArgumentException( "Missing mandatory attribute 'dest'." );
        }
        if ( destDir.exists() && !destDir.isDirectory() )
        {
            throw new IllegalArgumentException( "Dest directory is a file." );
        }
        if ( !destDir.exists() && !destDir.mkdirs() )
        {
            throw new IllegalArgumentException( "Cannot create the dest directory." );
        }

        this.antFile = antFile;
        this.destDir = destDir;
    }

    /**
     * Generate the documentation
     *
     * @throws IOException if any
     */
    public void generateDoc()
        throws IOException
    {
        // 1. Generate Vizant graph
        generateVizantBuildGraph();

        // 2. Generate dot graph
        generateDotBuildGraph();

        // 3. Generate images from the dot file
        generateImages();

        // 4. Generate site
        generateSite();
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * @return the Ant file which be parsed.
     */
    private File getAntFile()
    {
        return this.antFile;
    }

    /**
     * @return the dest dir
     */
    private File getDestDir()
    {
        return this.destDir;
    }

    /**
     * @return xsl temp file.
     * @throws IOException if any
     */
    private File getXml2dot()
        throws IOException
    {
        if ( this.xml2dot == null )
        {
            this.xml2dot = FileUtils.createTempFile( "xml2dot", ".xsl", null );
            this.xml2dot.deleteOnExit();

            InputStream is = getClass().getClassLoader().getResourceAsStream( "vizant/xml2dot.xsl" );
            if ( is == null )
            {
                throw new IOException( "This resource doesn't exist." );
            }

            FileOutputStream w = new FileOutputStream( this.xml2dot );

            IOUtil.copy( is, w );

            IOUtil.close( is );

            IOUtil.close( w );
        }

        return this.xml2dot;
    }

    /**
     * @return xsl temp file.
     * @throws IOException if any
     */
    private File getXml2html()
        throws IOException
    {
        if ( this.xml2html == null )
        {
            this.xml2html = FileUtils.createTempFile( "xml2html", ".xsl", null );
            this.xml2html.deleteOnExit();

            InputStream is = getClass().getClassLoader().getResourceAsStream( "vizant/xml2html.xsl" );
            if ( is == null )
            {
                throw new IOException( "This resource doesn't exist." );
            }

            FileOutputStream w = new FileOutputStream( this.xml2html );

            IOUtil.copy( is, w );

            IOUtil.close( is );

            IOUtil.close( w );
        }

        return this.xml2html;
    }

    /**
     * @return xsl temp file.
     * @throws IOException if any
     */
    private File getXml2tg()
        throws IOException
    {
        if ( this.xml2tg == null )
        {
            this.xml2tg = FileUtils.createTempFile( "xml2tg", ".xsl", null );
            this.xml2tg.deleteOnExit();

            InputStream is = getClass().getClassLoader().getResourceAsStream( "vizant/xml2tg.xsl" );
            if ( is == null )
            {
                throw new IOException( "This resource doesn't exist." );
            }

            FileOutputStream w = new FileOutputStream( this.xml2tg );

            IOUtil.copy( is, w );

            IOUtil.close( is );

            IOUtil.close( w );
        }

        return this.xml2html;
    }

    /**
     * @return a temp file for the Vizant build graph file.
     */
    private File getBuildGraph()
    {
        if ( this.buildGraph == null )
        {
            this.buildGraph = FileUtils.createTempFile( "buildgraph", ".xml", null );
            this.buildGraph.deleteOnExit();
        }

        return this.buildGraph;
    }

    /**
     * @return a temp file for dot file.
     */
    private File getDot()
    {
        if ( this.dot == null )
        {
            this.dot = FileUtils.createTempFile( "buildgraph", ".dot", null );
            this.dot.deleteOnExit();
        }

        return this.dot;
    }

    /**
     * @return a temp file for build touch graph file.
     */
    private File getBuildtg()
    {
        if ( this.buildtg == null )
        {
            this.buildtg = FileUtils.createTempFile( "buildtg", ".xml", null );
            this.buildtg.deleteOnExit();
        }

        return this.buildtg;
    }

    /**
     * @return a minimal Ant project.
     */
    private Project getAntProject()
    {
        Project antProject = new Project();
        antProject.setBasedir( new File( "" ).getAbsolutePath() );

        return antProject;
    }

    /**
     * Call Vizant task
     *
     * @throws BuildException if any
     */
    private void generateVizantBuildGraph()
        throws BuildException
    {
        Vizant vizantTask = new Vizant();
        vizantTask.setProject( getAntProject() );
        vizantTask.setTaskName( "vizant" );
        vizantTask.init();
        vizantTask.setAntfile( getAntFile() );
        vizantTask.setOutfile( getBuildGraph() );
        vizantTask.setUniqueref( true );
        vizantTask.execute();
    }

    /**
     * Apply XSLT to generate dot file from the Vizant build graph
     *
     * @throws BuildException if any
     * @throws IOException if any
     */
    private void generateDotBuildGraph()
        throws BuildException, IOException
    {
        XSLTProcess xsltTask = new XSLTProcess();
        xsltTask.setProject( getAntProject() );
        xsltTask.setTaskName( "xslt" );
        xsltTask.init();
        xsltTask.setIn( getBuildGraph() );
        xsltTask.setOut( getDot() );
        xsltTask.setStyle( getXml2dot().getAbsolutePath() );
        xsltTask.execute();
    }

    /**
     * Call graphviz dot to generate images.
     *
     * @throws BuildException if any
     */
    private void generateImages()
        throws BuildException
    {
        String[] dotFormat = { "svg", "png" };
        for ( int i = 0; i < dotFormat.length; i++ )
        {
            String format = dotFormat[i];

            DotTask dotTask = new DotTask();
            dotTask.setProject( getAntProject() );
            dotTask.setTaskName( "dot" );
            dotTask.init();
            dotTask.setIn( getDot() );
            dotTask.setOut( new File( getDestDir(), "vizant." + format ) );
            dotTask.setFormat( format );
            dotTask.execute();
        }
    }

    /**
     * Generate the documentation site.
     *
     * @throws BuildException if any
     * @throws IOException if any
     */
    private void generateSite()
        throws BuildException, IOException
    {
        File targetHtml = new File( getDestDir(), "target.html" );
        XSLTProcess xsltTask = new XSLTProcess();
        xsltTask.setProject( getAntProject() );
        xsltTask.setTaskName( "xslt" );
        xsltTask.init();
        xsltTask.setIn( getBuildGraph() );
        xsltTask.setOut( targetHtml );
        xsltTask.setStyle( getXml2html().getAbsolutePath() );
        xsltTask.execute();

        xsltTask = new XSLTProcess();
        xsltTask.setProject( getAntProject() );
        xsltTask.setTaskName( "xslt" );
        xsltTask.init();
        xsltTask.setIn( getBuildGraph() );
        xsltTask.setOut( getBuildtg() );
        xsltTask.setStyle( getXml2tg().getAbsolutePath() );
        xsltTask.setReloadStylesheet( true );
        xsltTask.execute();

        // copy
        FileUtils.copyFile( getBuildtg(), new File( getDestDir(), "InitialXML._xml" ) );

        copyResources( this.getClass().getClassLoader(), "vizant/resources.txt", getDestDir() );
        copyResources( this.getClass().getClassLoader(), "touchgraph/resources.txt", getDestDir() );
    }

    /**
     * @param classloader the given class loader, not null
     * @param resourcesPath the path of a resources file in the given class loader, not null
     * @param outputDirectory the output directory, not null
     * @throws IOException if any
     */
    private static void copyResources( ClassLoader classloader, String resourcesPath, File outputDirectory )
        throws IOException
    {
        InputStream resourceList = classloader.getResourceAsStream( resourcesPath );

        if ( resourceList == null )
        {
            throw new IOException( "The resourcesPath '" + resourcesPath + "' doesn't exists in the class loader '"
                + classloader + "'." );
        }

        LineNumberReader reader = new LineNumberReader( new InputStreamReader( resourceList ) );

        String line = reader.readLine();

        while ( line != null )
        {
            InputStream is = classloader.getResourceAsStream( line );

            if ( is == null )
            {
                throw new IOException( "The resource " + line + " doesn't exist." );
            }

            File outputFile = new File( outputDirectory, line.substring( line.indexOf( '/' ) ) );

            if ( !outputFile.getParentFile().exists() )
            {
                outputFile.getParentFile().mkdirs();
            }

            FileOutputStream w = new FileOutputStream( outputFile );

            IOUtil.copy( is, w );

            IOUtil.close( is );

            IOUtil.close( w );

            line = reader.readLine();
        }
    }
}
