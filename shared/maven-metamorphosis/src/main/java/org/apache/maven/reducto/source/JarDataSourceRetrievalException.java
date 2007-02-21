package org.apache.maven.reducto.source;

/**
 * @author Jason van Zyl
 */
public class JarDataSourceRetrievalException
    extends Exception
{
    public JarDataSourceRetrievalException( String id )
    {
        super( id );
    }

    public JarDataSourceRetrievalException( String id,
                                            Throwable throwable )
    {
        super( id, throwable );
    }

    public JarDataSourceRetrievalException( Throwable throwable )
    {
        super( throwable );
    }
}
