package org.apache.maven.doxia.linkcheck.report;

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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.doxia.linkcheck.LinkCheckResult;
import org.apache.maven.doxia.module.xhtml.XhtmlSink;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Generate a report based on LinkCheckResults.
 */
public class LinkCheckReportGenerator
{
    /** The number of checked files. */
    private int nFiles;

    /** The number of checked links. */
    private int nLinks;

    /** The number of reported errors. */
    private int nErrors;

    /** The number of reported warnings. */
    private int nWarnings;

    /** The number of files with unsuccessfull checks. */
    private int nUnsuccessfull;

    /** The result of parsing the report file. */
    private LinkCheckResults results = new LinkCheckResults();

    /**
     * Constructor: parses the rawReportFile.
     * An IllegalArgumentException is thrown if the file does not exist.
     *
     * @param rawReportFile the file that contains the linkcheck results in xml format.
     */
    public LinkCheckReportGenerator( File rawReportFile )
    {
        if ( rawReportFile == null )
        {
            throw new NullPointerException( "rawReportFile can't be null!" );
        }

        try
        {
            parseReportFile( rawReportFile );
        }
        catch ( ParseException ex )
        {
            // TODO
        }
    }

    /**
     * Generate an html report, using a simple XhtmlSink.
     *
     * @param outFile the file to write the report to.
     * @throws IOException if the FileWriter cannot be created.
     */
    public void generateHTMLReport( File outFile )
        throws IOException
    {
        generateReport( new XhtmlSink( new FileWriter( outFile ) ) );
    }

    /**
     * Dump a report into a sink.
     *
     * @param sink the sink to receive the report.
     */
    public void generateReport( Sink sink )
    {
        doHeader( sink );

        doBody( sink );

        sink.flush();
    }

    /**
     * Generate header.
     *
     * @param sink the sink to receive the report.
     */
    private void doHeader( Sink sink )
    {
        sink.head();
        sink.title();
        sink.text( "Link Check Report" );
        sink.title_();
        sink.head_();
    }

    /**
     * Generate body.
     *
     * @param sink the sink to receive the report.
     */
    private void doBody( Sink sink )
    {
        sink.body();

        doSummary( sink );

        if ( nUnsuccessfull != 0 )
        {
            doDetails( sink );
        }

        sink.body_();
    }

    /**
     * Generate summary section.
     *
     * @param sink the sink to receive the report.
     */
    private void doSummary( Sink sink )
    {
        sink.section1();

        sink.sectionTitle1();
        sink.text( "Link Check Report" );
        sink.sectionTitle1_();

        sink.paragraph();
        sink.bold();
        sink.text( Integer.toString( nFiles ) );
        sink.bold_();
        sink.text( " file(s) and " );
        sink.bold();
        sink.text( Integer.toString( nLinks ) );
        sink.bold_();
        sink.text( " links checked. " );
        sink.bold();
        sink.text( Integer.toString( nErrors ) );
        sink.bold_();
        sink.text( " error(s) and " );
        sink.bold();
        sink.text( Integer.toString( nWarnings ) );
        sink.bold_();
        sink.text( " warning(s) reported in " );
        sink.bold();
        sink.text( Integer.toString( nUnsuccessfull ) );
        sink.bold_();
        sink.text( " file(s)." );
        sink.paragraph_();

        sink.section1_();
    }


    /**
     * Generate details section.
     *
     * @param sink the sink to receive the report.
     */
    private void doDetails( Sink sink )
    {
        int[] justify =
        {
             Parser.JUSTIFY_LEFT, Parser.JUSTIFY_LEFT
        };

        sink.section1();

        sink.sectionTitle1();
        sink.text( "Details" );
        sink.sectionTitle1_();

        // loop over files
        ArrayList fileList = new ArrayList( this.results.getFiles().keySet() );

        for ( Iterator files = fileList.iterator(); files.hasNext(); )
        {

            String filename = (String) files.next();

            sink.section2();

            sink.sectionTitle2();
            sink.text( filename );
            sink.sectionTitle2_();

            sink.table();

            sink.tableRows( justify, true );

            sink.tableRow();

            sink.tableHeaderCell();
            sink.text( "Error" );
            sink.tableHeaderCell_();

            sink.tableHeaderCell();
            sink.text( "Message" );
            sink.tableHeaderCell_();

            sink.tableRow_();

            // loop over errors/warnings
            List unsuccessfuls = results.getUnsuccessful( filename );

            for ( Iterator unsuccessful = unsuccessfuls.iterator(); unsuccessful.hasNext(); )
            {
                LinkCheckResult lcr = (LinkCheckResult) unsuccessful.next();

                sink.tableRow();

                sink.tableCell();

                if ( "error".equals( lcr.getStatus() ) )
                {
                    iconError( sink );
                }
                else if ( "warning".equals( lcr.getStatus() ) )
                {
                    iconWarning( sink );
                }
                else if ( "valid".equals( lcr.getStatus() ) )
                {
                    iconInfo( sink );
                }

                sink.tableCell_();

                sink.tableCell();

                sink.text( lcr.getTarget() + " - " );
                sink.italic();
                sink.text( lcr.getErrorMessage() );
                sink.italic_();

                sink.tableCell_();

                sink.tableRow_();
            }

            sink.tableRows_();

            sink.table_();

            sink.section2_();
        }

        sink.section1_();

    }

