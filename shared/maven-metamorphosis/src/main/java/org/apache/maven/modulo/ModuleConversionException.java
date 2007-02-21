package org.apache.maven.modulo;

/**
 * @author Jason van Zyl
 */
public class ModuleConversionException
    extends Exception
{
    public ModuleConversionException( String id )
    {
        super( id );
    }

    public ModuleConversionException( String id,
                                      Throwable throwable )
    {
        super( id, throwable );
    }

    public ModuleConversionException( Throwable throwable )
    {
        super( throwable );
    }
}
