package org.apache.maven.archiva.jarinfo.analysis.visitors;

import org.apache.maven.archiva.jarinfo.analysis.IdentificationWeights;
import org.apache.maven.archiva.jarinfo.analysis.JarEntryVisitor;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.JarDetails;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * EntryManifest - process the jar manifest. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EntryManifest
    extends AbstractJarEntryVisitor
    implements JarEntryVisitor
{

    @Override
    public void visitStart( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitStart( details, jar );

        Manifest manifest = jar.getManifest();
        if ( manifest == null )
        {
            return;
        }

        // Collection Sealed identifier
        boolean sealed = false;
        String sval = manifest.getMainAttributes().getValue( Attributes.Name.SEALED );
        if ( sval != null )
        {
            sealed = "true".equalsIgnoreCase( sval.trim() );
        }

        details.setSealed( sealed );

        // Gather up InspectedIds Bits
        addAttributeValues( "manifest.main", manifest.getMainAttributes() );

        for ( Attributes attribs : manifest.getEntries().values() )
        {
            addAttributeValues( "manifest.entry", attribs );
        }
    }

    private void addAttributeValues( String weightPrefix, Attributes attribs )
    {
        IdentificationWeights weights = IdentificationWeights.getInstance();
        String weightKey;

        // Implementation values.
        weightKey = weightPrefix + ".name.impl";
        details.getInspectedIds().addName( attribs.getValue( Attributes.Name.IMPLEMENTATION_TITLE ),
                                             weights.getWeight( weightKey ), weightKey );
        weightKey = weightPrefix + ".version.impl";
        details.getInspectedIds().addVersion( attribs.getValue( Attributes.Name.IMPLEMENTATION_VERSION ),
                                                weights.getWeight( weightKey ), weightKey );
        weightKey = weightPrefix + ".vendor.impl";
        details.getInspectedIds().addVendor( attribs.getValue( Attributes.Name.IMPLEMENTATION_VENDOR ),
                                               weights.getWeight( weightKey ), weightKey );

        // Specification values.
        weightKey = weightPrefix + ".name.spec";
        details.getInspectedIds().addName( attribs.getValue( Attributes.Name.SPECIFICATION_TITLE ),
                                             weights.getWeight( weightKey ), weightKey );
        weightKey = weightPrefix + ".version.spec";
        details.getInspectedIds().addVersion( attribs.getValue( Attributes.Name.SPECIFICATION_VERSION ),
                                                weights.getWeight( weightKey ), weightKey );
        weightKey = weightPrefix + ".vendor.spec";
        details.getInspectedIds().addVendor( attribs.getValue( Attributes.Name.SPECIFICATION_VENDOR ),
                                               weights.getWeight( weightKey ), weightKey );

        // Extension values.
        weightKey = weightPrefix + ".groupId";
        details.getInspectedIds().addGroupId( attribs.getValue( Attributes.Name.EXTENSION_NAME ),
                                                weights.getWeight( weightKey ), weightKey );
    }

    public void visitJarEntry( EntryDetail entry, JarEntry jarEntry )
        throws IOException
    {
        /* do nothing here */
    }

}
