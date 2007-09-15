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

import org.apache.maven.doxia.docrenderer.document.DocumentMeta;
import org.apache.maven.doxia.util.HtmlTools;

/**
 * A Doxia Sink that produces an aggregated FO model.
 */
public class FoAggregateSink extends FoSink
{

    /** Counts the current chapter level. */
    private int chapter = 0;

    /** A name for the current document. */
    private String docName;

    /** In fragment mode, some text has to be ignored (title...). */
    private boolean ignoreText;

    /**
     * Constructor.
     *
     * @param writer The writer for writing the result.
     */
    public FoAggregateSink( Writer writer )
    {
        super( writer );
    }

    // TODO page headers, page numbering
    // TODO add FOP compliance mode?

    /** {@inheritDoc} */
    public void head()
    {
        ignoreText = true;
    }

    /** {@inheritDoc} */
    public void head_()
    {
        ignoreText = false;
        newline();
    }

    /** {@inheritDoc} */
    public void title()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void title_()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void author()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void author_()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void date()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void date_()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void body()
    {
        startPageSequence();

        chapter++;

        if ( docName == null )
        {
            // TODO: log.warn( "No document root specified, local links might not be resolved correctly!" )
        }
        else {
            writeStartTag( "block", "id", docName );
        }

    }

    /** {@inheritDoc} */
    public void body_()
    {
        newline();
        writeEndTag( "block" );
        writeEndTag( "flow" );
        writeEndTag( "page-sequence" );

        // reset document name
        docName = null;
    }

