package org.apache.maven.mae.prompt;

import org.apache.maven.mae.MAEException;

public class PromptException
    extends MAEException
{
    private static final long serialVersionUID = 1L;

    public PromptException( String message, Object... params )
    {
        super( message, params );
    }

    public PromptException( String message, Throwable cause, Object... params )
    {
        super( message, cause, params );
    }

}
