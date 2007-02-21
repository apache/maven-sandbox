package org.apache.maven.plugin.plugintest.manager;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MavenPluginCollector;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @plexus.component role="org.apache.maven.plugin.plugintest.manager.PluginManagerAccess" role-hint="default"
 * @author jdcasey
 *
 */
public class DefaultPluginManagerAccess
    implements PluginManagerAccess, Contextualizable
{
    
    /**
     * @plexus.requirement
     */
    MavenPluginCollector pluginCollector;

    private PlexusContainer container;
    
    public void clearPluginData( Artifact pluginArtifact )
    {
        clearPluginData( pluginArtifact.getGroupId(), pluginArtifact.getArtifactId() );
    }
    
    public void clearPluginData( String groupId, String artifactId )
    {
        Plugin plugin = new Plugin();
        
        plugin.setArtifactId( artifactId );
        plugin.setGroupId( groupId );
        
        pluginCollector.flushPluginDescriptor( plugin );
        
        String key = plugin.getKey();
        
        container.removeChildContainer( key );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        this.container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

}
