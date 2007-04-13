package org.apache.maven.doxia.module.mediawiki.parser.state.link;

import org.apache.maven.doxia.module.mediawiki.parser.ParsingContext;
import org.apache.maven.doxia.module.mediawiki.parser.state.AbstractStateParserTCK;
import org.apache.maven.doxia.module.mediawiki.parser.state.StateParser;
import org.apache.maven.doxia.module.mediawiki.parser.testutil.MockManager;
import org.apache.maven.doxia.module.mediawiki.parser.testutil.ParserOptionsMockAndControl;
import org.apache.maven.doxia.module.mediawiki.parser.testutil.SinkMockAndControl;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

public class ExternalLinkParserTest
    extends AbstractStateParserTCK
{

    public void testConsume_EndMarkerNotPresent()
        throws ParseException
    {
        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "http://www.google.com", "link", mm );
        ParserOptionsMockAndControl optionsMac = new ParserOptionsMockAndControl();
        
        mm.register( optionsMac );
        
        mm.replayAll();
        
        StateParser parser = newParser();
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        assertConsume( parser, "http://www.google.com link", -1, sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }

    private SinkMockAndControl newSinkMock( String href, String label, MockManager mm )
    {
        SinkMockAndControl mac = new SinkMockAndControl();
        
        mm.register( mac );
        
        mac.link( href );
        mac.text( label );
        mac.link_();
        
        return mac;
    }

    private ExternalLinkParser newParser()
    {
        return new ExternalLinkParser();
    }

    public void testConsume_EndMarkerAtBeginningOfBuffer()
        throws ParseException
    {
        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "http://www.google.com", "link", mm );
        ParserOptionsMockAndControl optionsMac = new ParserOptionsMockAndControl();
        
        mm.register( optionsMac );
        
        mm.replayAll();
        
        StateParser parser = newParser();
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        String startLink = "http://www.google.com link";
        parser.consume( startLink, startLink.length(), sink, options );
        
        assertConsume( parser, "] other text", 1, sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }

    public void testConsume_EndMarkerAtEndOfBuffer()
        throws ParseException
    {
        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "http://www.google.com", "link", mm );
        ParserOptionsMockAndControl optionsMac = new ParserOptionsMockAndControl();
        
        mm.register( optionsMac );
        
        mm.replayAll();
        
        StateParser parser = newParser();
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        assertConsume( parser, "http://www.google.com link]", -1, sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }

    public void testConsume_EndMarkerInMiddleOfBuffer()
        throws ParseException
    {
        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "http://www.google.com", "link", mm );
        ParserOptionsMockAndControl optionsMac = new ParserOptionsMockAndControl();
        
        mm.register( optionsMac );
        
        mm.replayAll();
        
        StateParser parser = newParser();
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        String link = "http://www.google.com link]";
        
        assertConsume( parser, link + " other text", link.length(), sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }

    public void testConsume_EndMarkerPartialAtEndOfBuffer()
        throws ParseException
    {
        System.out.println( "SKIPPED: single-character end marker." );
    }

}
