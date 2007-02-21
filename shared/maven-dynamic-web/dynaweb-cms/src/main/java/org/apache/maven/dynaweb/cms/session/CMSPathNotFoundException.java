package org.apache.maven.dynaweb.cms.session;

public class CMSPathNotFoundException
    extends CMSAccessException
{

    public CMSPathNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public CMSPathNotFoundException( String message )
    {
        super( message );
    }

}
