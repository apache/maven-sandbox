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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class that mounts a Document in HTML as document
 *
 * @version $Id$
 */
public class GenerateHTMLDoc
{
    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( GenerateHTMLDoc.class );

    public GenerateHTMLDoc( File file, String destDir )
    {
        try
        {
            String filename = file.getName();
            PrintWriter out = null;
            BufferedReader br = null;
            try
            {
                out = new PrintWriter( new FileWriter( destDir + filename.substring( 0, filename.indexOf( "." ) ) + ".htm" ) );
                br = new BufferedReader( new FileReader( file ) );
            }
            catch ( FileNotFoundException fnfe )
            {
                log.error( "FileNotFoundException: " + fnfe.getMessage(), fnfe );
            }

            out.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" " +
                "\"http://www.w3.org/TR/html4/loose.dtd\">" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<style type=\"text/css\">" );
            out.println( ".TableHeadingColor     { background: #CCCCFF } /* Dark mauve */" );
            out.println( ".NavBarCell1    { background-color:#EEEEFF;}/* Light mauve */" );
            out.println( "</style>" );
            out.println( "<title>Javascript code documentation</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<h2>Filename: " + filename + "</h2>" );
            out.println( "<br>" );
            out.println( "<br>" );
            out.println( "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">" );
            out.println( "<tr class=\"TableHeadingColor\">" );
            out.println( "<td align=\"left\" colspan=\"2\"><font size=\"+2\"><b>Function Summary</b></font></td>" );
            out.println( "</tr>" );
            
            String functionName = "";
            boolean summary = true;
            
            while ( br.ready() )
            {
                String content = br.readLine();

                while ( summary && null != content && content.indexOf( "summary" ) == -1 )
                {
                    content = br.readLine();
                }
                summary = false;
                if ( null != content && content.indexOf( "/**" ) != -1 )
                {
                    boolean description = true;
                    boolean parameterList = false;
                    boolean useList = false;

                    StringWriter docBuffer = new StringWriter();
                    PrintWriter doc = new PrintWriter( docBuffer );

                    content = br.readLine();
                    while ( null != content && content.indexOf( "*/" ) == -1 )
                    {
                        if ( content.indexOf( "* @" ) != -1 )
                        {
                            if ( ( content.indexOf( "author" ) == -1 ) )
                            {
                                if ( content.indexOf( "param" ) != -1 )
                                {
                                    if ( parameterList == false )
                                    {
                                        parameterList = true;
                                        doc.println( "<font size=\"-1\" face=\"Verdana\"><b>Parameters: </b></font><br>" );
                                    }
                                    doc.print( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" );
                                    doc.println( content.substring( content.indexOf( "* @param" ) + 9 ) );
                                }
                                else if ( content.indexOf( "use" ) != -1 )
                                {
                                    if ( useList == false )
                                    {
                                        useList = true;
                                        doc.println( "<font size=\"-1\" face=\"Verdana\"><b>Uso: </b></font><br>" );
                                    }
                                    doc.print( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" );
                                    doc.println( content.substring( content.indexOf( "* @use" ) + 7 ) );
                                }
                                else if ( content.indexOf( "return" ) != -1 )
                                {
                                    doc.print( "<font size=\"-1\" face=\"Verdana\"><b>Return type: </b></font>" );
                                    doc.print( content.substring( content.indexOf( "* @return" ) + 10 ) );
                                }
                                doc.println( "<br>" );
                            }
                        }
                        else
                        {
                            if ( description )
                            {
                                description = false;
                                doc.println( "<font size=\"-1\" face=\"Verdana\"><b>Description: </b></font>" );
                            }
                            else
                                doc.println( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" );
                            doc.println( content.substring( content.indexOf( "*" ) + 1 ) + "<br>" );
                        }
                        content = br.readLine();
                    }
                    while ( null != content && content.indexOf( "function" ) == -1 )
                    {
                        content = br.readLine();
                    }
                    if ( content.indexOf( "function" ) != -1 )
                    {
                        if ( content.indexOf( "{" ) != -1 )
                        {
                            functionName = content.substring( content.indexOf( "function" ) + 9, content.indexOf( "{" ) );
                        }
                        else
                        {
                            functionName = content.substring( content.indexOf( "function" ) + 9 );
                        }
                    }

                    out.println( "<tr>" );
                    out.println( "<td width=\"30%\" bgcolor=\"#f3f3f3\"><font face=\"Verdana\"><b>" + functionName + 
                                 "</b></font></td>" );
                    out.println( "<td width=\"70%\">" );
                    out.println( docBuffer.getBuffer() );
                    out.println( "</td>" );
                    out.println( "</tr>" );
                }
            }
            out.println( "</table>" );
            out.println( "<a href=\"javascript:history.back()\"><font size=\"+1\">Back</font></a>" );
            out.println( "</body>" );
            out.println( "</html>" );

            out.close();
        }
        catch ( IOException ioe )
        {
            log.error( "IOException: " + ioe.getMessage(), ioe );
        }

        if ( log.isInfoEnabled() )
        {
            log.info( "Html generated with success!" );
        }
    }

    public static void main( String args[] )
        throws Exception
    {

        GenerateHTMLDoc main1 = new GenerateHTMLDoc( new File( args[0] ), args[1] );
    }
}
