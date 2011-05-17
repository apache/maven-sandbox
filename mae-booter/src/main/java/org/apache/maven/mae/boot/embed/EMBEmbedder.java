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

package org.apache.maven.mae.boot.embed;

import static org.apache.maven.mae.conf.MAELibraries.loadLibraries;

import org.apache.maven.Maven;
import org.apache.maven.cli.CLIReportingUtils;
import org.apache.maven.exception.DefaultExceptionHandler;
import org.apache.maven.exception.ExceptionHandler;
import org.apache.maven.exception.ExceptionSummary;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.mae.MAEExecutionRequest;
import org.apache.maven.mae.boot.log.EventLogger;
import org.apache.maven.mae.boot.main.EMBMain;
import org.apache.maven.mae.boot.services.EMBServiceManager;
import org.apache.maven.mae.conf.MAEConfiguration;
import org.apache.maven.mae.conf.MAELibrary;
import org.apache.maven.mae.conf.loader.MAELibraryLoader;
import org.apache.maven.mae.conf.mgmt.MAEManagementException;
import org.apache.maven.mae.conf.mgmt.MAEManagementView;
import org.apache.maven.mae.conf.mgmt.LoadOnFinish;
import org.apache.maven.mae.conf.mgmt.LoadOnStart;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ExtrudablePlexusContainer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.properties.internal.EnvironmentUtils;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.apache.maven.settings.building.SettingsProblem;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.cipher.PlexusCipherException;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;
import org.sonatype.plexus.components.sec.dispatcher.SecUtil;
import org.sonatype.plexus.components.sec.dispatcher.model.SettingsSecurity;

import com.google.inject.Injector;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Component( role = EMBEmbedder.class )
public class EMBEmbedder
{

    private static boolean embInfoShown;

    private final Logger logger;

    private final PrintStream standardOut;

    private final boolean shouldShowErrors;

    private final Maven maven;

    private final boolean showVersion;

    private final ExtrudablePlexusContainer container;

    private final MAEConfiguration embConfiguration;

    private final SettingsBuilder settingsBuilder;

    private final MavenExecutionRequestPopulator executionRequestPopulator;

    private final DefaultSecDispatcher securityDispatcher;

    private transient final EMBServiceManager serviceManager;

    private final List<MAELibraryLoader> libraryLoaders;

    private boolean infoPrinted = false;

    EMBEmbedder( final Maven maven, final MAEConfiguration embConfiguration, final ExtrudablePlexusContainer container,
                 final SettingsBuilder settingsBuilder, final MavenExecutionRequestPopulator executionRequestPopulator,
                 final DefaultSecDispatcher securityDispatcher, final EMBServiceManager serviceManager,
                 final List<MAELibraryLoader> libraryLoaders, final PrintStream standardOut, final Logger logger,
                 final boolean shouldShowErrors, final boolean showVersion )
    {
        this.maven = maven;
        this.embConfiguration = embConfiguration;
        this.container = container;

        this.settingsBuilder = settingsBuilder;
        this.executionRequestPopulator = executionRequestPopulator;
        this.securityDispatcher = securityDispatcher;
        this.serviceManager = serviceManager;
        this.libraryLoaders = libraryLoaders;
        this.standardOut = standardOut;
        this.logger = logger;
        this.shouldShowErrors = shouldShowErrors;
        this.showVersion = showVersion;
    }

    public synchronized Injector injector()
        throws EMBEmbeddingException
    {
        printInfo( null );
        return container.getInjector();
    }

    public synchronized Map<Object, Throwable> wire( final Object... instances )
        throws EMBEmbeddingException
    {
        printInfo( null );
        return container.extrudeDependencies( instances );
    }

    public synchronized EMBServiceManager serviceManager()
        throws EMBEmbeddingException
    {
        printInfo( null );
        return serviceManager;
    }

