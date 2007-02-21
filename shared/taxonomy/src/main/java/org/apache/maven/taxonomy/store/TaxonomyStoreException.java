package org.apache.maven.taxonomy.store;

/**
 * @author Jason van Zyl
 */
public class TaxonomyStoreException
    extends Exception
{
    public TaxonomyStoreException( String id )
    {
        super( id );
    }

    public TaxonomyStoreException( String id,
                                   Throwable throwable )
    {
        super( id, throwable );
    }

    public TaxonomyStoreException( Throwable throwable )
    {
        super( throwable );
    }
}
