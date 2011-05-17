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

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DefaultMAEExecutionRequest
    implements MAEExecutionRequest
{

    private Settings settings;

    private String password;

    private final DefaultMavenExecutionRequest embedded = new DefaultMavenExecutionRequest();

    public DefaultMAEExecutionRequest copyOf()
    {
        return new DefaultMAEExecutionRequest().setPasswordToEncrypt( getPasswordToEncyrpt() )
                                               .setSettings( getSettings() )
                                               .setBaseDirectory( new File( getBaseDirectory() ) )
                                               .setStartTime( getStartTime() )
                                               .setGoals( getGoals() )
                                               .setSystemProperties( getSystemProperties() )
                                               .setUserProperties( getUserProperties() )
                                               .setReactorFailureBehavior( getReactorFailureBehavior() )
                                               .setSelectedProjects( getSelectedProjects() )
                                               .setResumeFrom( getResumeFrom() )
                                               .setMakeBehavior( getMakeBehavior() )
                                               .setThreadCount( getThreadCount() )
                                               .setPerCoreThreadCount( isPerCoreThreadCount() )
                                               .setRecursive( isRecursive() )
                                               .setPom( getPom() )
                                               .setShowErrors( isShowErrors() )
                                               .setLoggingLevel( getLoggingLevel() )
                                               .setUpdateSnapshots( isUpdateSnapshots() )
                                               .setNoSnapshotUpdates( isNoSnapshotUpdates() )
                                               .setGlobalChecksumPolicy( getGlobalChecksumPolicy() )
                                               .setLocalRepositoryPath( getLocalRepositoryPath() )
                                               .setLocalRepositoryPath( getLocalRepositoryPath() )
                                               .setLocalRepository( getLocalRepository() )
                                               .setInteractiveMode( isInteractiveMode() )
                                               .setOffline( isOffline() )
                                               .setProfiles( getProfiles() )
                                               .setActiveProfiles( getActiveProfiles() )
                                               .setInactiveProfiles( getInactiveProfiles() )
                                               .setProxies( getProxies() )
                                               .setServers( getServers() )
                                               .setMirrors( getMirrors() )
                                               .setPluginGroups( getPluginGroups() )
                                               .setProjectPresent( isProjectPresent() )
                                               .setUserSettingsFile( getUserSettingsFile() )
                                               .setGlobalSettingsFile( getGlobalSettingsFile() )
                                               .setRemoteRepositories( getRemoteRepositories() )
                                               .setPluginArtifactRepositories( getPluginArtifactRepositories() )
                                               .setUserToolchainsFile( getUserToolchainsFile() )
                                               .setExecutionListener( getExecutionListener() );
    }

    public MavenExecutionRequest asMavenExecutionRequest()
    {
        return embedded;
    }

    public DefaultMAEExecutionRequest setPasswordToEncrypt( final String password )
    {
        this.password = password;
        return this;
    }

    public String getPasswordToEncyrpt()
    {
        return password;
    }

    public DefaultMAEExecutionRequest setSettings( final Settings settings )
    {
        this.settings = settings;
        return this;
    }

    public Settings getSettings()
    {
        return settings;
    }

    public DefaultMAEExecutionRequest addActiveProfile( final String profile )
    {
        embedded.addActiveProfile( profile );
        return this;
    }

    public DefaultMAEExecutionRequest addActiveProfiles( final List<String> profiles )
    {
        embedded.addActiveProfiles( profiles );
        return this;
    }

    public DefaultMAEExecutionRequest addInactiveProfile( final String profile )
    {
        embedded.addInactiveProfile( profile );
        return this;
    }

    public DefaultMAEExecutionRequest addInactiveProfiles( final List<String> profiles )
    {
        embedded.addInactiveProfiles( profiles );
        return this;
    }

    public DefaultMAEExecutionRequest addMirror( final Mirror mirror )
    {
        embedded.addMirror( mirror );
        return this;
    }

    public DefaultMAEExecutionRequest addPluginArtifactRepository( final ArtifactRepository repository )
    {
        embedded.addPluginArtifactRepository( repository );
        return this;
    }

    public DefaultMAEExecutionRequest addPluginGroup( final String pluginGroup )
    {
        embedded.addPluginGroup( pluginGroup );
        return this;
    }

    public DefaultMAEExecutionRequest addPluginGroups( final List<String> pluginGroups )
    {
        embedded.addPluginGroups( pluginGroups );
        return this;
    }

    public DefaultMAEExecutionRequest addProfile( final Profile profile )
    {
        embedded.addProfile( profile );
        return this;
    }

    public DefaultMAEExecutionRequest addProxy( final Proxy proxy )
    {
        embedded.addProxy( proxy );
        return this;
    }

    public DefaultMAEExecutionRequest addRemoteRepository( final ArtifactRepository repository )
    {
        embedded.addRemoteRepository( repository );
        return this;
    }

    public DefaultMAEExecutionRequest addServer( final Server server )
    {
        embedded.addServer( server );
        return this;
    }

    public List<String> getActiveProfiles()
    {
        return embedded.getActiveProfiles();
    }

    public String getBaseDirectory()
    {
        return embedded.getBaseDirectory();
    }

    public ExecutionListener getExecutionListener()
    {
        return embedded.getExecutionListener();
    }

    public String getGlobalChecksumPolicy()
    {
        return embedded.getGlobalChecksumPolicy();
    }

    public File getGlobalSettingsFile()
    {
        return embedded.getGlobalSettingsFile();
    }

    public List<String> getGoals()
    {
        return embedded.getGoals();
    }

    public List<String> getInactiveProfiles()
    {
        return embedded.getInactiveProfiles();
    }

    public ArtifactRepository getLocalRepository()
    {
        return embedded.getLocalRepository();
    }

    public File getLocalRepositoryPath()
    {
        return embedded.getLocalRepositoryPath();
    }

    public int getLoggingLevel()
    {
        return embedded.getLoggingLevel();
    }

    public String getMakeBehavior()
    {
        return embedded.getMakeBehavior();
    }

    public List<Mirror> getMirrors()
    {
        return embedded.getMirrors();
    }

    public List<ArtifactRepository> getPluginArtifactRepositories()
    {
        return embedded.getPluginArtifactRepositories();
    }

    public List<String> getPluginGroups()
    {
        return embedded.getPluginGroups();
    }

    public File getPom()
    {
        return embedded.getPom();
    }

    public List<Profile> getProfiles()
    {
        return embedded.getProfiles();
    }

    public ProjectBuildingRequest getProjectBuildingRequest()
    {
        return embedded.getProjectBuildingRequest();
    }

    public List<Proxy> getProxies()
    {
        return embedded.getProxies();
    }

    public String getReactorFailureBehavior()
    {
        return embedded.getReactorFailureBehavior();
    }

    public List<ArtifactRepository> getRemoteRepositories()
    {
        return embedded.getRemoteRepositories();
    }

    public String getResumeFrom()
    {
        return embedded.getResumeFrom();
    }

    public List<String> getSelectedProjects()
    {
        return embedded.getSelectedProjects();
    }

    public List<Server> getServers()
    {
        return embedded.getServers();
    }

    public Date getStartTime()
    {
        return embedded.getStartTime();
    }

    public Properties getSystemProperties()
    {
        return embedded.getSystemProperties();
    }

    public String getThreadCount()
    {
        return embedded.getThreadCount();
    }

    public Properties getUserProperties()
    {
        return embedded.getUserProperties();
    }

    public File getUserSettingsFile()
    {
        return embedded.getUserSettingsFile();
    }

    public File getUserToolchainsFile()
    {
        return embedded.getUserToolchainsFile();
    }

    public boolean isInteractiveMode()
    {
        return embedded.isInteractiveMode();
    }

    public boolean isNoSnapshotUpdates()
    {
        return embedded.isNoSnapshotUpdates();
    }

    public boolean isOffline()
    {
        return embedded.isOffline();
    }

    public boolean isPerCoreThreadCount()
    {
        return embedded.isPerCoreThreadCount();
    }

    public boolean isProjectPresent()
    {
        return embedded.isProjectPresent();
    }

    public boolean isRecursive()
    {
        return embedded.isRecursive();
    }

    public boolean isShowErrors()
    {
        return embedded.isShowErrors();
    }

    public boolean isThreadConfigurationPresent()
    {
        return embedded.isThreadConfigurationPresent();
    }

    public boolean isUpdateSnapshots()
    {
        return embedded.isUpdateSnapshots();
    }

    public DefaultMAEExecutionRequest setActiveProfiles( final List<String> activeProfiles )
    {
        embedded.setActiveProfiles( activeProfiles );
        return this;
    }

    public DefaultMAEExecutionRequest setBaseDirectory( final File basedir )
    {
        embedded.setBaseDirectory( basedir );
        return this;
    }

    public DefaultMAEExecutionRequest setExecutionListener( final ExecutionListener executionListener )
    {
        embedded.setExecutionListener( executionListener );
        return this;
    }

    public DefaultMAEExecutionRequest setGlobalChecksumPolicy( final String globalChecksumPolicy )
    {
        embedded.setGlobalChecksumPolicy( globalChecksumPolicy );
        return this;
    }

    public DefaultMAEExecutionRequest setGlobalSettingsFile( final File globalSettingsFile )
    {
        embedded.setGlobalSettingsFile( globalSettingsFile );
        return this;
    }

    public DefaultMAEExecutionRequest setGoals( final List<String> goals )
    {
        embedded.setGoals( goals );
        return this;
    }

    public DefaultMAEExecutionRequest setInactiveProfiles( final List<String> inactiveProfiles )
    {
        embedded.setInactiveProfiles( inactiveProfiles );
        return this;
    }

    public DefaultMAEExecutionRequest setInteractiveMode( final boolean interactive )
    {
        embedded.setInteractiveMode( interactive );
        return this;
    }

    public DefaultMAEExecutionRequest setLocalRepository( final ArtifactRepository localRepository )
    {
        embedded.setLocalRepository( localRepository );
        return this;
    }

    public DefaultMAEExecutionRequest setLocalRepositoryPath( final File localRepository )
    {
        embedded.setLocalRepositoryPath( localRepository );
        return this;
    }

    public DefaultMAEExecutionRequest setLocalRepositoryPath( final String localRepository )
    {
        embedded.setLocalRepositoryPath( localRepository );
        return this;
    }

    public DefaultMAEExecutionRequest setLoggingLevel( final int loggingLevel )
    {
        embedded.setLoggingLevel( loggingLevel );
        return this;
    }

    public DefaultMAEExecutionRequest setMakeBehavior( final String makeBehavior )
    {
        embedded.setMakeBehavior( makeBehavior );
        return this;
    }

    public DefaultMAEExecutionRequest setMirrors( final List<Mirror> mirrors )
    {
        embedded.setMirrors( mirrors );
        return this;
    }

    public DefaultMAEExecutionRequest setNoSnapshotUpdates( final boolean noSnapshotUpdates )
    {
        embedded.setNoSnapshotUpdates( noSnapshotUpdates );
        return this;
    }

    public DefaultMAEExecutionRequest setOffline( final boolean offline )
    {
        embedded.setOffline( offline );
        return this;
    }

    public DefaultMAEExecutionRequest setPerCoreThreadCount( final boolean perCoreThreadCount )
    {
        embedded.setPerCoreThreadCount( perCoreThreadCount );
        return this;
    }

    public DefaultMAEExecutionRequest setPluginArtifactRepositories( final List<ArtifactRepository> pluginArtifactRepositories )
    {
        embedded.setPluginArtifactRepositories( pluginArtifactRepositories );
        return this;
    }

    public DefaultMAEExecutionRequest setPluginGroups( final List<String> pluginGroups )
    {
        embedded.setPluginGroups( pluginGroups );
        return this;
    }

    public DefaultMAEExecutionRequest setPom( final File pom )
    {
        embedded.setPom( pom );
        return this;
    }

    public DefaultMAEExecutionRequest setProfiles( final List<Profile> profiles )
    {
        embedded.setProfiles( profiles );
        return this;
    }

    public void setProjectBuildingConfiguration( final ProjectBuildingRequest projectBuildingConfiguration )
    {
        embedded.setProjectBuildingConfiguration( projectBuildingConfiguration );
    }

    public DefaultMAEExecutionRequest setProjectPresent( final boolean projectPresent )
    {
        embedded.setProjectPresent( projectPresent );
        return this;
    }

    public DefaultMAEExecutionRequest setProxies( final List<Proxy> proxies )
    {
        embedded.setProxies( proxies );
        return this;
    }

    public DefaultMAEExecutionRequest setReactorFailureBehavior( final String failureBehavior )
    {
        embedded.setReactorFailureBehavior( failureBehavior );
        return this;
    }

    public DefaultMAEExecutionRequest setRecursive( final boolean recursive )
    {
        embedded.setRecursive( recursive );
        return this;
    }

    public DefaultMAEExecutionRequest setRemoteRepositories( final List<ArtifactRepository> remoteRepositories )
    {
        embedded.setRemoteRepositories( remoteRepositories );
        return this;
    }

    public DefaultMAEExecutionRequest setResumeFrom( final String project )
    {
        embedded.setResumeFrom( project );
        return this;
    }

    public DefaultMAEExecutionRequest setSelectedProjects( final List<String> selectedProjects )
    {
        embedded.setSelectedProjects( selectedProjects );
        return this;
    }

    public DefaultMAEExecutionRequest setServers( final List<Server> servers )
    {
        embedded.setServers( servers );
        return this;
    }

    public DefaultMAEExecutionRequest setShowErrors( final boolean showErrors )
    {
        embedded.setShowErrors( showErrors );
        return this;
    }

    public DefaultMAEExecutionRequest setStartTime( final Date startTime )
    {
        embedded.setStartTime( startTime );
        return this;
    }

    public DefaultMAEExecutionRequest setSystemProperties( final Properties properties )
    {
        embedded.setSystemProperties( properties );
        return this;
    }

    public DefaultMAEExecutionRequest setThreadCount( final String threadCount )
    {
        embedded.setThreadCount( threadCount );
        return this;
    }

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

    public DefaultMAEExecutionRequest setUserProperties( final Properties userProperties )
    {
        embedded.setUserProperties( userProperties );
        return this;
    }

    public DefaultMAEExecutionRequest setUserSettingsFile( final File userSettingsFile )
    {
        embedded.setUserSettingsFile( userSettingsFile );
        return this;
    }

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