    public MavenExecutionResult execute( final MAEExecutionRequest request )
        throws EMBEmbeddingException
    {
        final PrintStream oldOut = System.out;
        try
        {
            if ( standardOut != null )
            {
                System.setOut( standardOut );
            }

            doExecutionStarting();

            injectEnvironment( request );
            printInfo( request );
            return maven.execute( request.asMavenExecutionRequest() );
        }
        finally
        {
            doExecutionFinished();
            System.setOut( oldOut );
        }
    }

    public String encryptMasterPassword( final MAEExecutionRequest request )
        throws EMBEmbeddingException
    {
        printInfo( null );

        String passwd = request.getPasswordToEncyrpt();
        if ( passwd == null )
        {
            passwd = "";
        }

        try
        {
            final DefaultPlexusCipher cipher = new DefaultPlexusCipher();

            final String result = cipher.encryptAndDecorate( passwd, DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION );
            logger.info( result );

            return result;
        }
        catch ( final PlexusCipherException e )
        {
            throw new EMBEmbeddingException( "Failed to encrypt master password: {0}", e, e.getMessage() );
        }
    }

    public String encryptPassword( final MAEExecutionRequest request )
        throws EMBEmbeddingException
    {
        printInfo( null );

        final String passwd = request.getPasswordToEncyrpt();

        String configurationFile = securityDispatcher.getConfigurationFile();

        if ( configurationFile.startsWith( "~" ) )
        {
            configurationFile = System.getProperty( "user.home" ) + configurationFile.substring( 1 );
        }

        final String file = System.getProperty( DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION, configurationFile );

        String master = null;

        try
        {
            final SettingsSecurity sec = SecUtil.read( file, true );
            if ( sec != null )
            {
                master = sec.getMaster();
            }

            if ( master == null )
            {
                throw new IllegalStateException( "Master password is not set in the setting security file: " + file );
            }

            final DefaultPlexusCipher cipher = new DefaultPlexusCipher();
            final String masterPasswd =
                cipher.decryptDecorated( master, DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION );

            final String result = cipher.encryptAndDecorate( passwd, masterPasswd );
            logger.info( result );

            return result;
        }
        catch ( final PlexusCipherException e )
        {
            throw new EMBEmbeddingException( "Failed to encrypt password: {0}", e, e.getMessage() );
        }
        catch ( final SecDispatcherException e )
        {
            throw new EMBEmbeddingException( "Failed to encrypt password: {0}", e, e.getMessage() );
        }
    }

    protected void doExecutionStarting()
        throws EMBEmbeddingException
    {
        for ( final MAELibrary library : embConfiguration.getLibraries() )
        {
            final Set<ComponentKey<?>> components = library.getManagementComponents( LoadOnStart.class );
            if ( components != null && !components.isEmpty() )
            {
                final MAEManagementView mgmtView = new EmbedderManagementView( container, embConfiguration );
                for ( final ComponentKey<?> key : components )
                {
                    try
                    {
                        final LoadOnStart los = (LoadOnStart) container.lookup( key.getRole(), key.getHint() );
                        los.executionStarting( mgmtView );
                    }
                    catch ( final ComponentLookupException e )
                    {
                        throw new EMBEmbeddingException(
                                                         "Failed to lookup load-on-start component for initialization: %s.\nReason: %s",
                                                         e, key, e.getMessage() );
                    }
                }
            }
        }
    }

    protected void doExecutionFinished()
    {
        for ( final MAELibrary library : embConfiguration.getLibraries() )
        {
            final Set<ComponentKey<?>> components = library.getManagementComponents( LoadOnFinish.class );
            if ( components != null && !components.isEmpty() )
            {
                final MAEManagementView mgmtView = new EmbedderManagementView( container, embConfiguration );
                for ( final ComponentKey<?> key : components )
                {
                    try
                    {
                        final LoadOnFinish lof = (LoadOnFinish) container.lookup( key.getRole(), key.getHint() );
                        lof.executionFinished( mgmtView );
                    }
                    catch ( final ComponentLookupException e )
                    {
                        logger.error( String.format( "Failed to lookup load-on-start component for initialization: %s.\nReason: %s",
                                                     key, e.getMessage() ), e );
                    }
                }
            }
        }
    }

