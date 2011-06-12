package org.codehaus.plexus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

public class CollectionUtilsTest
    extends Assert
{
    @SuppressWarnings( "rawtypes" )
    @Test
    public void testMergeMaps()
        throws Exception
    {
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

    @SuppressWarnings( "rawtypes" )
    @Test
    public void testMergeMapsArray()
        throws Exception
    {
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

        Map[] maps = new Map[2];
        maps[0] = dom;
        maps[1] = rec;

        Map res = CollectionUtils.mergeMaps( maps );
        assertEquals( expected, res );
    }

    /**
     * This test fails. I don't know why.
     * 
     * @throws Exception
     */
    @Test
    public void testIntersection()
        throws Exception
    {
        Collection<String> c1 = new ArrayList<String>();
        Collection<String> c2 = new ArrayList<String>();
        /*
         * An exhaustive black box test here would involve generating a great deal of data, perhaps even different sizes
         * and collection classes.
         */

        c1.add( "red" );
        c1.add( "blue" );
        c1.add( "green" );
        c1.add( "socialist" );
        c1.add( "red" );
        c1.add( "purple" );
        c1.add( "porpoise" );
        c1.add( "green" );
        c1.add( "blue" );
        c1.add( "gray" );

        c2.add( "blue" );
        c2.add( "12" );
        c2.add( "15" );
        c2.add( "blue" );
        c2.add( "porpoise" );
        c2.add( "33.3" );
        c2.add( "jabberwock" );

        Multiset<String> correct = HashMultiset.create();
        correct.add( "blue" );
        correct.add( "blue" );
        correct.add( "porpoise" );

        @SuppressWarnings( "unchecked" )
        Collection<String> res = CollectionUtils.intersection( c1, c2 );
        Multiset<String> actual = HashMultiset.create();
        actual.addAll( res );
        assertEquals( correct, actual );
    }

    @Test
    public void testSubtract()
        throws Exception
    {

        Collection<String> c1 = new ArrayList<String>();
        Collection<String> c2 = new ArrayList<String>();
        /*
         * An exhaustive black box test here would involve generating a great deal of data, perhaps even different sizes
         * and collection classes.
         */

        c1.add( "red" );
        c1.add( "blue" );
        c1.add( "green" );
        c1.add( "socialist" );
        c1.add( "red" );
        c1.add( "purple" );
        c1.add( "porpoise" );
        c1.add( "green" );
        c1.add( "blue" );
        c1.add( "gray" );

        c2.add( "blue" );
        c2.add( "12" );
        c2.add( "15" );
        c2.add( "blue" );
        c2.add( "porpoise" );
        c2.add( "33.3" );
        c2.add( "jabberwock" );

        Multiset<String> correct = HashMultiset.create();
        correct.addAll( c1 );
        for ( String s : c2 )
        {
            correct.remove( s );
        }

        @SuppressWarnings( "unchecked" )
        Collection<String> res = CollectionUtils.subtract( c1, c2 );
        Multiset<String> act = HashMultiset.create();
        act.addAll( res );

        assertEquals( correct, act );
    }

    @Test
    public void testGetCardinalityMap()
        throws Exception
    {
        Collection<String> c1 = new ArrayList<String>();

        c1.add( "red" );
        c1.add( "blue" );
        c1.add( "green" );
        c1.add( "socialist" );
        c1.add( "red" );
        c1.add( "purple" );
        c1.add( "porpoise" );
        c1.add( "green" );
        c1.add( "blue" );
        c1.add( "gray" );

        @SuppressWarnings( "unchecked" )
        Map<String, Integer> counts = CollectionUtils.getCardinalityMap( c1 );
        Map<String, Integer> correct = new HashMap<String, Integer>();
        correct.put( "red", 2 );
        correct.put( "blue", 2 );
        correct.put( "green", 2 );
        correct.put( "purple", 1 );
        correct.put( "socialist", 1 );
        correct.put( "porpoise", 1 );
        correct.put( "gray", 1 );

        assertEquals( correct, counts );
    }
    
    @Test
    public void testIteratorToList()
        throws Exception
    {
        Collection<String> c1 = new ArrayList<String>();

        c1.add( "red" );
        c1.add( "blue" );
        c1.add( "green" );
        c1.add( "socialist" );
        c1.add( "red" );
        c1.add( "purple" );
        c1.add( "porpoise" );
        c1.add( "green" );
        c1.add( "blue" );
        c1.add( "gray" );
        
        List<String> expected = Lists.newArrayList( c1.iterator() );
        @SuppressWarnings( "unchecked" )
        List<String> actual = CollectionUtils.iteratorToList( c1.iterator() );
        assertEquals( expected, actual );
    }
}
