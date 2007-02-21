package org.apache.maven.taxonomy;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jason van Zyl
 */
public class TaxonNode
{
    private String name;

    private String description;

    private List taxons;

    private Map attributes;

    // ----------------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------------

    public TaxonNode( String name,
                      String description )
    {
        this.name = name;

        this.description = description;

        this.taxons = new ArrayList();

        this.attributes = new HashMap();
    }

    public TaxonNode( String name,
                      String description,
                      List nodes )
    {
        this( name, description );

        this.taxons = nodes;
    }

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    public void addTaxon( TaxonNode taxon )
    {
        taxons.add( taxon );
    }

    public TaxonNode addAttribute( String key, String value )
    {
        attributes.put( key, value );

        return this;
    }

    // ----------------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public List getTaxons()
    {
        return taxons;
    }
}
