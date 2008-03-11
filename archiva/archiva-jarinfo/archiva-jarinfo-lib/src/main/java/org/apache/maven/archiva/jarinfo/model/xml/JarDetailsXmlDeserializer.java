package org.apache.maven.archiva.jarinfo.model.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.archiva.jarinfo.model.ClassDetail;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.IdValue;
import org.apache.maven.archiva.jarinfo.model.JarDetails;
import org.apache.maven.archiva.jarinfo.utils.EmptyUtils;
import org.apache.maven.archiva.jarinfo.utils.Timestamp;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * JarDetailsXmlDeserializer - deserialize XML to JarDetails 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JarDetailsXmlDeserializer
    extends AbstractJarDetailsXml
{
    private static final Set<String> BOOLS;

    static
    {
        BOOLS = new HashSet<String>();
        BOOLS.add( "true" );
        BOOLS.add( "yes" );
        BOOLS.add( "on" );
        BOOLS.add( "1" );
    }

    public static JarDetails deserialize( Document doc )
    {
        JarDetails details = new JarDetails();
        Element root = doc.getRootElement();

        // Assigned Id
        if ( elemExists( root, ASSIGNED_ID ) )
        {
            Element elemId = root.element( ASSIGNED_ID );
            details.getAssignedId().setGroupId( get( elemId, GROUP_ID ) );
            details.getAssignedId().setArtifactId( get( elemId, ARTIFACT_ID ) );
            details.getAssignedId().setVersion( get( elemId, VERSION ) );
            details.getAssignedId().setName( get( elemId, NAME ) );
            details.getAssignedId().setVendor( get( elemId, VENDOR ) );
        }

        // Basics
        details.setFilename( get( root, FILENAME ) );
        details.setTimestamp( Timestamp.convert( get( root, TIMESTAMP ) ) );
        details.setSize( toLong( get( root, SIZE ) ) );
        details.setHashes( getHashes( root ) );
        details.setSealed( toBool( get( root, SEALED ) ) );

        // Generator
        if ( elemExists( root, GENERATOR ) )
        {
            Element elemGen = root.element( GENERATOR );
            details.getGenerator().setName( get( elemGen, NAME ) );
            details.getGenerator().setVersion( get( elemGen, VERSION ) );
            details.getGenerator().setTimestamp( Timestamp.convert( get( elemGen, TIMESTAMP ) ) );
        }

        // Jar Entries
        if ( elemExists( root, ENTRIES ) )
        {
            Element elemEntries = root.element( ENTRIES );

            Iterator<?> entryIter = elemEntries.elementIterator();
            while ( entryIter.hasNext() )
            {
                Node node = (Node) entryIter.next();
                if ( node.getNodeType() != Node.ELEMENT_NODE )
                {
                    // Skip non-element nodes.
                    continue;
                }

                Element elemEntry = (Element) node;
                EntryDetail entry = new EntryDetail();

                entry.setDirectory( elemEntry.getName().equals( DIRECTORY ) );
                entry.setName( get( elemEntry, NAME ) );
                entry.setSize( toLong( get( elemEntry, SIZE ) ) );
                entry.setTimestamp( Timestamp.convert( get( elemEntry, TIMESTAMP ) ) );
                entry.setHashes( getHashes( elemEntry ) );

                details.addEntry( entry );
            }
        }

        // Bytecode
        if ( elemExists( root, BYTECODE ) )
        {
            Element elemBytecode = root.element( BYTECODE );

            details.getBytecode().setDebug( toBool( get( elemBytecode, DEBUG ) ) );
            details.getBytecode().setRequiredJdk( get( elemBytecode, JDK ) );
            details.getBytecode().setHashes( getHashes( elemBytecode ) );

            Iterator<?> classIter = elemBytecode.elementIterator( CLASS );
            while ( classIter.hasNext() )
            {
                Node node = (Node) classIter.next();
                if ( node.getNodeType() != Node.ELEMENT_NODE )
                {
                    // Skip non-element nodes.
                    continue;
                }

                Element elemClass = (Element) node;
                ClassDetail cdetail = new ClassDetail();

                cdetail.setName( get( elemClass, NAME ) );
                cdetail.setClassVersion( get( elemClass, VERSION ) );
                cdetail.setTargetJdk( get( elemClass, JDK ) );
                cdetail.setDebug( toBool( get( elemClass, DEBUG ) ) );

                cdetail.setHashes( getHashes( elemClass ) );

                cdetail.setImports( getStringList( elemClass, IMPORT ) );
                cdetail.setMethods( getStringList( elemClass, METHOD ) );

                details.getBytecode().addClass( cdetail );
            }
        }

        // Inspected Ids
        if ( elemExists( root, INSPECTED ) )
        {
            Element elemInspected = root.element( INSPECTED );

            for ( IdValue idvalue : getIdValues( elemInspected, GROUP_IDS, GROUP_ID ) )
            {
                details.getInspectedIds().addGroupId( idvalue );
            }

            for ( IdValue idvalue : getIdValues( elemInspected, ARTIFACT_IDS, ARTIFACT_ID ) )
            {
                details.getInspectedIds().addArtifactId( idvalue );
            }

            for ( IdValue idvalue : getIdValues( elemInspected, VERSIONS, VERSION ) )
            {
                details.getInspectedIds().addVersion( idvalue );
            }

            for ( IdValue idvalue : getIdValues( elemInspected, NAMES, NAME ) )
            {
                details.getInspectedIds().addName( idvalue );
            }

            for ( IdValue idvalue : getIdValues( elemInspected, VENDORS, VENDOR ) )
            {
                details.getInspectedIds().addVendor( idvalue );
            }
        }

        return details;
    }

    private static boolean elemExists( Element elem, String childName )
    {
        return ( elem.element( childName ) != null );
    }

    private static String get( Element elem, String name )
    {
        Element child = elem.element( name );
        if ( child == null )
        {
            return null;
        }

        return child.getTextTrim();
    }

    private static Map<String, String> getHashes( Element elem )
    {
        Map<String, String> hashes = new HashMap<String, String>();

        Iterator<?> nodes = elem.elementIterator( HASH );
        while ( nodes.hasNext() )
        {
            Node node = (Node) nodes.next();
            if ( node.getNodeType() == Node.ELEMENT_NODE )
            {
                Element hash = (Element) node;
                String algo = hash.attributeValue( ALGORITHM );
                String hex = hash.getTextTrim();
                hashes.put( algo, hex );
            }
        }
        return hashes;
    }

    private static List<IdValue> getIdValues( Element elem, String groupingName, String name )
    {
        List<IdValue> idvalues = new ArrayList<IdValue>();

        Element elemGrouping = elem.element( groupingName );
        if ( elemGrouping == null )
        {
            return idvalues;
        }

        Iterator<?> nodes = elemGrouping.elementIterator( name );
        while ( nodes.hasNext() )
        {
            Node node = (Node) nodes.next();

            if ( node.getNodeType() == Node.ELEMENT_NODE )
            {
                // Skip non-element nodes.
                continue;
            }

            Element elemChild = (Element) node;

            String value = get( elemChild, VALUE );
            int weight = toInteger( get( elemChild, WEIGHT ) );

            IdValue idvalue = new IdValue( value, weight );
            idvalue.getOrigins().addAll( getStringList( elemChild, ORIGIN ) );

            idvalues.add( idvalue );
        }

        return idvalues;
    }

    private static List<String> getStringList( Element elem, String childName )
    {
        List<String> strlist = new ArrayList<String>();
        Iterator<?> nodes = elem.elementIterator( childName );
        while ( nodes.hasNext() )
        {
            Node node = (Node) nodes.next();
            if ( node.getNodeType() != Node.ELEMENT_NODE )
            {
                // Skip non-element nodes.
                continue;
            }

            strlist.add( ( (Element) node ).getTextTrim() );
        }
        return strlist;
    }

    private static boolean toBool( String str )
    {
        if ( EmptyUtils.isEmpty( str ) )
        {
            return false;
        }

        return BOOLS.contains( str.trim().toLowerCase() );
    }

    private static int toInteger( String text )
    {
        try
        {
            return Integer.parseInt( text );
        }
        catch ( NumberFormatException e )
        {
            return -1;
        }
    }

    private static long toLong( String text )
    {
        try
        {
            return Long.parseLong( text );
        }
        catch ( NumberFormatException e )
        {
            return -1;
        }
    }
}
