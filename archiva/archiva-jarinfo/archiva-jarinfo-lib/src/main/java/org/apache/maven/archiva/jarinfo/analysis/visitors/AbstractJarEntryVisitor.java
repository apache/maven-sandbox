package org.apache.maven.archiva.jarinfo.analysis.visitors;

import org.apache.maven.archiva.jarinfo.analysis.JarEntryVisitor;
import org.apache.maven.archiva.jarinfo.model.JarDetails;

import java.io.IOException;
import java.util.jar.JarFile;

/**
 * AbstractJarEntryVisitor 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractJarEntryVisitor
    implements JarEntryVisitor
{
    protected JarDetails details;

    protected JarFile jar;

    public void visitStart( JarDetails details, JarFile jar )
        throws IOException
    {
        this.details = details;
        this.jar = jar;
    }

    public void visitFinished( JarDetails finishDetails, JarFile finishJar )
        throws IOException
    {
        if ( !finishDetails.getFilename().equals( this.details.getFilename() ) )
        {
            throw new IllegalStateException( "Encountered finish for different details. started as ["
                + this.details.getFilename() + "], finished as [" + finishDetails.getFilename() + "]" );
        }

        if ( !finishJar.getName().equals( this.jar.getName() ) )
        {
            throw new IllegalStateException( "Encountered finish for different details. started as ["
                + this.jar.getName() + "], finished as [" + finishJar.getName() + "]" );
        }
    }
}
