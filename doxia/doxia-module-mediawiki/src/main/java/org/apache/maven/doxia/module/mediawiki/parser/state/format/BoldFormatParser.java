package org.apache.maven.doxia.module.mediawiki.parser.state.format;

import org.apache.maven.doxia.module.mediawiki.parser.ParsingContext;
import org.apache.maven.doxia.module.mediawiki.parser.state.AbstractStateParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

public class BoldFormatParser
    extends AbstractStateParser
{

    private static final char[] END_MARKER = "'''".toCharArray();

    protected void addContent( char[] buffer, int contentSize, Sink sink, ParsingContext options )
        throws ParseException
    {
        sink.text( String.copyValueOf( buffer, 0, contentSize ) );
    }

    protected char[] getEndMarker()
    {
        return END_MARKER;
    }

    protected String getStateName()
    {
        return "Bold Format";
    }

    public void beginState( Sink sink, ParsingContext options )
        throws ParseException
    {
        sink.bold();
    }

    public void endState( Sink sink, ParsingContext options )
        throws ParseException
    {
        sink.bold_();
    }

}
