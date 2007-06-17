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

import java.io.Writer;
import java.util.Stack;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.LineBreaker;
import org.apache.maven.doxia.parser.Parser;

/**
 * A Doxia Sink that produces a FO model.
 */
public class FoSink implements Sink
{

    /** System-dependent end-of-line string. */
    private static final String EOL = System.getProperty( "line.separator" );

    /** Linebreaker for writing the result. */
    private final LineBreaker out;

    /** Used to get the current position in numbered lists. */
    private final Stack listStack = new Stack();

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

    /** Constructor.
     * @param writer The writer for writing the result.
     */
    public FoSink( Writer writer )
    {
        this.out = new LineBreaker( writer );
    }

    // TODO factor out all attributes and re-use them, configuration file?
    // TODO page headers, page numbering
    // TODO add aggregate mode?
    // TODO add FOP compliance mode?

    /** {@inheritDoc} */
    public void head()
    {
        writeln( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );

        writeln( "<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">" );

        writeln( "<fo:layout-master-set>" );
        writeln( "  <fo:simple-page-master margin-right=\"1in\" margin-left=\"1in\" margin-bottom=\"0.6in\" margin-top=\"0.625in\" page-height=\"11.70in\" page-width=\"8.25in\" master-name=\"body\">" );
        writeln( "    <fo:region-body margin-bottom=\"0.8in\" margin-top=\"0.7in\"/><fo:region-before extent=\"0.35in\"/><fo:region-after extent=\"0.125in\"/>" );
        writeln( "  </fo:simple-page-master>" );
        writeln( "</fo:layout-master-set>" );
        writeln( "" );

        writeln( "<fo:page-sequence initial-page-number=\"1\" master-reference=\"body\">" );
        writeln( "  <fo:flow flow-name=\"xsl-region-body\">" );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        // nothing?
    }

    /** {@inheritDoc} */
    public void title()
    {
        lnwrite( "<fo:block text-align=\"center\" font-family=\"Garamond,serif\" font-size=\"16pt\" space-before.optimum=\"30pt\" space-after.optimum=\"14pt\">" );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        writeln( "</fo:block>" );
    }

    /** {@inheritDoc} */
    public void author()
    {
        lnwrite( "<fo:block text-align=\"center\" font-family=\"Garamond,serif\" font-size=\"12pt\" space-before.optimum=\"20pt\" space-after.optimum=\"14pt\">" );
    }

    /** {@inheritDoc} */
    public void author_()
    {
        writeln( "</fo:block>" );
    }

    /** {@inheritDoc} */
    public void date()
    {
        lnwrite( "<fo:block text-align=\"center\" font-family=\"Garamond,serif\" font-size=\"12pt\" space-before.optimum=\"20pt\" space-after.optimum=\"30pt\">" );
    }

    /** {@inheritDoc} */
    public void date_()
    {
        writeln( "</fo:block>" );
    }

    /** {@inheritDoc} */
    public void body()
    {
        // nothing?
    }

    /** {@inheritDoc} */
    public void body_()
    {
         newline();
         writeln( "  </fo:flow>" );
         writeln( "</fo:page-sequence>" );
         writeln( "</fo:root>" );
         flush();
         close();
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
        onSectionTitle( 1 );
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
        onSectionTitle( 2 );
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
        onSectionTitle( 3 );
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
        onSectionTitle( 4 );
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
        onSectionTitle( 5 );
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
        writeln( "<fo:block font-family=\"Garamond,serif\" font-size=\"11pt\" line-height=\"12pt\" white-space-collapse=\"true\">" );
    }

    /** Starts a section title. */
    private void onSectionTitle( int depth )
    {
        String baseAttribs = "font-family=\"Helvetica,sans-serif\" color=\"#000000\" keep-with-next=\"always\" ";
        String levelAttribs = "";

        StringBuffer title = new StringBuffer( 10 );

        if ( depth == 1 )
        {
            levelAttribs = "font-size=\"12pt\" font-weight=\"bold\" space-before=\"18pt\" space-after=\"6pt\"";
            title.append( section );
            title.append( "   " );
        }
        else if ( depth == 2 )
        {
            levelAttribs = "font-size=\"9.5pt\" font-weight=\"bold\" space-before=\"18pt\" space-after=\"5pt\"";
            title.append( section );
            title.append( "." );
            title.append( subsection );
            title.append( "   " );
        }
        else if ( depth == 3 )
        {
            levelAttribs = "font-size=\"9.5pt\" font-weight=\"bold\" space-before=\"15pt\" space-after=\"3pt\"";
            title.append( section );
            title.append( "." );
            title.append( subsection );
            title.append( "." );
            title.append( subsubsection );
            title.append( "   " );
        }
        else if ( depth == 4 )
        {
            levelAttribs = "font-size=\"9.5pt\" font-weight=\"bold\" space-before=\"9pt\" space-after=\"3pt\"";
        }
        else
        {
            levelAttribs = "font-size=\"9.5pt\" font-weight=\"bold\" font-style=\"italic\" space-after=\"3pt\"";
        }

        lnwrite( "<fo:block " + baseAttribs + levelAttribs + ">" );
        write( title.toString() );
    }

    /** Ends a section title. */
    private void onSectionTitle_()
    {
        writeln( "</fo:block>" );
    }

    /** Ends a section/subsection. */
    private void onSection_()
    {
        writeln( "</fo:block>" );
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void list()
    {
        newline();
        writeln( "<fo:list-block provisional-distance-between-starts=\"1em\" provisional-label-separation=\"1em\" start-indent=\"inherited-property-value(start-indent)\" space-before.optimum=\"10pt\">" );
    }

    /** {@inheritDoc} */
    public void list_()
    {
        writeln( "</fo:list-block>" );
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        writeln( "  <fo:list-item space-before=\"0.15em\" space-after=\"0.25em\" start-indent=\"inherited-property-value(start-indent) + .5em\">" );
        writeln( "    <fo:list-item-label><fo:block>&#8226;</fo:block></fo:list-item-label>" );
        writeln( "    <fo:list-item-body start-indent=\"body-start()\">" );
        write( "      <fo:block>" );
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        writeln( "      </fo:block>" );
        writeln( "    </fo:list-item-body>" );
        writeln( "  </fo:list-item>" );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        listStack.push( new NumberedListItem( numbering ) );
        newline();
        writeln( "<fo:list-block provisional-distance-between-starts=\"1em\" provisional-label-separation=\"1em\" start-indent=\"inherited-property-value(start-indent)\" space-before.optimum=\"10pt\">" );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        listStack.pop();
        writeln( "</fo:list-block>" );
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        NumberedListItem current = (NumberedListItem) listStack.peek();
        current.next();

        lnwrite( "<fo:list-item space-before=\"0.15em\" space-after=\"0.25em\" start-indent=\"inherited-property-value(start-indent) + .5em\">" );
        lnwrite( "<fo:list-item-label><fo:block>" );
        write( current.getListItemSymbol() );
        write( "</fo:block></fo:list-item-label>" );
        lnwrite( "<fo:list-item-body start-indent=\"body-start()\"><fo:block>" );
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        writeln( "</fo:block></fo:list-item-body></fo:list-item>" );
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        newline();
        writeln( "<fo:block start-indent=\"1em\" end-indent=\"1em\" space-before.optimum=\"10pt\">" );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        writeln( "</fo:block>" );
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
        writeln( "  <fo:block start-indent=\"1em\" end-indent=\"1em\" font-weight=\"bold\">" );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        writeln( "  </fo:block>" );
    }

    /** {@inheritDoc} */
    public void definition()
    {
        newline();
        writeln( "  <fo:block space-before=\"0.6em\" space-after=\"0.6em\" start-indent=\"inherited-property-value(start-indent) + 1em\" end-indent=\"inherited-property-value(start-indent) + 1em\">" );
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        writeln( "  </fo:block>" );
    }

    /** {@inheritDoc} */
    public void figure()
    {
        newline();
        writeln( "  <fo:block display-align=\"center\" text-align=\"center\">" );
        write( "    <fo:external-graphic height=\"auto\" width=\"auto\" content-height=\"auto\" content-width=\"auto\" display-align=\"center\" text-align=\"center\"" );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        writeln( "  </fo:block>" );
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
        lnwrite( "  <fo:block keep-with-previous=\"always\" text-align=\"center\" font-family=\"Garamond,serif\" font-size=\"10pt\" font-style=\"italic\" space-before.optimum=\"20pt\" space-after.optimum=\"30pt\"> " );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        writeln( "  </fo:block>" );
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        lnwrite( "  <fo:block font-family=\"Garamond,serif\" font-size=\"11pt\" line-height=\"12pt\" white-space-collapse=\"true\" space-before.optimum=\"10pt\">" );
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        writeln( "  </fo:block>" );
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        this.verbatim = true;
        StringBuffer buffer = new StringBuffer( 512 );
        buffer.append( "  <fo:block font-family=\"monospace\" font-size=\"10pt\"" );

        if ( boxed )
        {
            buffer.append( " wrap-option=\"wrap\"" );
            buffer.append( " white-space-collapse=\"false\"" );
            buffer.append( " color=\"black\"" );
            buffer.append( " border-style=\"solid\"" );
            buffer.append( " border-width=\"0.5pt\"" );
            buffer.append( " border-color=\"#454545\"" );
            buffer.append( " padding-before=\"0.25em\"" );
            buffer.append( " padding-after=\"0.25em\"" );
            buffer.append( " padding-start=\"0.25em\"" );
            buffer.append( " padding-end=\"0.25em\"" );
            buffer.append( " start-indent=\"inherited-property-value(start-indent) + 2.5em\"" );
            buffer.append( " end-indent=\"inherited-property-value(end-indent) + 3em\"" );
            buffer.append( " space-before=\"0.75em\"" );
            buffer.append( " space-after=\"1em\"" );
        }
        buffer.append( ">" );

        writeln( buffer.toString() );
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        this.verbatim = false;
        writeln( "  </fo:block>" );
    }

    /** {@inheritDoc} */
    /** {@inheritDoc} */
    public void horizontalRule()
    {
        newline();
        writeln( "  <fo:block line-height=\"1pt\"><fo:leader leader-length.optimum=\"100%\" leader-pattern=\"rule\" rule-thickness=\"0.5pt\" color=\"black\"/></fo:block>" );
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        writeln( "  <fo:block break-before=\"page\"/>" );
    }

    /** {@inheritDoc} */
    public void table()
    {
        newline();
        writeln( "  <fo:block padding-before=\"9pt\" padding-after=\"12pt\">" );
        // <fo:table-and-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeln( "  <fo:table-and-caption>" );
        // table-layout="auto" is not supported by FOP 0.93
        writeln( "    <fo:table table-omit-footer-at-break=\"false\" table-layout=\"fixed\" width=\"100%\">" );
    }

    /** {@inheritDoc} */
    public void table_()
    {
        writeln( "    </fo:table>" );
        // <fo:table-and-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeln( "  </fo:table-and-caption>" );
        writeln( "  </fo:block>" );
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )
    {
        this.tableGrid = grid;
        this.cellJustif = justification;
        // FOP hack to center the table, see http://xmlgraphics.apache.org/fop/fo.html#fo-center-table-horizon
        writeln( "      <fo:table-column column-width=\"proportional-column-width(1)\"/>" );
        // TODO: calculate width[i]
        for ( int i = 0;  i < cellJustif.length; i++ )
        {
            writeln( "      <fo:table-column column-width=\"1in\"/>" );
        }
        writeln( "      <fo:table-column column-width=\"proportional-column-width(1)\"/>" );
        writeln( "<fo:table-body>" );
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        this.cellJustif = null;
        writeln( "</fo:table-body>" );
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        // TODO spacer rows
        writeln( "<fo:table-row keep-together=\"always\" keep-with-next=\"always\">" );
        this.cellCount = 0;
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        writeln( "</fo:table-row>" );
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        tableCell( false );
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        // TODO: how to implement?
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        // nop
    }

    /** Writes a table cell.
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
            writeln( " padding-start=\"2.5pt\" padding-end=\"5pt\" padding-before=\"4pt\" padding-after=\"1.5pt\">" );
         }
         else
         {
             writeln( "<fo:table-cell padding-start=\"2.5pt\" padding-end=\"5pt\" padding-before=\"4pt\" padding-after=\"1.5pt\">" );
         }
        writeln( "<fo:block text-align=\"" + justif + "\">" );
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        writeln( "</fo:block>" );
        writeln( "</fo:table-cell>" );
        ++cellCount;
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        // <fo:table-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //lnwrite( "<fo:table-caption>" );
        // TODO: how to implement this otherwise?
        // table-footer doesn't work because it has to be declared before table-body.
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        // <fo:table-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeln( "  </fo:table-caption>" );
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        lnwrite( "  <fo:inline id=\"" + name + "\">" );
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        write( "  </fo:inline>" );
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( name.startsWith( "http", 0 ) || name.startsWith( "mailto", 0 ) || name.startsWith( "ftp", 0 ) )
        {
            lnwrite( "  <fo:basic-link external-destination=\"" + name + "\">" );
        }
        else if ( name.startsWith( "#", 0 ) )
        {
            lnwrite( "  <fo:basic-link internal-destination=\"" + name + "\">" );
        }
        else
        {
            // TODO: aggregate mode: link to another document, construct relative path
            lnwrite( "<fo:basic-link internal-destination=\"" + name + "\">" );
        }
        write( "    <fo:inline color=\"green\">" );
    }

    /** {@inheritDoc} */
    public void link_()
    {
        write( "   </fo:inline></fo:basic-link>" );
    }

    /** {@inheritDoc} */
    public void italic()
    {
        lnwrite( "  <fo:inline font-style=\"italic\">" );
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        writeln( "  </fo:inline>" );
    }

    /** {@inheritDoc} */
    public void bold()
    {
        lnwrite( "  <fo:inline font-weight=\"bold\">" );
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        writeln( "  </fo:inline>" );
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        lnwrite( "  <fo:inline font-family=\"monospace\" font-size=\"10pt\">" );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        writeln( "  </fo:inline>" );
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        newline();
        writeln( "<fo:block/>" );
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
        // nop
    }

    /** {@inheritDoc} */
    public void flush()
    {
        out.flush();
    }

    /** {@inheritDoc} */
    public void close()
    {
        out.close();
    }


    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void write( String text )
    {
        out.write( text, true );
    }

    private void writeln( String text )
    {
        out.write( text, true );
        newline();
    }

    private void lnwrite( String text )
    {
        newline();
        out.write( text, true );
    }

    private void content( String text )
    {
        out.write( escaped( text ), true );
    }

    private void newline()
    {
        out.write( EOL, false );
    }

    private String escaped( String text )
    {
        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case ' ':
                    if ( verbatim )
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
                    if ( verbatim )
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


}
