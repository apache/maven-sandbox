package org.apache.maven.doxia.module.mediawiki.parser.state;

public class NoSuchStateException
    extends Exception
{

    private final String lookahead;

    public NoSuchStateException( String lookahead, Throwable cause )
    {
        super( buildMessage( lookahead ), cause );
        this.lookahead = lookahead;
    }

    public NoSuchStateException( String lookahead )
    {
        super( buildMessage( lookahead ) );
        this.lookahead = lookahead;
    }
    
    private static String buildMessage( String lookahead )
    {
        return "No state parser found for: \'" + lookahead + "\'.";
    }

    public String getLookahead()
    {
        return lookahead;
    }

}
