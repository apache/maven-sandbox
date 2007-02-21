package org.apache.maven.taxonomy.store;

import org.apache.maven.taxonomy.model.Taxon;
import org.apache.maven.taxonomy.TaxonNode;

/**
 * @author Jason van Zyl
 */
public interface TaxonomyStore
{
    String ROLE = TaxonomyStore.class.getName();

    int addTaxon( TaxonNode taxon )
        throws TaxonomyStoreException;

    TaxonNode getTaxon( int id )
        throws TaxonomyStoreException;

    void store()
        throws TaxonomyStoreException;

    void load()
        throws TaxonomyStoreException;
}
