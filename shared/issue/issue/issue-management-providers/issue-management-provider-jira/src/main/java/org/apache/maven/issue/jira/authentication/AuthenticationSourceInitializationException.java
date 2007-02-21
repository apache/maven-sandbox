package org.apache.maven.issue.jira.authentication;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class AuthenticationSourceInitializationException
    extends Exception
{
    public AuthenticationSourceInitializationException( String message )
    {
        super( message );
    }

    public AuthenticationSourceInitializationException( Throwable cause )
    {
        super( cause );
    }

    public AuthenticationSourceInitializationException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
