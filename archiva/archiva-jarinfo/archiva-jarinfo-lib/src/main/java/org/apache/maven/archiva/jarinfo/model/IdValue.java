package org.apache.maven.archiva.jarinfo.model;

import java.util.Set;
import java.util.TreeSet;

public class IdValue
{
    private String value;

    private long weight;

    private Set<String> origins = new TreeSet<String>();

    public IdValue( String value )
    {
        this( value, 0 );
    }

    public IdValue( String value, int weight )
    {
        this.value = value;
        this.weight = weight;
    }

    public IdValue( String value, int weight, String origin )
    {
        this( value, weight );
        addOrigin( origin );
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public long getWeight()
    {
        return weight;
    }

    public void setWeight( long weight )
    {
        this.weight = weight;
    }

    public void addOrigin( String origin )
    {
        this.origins.add( origin );
    }

    public Set<String> getOrigins()
    {
        return origins;
    }

    public void setOrigins( Set<String> origins )
    {
        this.origins = origins;
    }

    public void addWeight( int additionalWeight )
    {
        this.weight += additionalWeight;
    }
}
