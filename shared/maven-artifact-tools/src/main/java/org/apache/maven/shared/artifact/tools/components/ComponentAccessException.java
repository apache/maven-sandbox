package org.apache.maven.shared.artifact.tools.components;

public class ComponentAccessException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    public ComponentAccessException( String message, Throwable cause )
    {
        super( message, cause );
    }

}
