package org.apache.maven.archiva.jarinfo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.archiva.jarinfo.utils.EmptyUtils;
import org.apache.maven.archiva.jarinfo.utils.IdValueComparator;

/**
 * InspectedIds 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class InspectedIds
{
    private Map<String, IdValue> groupIds = new HashMap<String, IdValue>();

    private Map<String, IdValue> artifactIds = new HashMap<String, IdValue>();

    private Map<String, IdValue> versions = new HashMap<String, IdValue>();

    private Map<String, IdValue> names = new HashMap<String, IdValue>();

    private Map<String, IdValue> vendors = new HashMap<String, IdValue>();

    public void addArtifactId( IdValue idvalue )
    {
        this.artifactIds.put( idvalue.getValue(), idvalue );
    }

    public void addArtifactId( String id, int weight, String origin )
    {
        bumpWeight( artifactIds, id, weight, origin );
    }
    
    public void addGroupId( IdValue idvalue )
    {
        this.groupIds.put( idvalue.getValue(), idvalue );
    }

    public void addGroupId( String id, int weight, String origin )
    {
        bumpWeight( groupIds, id, weight, origin );
    }
    
    public void addName( IdValue idvalue )
    {
        this.names.put( idvalue.getValue(), idvalue );
    }

    public void addName( String name, int weight, String origin )
    {
        bumpWeight( names, name, weight, origin );
    }
    
    public void addVendor( IdValue idvalue )
    {
        this.vendors.put( idvalue.getValue(), idvalue );
    }

    public void addVendor( String vendor, int weight, String origin )
    {
        bumpWeight( vendors, vendor, weight, origin );
    }
    
    public void addVersion( IdValue idvalue )
    {
        this.versions.put( idvalue.getValue(), idvalue );
    }

    public void addVersion( String version, int weight, String origin )
    {
        bumpWeight( versions, version, weight, origin );
    }
    
    private void bumpWeight( Map<String, IdValue> values, String key, int additionalWeight, String origin )
    {
        if ( EmptyUtils.isEmpty( key ) )
        {
            return;
        }
        
        IdValue idvalue = values.get( key );
        if ( idvalue == null )
        {
            values.put( key, new IdValue( key, additionalWeight, origin ) );
            return;
        }

        idvalue.addWeight( additionalWeight );
        idvalue.addOrigin( origin );
    }

    public void clearArtifactIds()
    {
        artifactIds.clear();
    }

    public void clearGroupIds()
    {
        groupIds.clear();
    }

    public void clearNames()
    {
        names.clear();
    }

    public void clearVendors()
    {
        vendors.clear();
    }

    public void clearVersions()
    {
        versions.clear();
    }

    public List<IdValue> getArtifactIdList()
    {
        return toSortedList( artifactIds );
    }

    public Map<String, IdValue> getArtifactIds()
    {
        return artifactIds;
    }

    public List<IdValue> getGroupIdList()
    {
        return toSortedList( groupIds );
    }

    public Map<String, IdValue> getGroupIds()
    {
        return groupIds;
    }

    public List<IdValue> getNameList()
    {
        return toSortedList( names );
    }

    public Map<String, IdValue> getNames()
    {
        return names;
    }

    public List<IdValue> getVendorList()
    {
        return toSortedList( vendors );
    }

    public Map<String, IdValue> getVendors()
    {
        return vendors;
    }

    public List<IdValue> getVersionList()
    {
        return toSortedList( versions );
    }

    public Map<String, IdValue> getVersions()
    {
        return versions;
    }

    public boolean isEmpty()
    {
        return ( EmptyUtils.isEmpty( groupIds ) && EmptyUtils.isEmpty( artifactIds )
            && EmptyUtils.isEmpty( versions ) && EmptyUtils.isEmpty( names ) && EmptyUtils
            .isEmpty( vendors ) );
    }

    private List<IdValue> toSortedList( Map<String, IdValue> weightedMap )
    {
        List<IdValue> ret = new ArrayList<IdValue>();
        ret.addAll( weightedMap.values() );

        Collections.sort( ret, new IdValueComparator() );

        return Collections.unmodifiableList( ret );
    }

	public void clearAll() {
		clearGroupIds();
		clearArtifactIds();
		clearVersions();
		clearNames();
		clearVendors();
	}
}
