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

package org.apache.maven.mae.boot.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.maven.cli.CLIManager;
import org.apache.maven.cli.CLIReportingUtils;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.lifecycle.internal.LifecycleWeaveBuilder;
import org.apache.maven.mae.MAEExecutionRequest;
import org.apache.maven.mae.boot.embed.EMBEmbedder;
import org.apache.maven.mae.boot.embed.EMBEmbeddingException;
import org.apache.maven.mae.boot.log.BatchTransferListener;
import org.apache.maven.mae.boot.log.InteractiveTransferListener;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.properties.internal.EnvironmentUtils;
import org.apache.maven.repository.ArtifactTransferListener;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class EMBMain
{
    public static final String LOCAL_REPO_PROPERTY = "maven.repo.local";

    public static final String THREADS_DEPRECATED = "maven.threads.experimental";

    public static final String userHome = System.getProperty( "user.home" );

    public static final File userMavenConfigurationHome = new File( userHome, ".m2" );

    public static final File DEFAULT_USER_SETTINGS_FILE = new File( userMavenConfigurationHome, "settings.xml" );

    public static final File CONFIGURATION_DIRECTORY = userMavenConfigurationHome;

    public static final File DEFAULT_GLOBAL_SETTINGS_FILE =
        new File( System.getProperty( "maven.home", System.getProperty( "user.dir", "" ) ), "conf/settings.xml" );

    public static final File DEFAULT_USER_TOOLCHAINS_FILE = new File( userMavenConfigurationHome, "toolchains.xml" );

    private final ClassWorld classWorld;

    public EMBMain()
    {
        this( null );
    }

    // This supports painless invocation by the Verifier during embedded execution of the core ITs
    public EMBMain( final ClassWorld classWorld )
    {
        this.classWorld = classWorld;
    }

    public static void main( final String[] args )
    {
        final int result = main( args, null );

        System.exit( result );
    }

    /** @noinspection ConfusingMainMethod */
    public static int main( final String[] args, final ClassWorld classWorld )
    {
        final EMBMain cli = new EMBMain();
        return cli.doMain( new CliRequest( args, classWorld ) );
    }

    public static int doMain( final String[] args, final ClassWorld classWorld )
    {
        final EMBMain cli = new EMBMain();
        return cli.doMain( new CliRequest( args, classWorld ) );
    }

    // This supports painless invocation by the Verifier during embedded execution of the core ITs
    public int doMain( final String[] args, final String workingDirectory, final PrintStream stdout,
                       final PrintStream stderr )
    {
        final CliRequest cliRequest = new CliRequest( args, classWorld );
        cliRequest.workingDirectory = workingDirectory;
        cliRequest.builder.withStandardOut( stdout );
        cliRequest.builder.withStandardErr( stderr );

        return doMain( cliRequest );
    }

    public int doMain( final CliRequest cliRequest )
    {
        try
        {
            initialize( cliRequest );
            // Need to process cli options first to get possible logging options
            cli( cliRequest );
            properties( cliRequest );
            settings( cliRequest );
            populateRequest( cliRequest );
            encryption( cliRequest );
            return execute( cliRequest );
        }
        catch ( final ExitException e )
        {
            return e.exitCode;
        }
        catch ( final Exception e )
        {
            CLIReportingUtils.showError( cliRequest.builder.logger(), "Error executing Maven.", e,
                                         cliRequest.builder.shouldShowErrors() );

            return 1;
        }
        finally
        {
            if ( cliRequest.fileStream != null )
            {
                cliRequest.fileStream.close();
            }
        }
    }

    protected void initialize( final CliRequest cliRequest )
    {
        if ( cliRequest.workingDirectory == null )
        {
            cliRequest.workingDirectory = System.getProperty( "user.dir" );
        }
    }

    //
    // Every bit of information taken from the CLI should be processed here.
    //
    protected void cli( final CliRequest cliRequest )
        throws Exception
    {
        final EMBCLIManager cliManager = new EMBCLIManager();

        try
        {
            cliRequest.commandLine = cliManager.parse( cliRequest.args );
        }
        catch ( final ParseException e )
        {
            cliRequest.builder.standardErr().println( "Unable to parse command line options: " + e.getMessage() );
            cliManager.displayHelp( cliRequest.builder.standardOut() );
            throw e;
        }

        cliRequest.builder.withErrorMode( cliRequest.commandLine.hasOption( CLIManager.ERRORS )
                        || cliRequest.commandLine.hasOption( CLIManager.DEBUG ) );
        cliRequest.builder.withDebugMode( cliRequest.commandLine.hasOption( CLIManager.DEBUG ) );
        cliRequest.builder.withQuietMode( cliRequest.commandLine.hasOption( CLIManager.QUIET ) );
        cliRequest.builder.withVersion( cliRequest.commandLine.hasOption( CLIManager.SHOW_VERSION ) );
        if ( cliRequest.commandLine.hasOption( EMBCLIManager.XAVEN_DEBUG_LOG_HANDLES ) )
        {
            cliRequest.builder.withDebugLogHandles( cliRequest.commandLine.getOptionValue( EMBCLIManager.XAVEN_DEBUG_LOG_HANDLES )
                                                                          .split( "\\s*,\\s*" ) );
        }

        if ( cliRequest.commandLine.hasOption( CLIManager.LOG_FILE ) )
        {
            cliRequest.builder.withLogFile( new File( cliRequest.commandLine.getOptionValue( CLIManager.LOG_FILE ) ) );
        }

        // TODO: these should be moved out of here. Wrong place.
        //
        if ( cliRequest.commandLine.hasOption( CLIManager.HELP ) )
        {
            cliManager.displayHelp( cliRequest.builder.standardOut() );
            throw new ExitException( 0 );
        }

        if ( cliRequest.commandLine.hasOption( CLIManager.VERSION ) )
        {
            try
            {
                EMBEmbedder.showVersion( cliRequest.builder.embConfiguration(), cliRequest.builder.libraryLoaders(),
                                         cliRequest.builder.standardOut() );
            }
            catch ( final IOException e )
            {
                cliRequest.builder.logger()
                                  .error( "Failed to retrieve EMB extension information: " + e.getMessage(), e );
            }

            throw new ExitException( 0 );
        }
    }

    protected void properties( final CliRequest cliRequest )
    {
        populateProperties( cliRequest.commandLine, cliRequest.systemProperties, cliRequest.userProperties );
    }

    //
    // This should probably be a separate tool and not be baked into Maven.
    //
    protected void encryption( final CliRequest cliRequest )
        throws Exception
    {
        if ( cliRequest.commandLine.hasOption( CLIManager.ENCRYPT_MASTER_PASSWORD ) )
        {
            final String passwd = cliRequest.commandLine.getOptionValue( CLIManager.ENCRYPT_MASTER_PASSWORD );

            cliRequest.request.setPasswordToEncrypt( passwd );
            cliRequest.builder.build().encryptMasterPassword( cliRequest.request );

            throw new ExitException( 0 );
        }
        else if ( cliRequest.commandLine.hasOption( CLIManager.ENCRYPT_PASSWORD ) )
        {
            final String passwd = cliRequest.commandLine.getOptionValue( CLIManager.ENCRYPT_PASSWORD );

            cliRequest.request.setPasswordToEncrypt( passwd );
            cliRequest.builder.build().encryptPassword( cliRequest.request );

            throw new ExitException( 0 );
        }
    }

    protected int execute( final CliRequest cliRequest )
        throws EMBEmbeddingException
    {
        final EMBEmbedder embedder = cliRequest.builder.build();
        final MavenExecutionResult result = embedder.execute( cliRequest.request );

        return embedder.formatErrorOutput( cliRequest.request, result );
    }

    protected ModelProcessor createModelProcessor( final PlexusContainer container )
        throws ComponentLookupException
    {
        return container.lookup( ModelProcessor.class );
    }

    protected void settings( final CliRequest cliRequest )
        throws Exception
    {
        File userSettingsFile;

        if ( cliRequest.commandLine.hasOption( CLIManager.ALTERNATE_USER_SETTINGS ) )
        {
            userSettingsFile = new File( cliRequest.commandLine.getOptionValue( CLIManager.ALTERNATE_USER_SETTINGS ) );
            userSettingsFile = resolveFile( userSettingsFile, cliRequest.workingDirectory );

            if ( !userSettingsFile.isFile() )
            {
                throw new FileNotFoundException( "The specified user settings file does not exist: " + userSettingsFile );
            }
        }
        else
        {
            userSettingsFile = DEFAULT_USER_SETTINGS_FILE;
        }

        cliRequest.request.setUserSettingsFile( userSettingsFile );
        cliRequest.builder.logger().debug( "Reading user settings from " + userSettingsFile );

        File globalSettingsFile;

        if ( cliRequest.commandLine.hasOption( CLIManager.ALTERNATE_GLOBAL_SETTINGS ) )
        {
            globalSettingsFile =
                new File( cliRequest.commandLine.getOptionValue( CLIManager.ALTERNATE_GLOBAL_SETTINGS ) );
            globalSettingsFile = resolveFile( globalSettingsFile, cliRequest.workingDirectory );

            if ( !globalSettingsFile.isFile() )
            {
                throw new FileNotFoundException( "The specified global settings file does not exist: "
                                + globalSettingsFile );
            }
        }
        else
        {
            globalSettingsFile = DEFAULT_GLOBAL_SETTINGS_FILE;
        }

        cliRequest.request.setGlobalSettingsFile( globalSettingsFile );
        cliRequest.builder.logger().debug( "Reading global settings from " + globalSettingsFile );
    }

    protected MAEExecutionRequest populateRequest( final CliRequest cliRequest )
        throws EMBEmbeddingException
    {
        // cliRequest.builder.build();

        final MAEExecutionRequest request = cliRequest.request;
        final CommandLine commandLine = cliRequest.commandLine;
        final String workingDirectory = cliRequest.workingDirectory;
        final boolean debug = cliRequest.builder.shouldShowDebug();
        final boolean quiet = cliRequest.builder.shouldBeQuiet();
        final boolean showErrors = cliRequest.builder.shouldShowErrors();

        // ----------------------------------------------------------------------
        // Now that we have everything that we need we will fire up plexus and
        // bring the maven component to life for use.
        // ----------------------------------------------------------------------

        if ( commandLine.hasOption( CLIManager.BATCH_MODE ) )
        {
            request.setInteractiveMode( false );
            cliRequest.builder.embConfiguration().nonInteractive();
        }

        boolean noSnapshotUpdates = false;
        if ( commandLine.hasOption( CLIManager.SUPRESS_SNAPSHOT_UPDATES ) )
        {
            noSnapshotUpdates = true;
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        @SuppressWarnings( "unchecked" )
        final List<String> goals = commandLine.getArgList();

        boolean recursive = true;

        // this is the default behavior.
        String reactorFailureBehaviour = MavenExecutionRequest.REACTOR_FAIL_FAST;

        if ( commandLine.hasOption( CLIManager.NON_RECURSIVE ) )
        {
            recursive = false;
        }

        if ( commandLine.hasOption( CLIManager.FAIL_FAST ) )
        {
            reactorFailureBehaviour = MavenExecutionRequest.REACTOR_FAIL_FAST;
        }
        else if ( commandLine.hasOption( CLIManager.FAIL_AT_END ) )
        {
            reactorFailureBehaviour = MavenExecutionRequest.REACTOR_FAIL_AT_END;
        }
        else if ( commandLine.hasOption( CLIManager.FAIL_NEVER ) )
        {
            reactorFailureBehaviour = MavenExecutionRequest.REACTOR_FAIL_NEVER;
        }

        if ( commandLine.hasOption( CLIManager.OFFLINE ) )
        {
            request.setOffline( true );
        }

        boolean updateSnapshots = false;

        if ( commandLine.hasOption( CLIManager.UPDATE_SNAPSHOTS ) )
        {
            updateSnapshots = true;
        }

        String globalChecksumPolicy = null;

        if ( commandLine.hasOption( CLIManager.CHECKSUM_FAILURE_POLICY ) )
        {
            globalChecksumPolicy = MavenExecutionRequest.CHECKSUM_POLICY_FAIL;
        }
        else if ( commandLine.hasOption( CLIManager.CHECKSUM_WARNING_POLICY ) )
        {
            globalChecksumPolicy = MavenExecutionRequest.CHECKSUM_POLICY_WARN;
        }

        final File baseDirectory = new File( workingDirectory, "" ).getAbsoluteFile();

        // ----------------------------------------------------------------------
        // Profile Activation
        // ----------------------------------------------------------------------

        final List<String> activeProfiles = new ArrayList<String>();

        final List<String> inactiveProfiles = new ArrayList<String>();

        if ( commandLine.hasOption( CLIManager.ACTIVATE_PROFILES ) )
        {
            final String[] profileOptionValues = commandLine.getOptionValues( CLIManager.ACTIVATE_PROFILES );
            if ( profileOptionValues != null )
            {
                for ( int i = 0; i < profileOptionValues.length; ++i )
                {
                    final StringTokenizer profileTokens = new StringTokenizer( profileOptionValues[i], "," );

                    while ( profileTokens.hasMoreTokens() )
                    {
                        final String profileAction = profileTokens.nextToken().trim();

                        if ( profileAction.startsWith( "-" ) || profileAction.startsWith( "!" ) )
                        {
                            inactiveProfiles.add( profileAction.substring( 1 ) );
                        }
                        else if ( profileAction.startsWith( "+" ) )
                        {
                            activeProfiles.add( profileAction.substring( 1 ) );
                        }
                        else
                        {
                            activeProfiles.add( profileAction );
                        }
                    }
                }
            }
        }

        ArtifactTransferListener transferListener;

        if ( request.isInteractiveMode() )
        {
            transferListener = new InteractiveTransferListener( cliRequest.builder.standardOut() );
        }
        else
        {
            transferListener = new BatchTransferListener( cliRequest.builder.standardOut() );
        }

        transferListener.setShowChecksumEvents( false );

        String alternatePomFile = null;
        if ( commandLine.hasOption( CLIManager.ALTERNATE_POM_FILE ) )
        {
            alternatePomFile = commandLine.getOptionValue( CLIManager.ALTERNATE_POM_FILE );
        }

        int loggingLevel;

        if ( debug )
        {
            loggingLevel = MavenExecutionRequest.LOGGING_LEVEL_DEBUG;
        }
        else if ( quiet )
        {
            // TODO: we need to do some more work here. Some plugins use sys out or log errors at info level.
            // Ideally, we could use Warn across the board
            loggingLevel = MavenExecutionRequest.LOGGING_LEVEL_ERROR;
            // TODO:Additionally, we can't change the mojo level because the component key includes the version and it
            // isn't known ahead of time. This seems worth changing.
        }
        else
        {
            loggingLevel = MavenExecutionRequest.LOGGING_LEVEL_INFO;
        }

        File userToolchainsFile;
        if ( commandLine.hasOption( CLIManager.ALTERNATE_USER_TOOLCHAINS ) )
        {
            userToolchainsFile = new File( commandLine.getOptionValue( CLIManager.ALTERNATE_USER_TOOLCHAINS ) );
            userToolchainsFile = resolveFile( userToolchainsFile, workingDirectory );
        }
        else
        {
            userToolchainsFile = EMBMain.DEFAULT_USER_TOOLCHAINS_FILE;
        }

        request.setBaseDirectory( baseDirectory )
               .setGoals( goals )
               .setSystemProperties( cliRequest.systemProperties )
               .setUserProperties( cliRequest.userProperties )
               .setReactorFailureBehavior( reactorFailureBehaviour )
               // default: fail fast
               .setRecursive( recursive )
               // default: true
               .setShowErrors( showErrors )
               .addActiveProfiles( activeProfiles )
               // optional
               .addInactiveProfiles( inactiveProfiles )
               // optional
               .setLoggingLevel( loggingLevel )
               // default: batch mode which goes along with interactive
               .setUpdateSnapshots( updateSnapshots )
               // default: false
               .setNoSnapshotUpdates( noSnapshotUpdates )
               // default: false
               .setGlobalChecksumPolicy( globalChecksumPolicy )
               // default: warn
               .setUserToolchainsFile( userToolchainsFile );

        if ( alternatePomFile != null )
        {
            final File pom = resolveFile( new File( alternatePomFile ), workingDirectory );

            request.setPom( pom );
        }
        else
        {
            final File pom = cliRequest.builder.modelProcessor().locatePom( baseDirectory );
            cliRequest.builder.resetContainer();

            if ( pom.isFile() )
            {
                request.setPom( pom );
            }
        }

        if ( ( request.getPom() != null ) && ( request.getPom().getParentFile() != null ) )
        {
            request.setBaseDirectory( request.getPom().getParentFile() );
        }

        if ( commandLine.hasOption( CLIManager.RESUME_FROM ) )
        {
            request.setResumeFrom( commandLine.getOptionValue( CLIManager.RESUME_FROM ) );
        }

        if ( commandLine.hasOption( CLIManager.PROJECT_LIST ) )
        {
            final String projectList = commandLine.getOptionValue( CLIManager.PROJECT_LIST );
            final String[] projects = StringUtils.split( projectList, "," );
            request.setSelectedProjects( Arrays.asList( projects ) );
        }

        if ( commandLine.hasOption( CLIManager.ALSO_MAKE ) && !commandLine.hasOption( CLIManager.ALSO_MAKE_DEPENDENTS ) )
        {
            request.setMakeBehavior( MavenExecutionRequest.REACTOR_MAKE_UPSTREAM );
        }
        else if ( !commandLine.hasOption( CLIManager.ALSO_MAKE )
                        && commandLine.hasOption( CLIManager.ALSO_MAKE_DEPENDENTS ) )
        {
            request.setMakeBehavior( MavenExecutionRequest.REACTOR_MAKE_DOWNSTREAM );
        }
        else if ( commandLine.hasOption( CLIManager.ALSO_MAKE )
                        && commandLine.hasOption( CLIManager.ALSO_MAKE_DEPENDENTS ) )
        {
            request.setMakeBehavior( MavenExecutionRequest.REACTOR_MAKE_BOTH );
        }

        String localRepoProperty = request.getUserProperties().getProperty( EMBMain.LOCAL_REPO_PROPERTY );

        if ( localRepoProperty == null )
        {
            localRepoProperty = request.getSystemProperties().getProperty( EMBMain.LOCAL_REPO_PROPERTY );
        }

        if ( localRepoProperty != null )
        {
            request.setLocalRepositoryPath( localRepoProperty );
        }

        final String threadConfiguration =
            commandLine.hasOption( CLIManager.THREADS ) ? commandLine.getOptionValue( CLIManager.THREADS )
                            : request.getSystemProperties().getProperty( EMBMain.THREADS_DEPRECATED ); // TODO: Remove
                                                                                                       // this setting.
                                                                                                       // Note that the
                                                                                                       // int-tests use
                                                                                                       // it

        if ( threadConfiguration != null )
        {
            request.setPerCoreThreadCount( threadConfiguration.contains( "C" ) );
            if ( threadConfiguration.contains( "W" ) )
            {
                LifecycleWeaveBuilder.setWeaveMode( request.getUserProperties() );
            }
            request.setThreadCount( threadConfiguration.replace( "C", "" ).replace( "W", "" ).replace( "auto", "" ) );
        }

        return request;
    }

    protected File resolveFile( final File file, final String workingDirectory )
    {
        if ( file == null )
        {
            return null;
        }
        else if ( file.isAbsolute() )
        {
            return file;
        }
        else if ( file.getPath().startsWith( File.separator ) )
        {
            // drive-relative Windows path
            return file.getAbsoluteFile();
        }
        else
        {
            return new File( workingDirectory, file.getPath() ).getAbsoluteFile();
        }
    }

    // ----------------------------------------------------------------------
    // System properties handling
    // ----------------------------------------------------------------------

    protected void populateProperties( final CommandLine commandLine, final Properties systemProperties,
                                       final Properties userProperties )
    {
        EnvironmentUtils.addEnvVars( systemProperties );

        // ----------------------------------------------------------------------
        // Options that are set on the command line become system properties
        // and therefore are set in the session properties. System properties
        // are most dominant.
        // ----------------------------------------------------------------------

        if ( commandLine.hasOption( CLIManager.SET_SYSTEM_PROPERTY ) )
        {
            final String[] defStrs = commandLine.getOptionValues( CLIManager.SET_SYSTEM_PROPERTY );

            if ( defStrs != null )
            {
                for ( int i = 0; i < defStrs.length; ++i )
                {
                    setCliProperty( defStrs[i], userProperties );
                }
            }
        }

        systemProperties.putAll( System.getProperties() );
    }

    protected static void setCliProperty( final String property, final Properties properties )
    {
        String name;

        String value;

        final int i = property.indexOf( "=" );

        if ( i <= 0 )
        {
            name = property.trim();

            value = "true";
        }
        else
        {
            name = property.substring( 0, i ).trim();

            value = property.substring( i + 1 );
        }

        properties.setProperty( name, value );

        // ----------------------------------------------------------------------
        // I'm leaving the setting of system properties here as not to break
        // the SystemPropertyProfileActivator. This won't harm embedding. jvz.
        // ----------------------------------------------------------------------

        System.setProperty( name, value );
    }

    public static class ExitException
        extends Exception
    {

        private static final long serialVersionUID = 1L;

        public int exitCode;

        public ExitException( final int exitCode )
        {
            this.exitCode = exitCode;
        }

    }

}