    protected synchronized void injectEnvironment( final MAEExecutionRequest request )
        throws EMBEmbeddingException
    {
        injectLogSettings( request );

        initializeEMB( request );

        injectProperties( request );

        injectSettings( request );

        injectFromProperties( request );
    }

    private void initializeEMB( final MAEExecutionRequest request )
    {
        embConfiguration.withEMBExecutionRequest( request );
        if ( request.isInteractiveMode() )
        {
            embConfiguration.interactive();
        }
        else
        {
            embConfiguration.nonInteractive();
        }

        if ( Logger.LEVEL_DEBUG == request.getLoggingLevel() )
        {
            embConfiguration.withDebug();
        }
        else
        {
            embConfiguration.withoutDebug();
        }
    }

    protected void injectFromProperties( final MAEExecutionRequest request )
    {
        String localRepoProperty = request.getUserProperties().getProperty( EMBMain.LOCAL_REPO_PROPERTY );

        if ( localRepoProperty == null )
        {
            localRepoProperty = request.getSystemProperties().getProperty( EMBMain.LOCAL_REPO_PROPERTY );
        }

        if ( localRepoProperty != null )
        {
            request.setLocalRepositoryPath( localRepoProperty );
        }
    }

    protected void injectLogSettings( final MAEExecutionRequest request )
    {
        final int logLevel = request.getLoggingLevel();

        if ( Logger.LEVEL_DEBUG == logLevel )
        {
            embConfiguration.withDebug();
        }
        else
        {
            embConfiguration.withoutDebug();
        }

        logger.setThreshold( logLevel );
        container.getLoggerManager().setThresholds( request.getLoggingLevel() );

        // final Configurator log4jConfigurator = new Configurator()
        // {
        // @SuppressWarnings( "unchecked" )
        // public void doConfigure( final URL notUsed, final LoggerRepository repo )
        // {
        // final Enumeration<org.apache.log4j.Logger> loggers = repo.getCurrentLoggers();
        // while ( loggers.hasMoreElements() )
        // {
        // final org.apache.log4j.Logger logger = loggers.nextElement();
        // if ( Logger.LEVEL_DEBUG == logLevel )
        // {
        // logger.setLevel( Level.DEBUG );
        // }
        // else if ( Logger.LEVEL_ERROR == logLevel )
        // {
        // logger.setLevel( Level.ERROR );
        // }
        // }
        // }
        // };
        //
        // log4jConfigurator.doConfigure( null, LogManager.getLoggerRepository() );

        request.setExecutionListener( new EventLogger( logger ) );
    }

    protected void injectProperties( final MAEExecutionRequest request )
    {
        final Properties systemProperties = new Properties();

        EnvironmentUtils.addEnvVars( systemProperties );
        systemProperties.putAll( System.getProperties() );

        if ( request.getSystemProperties() != null )
        {
            systemProperties.putAll( request.getSystemProperties() );
        }

        request.setSystemProperties( systemProperties );
    }

    protected void injectSettings( final MAEExecutionRequest request )
        throws EMBEmbeddingException
    {
        Settings settings = request.getSettings();
        SettingsBuildingResult settingsResult = null;
        if ( settings == null )
        {
            final SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();
            settingsRequest.setGlobalSettingsFile( request.getGlobalSettingsFile() );
            settingsRequest.setUserSettingsFile( request.getUserSettingsFile() );

            settingsRequest.setSystemProperties( request.getSystemProperties() );
            settingsRequest.setUserProperties( request.getUserProperties() );

            try
            {
                settingsResult = settingsBuilder.build( settingsRequest );
            }
            catch ( final SettingsBuildingException e )
            {
                throw new EMBEmbeddingException(
                                                 "Failed to build settings; {0}\nGlobal settings: {1}\nUser settings: {2}",
                                                 e, e.getMessage(), request.getGlobalSettingsFile(),
                                                 request.getUserSettingsFile() );
            }

            settings = settingsResult.getEffectiveSettings();
        }

        try
        {
            executionRequestPopulator.populateFromSettings( request.asMavenExecutionRequest(), settings );
        }
        catch ( final MavenExecutionRequestPopulationException e )
        {
            throw new EMBEmbeddingException( "Failed to populate request from settings; {0}", e, e.getMessage() );
        }

        if ( !settingsResult.getProblems().isEmpty() && logger.isWarnEnabled() )
        {
            logger.warn( "" );
            logger.warn( "Some problems were encountered while building the effective settings" );

            for ( final SettingsProblem problem : settingsResult.getProblems() )
            {
                logger.warn( problem.getMessage() + " @ " + problem.getLocation() );
            }

            logger.warn( "" );
        }
    }

