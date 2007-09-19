package org.apache.maven.doxia.module.fo;

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

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.util.HtmlTools;

/**
 * A Doxia Sink that produces a FO model.
 */
public class FoSink implements Sink
{

    /** System-dependent end-of-line string. */
    private static final String EOL = System.getProperty( "line.separator" );

    /** For writing the result. */
    private final Writer out;

    /** Used to get the current position in numbered lists. */
    private final Stack listStack = new Stack();

    /** Used to get attributes for a given FO element. */
    private final FoConfiguration config;

    /** Counts the current section level. */
    private int section = 0;

    /** Counts the current subsection level. */
    private int subsection = 0;

    /** Counts the current subsubsection level. */
    private int subsubsection = 0;

    /** Drawing borders on table cells. */
    private boolean tableGrid;

    /** Alignment of table cells. */
    private int[] cellJustif;

    /** Current table cell. */
    private int cellCount;

    /** Verbatim flag. */
    private boolean verbatim;

    /**
     * Constructor.
     *
     * @param writer The writer for writing the result.
     */
    public FoSink( Writer writer )
    {
        this.out = writer;
        this.config = new FoConfiguration();
    }

    /**
     * Returns the configuration object of this sink.
     *
     * @return The configuration object of this sink.
     */
    public FoConfiguration getFoConfiguration()
    {
        return config;
    }

    // TODO add FOP compliance mode?

    /** {@inheritDoc} */
    public void head()
    {
        beginDocument();
        startPageSequence( "0" );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        newline();
    }

