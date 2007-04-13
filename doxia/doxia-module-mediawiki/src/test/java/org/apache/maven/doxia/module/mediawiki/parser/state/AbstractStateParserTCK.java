package org.apache.maven.doxia.module.mediawiki.parser.state;

import org.apache.maven.doxia.module.mediawiki.parser.ParsingContext;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

import junit.framework.TestCase;

public abstract class AbstractStateParserTCK
    extends TestCase
{

    public abstract void testConsume_EndMarkerNotPresent()
        throws ParseException;

    public abstract void testConsume_EndMarkerAtBeginningOfBuffer()
        throws ParseException;

    public abstract void testConsume_EndMarkerAtEndOfBuffer()
        throws ParseException;

    public abstract void testConsume_EndMarkerInMiddleOfBuffer()
        throws ParseException;

    public abstract void testConsume_EndMarkerPartialAtEndOfBuffer()
        throws ParseException;

    protected void assertConsume( StateParser parser, String testInput, int testResult, Sink sink, ParsingContext options )
        throws ParseException
    {
        int result = parser.consume( testInput, testInput.length(), sink, options );

        assertEquals( "Remaining unconsumed input is incorrect.", testResult, result );
    }

}
