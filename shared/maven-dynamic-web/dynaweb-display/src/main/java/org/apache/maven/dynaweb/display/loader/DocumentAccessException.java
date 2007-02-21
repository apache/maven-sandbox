package org.apache.maven.dynaweb.display.loader;

public class DocumentAccessException
    extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final String path;

    public DocumentAccessException( final String path, final String message, final Throwable cause )
    {
        super( "Error loading: " + path + ". " + message, cause );
        this.path = path;
    }

    public DocumentAccessException( final String path, final String message )
    {
        super( "Error loading: " + path + ". " + message );
        this.path = path;
    }
    
    public String getPath()
    {
        return path;
    }

}