    /**
     * Generate an info icon.
     *
     * @param sink the sink to receive the report.
     */
    private void iconInfo( Sink sink )
    {
        sink.figure();
        sink.figureGraphics( "images/icon_info_sml.gif" );
        sink.figure_();
    }

    /**
     * Generate a warning icon.
     *
     * @param sink the sink to receive the report.
     */
    private void iconWarning( Sink sink )
    {
        sink.figure();
        sink.figureGraphics( "images/icon_warning_sml.gif" );
        sink.figure_();
    }

    /**
     * Generate an error icon.
     *
     * @param sink the sink to receive the report.
     */
    private void iconError( Sink sink )
    {
        sink.figure();
        sink.figureGraphics( "images/icon_error_sml.gif" );
        sink.figure_();
    }

    /**
     * Parse the report file.
     *
     * @param rawReportFile the report file.
     * @throws ParseException if there's a problem parsing the file.
     */
    private void parseReportFile( File rawReportFile )
        throws ParseException
    {
        try
        {
            XmlPullParser parser = new MXParser();

            parser.setInput( new FileReader( rawReportFile ) );

            parseXml( parser );
        }
        catch ( XmlPullParserException ex )
        {
            throw new ParseException( "Error parsing the model: " + ex.getMessage(), ex );
        }
        catch ( FileNotFoundException ex )
        {
            throw new ParseException( "rawReportFile does not exist! " + ex.getMessage(), ex );
        }
    }

    /**
     * Parse the report file with the given XmlPullParser.
     *
     * @param parser A parser.
     * @throws XmlPullParserException if there's a problem parsing the file.
     */
    private void parseXml( XmlPullParser parser )
        throws XmlPullParserException
    {
        List list = new ArrayList();

        String text = "";

        String fileName = "";

        LinkCheckResult lcr = new LinkCheckResult();

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( "file".equals( parser.getName() ) )
                {
                    list = new ArrayList();
                }
                else if ( "result".equals( parser.getName() ) )
                {
                    lcr = new LinkCheckResult();
                }
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                if ( "file".equals( parser.getName() ) )
                {
                    nFiles++;

                    this.results.addUnsuccessful( fileName, list );
                }
                else if ( "name".equals( parser.getName() ) )
                {
                    fileName = text;
                }
                else if ( "successful".equals( parser.getName() ) )
                {
                    nLinks = nLinks + Integer.parseInt( text );
                }
                else if ( "unsuccessful".equals( parser.getName() ) )
                {
                    int invalid = Integer.parseInt( text );

                    nLinks = nLinks + invalid;

                    if ( invalid != 0 )
                    {
                        nUnsuccessfull++;
                    }
                }
                else if ( "result".equals( parser.getName() ) )
                {
                    list.add( lcr );
                }
                else if ( "status".equals( parser.getName() ) )
                {
                    lcr.setStatus( text );

                    if ( "error".equals( text ) )
                    {
                        nErrors++;
                    }
                    else if ( "warning".equals( text ) )
                    {
                        nWarnings++;
                    }
                }
                else if ( "target".equals( parser.getName() ) )
                {
                    lcr.setTarget( text );
                }
                else if ( "errorMessage".equals( parser.getName() ) )
                {
                    lcr.setErrorMessage( text );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                text = parser.getText();
            }

            try
            {
                eventType = parser.next();
            }
            catch ( IOException io )
            {
                throw new XmlPullParserException( "Cannot parse report file: " + io.getMessage(), parser, io );
            }
        }
    }



}
