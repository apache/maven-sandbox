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
 * @version $Id$
 */
public class GenerateHTMLIndex
{
    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( GenerateHTMLIndex.class );

    public GenerateHTMLIndex( String jSDir, String destDir )
        throws IllegalArgumentException
    {
        if ( jSDir == null )
        {
            throw new IllegalArgumentException( "jSDir attribute can't be empty" );
        }
        File js = new File( jSDir );
        if ( !js.exists() )
        {
            throw new IllegalArgumentException( "JS directory does't exist." );
        }
        if ( js.exists() && !js.isDirectory() )
        {
            throw new IllegalArgumentException( "JS directory is a file." );
        }
        if ( !"/".equals( jSDir.substring( jSDir.length() - 1 ) ) )
        {
            jSDir = jSDir + "/";
        }

        if ( destDir == null )
        {
            throw new IllegalArgumentException( "destDir attribute can't be empty" );
        }
        File dest = new File( destDir );
        if ( dest.exists() && !dest.isDirectory() )
        {
            throw new IllegalArgumentException( "Dest directory is a file." );
        }
        if ( !dest.exists() && !dest.mkdirs() )
        {
            throw new IllegalArgumentException( "Cannot create the dest directory." );
        }
        if ( !"/".equals( destDir.substring( destDir.length() - 1 ) ) )
        {
            destDir = destDir + "/";
        }

        List files = new ArrayList();
        collectFiles( js, files );

        try
        {
            Writer writer = null;
            try
            {
                writer = new FileWriter( new File( dest, "index.htm" ) ); // platform encoding
            }
            catch ( FileNotFoundException fnfe )
            {
                try
                {
                    dest.mkdir();
                    writer = new FileWriter( new File( dest, "index.htm" ) );
                }
                catch ( FileNotFoundException e )
                {
                    log.error( "FileNotFoundException: " + e.getMessage(), e );
                }
            }
    
            PrintWriter out = new PrintWriter( writer );

            out.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" " +
                "\"http://www.w3.org/TR/html4/loose.dtd\">" );
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
                GenerateHTMLDoc docGenerator = new GenerateHTMLDoc( file, destDir );
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
        catch ( IOException ioe )
        {
            log.error( "IOException: " + ioe.getMessage(), ioe );
        }

    }

    private static void collectFiles( File baseDir, List files )
    {
        File[] fileList = baseDir.listFiles();
        for ( int i = 0; i < fileList.length; i++ )
        {
            if ( fileList[i].isDirectory() )
            {
                collectFiles( fileList[i], files );
            }
            else if ( fileList[i].getName().endsWith( ".js" ) )
            {
                files.add( fileList[i] );
            }
        }
    }

    public static void main( String[] args )
    {
        GenerateHTMLIndex index = new GenerateHTMLIndex( args[0], args[1] );
    }
}
