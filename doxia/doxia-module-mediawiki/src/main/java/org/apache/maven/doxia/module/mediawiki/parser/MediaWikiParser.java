package org.apache.maven.doxia.module.mediawiki.parser;

import org.apache.maven.doxia.module.common.ByLineReaderSource;
import org.apache.maven.doxia.module.mediawiki.parser.state.StateFactory;
import org.apache.maven.doxia.module.mediawiki.parser.state.StateParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import java.io.Reader;
import java.util.Stack;

public class MediaWikiParser
    implements Parser, LogEnabled
{

    private Logger logger;

    // NOTE: set this to the longest start-marker in the grammar!
    private static final int LOOKAHEAD_LEN = 2;

    private Stack states = new Stack();

    private StateParser currentState;

    private StateFactory stateFactory;

    public void reset()
    {
        currentState = null;
        states.clear();
    }

    public void parse( Reader reader, Sink sink )
        throws ParseException
    {
        // punt on the options.
        parse( reader, sink, new DefaultParserOptions() );
    }

    public void parse( Reader reader, Sink sink, ParsingContext options )
        throws ParseException
    {
        // if null, punt.
        if ( options == null )
        {
            options = new DefaultParserOptions();
        }

        ByLineReaderSource source = new ByLineReaderSource( reader );
        String line = null;
        StringBuffer buffer = new StringBuffer();

        while ( ( line = source.getNextLine() ) != null )
        {
            int lastLineIdx = buffer.length();
            
            buffer.append( '\n' ).append( line );
            
            while( buffer.length() > LOOKAHEAD_LEN )
            {
                StateParser newState = stateFactory.getState( buffer );

                // if we've detected a new markup class, push the last one onto the stack.
                if ( newState != null )
                {
                    if ( currentState != null )
                    {
                        states.push( currentState );
                    }
                    currentState = newState;
                    currentState.beginState( sink, options );
                }
                else
                {
                    if ( currentState == null )
                    {
                        throw new ParseException( "Cannot parse document; cannot find root-state parser for: \'" + buffer + "\'." );
                    }

                    int bufferLen = buffer.length();
                    int remainder = currentState.consume( buffer, bufferLen, sink, options );
                    if ( remainder > -1 )
                    {
                        if ( remainder >= lastLineIdx )
                        {
                            options.incrementLine();
                        }
                        buffer = buffer.delete( 0, remainder );
                        
                        currentState.endState( sink, options );

                        if ( !states.isEmpty() )
                        {
                            currentState = (StateParser) states.pop();
                        }
                        else
                        {
                            currentState = null;
                        }
                    }
                }
            }
        }
    }

    protected Logger getLogger()
    {
        if ( logger == null )
        {
            logger = new ConsoleLogger( Logger.LEVEL_INFO, "MediaWikiParser::Logger" );
        }

        return logger;
    }

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

}
