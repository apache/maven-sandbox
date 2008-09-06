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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.jxr.java.src.html.Pass1;
import org.apache.maven.jxr.java.src.html.Pass2;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * Creates a set of HTML pages out of Java source code.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @plexus.component role="org.apache.maven.jxr.java.src.JavaSrc" role-hint="default"
 */
public class GenerateHTMLJavaSrc
    extends AbstractLogEnabled
    implements JavaSrc
{
    /** Field VERSION */
    public static final String VERSION = getVersion();

    /** Field USAGE */
    public static final String USAGE = "Usage: java " + JavaSrcOptions.getOptions() + "\n    "
        + JavaSrc.class.getName();

    /**
     * Default location for css
     */
    private static final String DEFAULT_CSS_NAME = "styles.css";

    private static final String RESOURCE_CSS_DIR = "org/apache/maven/jxr/java/src/css";

    private JavaSrcOptions options;

    /**
     * @return the output dir
     * @see JavaSrcOptions#getDestDir()
     */
    public File getDestDir()
    {
        return new File( getOptions().getDestDir() );
    }

    /**
     * @return a File list of the source dir.
     * @see JavaSrcOptions#getSrcDirs()
     */
    public List getSrcDirs()
    {
        List tmp = new LinkedList();
        for ( Iterator it = getOptions().getSrcDirs().iterator(); it.hasNext(); )
        {
            tmp.add( new File( (String) it.next() ) );
        }

        return tmp;
    }

    /**
     * Getter for the options
     *
     * @return the options
     */
    public JavaSrcOptions getOptions()
    {
        if ( this.options == null )
        {
            this.options = new JavaSrcOptions();
        }

        return this.options;
    }

    /**
     * Main entry point for JavaSrc.
     *
     * @param args CLI arguments.
     */
    public static void main( String[] args )
    {
        GenerateHTMLJavaSrc main = new GenerateHTMLJavaSrc();

        try
        {
            main.initializeRequiredOptions();
            main.initializeOptionalOptions();
        }
        catch ( IOException e )
        {
            System.out.println( "IOException: Error when parsing options:" );
            System.out.println( USAGE );
            return;
        }
        catch ( IllegalArgumentException e )
        {
            System.out.println( "IllegalArgumentException: Error when parsing options:" );
            System.out.println( USAGE );
            return;
        }

        try
        {
            File srcDir = (File) main.getSrcDirs().get( 0 );
            main.generate( srcDir, new File( main.getOptions().getDestDir() ) );
        }
        catch ( IOException e )
        {
            System.out.println( "IOException: " + e.getMessage() );
        }
        catch ( OutOfMemoryError e )
        {
            System.out.println( "Out Of Memory: add JVM property -Xms=**m and -Xmx=**m" );
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    public void generate( File srcDir, File destDir )
        throws IllegalArgumentException, IOException
    {
        addSrcDir( srcDir );
        setDestDir( destDir );

        Pass1 p1 = new Pass1( getOptions() );
        p1.run();

        Pass2 p2 = new Pass2( getOptions() );
        p2.run();

        if ( StringUtils.isNotEmpty( getOptions().getStylesheetfile() ) )
        {
            copyStylesheet( getOptions().getStylesheetfile(), getDestDir() );
        }
        else
        {
            copyDefaultStylesheet( getDestDir() );
        }
    }

    /** {@inheritDoc} */
    public void generate( File srcDir, File destDir, JavaSrcOptions option )
        throws IllegalArgumentException, IOException
    {
        if ( option == null )
        {
            throw new IllegalArgumentException( "option is required." );
        }
        this.options = option;

        generate( srcDir, destDir );
    }

    // ----------------------------------------------------------------------
    // private methods
    // ----------------------------------------------------------------------

    /**
     * Setter for the destDir
     *
     * @param destDir the destDir to set
     * @throws IOException if any
     * @throws IllegalArgumentException if any
     */
    private void setDestDir( File destDir )
        throws IOException, IllegalArgumentException
    {
        if ( destDir == null )
        {
            throw new IllegalArgumentException( "destDir is required." );
        }
        if ( destDir.exists() && !destDir.isDirectory() )
        {
            throw new IOException( "Dest directory is a file." );
        }
        if ( !destDir.exists() && !destDir.mkdirs() )
        {
            throw new IOException( "Cannot create the dest directory." );
        }

        getOptions().setDestDir( destDir.getAbsolutePath() );
    }

    /**
     * Setter for the srcDir
     *
     * @param srcDir the srcDir to set
     * @throws IOException if any
     * @throws IllegalArgumentException if any
     */
    private void addSrcDir( File srcDir )
        throws IOException, IllegalArgumentException
    {
        if ( srcDir == null )
        {
            throw new IllegalArgumentException( "srcDir is required." );
        }
        if ( !srcDir.exists() )
        {
            throw new IOException( "srcDir doesn't exist." );
        }
        if ( !srcDir.isDirectory() )
        {
            throw new IOException( "srcDir is not a directory." );
        }

        getOptions().addSrcDir( srcDir.getAbsolutePath() );
    }

    /**
     * Initialize required fields
     *
     * @throws IOException if any
     * @throws IllegalArgumentException if any
     * @see JavaSrc#main(String[])
     */
    private void initializeRequiredOptions()
        throws IOException, IllegalArgumentException
    {
        String srcDir = System.getProperty( "srcDir" );
        if ( StringUtils.isEmpty( srcDir ) )
        {
            throw new IllegalArgumentException( "srcDir should be not null" );
        }
        addSrcDir( new File( srcDir ) );

        String destDir = System.getProperty( "destDir" );
        if ( StringUtils.isEmpty( destDir ) )
        {
            throw new IllegalArgumentException( "destDir should be not null" );
        }
        setDestDir( new File( destDir ) );
    }

    /**
     * Initialize optional fields
     *
     * @throws IllegalArgumentException if any
     * @see JavaSrc#main(String[])
     */
    private void initializeOptionalOptions()
    {
        String bottom = System.getProperty( "bottom" );
        if ( StringUtils.isNotEmpty( bottom ) )
        {
            getOptions().setBottom( bottom );
        }

        String docencoding = System.getProperty( "docencoding" );
        if ( StringUtils.isNotEmpty( docencoding ) )
        {
            getOptions().setDocencoding( docencoding );
        }

        String doctitle = System.getProperty( "doctitle" );
        if ( StringUtils.isNotEmpty( doctitle ) )
        {
            getOptions().setDoctitle( doctitle );
        }

        String encoding = System.getProperty( "encoding" );
        if ( StringUtils.isNotEmpty( encoding ) )
        {
            getOptions().setEncoding( encoding );
        }

        String footer = System.getProperty( "footer" );
        if ( StringUtils.isNotEmpty( footer ) )
        {
            getOptions().setFooter( footer );
        }

        String header = System.getProperty( "header" );
        if ( StringUtils.isNotEmpty( header ) )
        {
            getOptions().setHeader( header );
        }

        String packagesheader = System.getProperty( "packagesheader" );
        if ( StringUtils.isNotEmpty( packagesheader ) )
        {
            getOptions().setPackagesheader( packagesheader );
        }

        String recurseStr = System.getProperty( "recurse" );
        if ( recurseStr != null )
        {
            recurseStr = recurseStr.trim();
            if ( recurseStr.equalsIgnoreCase( "off" ) || recurseStr.equalsIgnoreCase( "false" )
                || recurseStr.equalsIgnoreCase( "no" ) || recurseStr.equalsIgnoreCase( "0" ) )
            {
                getOptions().setRecurse( false );
            }
        }

        String stylesheetfile = System.getProperty( "stylesheetfile" );
        if ( StringUtils.isNotEmpty( stylesheetfile ) )
        {
            getOptions().setStylesheetfile( stylesheetfile );
        }

        String top = System.getProperty( "top" );
        if ( StringUtils.isNotEmpty( top ) )
        {
            getOptions().setTop( top );
        }

        String verboseStr = System.getProperty( "verbose" );
        if ( verboseStr != null )
        {
            verboseStr = verboseStr.trim();
            if ( verboseStr.equalsIgnoreCase( "on" ) || verboseStr.equalsIgnoreCase( "true" )
                || verboseStr.equalsIgnoreCase( "yes" ) || verboseStr.equalsIgnoreCase( "1" ) )
            {
                getOptions().setVerbose( true );
            }
        }

        String windowtitle = System.getProperty( "windowtitle" );
        if ( StringUtils.isNotEmpty( windowtitle ) )
        {
            getOptions().setWindowtitle( windowtitle );
        }
    }

    /**
     * Method that copy the <code>DEFAULT_STYLESHEET_NAME</code> file from the current class
     * loader to the <code>outputDirectory</code>.
     *
     * @param outputDirectory the output directory
     * @throws IOException if any
     * @see #RESOURCE_CSS_DIR
     * @see #DEFAULT_CSS_NAME
     */
    private static void copyDefaultStylesheet( File outputDirectory )
        throws IOException
    {
        if ( outputDirectory == null || !outputDirectory.exists() )
        {
            throw new IOException( "The outputDirectory " + outputDirectory + " doesn't exists." );
        }

        InputStream is = getStream( RESOURCE_CSS_DIR + "/" + DEFAULT_CSS_NAME );
        copyStylesheetInputStream( is, outputDirectory );
    }

    /**
     * Method that copy a given stylesheet file to the <code>outputDirectory</code>.
     *
     * @param outputDirectory the output directory
     * @throws IOException if any
     * @see #DEFAULT_CSS_NAME
     */
    private static void copyStylesheet( String stylesheetFile, File outputDirectory )
        throws IOException
    {
        if ( outputDirectory == null || !outputDirectory.exists() )
        {
            throw new IOException( "The outputDirectory " + outputDirectory + " doesn't exists." );
        }

        File stylesheet = new File( stylesheetFile );
        if ( !stylesheet.exists() || stylesheet.isDirectory() )
        {
            throw new IOException( "The stylesheet " + stylesheetFile + " doesn't exists or not a file." );
        }

        InputStream is = new FileInputStream( stylesheet );
        copyStylesheetInputStream( is, outputDirectory );
    }

    /**
     * Method that copy a stylesheet input stream to the <code>outputDirectory</code>.
     *
     * @param outputDirectory the output directory
     * @throws IOException if any
     * @see #DEFAULT_CSS_NAME
     */
    private static void copyStylesheetInputStream( InputStream is, File outputDirectory )
        throws IOException
    {
        if ( is == null )
        {
            throw new IOException( "The inputstream could not be null." );
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
    private static InputStream getStream( String resource )
    {
        return JavaSrc.class.getClassLoader().getResourceAsStream( resource );
    }

    /**
     * Get the version of JavaSrc from Maven <code>pom.properties</code>.
     *
     * @return the current version of JavaSrc.
     */
    private static String getVersion()
    {
        InputStream is = getStream( "META-INF/maven/org.apache.maven.jxr/maven-jxr-java/pom.properties" );
        if ( is != null )
        {
            Properties p = new Properties();
            try
            {
                p.load( is );
                return p.getProperty( "version" );
            }
            catch ( IOException e )
            {
                // nop
            }
            finally
            {
                IOUtil.close( is );
            }
        }

        return "";
    }
}
