package org.codehaus.plexus.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CollectionUtilsTest extends Assert
{
    @SuppressWarnings( "rawtypes" )
    @Test
    public void testMergeMaps() throws Exception {
        Map<String, String> dom = new HashMap<String, String>();
        Map<String, String> rec = new HashMap<String, String>();
        Map<String, String> expected = new HashMap<String, String>(); 
        
        dom.put( "name", "Sir Launcelot of Camelot" );
        dom.put( "quest", "the holy grail" );
        dom.put( "favorite color", "yellow" );
        
        rec.put( "meaning of life", "42" );
        rec.put( "quest", "the perfect tan" );
        
        expected.put( "name", "Sir Launcelot of Camelot" );
        expected.put( "quest", "the holy grail" );
        expected.put( "favorite color", "yellow" );
        expected.put( "meaning of life", "42" );
        
        Map res = CollectionUtils.mergeMaps( dom, rec );
        assertEquals( expected, res );
    }
}
