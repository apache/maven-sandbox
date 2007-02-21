package org.apache.maven.pomo;

/**
 * @author Jason van Zyl
 */
public class ModelGenerationException
    extends Exception
{
    public ModelGenerationException( String id )
    {
        super( id );
    }

    public ModelGenerationException( String id,
                                     Throwable throwable )
    {
        super( id, throwable );
    }

    public ModelGenerationException( Throwable throwable )
    {
        super( throwable );
    }
}
