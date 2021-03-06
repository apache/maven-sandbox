/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.mae;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.model.Profile;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

public class DefaultMAEExecutionRequest
    implements MAEExecutionRequest
{

    private Settings settings;

    private String password;

    private final DefaultMavenExecutionRequest embedded = new DefaultMavenExecutionRequest();

    // public DefautMAEExecutionRequest()
    // {
    // }

    @Override
    public DefaultMAEExecutionRequest copyOf()
    {
        return new DefaultMAEExecutionRequest().setPasswordToEncrypt( getPasswordToEncyrpt() ).setSettings( getSettings() ).setBaseDirectory( new File(
                                                                                                                                                        getBaseDirectory() ) ).setStartTime( getStartTime() ).setGoals( getGoals() ).setSystemProperties( getSystemProperties() ).setUserProperties( getUserProperties() ).setReactorFailureBehavior( getReactorFailureBehavior() ).setSelectedProjects( getSelectedProjects() ).setResumeFrom( getResumeFrom() ).setMakeBehavior( getMakeBehavior() ).setThreadCount( getThreadCount() ).setPerCoreThreadCount( isPerCoreThreadCount() ).setRecursive( isRecursive() ).setPom( getPom() ).setShowErrors( isShowErrors() ).setLoggingLevel( getLoggingLevel() ).setUpdateSnapshots( isUpdateSnapshots() ).setNoSnapshotUpdates( isNoSnapshotUpdates() ).setGlobalChecksumPolicy( getGlobalChecksumPolicy() ).setLocalRepositoryPath( getLocalRepositoryPath() ).setLocalRepositoryPath( getLocalRepositoryPath() ).setLocalRepository( getLocalRepository() ).setInteractiveMode( isInteractiveMode() ).setOffline( isOffline() ).setProfiles( getProfiles() ).setActiveProfiles( getActiveProfiles() ).setInactiveProfiles( getInactiveProfiles() ).setProxies( getProxies() ).setServers( getServers() ).setMirrors( getMirrors() ).setPluginGroups( getPluginGroups() ).setProjectPresent( isProjectPresent() ).setUserSettingsFile( getUserSettingsFile() ).setGlobalSettingsFile( getGlobalSettingsFile() ).setRemoteRepositories( getRemoteRepositories() ).setPluginArtifactRepositories( getPluginArtifactRepositories() ).setUserToolchainsFile( getUserToolchainsFile() ).setExecutionListener( getExecutionListener() );
    }

    @Override
    public MavenExecutionRequest asMavenExecutionRequest()
    {
        return embedded;
    }

    @Override
    public DefaultMAEExecutionRequest setPasswordToEncrypt( final String password )
    {
        this.password = password;
        return this;
    }

    @Override
    public String getPasswordToEncyrpt()
    {
        return password;
    }

    @Override
    public DefaultMAEExecutionRequest setSettings( final Settings settings )
    {
        this.settings = settings;
        return this;
    }

    @Override
    public Settings getSettings()
    {
        return settings;
    }

    @Override
    public DefaultMAEExecutionRequest addActiveProfile( final String profile )
    {
        embedded.addActiveProfile( profile );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addActiveProfiles( final List<String> profiles )
    {
        embedded.addActiveProfiles( profiles );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addInactiveProfile( final String profile )
    {
        embedded.addInactiveProfile( profile );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addInactiveProfiles( final List<String> profiles )
    {
        embedded.addInactiveProfiles( profiles );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addMirror( final Mirror mirror )
    {
        embedded.addMirror( mirror );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addPluginArtifactRepository( final ArtifactRepository repository )
    {
        embedded.addPluginArtifactRepository( repository );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addPluginGroup( final String pluginGroup )
    {
        embedded.addPluginGroup( pluginGroup );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addPluginGroups( final List<String> pluginGroups )
    {
        embedded.addPluginGroups( pluginGroups );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addProfile( final Profile profile )
    {
        embedded.addProfile( profile );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addProxy( final Proxy proxy )
    {
        embedded.addProxy( proxy );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addRemoteRepository( final ArtifactRepository repository )
    {
        embedded.addRemoteRepository( repository );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest addServer( final Server server )
    {
        embedded.addServer( server );
        return this;
    }

    @Override
    public List<String> getActiveProfiles()
    {
        return embedded.getActiveProfiles();
    }

    @Override
    public String getBaseDirectory()
    {
        return embedded.getBaseDirectory();
    }

    @Override
    public ExecutionListener getExecutionListener()
    {
        return embedded.getExecutionListener();
    }

    @Override
    public String getGlobalChecksumPolicy()
    {
        return embedded.getGlobalChecksumPolicy();
    }

    @Override
    public File getGlobalSettingsFile()
    {
        return embedded.getGlobalSettingsFile();
    }

    @Override
    public List<String> getGoals()
    {
        return embedded.getGoals();
    }

    @Override
    public List<String> getInactiveProfiles()
    {
        return embedded.getInactiveProfiles();
    }

    @Override
    public ArtifactRepository getLocalRepository()
    {
        return embedded.getLocalRepository();
    }

    @Override
    public File getLocalRepositoryPath()
    {
        return embedded.getLocalRepositoryPath();
    }

    @Override
    public int getLoggingLevel()
    {
        return embedded.getLoggingLevel();
    }

    @Override
    public String getMakeBehavior()
    {
        return embedded.getMakeBehavior();
    }

    @Override
    public List<Mirror> getMirrors()
    {
        return embedded.getMirrors();
    }

    @Override
    public List<ArtifactRepository> getPluginArtifactRepositories()
    {
        return embedded.getPluginArtifactRepositories();
    }

    @Override
    public List<String> getPluginGroups()
    {
        return embedded.getPluginGroups();
    }

    @Override
    public File getPom()
    {
        return embedded.getPom();
    }

    @Override
    public List<Profile> getProfiles()
    {
        return embedded.getProfiles();
    }

    @Override
    public ProjectBuildingRequest getProjectBuildingRequest()
    {
        return embedded.getProjectBuildingRequest();
    }

    @Override
    public List<Proxy> getProxies()
    {
        return embedded.getProxies();
    }

    @Override
    public String getReactorFailureBehavior()
    {
        return embedded.getReactorFailureBehavior();
    }

    @Override
    public List<ArtifactRepository> getRemoteRepositories()
    {
        return embedded.getRemoteRepositories();
    }

    @Override
    public String getResumeFrom()
    {
        return embedded.getResumeFrom();
    }

    @Override
    public List<String> getSelectedProjects()
    {
        return embedded.getSelectedProjects();
    }

    @Override
    public List<Server> getServers()
    {
        return embedded.getServers();
    }

    @Override
    public Date getStartTime()
    {
        return embedded.getStartTime();
    }

    @Override
    public Properties getSystemProperties()
    {
        return embedded.getSystemProperties();
    }

    @Override
    public String getThreadCount()
    {
        return embedded.getThreadCount();
    }

    @Override
    public Properties getUserProperties()
    {
        return embedded.getUserProperties();
    }

    @Override
    public File getUserSettingsFile()
    {
        return embedded.getUserSettingsFile();
    }

    @Override
    public File getUserToolchainsFile()
    {
        return embedded.getUserToolchainsFile();
    }

    @Override
    public boolean isInteractiveMode()
    {
        return embedded.isInteractiveMode();
    }

    @Override
    public boolean isNoSnapshotUpdates()
    {
        return embedded.isNoSnapshotUpdates();
    }

    @Override
    public boolean isOffline()
    {
        return embedded.isOffline();
    }

    @Override
    public boolean isPerCoreThreadCount()
    {
        return embedded.isPerCoreThreadCount();
    }

    @Override
    public boolean isProjectPresent()
    {
        return embedded.isProjectPresent();
    }

    @Override
    public boolean isRecursive()
    {
        return embedded.isRecursive();
    }

    @Override
    public boolean isShowErrors()
    {
        return embedded.isShowErrors();
    }

    @Override
    public boolean isThreadConfigurationPresent()
    {
        return embedded.isThreadConfigurationPresent();
    }

    @Override
    public boolean isUpdateSnapshots()
    {
        return embedded.isUpdateSnapshots();
    }

    @Override
    public DefaultMAEExecutionRequest setActiveProfiles( final List<String> activeProfiles )
    {
        embedded.setActiveProfiles( activeProfiles );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setBaseDirectory( final File basedir )
    {
        embedded.setBaseDirectory( basedir );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setExecutionListener( final ExecutionListener executionListener )
    {
        embedded.setExecutionListener( executionListener );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setGlobalChecksumPolicy( final String globalChecksumPolicy )
    {
        embedded.setGlobalChecksumPolicy( globalChecksumPolicy );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setGlobalSettingsFile( final File globalSettingsFile )
    {
        embedded.setGlobalSettingsFile( globalSettingsFile );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setGoals( final List<String> goals )
    {
        embedded.setGoals( goals );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setInactiveProfiles( final List<String> inactiveProfiles )
    {
        embedded.setInactiveProfiles( inactiveProfiles );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setInteractiveMode( final boolean interactive )
    {
        embedded.setInteractiveMode( interactive );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setLocalRepository( final ArtifactRepository localRepository )
    {
        embedded.setLocalRepository( localRepository );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setLocalRepositoryPath( final File localRepository )
    {
        embedded.setLocalRepositoryPath( localRepository );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setLocalRepositoryPath( final String localRepository )
    {
        embedded.setLocalRepositoryPath( localRepository );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setLoggingLevel( final int loggingLevel )
    {
        embedded.setLoggingLevel( loggingLevel );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setMakeBehavior( final String makeBehavior )
    {
        embedded.setMakeBehavior( makeBehavior );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setMirrors( final List<Mirror> mirrors )
    {
        embedded.setMirrors( mirrors );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setNoSnapshotUpdates( final boolean noSnapshotUpdates )
    {
        embedded.setNoSnapshotUpdates( noSnapshotUpdates );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setOffline( final boolean offline )
    {
        embedded.setOffline( offline );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setPerCoreThreadCount( final boolean perCoreThreadCount )
    {
        embedded.setPerCoreThreadCount( perCoreThreadCount );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setPluginArtifactRepositories( final List<ArtifactRepository> pluginArtifactRepositories )
    {
        embedded.setPluginArtifactRepositories( pluginArtifactRepositories );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setPluginGroups( final List<String> pluginGroups )
    {
        embedded.setPluginGroups( pluginGroups );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setPom( final File pom )
    {
        embedded.setPom( pom );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setProfiles( final List<Profile> profiles )
    {
        embedded.setProfiles( profiles );
        return this;
    }

    public void setProjectBuildingConfiguration( final ProjectBuildingRequest projectBuildingConfiguration )
    {
        embedded.setProjectBuildingConfiguration( projectBuildingConfiguration );
    }

    @Override
    public DefaultMAEExecutionRequest setProjectPresent( final boolean projectPresent )
    {
        embedded.setProjectPresent( projectPresent );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setProxies( final List<Proxy> proxies )
    {
        embedded.setProxies( proxies );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setReactorFailureBehavior( final String failureBehavior )
    {
        embedded.setReactorFailureBehavior( failureBehavior );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setRecursive( final boolean recursive )
    {
        embedded.setRecursive( recursive );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setRemoteRepositories( final List<ArtifactRepository> remoteRepositories )
    {
        embedded.setRemoteRepositories( remoteRepositories );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setResumeFrom( final String project )
    {
        embedded.setResumeFrom( project );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setSelectedProjects( final List<String> selectedProjects )
    {
        embedded.setSelectedProjects( selectedProjects );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setServers( final List<Server> servers )
    {
        embedded.setServers( servers );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setShowErrors( final boolean showErrors )
    {
        embedded.setShowErrors( showErrors );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setStartTime( final Date startTime )
    {
        embedded.setStartTime( startTime );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setSystemProperties( final Properties properties )
    {
        embedded.setSystemProperties( properties );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setThreadCount( final String threadCount )
    {
        embedded.setThreadCount( threadCount );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setUpdateSnapshots( final boolean updateSnapshots )
    {
        embedded.setUpdateSnapshots( updateSnapshots );
        return this;
    }

    public DefaultMAEExecutionRequest setUseReactor( final boolean reactorActive )
    {
        embedded.setUseReactor( reactorActive );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setUserProperties( final Properties userProperties )
    {
        embedded.setUserProperties( userProperties );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setUserSettingsFile( final File userSettingsFile )
    {
        embedded.setUserSettingsFile( userSettingsFile );
        return this;
    }

    @Override
    public DefaultMAEExecutionRequest setUserToolchainsFile( final File userToolchainsFile )
    {
        embedded.setUserToolchainsFile( userToolchainsFile );
        return this;
    }

    public boolean useReactor()
    {
        return embedded.useReactor();
    }

    @Override
    public MAEExecutionRequest withPluginGoal( final PluginGoal goal )
    {
        embedded.getGoals().add( goal.formatCliGoal() );
        return this;
    }

    @Override
    public MAEExecutionRequest withPluginGoals( final PluginGoal... goals )
    {
        for ( final PluginGoal goal : goals )
        {
            embedded.getGoals().add( goal.formatCliGoal() );
        }

        return this;
    }

    @Override
    public MAEExecutionRequest setSystemProperty( final String key, final String value )
    {
        embedded.getSystemProperties().setProperty( key, value );
        return this;
    }

    @Override
    public MAEExecutionRequest setUserProperty( final String key, final String value )
    {
        embedded.getUserProperties().setProperty( key, value );
        return this;
    }

}