    public static void showEMBInfo( final MAEConfiguration embConfig, final List<MAELibraryLoader> loaders,
                                    final PrintStream standardOut )
        throws IOException
    {
        if ( embInfoShown )
        {
            return;
        }

        standardOut.println();
        standardOut.println( "-- EMB Libraries Loaded --" );
        standardOut.println();

        final Collection<MAELibrary> libraries = loadLibraries( embConfig, loaders );
        for ( final MAELibrary ext : libraries )
        {
            standardOut.println( "+" + ext.getLabel() + " (Log handle: '" + ext.getLogHandle() + "')" );
        }

        standardOut.println();
        standardOut.println( "--------------------------" );
        standardOut.println();

        embInfoShown = true;
    }

    public static void showVersion( final MAEConfiguration embConfig, final List<MAELibraryLoader> loaders,
                                    final PrintStream standardOut )
        throws IOException
    {
        showEMBInfo( embConfig, loaders, standardOut );
        CLIReportingUtils.showVersion( standardOut );
    }

    protected synchronized void printInfo( final MAEExecutionRequest request )
    {
        if ( infoPrinted )
        {
            return;
        }

        infoPrinted = true;
        if ( showVersion || ( request != null && Logger.LEVEL_DEBUG == request.getLoggingLevel() ) )
        {
            try
            {
                showVersion( embConfiguration, libraryLoaders, standardOut );
            }
            catch ( final IOException e )
            {
                logger.error( "Failed to retrieve EMB extension information: " + e.getMessage(), e );
            }
        }

        if ( shouldShowErrors )
        {
            logger.info( "Error stacktraces are turned on." );
        }

        if ( request != null )
        {
            if ( MavenExecutionRequest.CHECKSUM_POLICY_WARN.equals( request.getGlobalChecksumPolicy() ) )
            {
                logger.info( "Disabling strict checksum verification on all artifact downloads." );
            }
            else if ( MavenExecutionRequest.CHECKSUM_POLICY_FAIL.equals( request.getGlobalChecksumPolicy() ) )
            {
                logger.info( "Enabling strict checksum verification on all artifact downloads." );
            }
        }
    }

    public int formatErrorOutput( final MAEExecutionRequest request, final MavenExecutionResult result )
    {
        if ( result.hasExceptions() )
        {
            final ExceptionHandler handler = new DefaultExceptionHandler();

            final Map<String, String> references = new LinkedHashMap<String, String>();

            MavenProject project = null;

            for ( final Throwable exception : result.getExceptions() )
            {
                final ExceptionSummary summary = handler.handleException( exception );

                logSummary( summary, references, "", shouldShowErrors );

                if ( project == null && exception instanceof LifecycleExecutionException )
                {
                    project = ( (LifecycleExecutionException) exception ).getProject();
                }
            }

            logger.error( "" );

            if ( !shouldShowErrors )
            {
                logger.error( "To see the full stack trace of the errors, re-run Maven with the -e switch." );
            }
            if ( !logger.isDebugEnabled() )
            {
                logger.error( "Re-run Maven using the -X switch to enable full debug logging." );
            }

            if ( !references.isEmpty() )
            {
                logger.error( "" );
                logger.error( "For more information about the errors and possible solutions"
                                + ", please read the following articles:" );

                for ( final Map.Entry<String, String> entry : references.entrySet() )
                {
                    logger.error( entry.getValue() + " " + entry.getKey() );
                }
            }

            if ( project != null && !project.equals( result.getTopologicallySortedProjects().get( 0 ) ) )
            {
                logger.error( "" );
                logger.error( "After correcting the problems, you can resume the build with the command" );
                logger.error( "  mvn <goals> -rf :" + project.getArtifactId() );
            }

            if ( MavenExecutionRequest.REACTOR_FAIL_NEVER.equals( request.getReactorFailureBehavior() ) )
            {
                logger.info( "Build failures were ignored." );

                return 0;
            }
            else
            {
                return 1;
            }
        }
        else
        {
            return 0;
        }
    }

