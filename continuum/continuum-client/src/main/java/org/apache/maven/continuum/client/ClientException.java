package org.apache.maven.continuum.client;

/**
 * A wrapper for the specific protocol exceptions that may be encountered.
 *
 * @author Andrew Williams
 */
public class ClientException
    extends Exception
{
    public ClientException( String message )
    {
        super( message );
    }

    public ClientException( Throwable cause )
    {
        super( cause );
    }

    public ClientException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
