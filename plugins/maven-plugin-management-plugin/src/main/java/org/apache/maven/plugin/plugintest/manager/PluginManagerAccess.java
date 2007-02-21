package org.apache.maven.plugin.plugintest.manager;

import org.apache.maven.artifact.Artifact;

public interface PluginManagerAccess
{

    void clearPluginData( Artifact pluginArtifact );
    
    void clearPluginData( String groupId, String artifactId );

}
