/*
 * Copyright 2010 Red Hat, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.maven.mae;

public class PluginGoal
{

    private final String groupId;

    private final String artifactId;

    private final String version;

    private final String pluginPrefix;

    private final String goal;

    public PluginGoal( final String groupId, final String artifactId, final String version, final String goal )
    {
        pluginPrefix = null;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.goal = goal;
    }

    public PluginGoal( final String groupId, final String artifactId, final String goal )
    {
        pluginPrefix = null;
        this.groupId = groupId;
        this.artifactId = artifactId;
        version = null;
        this.goal = goal;
    }

    public PluginGoal( final String pluginPrefix, final String goal )
    {
        this.pluginPrefix = pluginPrefix;
        groupId = null;
        artifactId = null;
        version = null;
        this.goal = goal;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public String getPluginPrefix()
    {
        return pluginPrefix;
    }

    public String getGoal()
    {
        return goal;
    }

    public String formatCliGoal()
    {
        final StringBuilder sb = new StringBuilder();
        if ( pluginPrefix != null )
        {
            sb.append( pluginPrefix );
        }
        else
        {
            sb.append( groupId ).append( ':' ).append( artifactId );
            if ( version != null )
            {
                sb.append( ':' ).append( version );
            }
        }
        sb.append( ':' ).append( goal );

        return sb.toString();
    }

    @Override
    public String toString()
    {
        return "Plugin+Goal: " + formatCliGoal();
    }

}
