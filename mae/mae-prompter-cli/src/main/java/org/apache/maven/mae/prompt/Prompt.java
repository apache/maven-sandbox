package org.apache.maven.mae.prompt;

import java.util.List;

public interface Prompt
{
    
    String getInput( String message )
        throws PromptException;

    String getInput( String message, String defaultReply )
        throws PromptException;

    int getSelection( String message, List<?> values )
        throws PromptException;

    int getSelection( String message, List<?> values, int defaultSelection )
        throws PromptException;

    String getPassword( String message )
        throws PromptException;
    
}
