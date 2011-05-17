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

public interface MAEExecutionRequest
{

    MAEExecutionRequest copyOf();

    MavenExecutionRequest asMavenExecutionRequest();

    MAEExecutionRequest setPasswordToEncrypt( final String password );

    String getPasswordToEncyrpt();

    MAEExecutionRequest setSettings( final Settings settings );

    Settings getSettings();

    // NOTE: Methods below are adapted from MavenExecutionRequest

    // Base directory
    MAEExecutionRequest setBaseDirectory( File basedir );

    String getBaseDirectory();

    // Timing (remove this)
    MAEExecutionRequest setStartTime( Date start );

    Date getStartTime();

    // Goals
    MAEExecutionRequest withPluginGoals( PluginGoal... goal );

    MAEExecutionRequest withPluginGoal( PluginGoal goal );

    MAEExecutionRequest setGoals( List<String> goals );

    List<String> getGoals();

    // Properties

    /**
     * Sets the system properties to use for interpolation and profile activation. The system properties are collected
     * from the runtime environment like {@link System#getProperties()} and environment variables.
     * 
     * @param systemProperties
     *            The system properties, may be {@code null}.
     * @return This request, never {@code null}.
     */
    MAEExecutionRequest setSystemProperties( Properties systemProperties );

    MAEExecutionRequest setSystemProperty( String key, String value );

    /**
     * Gets the system properties to use for interpolation and profile activation. The system properties are collected
     * from the runtime environment like {@link System#getProperties()} and environment variables.
     * 
     * @return The system properties, never {@code null}.
     */
    Properties getSystemProperties();

    /**
     * Sets the user properties to use for interpolation and profile activation. The user properties have been
     * configured directly by the user on his discretion, e.g. via the {@code -Dkey=value} parameter on the command
     * line.
     * 
     * @param userProperties
     *            The user properties, may be {@code null}.
     * @return This request, never {@code null}.
     */
    MAEExecutionRequest setUserProperties( Properties userProperties );

    MAEExecutionRequest setUserProperty( String key, String value );

    /**
     * Gets the user properties to use for interpolation and profile activation. The user properties have been
     * configured directly by the user on his discretion, e.g. via the {@code -Dkey=value} parameter on the command
     * line.
     * 
     * @return The user properties, never {@code null}.
     */
    Properties getUserProperties();

    // Reactor
    MAEExecutionRequest setReactorFailureBehavior( String failureBehavior );

    String getReactorFailureBehavior();

    MAEExecutionRequest setSelectedProjects( List<String> projects );

    List<String> getSelectedProjects();

    MAEExecutionRequest setResumeFrom( String project );

    String getResumeFrom();

    MAEExecutionRequest setMakeBehavior( String makeBehavior );

    String getMakeBehavior();

    MAEExecutionRequest setThreadCount( String threadCount );

    String getThreadCount();

    boolean isThreadConfigurationPresent();

    MAEExecutionRequest setPerCoreThreadCount( boolean perCoreThreadCount );

    boolean isPerCoreThreadCount();

    // Recursive (really to just process the top-level POM)
    MAEExecutionRequest setRecursive( boolean recursive );

    boolean isRecursive();

    MAEExecutionRequest setPom( File pom );

    File getPom();

    // Errors
    MAEExecutionRequest setShowErrors( boolean showErrors );

    boolean isShowErrors();

    // Logging
    MAEExecutionRequest setLoggingLevel( int loggingLevel );

    int getLoggingLevel();

    // Update snapshots
    MAEExecutionRequest setUpdateSnapshots( boolean updateSnapshots );

    boolean isUpdateSnapshots();

    MAEExecutionRequest setNoSnapshotUpdates( boolean noSnapshotUpdates );

    boolean isNoSnapshotUpdates();

    // Checksum policy
    MAEExecutionRequest setGlobalChecksumPolicy( String globalChecksumPolicy );

    String getGlobalChecksumPolicy();

    // Local repository
    MAEExecutionRequest setLocalRepositoryPath( String localRepository );

    MAEExecutionRequest setLocalRepositoryPath( File localRepository );

    File getLocalRepositoryPath();

    MAEExecutionRequest setLocalRepository( ArtifactRepository repository );

    ArtifactRepository getLocalRepository();

    // Interactive
    MAEExecutionRequest setInteractiveMode( boolean interactive );

    boolean isInteractiveMode();

    // Offline
    MAEExecutionRequest setOffline( boolean offline );

    boolean isOffline();

    // Profiles
    List<Profile> getProfiles();

    MAEExecutionRequest addProfile( Profile profile );

    MAEExecutionRequest setProfiles( List<Profile> profiles );

    MAEExecutionRequest addActiveProfile( String profile );

    MAEExecutionRequest addActiveProfiles( List<String> profiles );

    MAEExecutionRequest setActiveProfiles( List<String> profiles );

    List<String> getActiveProfiles();

    MAEExecutionRequest addInactiveProfile( String profile );

    MAEExecutionRequest addInactiveProfiles( List<String> profiles );

    MAEExecutionRequest setInactiveProfiles( List<String> profiles );

    List<String> getInactiveProfiles();

    // Proxies
    List<Proxy> getProxies();

    MAEExecutionRequest setProxies( List<Proxy> proxies );

    MAEExecutionRequest addProxy( Proxy proxy );

    // Servers
    List<Server> getServers();

    MAEExecutionRequest setServers( List<Server> servers );

    MAEExecutionRequest addServer( Server server );

    // Mirrors
    List<Mirror> getMirrors();

    MAEExecutionRequest setMirrors( List<Mirror> mirrors );

    MAEExecutionRequest addMirror( Mirror mirror );

    // Plugin groups
    List<String> getPluginGroups();

    MAEExecutionRequest setPluginGroups( List<String> pluginGroups );

    MAEExecutionRequest addPluginGroup( String pluginGroup );

    MAEExecutionRequest addPluginGroups( List<String> pluginGroups );

    boolean isProjectPresent();

    MAEExecutionRequest setProjectPresent( boolean isProjectPresent );

    File getUserSettingsFile();

    MAEExecutionRequest setUserSettingsFile( File userSettingsFile );

    File getGlobalSettingsFile();

    MAEExecutionRequest setGlobalSettingsFile( File globalSettingsFile );

    MAEExecutionRequest addRemoteRepository( ArtifactRepository repository );

    MAEExecutionRequest addPluginArtifactRepository( ArtifactRepository repository );

    /**
     * Set a new list of remote repositories to use the execution request. This is necessary if you perform
     * transformations on the remote repositories being used. For example if you replace existing repositories with
     * mirrors then it's easier to just replace the whole list with a new list of transformed repositories.
     * 
     * @param repositories
     * @return
     */
    MAEExecutionRequest setRemoteRepositories( List<ArtifactRepository> repositories );

    List<ArtifactRepository> getRemoteRepositories();

    MAEExecutionRequest setPluginArtifactRepositories( List<ArtifactRepository> repositories );

    List<ArtifactRepository> getPluginArtifactRepositories();

    File getUserToolchainsFile();

    MAEExecutionRequest setUserToolchainsFile( File userToolchainsFile );

    ExecutionListener getExecutionListener();

    MAEExecutionRequest setExecutionListener( ExecutionListener executionListener );

    ProjectBuildingRequest getProjectBuildingRequest();

}
