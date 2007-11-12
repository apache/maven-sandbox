package org.apache.maven.jxr.js.doc;

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

import org.apache.log4j.Logger;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches all javascript files and creates a index HTML
 * with links to documentation
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @plexus.component role="org.apache.maven.jxr.js.doc.JSDoc" role-hint="default"
 */
public class GenerateHTMLIndex
    extends AbstractLogEnabled
    implements JSDoc
{
    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( GenerateHTMLIndex.class );

    private File jsDir;

    private File destDir;

    // ----------------------------------------------------------------------
    // Public
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void generate( String jsDirectory, String destDirectory )
        throws IllegalArgumentException, IOException
    {
        if ( jsDirectory == null )
        {
            throw new IllegalArgumentException( "JS directory attribute can't be empty" );
        }
        if ( !"/".equals( jsDirectory.substring( jsDirectory.length() - 1 ) ) )
        {
            jsDirectory = jsDirectory + "/";
        }
        File js = new File( jsDirectory );
        if ( !js.exists() )
        {
            throw new IllegalArgumentException( "JS directory doesn't exist." );
        }
        if ( js.exists() && !js.isDirectory() )
        {
            throw new IOException( "JS directory is a file." );
        }
        this.jsDir = js;

        if ( destDirectory == null )
        {
            throw new IllegalArgumentException( "destDir attribute can't be empty" );
        }
        if ( !"/".equals( destDirectory.substring( destDirectory.length() - 1 ) ) )
        {
            destDirectory = destDirectory + "/";
        }
        File dest = new File( destDirectory );
        if ( dest.exists() && !dest.isDirectory() )
        {
            throw new IOException( "Dest directory is a file." );
        }
        if ( !dest.exists() && !dest.mkdirs() )
        {
            throw new IOException( "Cannot create the dest directory." );
        }
        this.destDir = dest;

        List files = new ArrayList();
        collectFiles( jsDir, files );

        Writer writer = null;
        try
        {
            writer = new FileWriter( new File( this.destDir, "index.htm" ) ); // platform encoding
        }
        catch ( FileNotFoundException fnfe )
        {
            try
            {
                this.destDir.mkdir();
                writer = new FileWriter( new File( this.destDir, "index.htm" ) );
            }
            catch ( FileNotFoundException e )
            {
                log.error( "FileNotFoundException: " + e.getMessage(), e );
            }
        }

        PrintWriter out = new PrintWriter( writer );

        out.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
            + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
        out.println( "<html>" );
        out.println( "<head>" );
        out.println( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" );
        out.println( "<style type=\"text/css\">" );
        out.println( ".TableHeadingColor     { background: #CCCCFF } /* Dark mauve */" );
        out.println( ".NavBarCell1    { background-color:#EEEEFF;}/* Light mauve */" );
        out.println( "</style>" );
        out.println( "<title>JavaScript Code Documentation</title>" );
        out.println( "</head>" );
        out.println( "<body>" );
        out.println( "<h2>Index</h2>" );
        out.println( "<br>" );
        out.println( "<br>" );
        out.println( "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">" );
        out.println( "<tr class=\"TableHeadingColor\">" );
        out.println( "<td align=\"left\"><font size=\"+2\"><b>Filename</b></font></td>" );
        out.println( "<td align=\"left\"><font size=\"+2\"><b>Summary</b></font></td>" );
        out.println( "</tr>" );

        for ( int i = 0; i < files.size(); i++ )
        {
            File file = (File) files.get( i );

            GenerateHTMLDoc docGenerator = new GenerateHTMLDoc( file, this.destDir );
            docGenerator.generate();
        }

        if ( log.isInfoEnabled() )
        {
            log.info( "Number of .js files: " + files.size() );
        }

        for ( int i = 0; i < files.size(); i++ )
        {
            File file = (File) files.get( i );
            if ( log.isInfoEnabled() )
            {
                log.info( "file: " + file.getName() );
            }

            out.println( "<tr>" );
            out.println( "<td width=\"30%\" bgcolor=\"#f3f3f3\"><font face=\"Verdana\"><b><a href=\""
                + file.getName().substring( 0, file.getName().indexOf( "." ) ) + ".htm" + "\">" + file.getName()
                + "</a></b></font></td>" );
            out.println( "<td width=\"70%\">" );

            try
            {
                FileInputStream fis = new FileInputStream( file );
                BufferedReader br = new BufferedReader( new InputStreamReader( fis ) ); // platform encoding
                String content;

                while ( br.ready() )
                {
                    content = br.readLine();
                    if ( null != content && content.indexOf( "/**" ) != -1 )
                    {
                        content = br.readLine();
                        while ( null != content && content.indexOf( "*/" ) == -1 )
                        {
                            if ( content.indexOf( "* @" ) != -1 && content.indexOf( "summary" ) != -1 )
                            {
                                out.println( content.substring( content.indexOf( "* @summary" ) + 11 ) );
                                out.println( "<br>" );
                            }
                            content = br.readLine();
                        }
                    }
                }

            }
            catch ( FileNotFoundException fnfe )
            {
                log.error( "FileNotFoundException: " + fnfe.getMessage(), fnfe );
            }

            out.println( "</td>" );
            out.println( "</tr>" );
        }

        out.println( "</table>" );
        out.println( "</body>" );
        out.println( "</html>" );

        out.close();
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * @param baseDir
     * @param files
     */
    private static void collectFiles( File baseDir, List files )
    {
        File[] fileList = baseDir.listFiles();
        for ( int i = 0; i < fileList.length; i++ )
        {
            if ( fileList[i].isDirectory() )
            {
                collectFiles( fileList[i], files );
            }
            else if ( fileList[i].getName().toLowerCase().endsWith( ".js" ) )
            {
                files.add( fileList[i] );
            }
        }
    }
}
