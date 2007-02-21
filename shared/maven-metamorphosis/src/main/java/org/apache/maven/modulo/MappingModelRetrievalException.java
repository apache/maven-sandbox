package org.apache.maven.modulo;

/**
 * @author Jason van Zyl
 */
public class MappingModelRetrievalException
    extends Exception
{
    public MappingModelRetrievalException( String id )
    {
        super( id );
    }

    public MappingModelRetrievalException( String id,
                                           Throwable throwable )
    {
        super( id, throwable );
    }

    public MappingModelRetrievalException( Throwable throwable )
    {
        super( throwable );
    }
}
