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

/**
 * Class that mounts a Document in HTML as document
 *
 * @version $Id$
 */
public class GenerateHTMLDoc
{

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( GenerateHTMLDoc.class );

    private static PrintWriter out;

    private static BufferedReader br;

    private static int functionCount = 0;

    private static String functionName = "";

    private static boolean parameterList = false;

    private static boolean useList = false;

    private boolean summary = true;

    private boolean description = true;

    public GenerateHTMLDoc( File fis, String destDir )
    {
        try
        {
            String nomeArquivo = fis.getName();
            try
            {
                out = new PrintWriter( new FileWriter( destDir + nomeArquivo.substring( 0, nomeArquivo.indexOf( "." ) ) + ".htm" ) );
                br = new BufferedReader( new FileReader( fis ) );
            }
            catch ( FileNotFoundException fnfe )
            {
                log.error( "FileNotFoundException: " + fnfe.getMessage(), fnfe );
            }

            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<style type=\"text/css\">" );
            out.println( ".TableHeadingColor     { background: #CCCCFF } /* Dark mauve */" );
            out.println( ".NavBarCell1    { background-color:#EEEEFF;}/* Light mauve */" );
            out.println( "</style>" );
            out.println( "<title>Javascript code documentation</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<H2>Filename: " + nomeArquivo + "</H2>" );
            out.println( "<br>" );
            out.println( "<br>" );
            out.println( "<TABLE BORDER=\"1\" CELLPADDING=\"3\" CELLSPACING=\"0\" WIDTH=\"100%\">" );
            out.println( "<TR CLASS=\"TableHeadingColor\">" );
            out.println( "<TD ALIGN=\"left\" colspan=\"2\"><FONT SIZE=\"+2\">" );
            out.println( "<B>Function Summary</B></FONT></TD>" );
            out.println( "</TR>" );
            
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
                    out.println( "<TR>" );
                    out.println( "<TD WIDTH=\"30%\" BGCOLOR=\"#f3f3f3\"><font face=\"Verdana\"><b><span id=\"Function"
                        + functionCount + "\"></span></b></font></TD>" );
                    out.println( "<TD WIDTH=\"70%\">" );
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
                                        out.println( "<font size=\"-1\" face=\"Verdana\"><b>Parameters: </b></font>" );
                                        out.println( "<BR>" );
                                    }
                                    out.println( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" );
                                    out.println( content.substring( content.indexOf( "* @param" ) + 9 ) );
                                }
                                else if ( content.indexOf( "use" ) != -1 )
                                {
                                    if ( useList == false )
                                    {
                                        useList = true;
                                        out.println( "<font size=\"-1\" face=\"Verdana\"><b>Uso: </b></font><BR>" );
                                    }
                                    out.print( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" );
                                    out.println( content.substring( content.indexOf( "* @use" ) + 7 ) );
                                }
                                else if ( content.indexOf( "return" ) != -1 )
                                {
                                    out.print( "<font size=\"-1\" face=\"Verdana\"><b>Return type: </b></font>" );
                                    out.print( content.substring( content.indexOf( "* @return" ) + 10 ) );
                                }
                                out.println( "<BR>" );
                            }
                        }
                        else
                        {
                            if ( description )
                            {
                                description = false;
                                out.println( "<font size=\"-1\" face=\"Verdana\"><b>Description: </b></font>" );
                            }
                            else
                                out.println( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" );
                            out.println( content.substring( content.indexOf( "*" ) + 1 ) + "<BR>" );
                        }
                        content = br.readLine();
                    }
                    description = true;
                    parameterList = false;
                    useList = false;
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
                    out.println( "</TD>" );
                    out.println( "<script>document.all.Function" + functionCount + ".innerHTML = \"" + functionName
                        + "\"; </script>" );
                    functionCount++;
                    out.println( "</TR>" );
                }
            }
            out.println( "</TABLE>" );
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
