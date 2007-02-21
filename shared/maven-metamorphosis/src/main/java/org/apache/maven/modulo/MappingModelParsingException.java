package org.apache.maven.modulo;

/**
 * @author Jason van Zyl
 */
public class MappingModelParsingException
    extends Exception
{
    public MappingModelParsingException( String id )
    {
        super( id );
    }

    public MappingModelParsingException( String id,
                                         Throwable throwable )
    {
        super( id, throwable );
    }

    public MappingModelParsingException( Throwable throwable )
    {
        super( throwable );
    }
}
