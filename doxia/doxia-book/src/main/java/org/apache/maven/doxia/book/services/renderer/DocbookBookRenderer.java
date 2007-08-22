package org.apache.maven.doxia.book.services.renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.apache.maven.doxia.module.docbook.DocBookSink;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * An implementation of <code>BookRenderer</code> for docbook
 * 
 * @plexus.component role-hint="doc-book"
 * 
 * @author Eric Redmond
 */
public class DocbookBookRenderer extends AbstractLogEnabled implements BookRenderer
{
    /**
     * @plexus.requirement
     */
    private Doxia doxia;

    // ----------------------------------------------------------------------
    // BookRenderer Implementation
    // ----------------------------------------------------------------------

    public void renderBook( BookContext context ) throws BookDoxiaException
    {
        BookModel book = context.getBook();

        if ( !context.getOutputDirectory().exists() )
        {
            if ( !context.getOutputDirectory().mkdirs() )
            {
                throw new BookDoxiaException( "Could not make directory: "
                                + context.getOutputDirectory().getAbsolutePath() + "." );
            }
        }

        File bookFile = new File( context.getOutputDirectory(), book.getId() + ".xml" );

        FileWriter fileWriter;

        try
        {
            fileWriter = new FileWriter( bookFile );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while opening file.", e );
        }

        // ----------------------------------------------------------------------
        // Create the Dockbook File
        // ----------------------------------------------------------------------

        // TODO: Write out TOC?

        DocBookSink sink = new DocBookSink( fileWriter, true );

        for ( Iterator it = book.getChapters().iterator(); it.hasNext(); )
        {
            Chapter chapter = (Chapter) it.next();

            renderChapter( fileWriter, chapter, context, sink );
        }

        sink.book_();

        try
        {
            fileWriter.close();
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while closing file.", e );
        }
    }

    private void renderChapter( Writer writer, Chapter chapter, BookContext context, Sink sink )
        throws BookDoxiaException
    {
        for ( Iterator it = chapter.getSections().iterator(); it.hasNext(); )
        {
            Section section = (Section) it.next();

            renderSection( writer, section, context, sink );
        }
    }

    private void renderSection( Writer writer, Section section, BookContext context, Sink sink )
        throws BookDoxiaException
    {
        BookContext.BookFile bookFile = (BookContext.BookFile) context.getFiles().get( section.getId() );

        if ( bookFile == null )
        {
            throw new BookDoxiaException( "No document that matches section with id=" + section.getId() + "." );
        }

        try
        {
            doxia.parse( new FileReader( bookFile.getFile() ), bookFile.getParserId(), sink );
        }
        catch ( ParserNotFoundException e )
        {
            throw new BookDoxiaException( "Parser not found: " + bookFile.getParserId() + ".", e );
        }
        catch ( ParseException e )
        {
            throw new BookDoxiaException(
                                          "Error while parsing document: " + bookFile.getFile().getAbsolutePath() + ".",
                                          e );
        }
        catch ( FileNotFoundException e )
        {
            throw new BookDoxiaException( "Could not find document: " + bookFile.getFile().getAbsolutePath() + ".", e );
        }
    }
}
