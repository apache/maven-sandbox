package org.apache.maven.plugin.plugintest.stage;

public class PluginStagingException
    extends Exception
{

    public PluginStagingException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public PluginStagingException( String message )
    {
        super( message );
    }

}
