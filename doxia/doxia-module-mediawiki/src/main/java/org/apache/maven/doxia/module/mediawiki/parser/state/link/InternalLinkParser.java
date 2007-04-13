package org.apache.maven.doxia.module.mediawiki.parser.state.link;

import org.apache.maven.doxia.module.mediawiki.parser.ParsingContext;
import org.apache.maven.doxia.module.mediawiki.parser.state.AbstractStateParser;
import org.apache.maven.doxia.module.mediawiki.parser.util.URLUtils;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

public class InternalLinkParser
    extends AbstractStateParser
{

    private static final char[] END_MARKER = "]]".toCharArray();
    
    private boolean urlFinished = false;

    private StringBuffer urlBuffer = new StringBuffer();

    private StringBuffer labelBuffer = new StringBuffer();

    protected void addContent( char[] buffer, int contentSize, Sink sink, ParsingContext options )
        throws ParseException
    {
        int labelStartIdx = 0;
        if ( !urlFinished )
        {
            for ( int i = 0; i < contentSize; i++ )
            {
                if ( buffer[i] == '|' )
                {
                    labelStartIdx = i+1;
                    urlFinished = true;
                    break;
                }
                else
                {
                    urlBuffer.append( buffer[i] );
                }
            }
        }
        
        if ( labelStartIdx < contentSize )
        {
            labelBuffer.append( buffer, labelStartIdx, contentSize - labelStartIdx );
        }
    }

    protected char[] getEndMarker()
    {
        return END_MARKER;
    }

    protected String getStateName()
    {
        return "Internal Link";
    }

    public void endState( Sink sink, ParsingContext options )
        throws ParseException
    {
        String href = urlBuffer.toString();
        String urlLabel = href;
        
        if ( !URLUtils.isAbsolute( href ) )
        {
            String baseUrl = options.getBaseUrl();
            String spaceToken = options.getUrlSpaceToken();
            
            href = URLUtils.appendToBaseURL( baseUrl, href );
            
            if ( spaceToken != null )
            {
                href = href.replaceAll( " ", spaceToken );
            }
        }
        
        if ( href.length() < 1 )
        {
            throw new ParseException( null, "No href provided for internal link.", options.getLine() );
        }
        
        String label;
        if ( labelBuffer != null && labelBuffer.length() > 0 )
        {
            label = labelBuffer.toString();
        }
        else
        {
            label = urlLabel;
        }
        
        sink.link( href );
        sink.text( label );
        sink.link_();
    }

}
