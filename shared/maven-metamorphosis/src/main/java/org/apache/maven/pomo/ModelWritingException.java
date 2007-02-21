package org.apache.maven.pomo;

/**
 * @author Jason van Zyl
 */
public class ModelWritingException
    extends Exception
{
    public ModelWritingException( String id )
    {
        super( id );
    }

    public ModelWritingException( String id,
                                  Throwable throwable )
    {
        super( id, throwable );
    }

    public ModelWritingException( Throwable throwable )
    {
        super( throwable );
    }
}
