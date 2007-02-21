package org.apache.maven.plugin.plugintest;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.plugintest.manager.PluginManagerAccess;

import java.util.Iterator;
import java.util.List;

/**
 * @author jdcasey
 * 
 * @goal flush-plugins
 */
public class FlushCachedPluginMojo
    extends AbstractMojo
{
    
    /**
     * @component
     */
    private PluginManagerAccess pluginManagerAccess;
    
    /**
     * List of plugins to flush, in the format groupId:artifactId
     * 
     * @parameter
     * @required
     */
    private List plugins;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( plugins != null )
        {
            for ( Iterator it = plugins.iterator(); it.hasNext(); )
            {
                String pluginId = ( String ) it.next();
                
                String[] idParts = pluginId.split( ":" );
                
                if ( idParts.length != 2 )
                {
                    getLog().warn( "Invalid plugin id: \'" + pluginId + "\'. Should be of the form: groupId:artifactId" );
                    continue;
                }
                
                getLog().info( "Flushing plugin: " + pluginId );
                
                pluginManagerAccess.clearPluginData( idParts[0], idParts[1] );
            }
        }
    }

}
