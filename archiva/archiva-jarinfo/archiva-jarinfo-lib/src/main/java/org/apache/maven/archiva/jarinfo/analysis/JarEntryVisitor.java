package org.apache.maven.archiva.jarinfo.analysis;

import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.JarDetails;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Visitor Interface for JarEntry(s) 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface JarEntryVisitor
{
    public void visitStart( JarDetails details, JarFile jar )
        throws IOException;

    public void visitJarEntry( EntryDetail entry, JarEntry jarEntry )
        throws IOException;

    public void visitFinished( JarDetails details, JarFile jar )
        throws IOException;
}