    /** {@inheritDoc} */
    public void title()
    {
        writeStartTag( "block", "doc.header.title" );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void author()
    {
        writeStartTag( "block", "doc.header.author" );
    }

    /** {@inheritDoc} */
    public void author_()
    {
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void date()
    {
        writeStartTag( "block", "doc.header.date" );
    }

    /** {@inheritDoc} */
    public void date_()
    {
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void body()
    {
        // noop
    }

    /** {@inheritDoc} */
    public void body_()
    {
        newline();
        writeEndTag( "flow" );
        writeEndTag( "page-sequence" );
        endDocument();
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void sectionTitle()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void sectionTitle_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void section1()
    {
        section++;
        subsection = 0;
        subsubsection = 0;
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        onSectionTitle( Sink.SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        onSection_();
    }

    /** {@inheritDoc} */
    public void section2()
    {
        subsection++;
        subsubsection = 0;
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        onSectionTitle( Sink.SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        onSection_();
    }

    /** {@inheritDoc} */
    public void section3()
    {
        subsubsection++;
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        onSectionTitle( Sink.SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        onSection_();
    }

    /** {@inheritDoc} */
    public void section4()
    {
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        onSectionTitle( Sink.SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        onSection_();
    }

    /** {@inheritDoc} */
    public void section5()
    {
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        onSectionTitle( Sink.SECTION_LEVEL_5 );
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        onSection_();
    }

    /** Starts a section/subsection. */
    private void onSection()
    {
        newline();
        writeStartTag( "block", "body.text" );
    }

    /**
     * Starts a section title.
     *
     * @param depth The section level.
     */
    private void onSectionTitle( int depth )
    {
        StringBuffer title = new StringBuffer( 16 );

        title.append( getChapterString() );

        newline();
        if ( depth == Sink.SECTION_LEVEL_1 )
        {
            writeStartTag( "block", "body.h1" );
            title.append( section ).append( "   " );
        }
        else if ( depth == Sink.SECTION_LEVEL_2 )
        {
            writeStartTag( "block", "body.h2" );
            title.append( section ).append( "." );
            title.append( subsection ).append( "   " );
        }
        else if ( depth == Sink.SECTION_LEVEL_3 )
        {
            writeStartTag( "block", "body.h3" );
            title.append( section ).append( "." );
            title.append( subsection ).append( "." );
            title.append( subsubsection ).append( "   " );
        }
        else if ( depth == Sink.SECTION_LEVEL_4 )
        {
            writeStartTag( "block", "body.h4" );
        }
        else
        {
            writeStartTag( "block", "body.h5" );
        }

        write( title.toString() );
    }

    /** Ends a section title. */
    private void onSectionTitle_()
    {
        writeEndTag( "block" );
    }

    /** Ends a section/subsection. */
    private void onSection_()
    {
        writeEndTag( "block" );
    }

    protected void resetSectionCounter()
    {
        this.section = 0;
    }

    protected String getChapterString()
    {
        return "";
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void list()
    {
        newline();
        writeStartTag( "list-block", "list" );
    }

    /** {@inheritDoc} */
    public void list_()
    {
        writeEndTag( "list-block" );
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        writeStartTag( "list-item", "list.item" );
        // TODO customize?
        writeln( "<fo:list-item-label><fo:block>&#8226;</fo:block></fo:list-item-label>" );
        writeStartTag( "list-item-body", "list.item" );
        writeStartTag( "block", null );
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        writeEndTag( "block" );
        writeEndTag( "list-item-body" );
        writeEndTag( "list-item" );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        listStack.push( new NumberedListItem( numbering ) );
        newline();
        writeStartTag( "list-block", "list" );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        listStack.pop();
        writeEndTag( "list-block" );
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        NumberedListItem current = (NumberedListItem) listStack.peek();
        current.next();

        writeStartTag( "list-item", "list.item" );

        writeStartTag( "list-item-label", null );
        writeStartTag( "block", null );
        write( current.getListItemSymbol() );
        writeEndTag( "block" );
        writeEndTag( "list-item-label" );

        writeStartTag( "list-item-body", "list.item" );
        writeStartTag( "block", null );
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        writeEndTag( "block" );
        writeEndTag( "list-item-body" );
        writeEndTag( "list-item" );
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        newline();
        writeStartTag( "block", "dl" );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void definitionListItem()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definitionListItem_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        writeStartTag( "block", "dt" );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void definition()
    {
        newline();
        writeStartTag( "block", "dd" );
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void figure()
    {
        newline();
        writeStartTag( "block", "figure.display" );
        write( "<fo:external-graphic"
            + config.getAttributeSet( "figure.graphics" ) );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String s )
    {
        // TODO: figure out file extension.
        writeln( " src=\"" + s + ".png\"/>" );
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        writeStartTag( "block", "figure.caption" );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        writeStartTag( "block", "normal.paragraph" );
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        this.verbatim = true;
        if ( boxed )
        {
            writeStartTag( "block", "body.source" );
        }
        else
        {
            writeStartTag( "block", "body.pre" );
        }
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        this.verbatim = false;
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        newline();
        writeStartTag( "block", null );
        writeEmptyTag( "leader", "body.rule" );
        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        writeln( "<fo:block break-before=\"page\"/>" );
    }

    /** {@inheritDoc} */
    public void table()
    {
        newline();
        writeStartTag( "block", "table.padding" );

        // <fo:table-and-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeStartTag( "table-and-caption", null );

        writeStartTag( "table", "table.layout" );
    }

    /** {@inheritDoc} */
    public void table_()
    {
        writeEndTag( "table" );

        // <fo:table-and-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeEndTag( "table-and-caption" );

        writeEndTag( "block" );
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )
    {
        this.tableGrid = grid;
        this.cellJustif = justification;

        // FOP hack to center the table, see
        // http://xmlgraphics.apache.org/fop/fo.html#fo-center-table-horizon
        writeln( "<fo:table-column column-width=\"proportional-column-width(1)\"/>" );

        // TODO: calculate width[i]
        for ( int i = 0;  i < cellJustif.length; i++ )
        {
            writeln( "<fo:table-column column-width=\"1in\"/>" );
        }

        writeln( "<fo:table-column column-width=\"proportional-column-width(1)\"/>" );
        writeStartTag( "table-body", null );
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        this.cellJustif = null;
        writeEndTag( "table-body" );
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        // TODO spacer rows
        writeStartTag( "table-row", "table.body.row" );
        this.cellCount = 0;
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        writeEndTag( "table-row" );
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        tableCell( false );
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        // TODO: fop can't handle cell width
        tableCell( false );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        // TODO: how to implement?
        tableCell( true );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        // TODO: fop can't handle cell width
        tableCell( true );
    }

    /**
     * Writes a table cell.
     *
     * @param headerRow Currently not used.
     */
    private void tableCell( boolean headerRow )
    {
         String justif = null;
 
         if ( cellJustif != null )
         {
             switch ( cellJustif[cellCount] )
             {
                 case Parser.JUSTIFY_LEFT:
                     justif = "left";
                     break;
                 case Parser.JUSTIFY_RIGHT:
                     justif = "right";
                     break;
                 case Parser.JUSTIFY_CENTER:
                 default:
                     justif = "center";
                     break;
             }
         }
 
         if ( justif != null )
         {
            // the column-number is needed for the hack to center the table, see tableRows.
            write( "<fo:table-cell column-number=\"" + String.valueOf( cellCount + 2 ) + "\"" );
            if ( tableGrid )
            {
                write( " border-style=\"solid\" border-width=\"0.2mm\"" );
            }
            writeln( config.getAttributeSet( "table.body.cell" ) + ">" );
         }
         else
         {
             writeStartTag( "table-cell", "table.body.cell" );
         }
        writeln( "<fo:block text-align=\"" + justif + "\">" );
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        writeEndTag( "block" );
        writeEndTag( "table-cell" );
        ++cellCount;
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        tableCell_();
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        // <fo:table-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeStartTag( "table-caption", null );

        // TODO: how to implement this otherwise?
        // table-footer doesn't work because it has to be declared before table-body.
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        // <fo:table-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeEndTag( "table-caption" );
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        String anchor = name;

        if ( anchor.startsWith( "#" ) )
        {
            anchor = "#" + HtmlTools.encodeId( anchor.substring( 1 ) );
        }
        else
        {
            anchor = "#" + HtmlTools.encodeId( anchor );
        }

        writeStartTag( "inline", "id", anchor );
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        writeEndTag( "inline" );
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( name.startsWith( "http" ) || name.startsWith( "mailto" )
            || name.startsWith( "ftp" ) )
        {
            writeStartTag( "basic-link", "external-destination", HtmlTools.escapeHTML( name ) );
            writeStartTag( "inline", "href.external" );
        }
        else
        {
            // treat everything else as internal, local (ie anchor is in the same source document)

            String anchor = name;

            if ( anchor.startsWith( "#" ) )
            {
                anchor = "#" + HtmlTools.encodeId( anchor.substring( 1 ) );
            }
            else
            {
                anchor = "#" + HtmlTools.encodeId( anchor );
            }

            writeStartTag( "basic-link", "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( "inline", "href.internal" );
        }
    }

    /** {@inheritDoc} */
    public void link_()
    {
        writeEndTag( "inline" );
        writeEndTag( "basic-link" );
    }

    /** {@inheritDoc} */
    public void italic()
    {
        writeStartTag( "inline", "italic" );
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        writeEndTag( "inline" );
    }

    /** {@inheritDoc} */
    public void bold()
    {
        writeStartTag( "inline", "bold" );
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        writeEndTag( "inline" );
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        writeStartTag( "inline", "monospace" );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        writeEndTag( "inline" );
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        newline();
            writeEmptyTag( "block", null );
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        write( "&#160;" );
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        content( text );
    }

    /** {@inheritDoc} */
    public void rawText( String text )
    {
        write( text );
    }

    /** {@inheritDoc} */
    public void flush()
    {
        try
        {
            out.flush();
        }
        catch ( IOException e )
        {
            // TODO: log
        }
    }

    /** {@inheritDoc} */
    public void close()
    {
        try
        {
            out.close();
        }
        catch ( IOException e )
        {
            // TODO: log
        }
    }

    /**
     * Writes the beginning of a FO document.
     */
    public void beginDocument()
    {
        writeln( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );

        writeln( "<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">" );

        writeStartTag( "layout-master-set", null );

        writeStartTag( "simple-page-master", "layout.master.set.cover-page" );
        writeEmptyTag( "region-body", "layout.master.set.cover-page.region-body" );
        writeEndTag( "simple-page-master" );

        writeStartTag( "simple-page-master", "layout.master.set.toc" );
        writeEmptyTag( "region-body", "layout.master.set.toc.region-body" );
        writeEmptyTag( "region-before", "layout.master.set.toc.region-before" );
        writeEmptyTag( "region-after", "layout.master.set.toc.region-after" );
        writeEndTag( "simple-page-master" );

        writeStartTag( "simple-page-master", "layout.master.set.body" );
        writeEmptyTag( "region-body", "layout.master.set.body.region-body" );
        writeEmptyTag( "region-before", "layout.master.set.body.region-before" );
        writeEmptyTag( "region-after", "layout.master.set.body.region-after" );
        writeEndTag( "simple-page-master" );

        writeEndTag( "layout-master-set" );
    }

    /**
     * Writes the end of a FO document, flushes and closes the stream.
     */
    public void endDocument()
    {
        writeEndTag( "root" );
        flush();
        close();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag name.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeStartTag( String tag, String attributeId )
    {
        String attribs = config.getAttributeSet( attributeId );
        newline();
        write( "<fo:" + tag + attribs + ">" );
    }

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag name.
     * @param id An id to add.
     * @param name The name (value) of the id.
     */
    protected void writeStartTag( String tag, String id, String name )
    {
        newline();
        write( "<fo:" + tag + " " + id + "=\"" + name + "\">" );
    }

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag name.
     * @param id An id to add.
     * @param name The name (value) of the id.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeStartTag( String tag, String id, String name, String attributeId )
    {
        // TODO: check if id is already in attributeId, if yes, override
        String attribs = config.getAttributeSet( attributeId );
        newline();
        write( "<fo:" + tag + " " + id + "=\"" + name + "\"" + attribs + ">" );
    }


    /**
     * Writes an empty tag, prepending EOL.
     *
     * @param tag The tag name.
     * @param id An id to add.
     * @param name The name (value) of the id.
     */
    protected void writeEmptyTag( String tag, String id, String name )
    {
        newline();
        write( "<fo:" + tag + " " + id + "=\"" + name + "\"/>" );
    }


    /**
     * Writes an end tag, appending EOL.
     *
     * @param tag The tag name.
     */
    protected void writeEndTag( String tag )
    {
        writeln( "</fo:" + tag + ">" );
    }

    /**
     * Writes a simple tag, appending EOL.
     *
     * @param tag The tag name.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeEmptyTag( String tag, String attributeId )
    {
        String attribs = config.getAttributeSet( attributeId );
        writeln( "<fo:" + tag + attribs + "/>" );
    }

    /**
     * Writes a text, swallowing any exceptions.
     *
     * @param text The text to write.
     */
    protected void write( String text )
    {
        try
        {
            out.write( text );
        }
        catch ( IOException e )
        {
            // TODO: log
        }
    }

    /**
     * Writes a text, appending EOL.
     *
     * @param text The text to write.
     */
    protected void writeln( String text )
    {
        write( text );
        newline();
    }

    /**
     * Writes content, escaping special characters.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        write( escaped( text, verbatim ) );
    }

    /** Writes EOL. */
    protected void newline()
    {
        write( EOL );
    }

    /**
     * Escapes special characters so that the text can be included in a fo file.
     *
     * @param text The text to process.
     * @param verb In verbatim mode, white space and newlines are escaped.
     * @return The text with special characters escaped.
     */
    public static String escaped( String text, boolean verb )
    {
        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case ' ':
                    if ( verb )
                    {
                        buffer.append( "&#160;" );
                    }
                    else
                    {
                        buffer.append( c );
                    }
                    break;
                case '<':
                    buffer.append( "&lt;" );
                    break;
                case '>':
                    buffer.append( "&gt;" );
                    break;
                case '&':
                    buffer.append( "&amp;" );
                    break;
                case '\u00a9': // copyright
                    buffer.append( "&#169;" );
                    break;
                case '\n':
                    buffer.append( EOL );
                    if ( verb )
                    {
                        buffer.append( "<fo:block/>" + EOL );
                    }
                    break;
                default:
                    buffer.append( c );
            }
        }

        return buffer.toString();
    }

    /** Starts a page sequence. */
    protected void startPageSequence( String initPageNumber )
    {
        writeln( "<fo:page-sequence initial-page-number=\"" + initPageNumber + "\" master-reference=\"body\">" );
        // TODO
        regionBefore( "Header text" );
        regionAfter( "Footer text" );
        writeln( "<fo:flow flow-name=\"xsl-region-body\">" );
        chapterHeading( null, true );
    }

    protected void regionBefore( String headerText )
    {
        // do nothing, overridden by AggregateSink
    }

    protected void regionAfter( String footerText )
    {
        // do nothing, overridden by AggregateSink
    }

    protected void chapterHeading( String headerText, boolean chapterNumber )
    {
        // do nothing, overridden by AggregateSink
    }

}
