package org.apache.maven.doxia.module.mediawiki.parser.state.link;

import org.apache.maven.doxia.module.mediawiki.parser.ParsingContext;
import org.apache.maven.doxia.module.mediawiki.parser.state.AbstractStateParserTCK;
import org.apache.maven.doxia.module.mediawiki.parser.state.StateParser;
import org.apache.maven.doxia.module.mediawiki.parser.testutil.MockManager;
import org.apache.maven.doxia.module.mediawiki.parser.testutil.ParserOptionsMockAndControl;
import org.apache.maven.doxia.module.mediawiki.parser.testutil.SinkMockAndControl;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

import java.net.MalformedURLException;

public class InternalLinkParserTest
    extends AbstractStateParserTCK
{
    
    public void testConsume_EndMarkerNotPresent()
        throws ParseException
    {
        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "internal-url", "link", mm );
        ParserOptionsMockAndControl optionsMac = newOptionsMock( null, null, mm );
        
        mm.replayAll();
        
        StateParser parser = newParser();
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        assertConsume( parser, "internal-url|link", -1, sink, options );
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

    private InternalLinkParser newParser()
    {
        return new InternalLinkParser();
    }

    public void testConsume_EndMarkerAtBeginningOfBuffer()
        throws ParseException
    {
        StateParser parser = newParser();

        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "internal-url", "link", mm );
        ParserOptionsMockAndControl optionsMac = newOptionsMock( null, null, mm );
        
        mm.replayAll();
        
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        String startLink = "internal-url|link";
        
        parser.consume( startLink, startLink.length(), sink, options );
        assertConsume( parser, "]] other text", 2, sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
        
    }

    public void testConsume_EndMarkerAtEndOfBuffer()
        throws ParseException
    {
        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "internal-url", "link", mm );
        ParserOptionsMockAndControl optionsMac = newOptionsMock( null, null, mm );
        
        mm.replayAll();
        
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        StateParser parser = newParser();
        
        assertConsume( parser, "internal-url|link]]", -1, sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }

    public void testConsume_EndMarkerInMiddleOfBuffer()
        throws ParseException
    {
        StateParser parser = newParser();

        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "internal-url", "link", mm );
        ParserOptionsMockAndControl optionsMac = newOptionsMock( null, null, mm );
        
        mm.replayAll();
        
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        String link = "internal-url|link]]";

        assertConsume( parser, link + " other text", link.length(), sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }

    public void testConsume_EndMarkerPartialAtEndOfBuffer()
        throws ParseException
    {
        StateParser parser = newParser();

        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "internal-url", "link", mm );
        ParserOptionsMockAndControl optionsMac = newOptionsMock( null, null, mm );
        
        mm.replayAll();
        
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        String startLink = "internal-url|link]";
        
        parser.consume( startLink, startLink.length(), sink, options );
        assertConsume( parser, "] other text", 1, sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }
    
    public void testAddToDocument_SimpleAdd() throws ParseException, MalformedURLException
    {
        StateParser parser = newParser();

        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "http://www.site.com/internal-url", "link", mm );
        ParserOptionsMockAndControl optionsMac = newOptionsMock( "http://www.site.com", "_", mm );
        
        mm.replayAll();
        
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        String startLink = "internal-url|link]]";
        parser.consume( startLink, startLink.length(), sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }

    private ParserOptionsMockAndControl newOptionsMock( String baseUrl, String urlSpaceToken, MockManager mm )
    {
        ParserOptionsMockAndControl optionsMac = new ParserOptionsMockAndControl();
        
        mm.register( optionsMac );
        
        optionsMac.getBaseUrl( baseUrl );
        optionsMac.getUrlSpaceToken( urlSpaceToken );
        
        return optionsMac;
    }

    public void testAddToDocument_NoLabel() throws ParseException, MalformedURLException
    {
        StateParser parser = newParser();

        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "http://www.site.com/internal-url", "internal-url", mm );
        ParserOptionsMockAndControl optionsMac = newOptionsMock( "http://www.site.com", "_", mm );
        
        mm.replayAll();
        
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        String startLink = "internal-url]]";
        parser.consume( startLink, startLink.length(), sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }

    public void testAddToDocument_HrefWithSpace() throws ParseException, MalformedURLException
    {
        StateParser parser = newParser();

        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = newSinkMock( "http://www.site.com/internal_url", "link", mm );
        ParserOptionsMockAndControl optionsMac = newOptionsMock( "http://www.site.com", "_", mm );
        
        mm.replayAll();
        
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        String startLink = "internal url|link]]";
        parser.consume( startLink, startLink.length(), sink, options );
        parser.endState( sink, options );
        
        mm.verifyAll();
    }

    public void testAddToDocument_MissingHref() throws ParseException
    {
        StateParser parser = newParser();

        MockManager mm = new MockManager();
        
        SinkMockAndControl sinkMac = new SinkMockAndControl();
        mm.register( sinkMac );
        
        ParserOptionsMockAndControl optionsMac = newOptionsMock( null, null, mm );
        
        optionsMac.getLine( 1 );
        
        mm.replayAll();
        
        Sink sink = sinkMac.getSink();
        ParsingContext options = optionsMac.getOptions();
        
        String startLink = "|link]]";
        parser.consume( startLink, startLink.length(), sink, options );
        
        try
        {
            parser.endState( sink, options );
            
            fail( "Failed to throw exception when url is missing." );
        }
        catch ( ParseException e )
        {
            // expected
        }
        
        mm.verifyAll();
    }

}
