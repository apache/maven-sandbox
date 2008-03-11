package org.apache.maven.archiva.jarinfo;

/**
 * JarInfoException 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JarInfoException
    extends Exception
{
    public JarInfoException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public JarInfoException( String message )
    {
        super( message );
    }
}
