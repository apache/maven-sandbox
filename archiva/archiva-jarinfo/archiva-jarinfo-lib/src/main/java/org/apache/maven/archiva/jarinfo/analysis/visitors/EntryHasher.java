package org.apache.maven.archiva.jarinfo.analysis.visitors;

import org.apache.maven.archiva.jarinfo.analysis.Hasher;
import org.apache.maven.archiva.jarinfo.analysis.JarEntryVisitor;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.JarDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * EntryHasher 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EntryHasher
    extends AbstractJarEntryVisitor
    implements JarEntryVisitor
{
    private Hasher bytecodeHasher = new Hasher( Hasher.SHA1 );

    private Hasher entryHasher = new Hasher( Hasher.SHA1 );

    private List<Hasher> hashers = new ArrayList<Hasher>();

    public void visitStart( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitStart( details, jar );
        entryHasher.reset();
        bytecodeHasher.reset();
    }

    public void visitJarEntry( EntryDetail entry, JarEntry jarEntry )
        throws IOException
    {
        if ( jarEntry.isDirectory() )
        {
            // No hashing directories.
            return;
        }

        if ( jarEntry.getSize() <= 0 )
        {
            // Nothing to hash.
            return;
        }

        hashers.clear();
        hashers.add( entryHasher );

        if ( jarEntry.getName().endsWith( ".class" ) )
        {
            hashers.add( bytecodeHasher );
        }

        entryHasher.reset();

        Hasher.update( hashers, jar.getInputStream( jarEntry ) );

        entry.setHash( entryHasher.getAlgorithm(), entryHasher.getHash() );
    }

    public void visitFinished( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitFinished( details, jar );
        details.getBytecode().setHash( bytecodeHasher.getAlgorithm(), bytecodeHasher.getHash() );
    }
}
