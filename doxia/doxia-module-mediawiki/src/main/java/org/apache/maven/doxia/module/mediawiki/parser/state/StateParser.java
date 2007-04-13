package org.apache.maven.doxia.module.mediawiki.parser.state;

import org.apache.maven.doxia.module.mediawiki.parser.ParsingContext;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

public interface StateParser
{

    void beginState( Sink sink, ParsingContext parseOptions )
        throws ParseException;

    /**
     * Consume the given character array. If the end marker for this state is encountered, return
     * the index of the first character AFTER the LAST character in the end marker.
     * @param buffer the content buffer to consume
     * @param bufferSize The size of the buffer to read. The rest is junk.
     * @param sink The Sink which will render the document events as they happen
     * @param options The ParsingContext used to help determine things like relative link locations, etc.
     * @return -1 if the whole buffer was used, otherwise the first index AFTER the last character 
     *   in the end marker. THIS INDEX MUST BE LESS THAN bufferSize.
     * @throws ParseException 
     */
    int consume( CharSequence buffer, int bufferSize, Sink sink, ParsingContext options )
        throws ParseException;

    void endState( Sink sink, ParsingContext parseOptions )
        throws ParseException;

}
