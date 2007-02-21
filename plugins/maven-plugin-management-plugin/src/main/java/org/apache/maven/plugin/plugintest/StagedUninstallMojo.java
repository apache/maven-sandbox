package org.apache.maven.plugin.plugintest;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.plugintest.backup.PluginBackupManager;
import org.apache.maven.plugin.plugintest.manager.PluginManagerAccess;

import java.io.File;
import java.io.IOException;

/**
 * Remove staged plugin information from the local repository.
 *
 * @goal unstage
 * 
 * @phase post-integration-test
 */
public class StagedUninstallMojo
    extends AbstractMojo
{

    /**
     * @parameter default-value="${project.artifact}"
     * @required
     * @readonly
     */
    private Artifact projectArtifact;
    
    /**
     * @parameter default-value="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter default-value="${project.build.directory}/plugin-staging-backup"
     * @required
     */
    private File backupDirectory;

    /**
     * @component
     */
    private PluginManagerAccess pluginManagerAccess;
    
    public void execute()
        throws MojoExecutionException
    {
        PluginBackupManager backupManager = new PluginBackupManager( localRepository, getLog() );
        
        try
        {
            backupManager.restorePluginData( backupDirectory );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error un-staging plugin: " + e.getMessage(), e );
        }

        pluginManagerAccess.clearPluginData( projectArtifact );
    }
}