    /**
     * Sets the name for the current document. This should allow anchors to be resolved uniquely.
     *
     * @param name the name for the current document.
     */
    public void setDocumentName( String name )
    {
        this.docName = name;

        // prepend "./" and strip extension
        if ( !docName.startsWith( "./" ) )
        {
            this.docName = "./" + docName;
        }

        if ( docName.indexOf( ".", 2 ) != -1)
        {
            this.docName = docName.substring( 0, docName.indexOf( ".", 2 ) );
        }
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------



    /** {@inheritDoc} */
    public void anchor( String name )
    {
        String anchor = "#" + name;

        if ( docName != null )
        {
            anchor = docName + anchor;
        }

        writeStartTag( "inline", "id", anchor );
    }


    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( name.startsWith( "http", 0 ) || name.startsWith( "mailto", 0 )
            || name.startsWith( "ftp", 0 ) )
        {
            // external links
            writeStartTag( "basic-link", "external-destination", HtmlTools.escapeHTML( name ) );
            writeStartTag( "inline", "href.external" );
        }
        else if ( name.startsWith( "./", 0 ) || name.startsWith( "../", 0 ) )
        {
            // internal non-local (ie anchor is not in the same source document) links
            // TODO:  link to another document, construct relative path

            String anchor = name;

            int dot = anchor.indexOf( ".", 2 );

            if ( dot != -1)
            {
                int hash = anchor.indexOf( "#", dot );

                if ( hash != -1 )
                {
                    int dot2 = anchor.indexOf( ".", hash );

                    if ( dot2 != -1)
                    {
                        anchor = anchor.substring( 0, dot ) + anchor.substring( hash, dot2 );
                    }
                    else
                    {
                        anchor = anchor.substring( 0, dot ) + anchor.substring( hash, anchor.length() );
                    }
                }
                else
                {
                    anchor = anchor.substring( 0, dot );
                }
            }

            writeStartTag( "basic-link", "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( "inline", "href.internal" );
        }
        else
        {
            // internal local (ie anchor is in the same source document) links
            String anchor = "#" + name;

            if ( docName != null )
            {
                anchor = docName + anchor;
            }

            writeStartTag( "basic-link", "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( "inline", "href.internal" );
        }
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
        if ( !ignoreText )
        {
            super.writeStartTag( tag, attributeId );
        }
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
        if ( !ignoreText )
        {
            super.writeStartTag( tag, id, name );
        }
    }

    /**
     * Writes an end tag, appending EOL.
     *
     * @param tag The tag name.
     */
    protected void writeEndTag( String tag )
    {
        if ( !ignoreText )
        {
            super.writeEndTag( tag );
        }
    }

    /**
     * Writes a simple tag, appending EOL.
     *
     * @param tag The tag name.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeEmptyTag( String tag, String attributeId )
    {
        if ( !ignoreText )
        {
            super.writeEmptyTag( tag, attributeId );
        }
    }

    /**
     * Writes a text, swallowing any exceptions.
     *
     * @param text The text to write.
     */
    protected void write( String text )
    {
        if ( !ignoreText )
        {
            super.write( text );
        }
    }

    /**
     * Writes a text, appending EOL.
     *
     * @param text The text to write.
     */
    protected void writeln( String text )
    {
        if ( !ignoreText )
        {
            super.writeln( text );
        }
    }

    /**
     * Writes content, escaping special characters.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        if ( !ignoreText )
        {
            super.content( text );
        }
    }

    /** Writes EOL. */
    protected void newline()
    {
        if ( !ignoreText )
        {
            super.newline();
        }
    }

    /** Starts a page sequence. */
    protected void startPageSequence()
    {
        if ( chapter == 0 )
        {
            super.startPageSequence( "0" );
        }
        else
        {
            super.startPageSequence( "auto" );
        }
    }

    protected void regionBefore()
    {
        writeStartTag( "static-content", "flow-name", "xsl-region-before" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( "table-column", "column-width", "5.625in" );
        writeEmptyTag( "table-column", "column-width", "0.625in" );
        writeStartTag( "table-body", null );
        writeStartTag( "table-row", null );
        writeStartTag( "table-cell", null );
        writeStartTag( "block", "header.style" );
        // TODO
        write( "Header text" );
        writeEndTag( "block" );
        writeEndTag( "table-cell" );
        writeStartTag( "table-cell", null );
        writeStartTag( "block", "page.number" );
        writeEmptyTag( "page-number", null );
        writeEndTag( "block" );
        writeEndTag( "table-cell" );
        writeEndTag( "table-row" );
        writeEndTag( "table-body" );
        writeEndTag( "table" );
        writeEndTag( "static-content" );
    }

    protected void regionAfter()
    {
        writeStartTag( "static-content", "flow-name", "xsl-region-after" );
        writeStartTag( "block", "footer.style" );
        // TODO
        write( "Footer text" );
        writeEndTag( "block" );
        writeEndTag( "static-content" );
    }


    public void coverPage( DocumentMeta meta )
    {
        String title = meta.getTitle();
        String author = meta.getAuthor();

        // TODO: remove hard-coded settings

        writeStartTag( "page-sequence", "master-reference", "cover-page" );
        writeStartTag( "flow", "flow-name", "xsl-region-body" );
        writeStartTag( "block", "text-align", "center" );
        //writeStartTag( "table", "table-layout", "fixed" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( "table-column", "column-width", "3.125in" );
        writeEmptyTag( "table-column", "column-width", "3.125in" );
        writeStartTag( "table-body", null );

        writeStartTag( "table-row", "height", "1.5in" );
        writeStartTag( "table-cell", null );
        // TODO: companyLogo
        writeEmptyTag( "block", null );
        writeEndTag( "table-cell" );
        writeStartTag( "table-cell", null );
        // TODO: projectLogo
        writeEmptyTag( "block", null );
        writeEndTag( "table-cell" );
        writeEndTag( "table-row" );

        writeln( "<fo:table-row keep-with-previous=\"always\" height=\"0.014in\">" );
        writeStartTag( "table-cell", "number-columns-spanned", "2" );
        writeStartTag( "block", "line-height", "0.014in" );
        writeEmptyTag( "leader", "chapter.rule" );
        writeEndTag( "block" );
        writeEndTag( "table-cell" );
        writeEndTag( "table-row" );

        writeStartTag( "table-row", "height", "7.447in" );
        writeStartTag( "table-cell", "number-columns-spanned", "2" );
        //writeStartTag( "table", "table-layout", "fixed" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( "table-column", "column-width", "2.083in" );
        writeEmptyTag( "table-column", "column-width", "2.083in" );
        writeEmptyTag( "table-column", "column-width", "2.083in" );

        writeStartTag( "table-body", null );

        writeStartTag( "table-row", null );
        writeStartTag( "table-cell", "number-columns-spanned", "3" );
        writeEmptyTag( "block", null );
        writeEmptyTag( "block", "space-before", "3.2235in" );
        writeEndTag( "table-cell" );
        writeEndTag( "table-row" );

        writeStartTag( "table-row", null );
        writeStartTag( "table-cell", null );
        writeEmptyTag( "block", "space-after", "0.5in" );
        writeEndTag( "table-cell" );

        writeStartTag( "table-cell", "number-columns-spanned", "2", "cover.border.left" );
        writeStartTag( "block", "cover.title" );
        write( title );
        // TODO: version
        writeEndTag( "block" );
        writeEndTag( "table-cell" );
        writeEndTag( "table-row" );

        writeStartTag( "table-row", null );
        writeStartTag( "table-cell", null );
        writeEmptyTag( "block", null );
        writeEndTag( "table-cell" );


        writeStartTag( "table-cell", "number-columns-spanned", "2", "cover.border.left.bottom" );
        writeStartTag( "block", "cover.subtitle" );
        // TODO: sub title (cover type)
        writeEndTag( "block" );
        writeEndTag( "table-cell" );
        writeEndTag( "table-row" );

        writeEndTag( "table-body" );
        writeEndTag( "table" );

        writeEndTag( "table-cell" );
        writeEndTag( "table-row" );

        writeStartTag( "table-row", "height", "0.014in" );
        writeStartTag( "table-cell", "number-columns-spanned", "2" );
        writeln( "<fo:block space-after=\"0.2in\" line-height=\"0.014in\">" );
        writeEmptyTag( "leader", "chapter.rule" );
        writeEndTag( "block" );
        writeEndTag( "table-cell" );
        writeEndTag( "table-row" );

        writeStartTag( "table-row", null );
        writeStartTag( "table-cell", "number-columns-spanned", "2" );
        writeEmptyTag( "block", null );
        writeEmptyTag( "block", "space-before", "0.2in" );
        writeEndTag( "table-cell" );
        writeEndTag( "table-row" );

        writeStartTag( "table-row", "height", "0.3in" );
        writeStartTag( "table-cell", null );
        writeStartTag( "block", "height", "0.3in", "cover.subtitle" );
        write( author );
        writeEndTag( "block" );
        writeEndTag( "table-cell" );

        writeStartTag( "table-cell", null );
        writeStartTag( "block", "height", "0.3in", "cover.subtitle" );
        // TODO: date
        writeEndTag( "block" );
        writeEndTag( "table-cell" );

        writeEndTag( "table-row" );
        writeEndTag( "table-body" );
        writeEndTag( "table" );
        writeEndTag( "block" );
        writeEndTag( "flow" );
        writeEndTag( "page-sequence" );
    }

}
