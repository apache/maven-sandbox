package org.apache.maven.dynaweb.cms.session;

public class CMSAccessException
    extends Exception
{

    public CMSAccessException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public CMSAccessException( String message )
    {
        super( message );
    }

}
