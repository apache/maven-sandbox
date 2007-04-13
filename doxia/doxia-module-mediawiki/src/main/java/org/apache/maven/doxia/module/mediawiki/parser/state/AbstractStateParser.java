package org.apache.maven.doxia.module.mediawiki.parser.state;

import org.apache.maven.doxia.module.mediawiki.parser.ParsingContext;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

public abstract class AbstractStateParser
    implements StateParser
{

    private int consumedEndMarkerIndex = -1;

    protected abstract char[] getEndMarker();

    protected abstract String getStateName();

    protected abstract void addContent( char[] buffer, int contentSize, Sink sink, ParsingContext options )
        throws ParseException;

    public int consume( CharSequence buffer, int bufferSize, Sink sink, ParsingContext context )
        throws ParseException
    {
        if ( buffer.length() < 1 )
        {
            return -1;
        }
        
        char[] endMarker = getEndMarker();

        if ( consumedEndMarkerIndex > -1 )
        {
            // increment to the first unchecked index
            consumedEndMarkerIndex++;
            
            int bufferMark = 0;
            for ( int i = consumedEndMarkerIndex; i < endMarker.length; i++ )
            {
                if ( endMarker[i] != buffer.charAt( i - consumedEndMarkerIndex ) )
                {
                    throw new ParseException( "Invalid end marker for state: " + getStateName() + " (expected: \'"
                        + String.copyValueOf( endMarker, consumedEndMarkerIndex, endMarker.length ) + "\'; got: \'"
                        + buffer + "\')." );
                }
                
                // increment the index in the end marker to pick up with on the next consume() call.
                consumedEndMarkerIndex++;
                bufferMark++;
            }

            return bufferMark;
        }
        else
        {
            int bufferLen = buffer.length();
            
            if ( bufferSize < bufferLen )
            {
                bufferLen = bufferSize;
            }
            
            int bufferMaxIdx = bufferLen - 1;
            
            int consumedToIdx = -1;
            int contentLastIdx = bufferLen - 1;
            int endMarkerIdx = 0;
            for ( int i = 0; i < bufferLen; i++ )
            {
                // if we're in the end marker (the contentLastIdx is changed), then see if we're past the
                // end of the end marker.
                if ( contentLastIdx != bufferMaxIdx && endMarkerIdx == endMarker.length )
                {
                    consumedToIdx = i;
                    break;
                }
                else if ( endMarker[endMarkerIdx] == buffer.charAt( i ) )
                {
                    // if we find the start of the end marker, cap the content sub-array.
                    if ( contentLastIdx == bufferMaxIdx )
                    {
                        contentLastIdx = i-1;
                    }
                    
                    // increment the index in the end marker to pick up with on the next consume() call.
                    endMarkerIdx++;
                    consumedEndMarkerIndex++;
                }
            }
            
            if ( contentLastIdx > -1 )
            {
                addContent( buffer.toString().toCharArray(), contentLastIdx + 1, sink, context );
            }
            
            return consumedToIdx;
        }
    }

    public void beginState( Sink sink, ParsingContext parseOptions )
        throws ParseException
    {
        // override if needed.
    }

    public void endState( Sink sink, ParsingContext parseOptions )
        throws ParseException
    {
        // override if needed.
    }

}