    protected void logSummary( final ExceptionSummary summary, final Map<String, String> references, String indent,
                               final boolean showErrors )
    {
        String referenceKey = "";

        if ( StringUtils.isNotEmpty( summary.getReference() ) )
        {
            referenceKey = references.get( summary.getReference() );
            if ( referenceKey == null )
            {
                referenceKey = "[Help " + ( references.size() + 1 ) + "]";
                references.put( summary.getReference(), referenceKey );
            }
        }

        String msg = indent + summary.getMessage();

        if ( StringUtils.isNotEmpty( referenceKey ) )
        {
            if ( msg.indexOf( '\n' ) < 0 )
            {
                msg += " -> " + referenceKey;
            }
            else
            {
                msg += '\n' + indent + "-> " + referenceKey;
            }
        }

        if ( showErrors )
        {
            logger.error( msg, summary.getException() );
        }
        else
        {
            logger.error( msg );
        }

        indent += "  ";

        for ( final ExceptionSummary child : summary.getChildren() )
        {
            logSummary( child, references, indent, showErrors );
        }
    }

    private static final class EmbedderManagementView
        implements MAEManagementView
    {

        private final PlexusContainer container;

        private final MAEConfiguration configuration;

        EmbedderManagementView( final PlexusContainer container, final MAEConfiguration configuration )
        {
            this.container = container;
            this.configuration = configuration;
        }

        @Override
        public <T> T lookup( final Class<T> role, final String hint )
            throws MAEManagementException
        {
            try
            {
                return container.lookup( role, hint );
            }
            catch ( final ComponentLookupException e )
            {
                throw new MAEManagementException(
                                                  "Failed to lookup component for managed component.\nRole: %s\nHint: %s\nReason: %s",
                                                  e, role, hint, e.getMessage() );
            }
        }

        @Override
        public <T> T lookup( final Class<T> role )
            throws MAEManagementException
        {
            try
            {
                return container.lookup( role );
            }
            catch ( final ComponentLookupException e )
            {
                throw new MAEManagementException(
                                                  "Failed to lookup component for managed component.\nRole: %s\nHint: %s\nReason: %s",
                                                  e, role, PlexusConstants.PLEXUS_DEFAULT_HINT, e.getMessage() );
            }
        }

        @Override
        public MAEConfiguration getConfiguration()
        {
            return configuration;
        }

        public <T> Map<String, T> lookupMap( final Class<T> role )
            throws MAEManagementException
        {
            try
            {
                return container.lookupMap( role );
            }
            catch ( final ComponentLookupException e )
            {
                throw new MAEManagementException(
                                                  "Failed to lookup component-map for managed component.\nRole: %s\nReason: %s",
                                                  e, role, e.getMessage() );
            }
        }

        public <T> List<T> lookupList( final Class<T> role )
            throws MAEManagementException
        {
            try
            {
                return container.lookupList( role );
            }
            catch ( final ComponentLookupException e )
            {
                throw new MAEManagementException(
                                                  "Failed to lookup component-list for managed component.\nRole: %s\nReason: %s",
                                                  e, role, e.getMessage() );
            }
        }

    }

}
