package org.apache.maven.archiva.jarinfo.analysis.visitors;

import org.apache.maven.archiva.jarinfo.analysis.JarEntryVisitor;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.JarDetails;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Gather the size of individual entries, and overall uncompressed size too.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EntrySizer
    extends AbstractJarEntryVisitor
    implements JarEntryVisitor
{
    private long overall;

    public void visitStart( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitStart( details, jar );
        overall = 0;
    }

    public void visitJarEntry( EntryDetail entry, JarEntry jarEntry )
    {
        if ( jarEntry.isDirectory() )
        {
            // skip
            return;
        }

        long size = jarEntry.getSize();
        entry.setSize( size );
        overall += size;
    }

    public void visitFinished( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitFinished( details, jar );
        details.setSizeUncompressed( overall );
    }
}
