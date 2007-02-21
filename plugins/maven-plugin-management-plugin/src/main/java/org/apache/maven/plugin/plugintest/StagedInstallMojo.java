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
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.plugintest.backup.PluginBackupManager;
import org.apache.maven.plugin.plugintest.manager.PluginManagerAccess;
import org.apache.maven.plugin.plugintest.stage.PluginStagingException;
import org.apache.maven.plugin.plugintest.stage.PluginStagingManager;

import java.io.File;
import java.io.IOException;

/**
 * Stages a plugin for integration testing.
 * 
 * @goal stage
 * 
 * @phase pre-integration-test
 */
public class StagedInstallMojo
    extends AbstractMojo
{

    /**
     * @parameter default-value="${project.artifact}"
     * @required
     * @readonly
     */
    private Artifact projectArtifact;

    /**
     * @parameter default-value="${project.file}"
     * @required
     * @readonly
     */
    private File pomFile;

    /**
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * @parameter default-value="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @component
     */
    private ArtifactInstaller artifactInstaller;

    /**
     * @parameter
     */
    private String goalPrefix;

    /**
     * @parameter default-value="${project.name}"
     * @required
     * @readonly
     */
    private String projectName;

    /**
     * @parameter default-value="${project.packaging}"
     * @required
     * @readonly
     */
    private String projectPackaging;

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
        // 1. backup the local-repository section for the plugin...put it in a place that's easy to restore from.
        PluginBackupManager backupManager = new PluginBackupManager( localRepository, getLog() );

        try
        {
            backupManager.backupInstalledPluginData( projectArtifact, backupDirectory );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error backing up plugin for testing.", e );
        }

        // 2. create a new artifact, to attach metadata to it.
        PluginStagingManager stagingManager =
            new PluginStagingManager( localRepository, artifactFactory, artifactInstaller, getLog() );

        Artifact artifact = stagingManager.duplicateProjectArtifact( projectArtifact, projectPackaging );

        // 3. add the plugin metadata for 'installing' into the local repo
        stagingManager.addPluginMetadata( artifact, projectName, goalPrefix );

        // 4. install the current plugin into the local repository
        try
        {
            stagingManager.installPlugin( artifact, pomFile );
        }
        catch ( PluginStagingException e )
        {
            try
            {
                backupManager.restorePluginData( backupDirectory );
            }
            catch ( IOException ioe )
            {
                getLog().error( "Error restoring plugin data backed up to: \'" + backupDirectory + "\' on plugin staging failure.", ioe );
            }
            
            throw new MojoExecutionException( "Error staging plugin: " + e.getMessage(), e );
        }

        // 5. clean up any plugin containers associated with this plugin's info...so it will reload
        pluginManagerAccess.clearPluginData( projectArtifact );
    }

}
