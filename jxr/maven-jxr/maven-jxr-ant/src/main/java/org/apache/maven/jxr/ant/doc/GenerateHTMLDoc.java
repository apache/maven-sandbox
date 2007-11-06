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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.maven.jxr.ant.doc.vizant.Vizant;
import org.apache.maven.jxr.util.DotUtil;
import org.apache.maven.jxr.util.DotUtil.DotNotPresentInPathException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * Generate HTML documentation for <a href="http://ant.apache.org/">Ant</a> file.
 * <br/>
 * <b>Note</b>: <a href="http://www.graphviz.org/">Graphviz</a> dot program should be in the path or specified
 * by <code>dotExecutable</code> parameter.
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

    /** Graphviz Dot executable file */
    private File dotExecutable;

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
     * @return the Ant file which be parsed.
     */
    public File getAntFile()
    {
        return this.antFile;
    }

    /**
     * @return the dest dir
     */
    public File getDestDir()
    {
        return this.destDir;
    }

    /**
     * Getter for the dotExecutable
     *
     * @return the dotExecutable
     */
    public File getDotExecutable()
    {
        return this.dotExecutable;
    }

    /**
     * Setter for the antFile
     *
     * @param antFile the antFile to set
     */
    public void setAntFile( File antFile )
    {
        this.antFile = antFile;
    }

    /**
     * Setter for the destDir
     *
     * @param destDir the destDir to set
     */
    public void setDestDir( File destDir )
    {
        this.destDir = destDir;
    }

    /**
     * Setter for the dotExecutable
     *
     * @param dotExecutable the dotExecutable to set
     */
    public void setDotExecutable( File dotExecutable )
    {
        this.dotExecutable = dotExecutable;
    }

    /**
     * Generate the documentation.
     *
     * @throws DotNotPresentInPathException if any
     * @throws AntDocException if any
     */
    public void generateDoc()
        throws DotNotPresentInPathException, AntDocException
    {
        // 1. Generate Vizant graph
        try
        {
            generateVizantBuildGraph();
        }
        catch ( IOException e )
        {
            throw new AntDocException( "IOException: " + e.getMessage() );
        }

        // 2. Generate dot graph
        try
        {
            generateDotBuildGraph();
        }
        catch ( IOException e )
        {
            throw new AntDocException( "IOException: " + e.getMessage() );
        }
        catch ( TransformerException e )
        {
            throw new AntDocException( "TransformerException: " + e.getMessage() );
        }

        // 3. Generate images from the dot file
        try
        {
            generateImages();
        }
        catch ( CommandLineException e )
        {
            throw new AntDocException( "CommandLineException: " + e.getMessage() );
        }

        // 4. Generate site
        try
        {
            generateSite();
        }
        catch ( IOException e )
        {
            throw new AntDocException( "IOException: " + e.getMessage() );
        }
        catch ( TransformerException e )
        {
            throw new AntDocException( "TransformerException: " + e.getMessage() );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * @return xsl temp file.
     * @throws IOException if any
     */
    private File getXml2dot()
        throws IOException
    {
        if ( this.xml2dot == null )
        {
            this.xml2dot = FileUtils.createTempFile( "xml2dot", ".xsl", getDestDir() );
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
            this.xml2html = FileUtils.createTempFile( "xml2html", ".xsl", getDestDir() );
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
            this.xml2tg = FileUtils.createTempFile( "xml2tg", ".xsl", getDestDir() );
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
            this.buildGraph = FileUtils.createTempFile( "buildgraph", ".xml", getDestDir() );
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
            this.dot = FileUtils.createTempFile( "buildgraph", ".dot", getDestDir() );
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
            this.buildtg = FileUtils.createTempFile( "buildtg", ".xml", getDestDir() );
            this.buildtg.deleteOnExit();
        }

        return this.buildtg;
    }

    /**
     * Call Vizant task
     *
     * @throws IOException if any
     */
    private void generateVizantBuildGraph()
        throws IOException
    {
        Vizant vizant = new Vizant();
        vizant.setAntfile( getAntFile() );
        vizant.setOutfile( getBuildGraph() );
        vizant.setUniqueref( true );
        vizant.execute();
    }

    /**
     * Apply XSLT to generate dot file from the Vizant build graph
     *
     * @throws IOException if any
     * @throws TransformerException if any
     */
    private void generateDotBuildGraph()
        throws IOException, TransformerException
    {
        SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        if ( !( transformerFactory.getFeature( javax.xml.transform.sax.SAXSource.FEATURE ) && transformerFactory
            .getFeature( javax.xml.transform.stream.StreamResult.FEATURE ) ) )
        {

            throw new TransformerException( "The supplied TrAX transformer library is inadeguate."
                + "Please upgrade to the latest version." );
        }

        Transformer serializer = transformerFactory.newTransformer( new StreamSource( getXml2dot() ) );
        serializer.transform( new StreamSource( getBuildGraph() ), new StreamResult( getDot() ) );
    }

    /**
     * Call graphviz dot to generate images.
     *
     * @throws CommandLineException if any
     * @throws DotNotPresentInPathException if any
     */
    private void generateImages()
        throws CommandLineException, DotNotPresentInPathException
    {
        String[] dotFormat = { "svg", "png" };
        for ( int i = 0; i < dotFormat.length; i++ )
        {
            String format = dotFormat[i];

            if ( getDotExecutable() != null )
            {
                DotUtil.executeDot( getDotExecutable(), getDot(), format, new File( getDestDir(), "vizant." + format ) );
            }
            else
            {
                DotUtil.executeDot( getDot(), format, new File( getDestDir(), "vizant." + format ) );
            }
        }
    }

    /**
     * Generate the documentation site.
     *
     * @throws IOException if any
     * @throws TransformerException if any
     */
    private void generateSite()
        throws IOException, TransformerException
    {
        File targetHtml = new File( getDestDir(), "target.html" );

        SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        if ( !( transformerFactory.getFeature( javax.xml.transform.sax.SAXSource.FEATURE ) && transformerFactory
            .getFeature( javax.xml.transform.stream.StreamResult.FEATURE ) ) )
        {

            throw new TransformerException( "The supplied TrAX transformer library is inadeguate."
                + "Please upgrade to the latest version." );
        }

        Transformer serializer = transformerFactory.newTransformer( new StreamSource( getXml2html() ) );
        serializer.transform( new StreamSource( getBuildGraph() ), new StreamResult( targetHtml ) );

        serializer = transformerFactory.newTransformer( new StreamSource( getXml2tg() ) );
        serializer.transform( new StreamSource( getBuildGraph() ), new StreamResult( getBuildtg() ) );

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
