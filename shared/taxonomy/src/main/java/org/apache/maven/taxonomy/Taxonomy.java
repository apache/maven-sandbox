package org.apache.maven.taxonomy;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Jason van Zyl
 */
public class Taxonomy
{
    private String id;

    private String name;

    private String description;

    private TaxonNode taxons;

    private int nodeId = 0;

    private Map store = new TreeMap();

    public Taxonomy()
    {
        this( "default", "default", "default" );
    }

    public Taxonomy( String id,
                     String name,
                     String description )
    {
        this.id = id;

        this.name = name;

        this.description = description;

        this.taxons = new TaxonNode( name, description );
    }

    public int addTaxon( String id, String name, String description )
    {
        TaxonNode t = new TaxonNode( name, description );

        return addTaxon( t );
    }

    public int addTaxon( TaxonNode t )
    {
        Integer i = new Integer( ++nodeId );

        store.put( i, t );

        taxons.addTaxon( t );

        return nodeId;
    }

    public TaxonNode getTaxon( int id )
    {
        return (TaxonNode) store.get( new Integer( id ) );
    }

    // ----------------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------------

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public TaxonNode getTaxons()
    {
        return taxons;
    }
}
