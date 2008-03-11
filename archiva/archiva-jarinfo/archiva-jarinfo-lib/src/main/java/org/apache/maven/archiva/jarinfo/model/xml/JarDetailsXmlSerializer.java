package org.apache.maven.archiva.jarinfo.model.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.maven.archiva.jarinfo.JarInfoException;
import org.apache.maven.archiva.jarinfo.model.ClassDetail;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.IdValue;
import org.apache.maven.archiva.jarinfo.model.JarDetails;
import org.apache.maven.archiva.jarinfo.utils.EmptyUtils;
import org.apache.maven.archiva.jarinfo.utils.Timestamp;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;

/**
 * JarDetailsXmlSerializer - serialize JarDetails to XML 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JarDetailsXmlSerializer
    extends AbstractJarDetailsXml
{
    private static final Namespace DEFAULT_NAMESPACE = Namespace.get( "", JARINFO_NAMESPACE_ID );

    private static void addHashes( Element elem, Map<String, String> hashes )
    {
        for ( Entry<String, String> hash : hashes.entrySet() )
        {
            Element hashelem = elem.addElement( HASH );
            hashelem.setText( hash.getValue() );
            hashelem.addAttribute( ALGORITHM, hash.getKey() );
        }
    }

    private static void addOptionalElement( Element elem, String name, String text )
        throws JarInfoException
    {
        if ( EmptyUtils.isEmpty( text ) )
        {
            return;
        }

        elem.addElement( name ).setText( text );
    }

    private static void addRequiredElement( Element elem, String name, String text )
        throws JarInfoException
    {
        if ( EmptyUtils.isEmpty( text ) )
        {
            throw new JarInfoException( "Cannot serialize JarDetails due to missing value for field " + elem.getName()
                + "." + name );
        }

        elem.addElement( name ).setText( text );
    }

    private static void addWeightedValues( Element elem, String groupName, String entryName, List<IdValue> valueList )
    {
        if ( EmptyUtils.isEmpty( valueList ) )
        {
            return;
        }

        Element groupElem = elem.addElement( groupName );

        for ( IdValue idvalue : valueList )
        {
            Element entryElem = groupElem.addElement( entryName );
            entryElem.addElement( VALUE ).setText( idvalue.getValue() );
            entryElem.addElement( WEIGHT ).setText( String.valueOf( idvalue.getWeight() ) );
            for ( String origin : idvalue.getOrigins() )
            {
                entryElem.addElement( ORIGIN ).setText( origin );
            }
        }
    }

    /**
     * Fix the default namespace on all elements recursively.
     */
    private static void fixDefaultNamespace( Element elem )
    {
        elem.remove( elem.getNamespace() );
        elem.setQName( QName.get( elem.getName(), DEFAULT_NAMESPACE, elem.getQualifiedName() ) );

        Node n;

        Iterator<?> it = elem.elementIterator();
        while ( it.hasNext() )
        {
            n = (Node) it.next();

            switch ( n.getNodeType() )
            {
                case Node.ELEMENT_NODE:
                    fixDefaultNamespace( (Element) n );
                    break;
            }
        }
    }

    public static Document serialize( JarDetails details )
        throws JarInfoException
    {
        Document doc = DocumentFactory.getInstance().createDocument();
        Element root = doc.addElement( DOC_ROOT );

        root.add( DEFAULT_NAMESPACE );
        root.addNamespace( "xsi", "http://www.w3.org/2001/XMLSchema-instance" );
        root.addAttribute( "xsi:schemaLocation", "http://archiva.apache.org/jarinfo-v1_0_0.xsd" );

        // Assigned Id
        if ( ( details.getAssignedId() != null ) && details.getAssignedId().valid() )
        {
            Element elemId = root.addElement( ASSIGNED_ID );
            addRequiredElement( elemId, GROUP_ID, details.getAssignedId().getGroupId() );
            addRequiredElement( elemId, ARTIFACT_ID, details.getAssignedId().getArtifactId() );
            addRequiredElement( elemId, VERSION, details.getAssignedId().getVersion() );
            addOptionalElement( elemId, NAME, details.getAssignedId().getName() );
            addOptionalElement( elemId, VENDOR, details.getAssignedId().getVendor() );
        }

        // Basics
        addRequiredElement( root, FILENAME, details.getFilename() );
        addRequiredElement( root, TIMESTAMP, Timestamp.convert( details.getTimestamp() ) );
        addRequiredElement( root, SIZE, String.valueOf( details.getSize() ) );
        addOptionalElement( root, SIZE_UNCOMPRESSED, String.valueOf( details.getSizeUncompressed() ) );
        addHashes( root, details.getHashes() );
        addRequiredElement( root, SEALED, toBool( details.isSealed() ) );

        // Generator
        if ( ( details.getGenerator() != null ) && details.getGenerator().exists() )
        {
            Element generatorElem = root.addElement( GENERATOR );
            addRequiredElement( generatorElem, NAME, details.getGenerator().getName() );
            addOptionalElement( generatorElem, VERSION, details.getGenerator().getVersion() );
            addRequiredElement( generatorElem, TIMESTAMP, Timestamp.convert( details.getGenerator().getTimestamp() ) );
        }

        // Jar Entries
        if ( !EmptyUtils.isEmpty( details.getEntries() ) )
        {
            Element entries = root.addElement( ENTRIES );

            int countDir = 0;
            int countFile = 0;

            for ( EntryDetail edetail : details.getEntries() )
            {
                if ( edetail.isDirectory() )
                {
                    countDir++;
                    Element entry = entries.addElement( DIRECTORY );
                    entry.addAttribute( NAME, edetail.getName() );
                    entry.addAttribute( TIMESTAMP, Timestamp.convert( edetail.getTimestamp() ) );
                }
                else
                {
                    countFile++;
                    Element entry = entries.addElement( FILE );
                    entry.addAttribute( NAME, edetail.getName() );
                    entry.addAttribute( SIZE, String.valueOf( edetail.getSize() ) );
                    entry.addAttribute( TIMESTAMP, Timestamp.convert( edetail.getTimestamp() ) );
                    // Only files with content are important to deal with.
                    if ( edetail.getSize() > 0 )
                    {
                        addHashes( entry, edetail.getHashes() );
                    }
                }
            }

            entries.addAttribute( COUNT_DIRS, String.valueOf( countDir ) );
            entries.addAttribute( COUNT_FILES, String.valueOf( countFile ) );
            entries.addAttribute( COUNT_TOTAL, String.valueOf( countDir + countFile ) );
        }

        // Bytecode
        if ( ( details.getBytecode() != null ) && !EmptyUtils.isEmpty( details.getEntries() ) )
        {
            Element bytecode = root.addElement( BYTECODE );

            int countClasses = 0;
            Set<String> packages = new TreeSet<String>();

            addHashes( bytecode, details.getBytecode().getHashes() );
            addOptionalElement( bytecode, JDK, details.getBytecode().getRequiredJdk() );
            addOptionalElement( bytecode, DEBUG, toBool( details.getBytecode().hasDebug() ) );

            for ( ClassDetail classDetail : details.getBytecode().getClasses() )
            {
                countClasses++;
                Element classElem = bytecode.addElement( CLASS );
                classElem.addAttribute( NAME, classDetail.getName() );
                classElem.addAttribute( VERSION, classDetail.getClassVersion() );
                classElem.addAttribute( JDK, classDetail.getTargetJdk() );
                classElem.addAttribute( DEBUG, toBool( classDetail.hasDebug() ) );
                addHashes( classElem, classDetail.getHashes() );

                for ( String importName : classDetail.getImports() )
                {
                    addOptionalElement( classElem, IMPORT, importName );
                }

                for ( String methodName : classDetail.getMethods() )
                {
                    addOptionalElement( classElem, METHOD, methodName );
                }

                packages.add( classDetail.getPackage() );
            }

            bytecode.addAttribute( COUNT_CLASSES, String.valueOf( countClasses ) );
            bytecode.addAttribute( COUNT_PACKAGES, String.valueOf( packages.size() ) );

            if ( !EmptyUtils.isEmpty( packages ) )
            {
                for ( String packageName : packages )
                {
                    bytecode.addElement( PACKAGE ).setText( packageName );
                }
            }
        }

        // InspectedIds
        if ( ( details != null ) && !details.getInspectedIds().isEmpty() )
        {
            Element idelem = root.addElement( INSPECTED );

            addWeightedValues( idelem, GROUP_IDS, GROUP_ID, details.getInspectedIds().getGroupIdList() );
            addWeightedValues( idelem, ARTIFACT_IDS, ARTIFACT_ID, details.getInspectedIds().getArtifactIdList() );
            addWeightedValues( idelem, VERSIONS, VERSION, details.getInspectedIds().getVersionList() );
            addWeightedValues( idelem, NAMES, NAME, details.getInspectedIds().getNameList() );
            addWeightedValues( idelem, VENDORS, VENDOR, details.getInspectedIds().getVendorList() );
        }

        fixDefaultNamespace( root );

        return doc;
    }

    private static String toBool( boolean flag )
    {
        return ( flag ) ? "true" : "false";
    }

}
