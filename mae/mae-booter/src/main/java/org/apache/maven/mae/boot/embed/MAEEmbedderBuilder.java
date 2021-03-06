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

package org.apache.maven.mae.boot.embed;

import static org.apache.maven.mae.conf.MAELibraries.loadLibraries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.maven.Maven;
import org.apache.maven.cli.MavenLoggerManager;
import org.apache.maven.cli.PrintStreamLogger;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.mae.boot.services.MAEServiceManager;
import org.apache.maven.mae.conf.CoreLibrary;
import org.apache.maven.mae.conf.MAEConfiguration;
import org.apache.maven.mae.conf.MAELibrary;
import org.apache.maven.mae.conf.loader.MAELibraryLoader;
import org.apache.maven.mae.conf.loader.ServiceLibraryLoader;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.internal.container.InstanceRegistry;
import org.apache.maven.mae.internal.container.MAEContainer;
import org.apache.maven.mae.internal.container.VirtualInstance;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.settings.building.SettingsBuilder;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

public class MAEEmbedderBuilder
{

    private static final MAELibraryLoader CORE_LOADER = new MAELibraryLoader()
    {
        @Override
        public Collection<? extends MAELibrary> loadLibraries( final MAEConfiguration embConfig )
            throws IOException
        {
            return Collections.singleton( new CoreLibrary() );
        }
    };

    private boolean showErrors = false;

    private boolean quiet = false;

    private boolean debug = false;

    private boolean showVersion = false;

    private PrintStream stdout = System.out;

    private PrintStream stderr = System.err;

    private InputStream stdin = System.in;

    private Logger logger;

    private File logFile;

    private MAEConfiguration config;

    private ClassWorld classWorld;

    private ClassLoader coreClassLoader;

    private Maven maven;

    private ModelProcessor modelProcessor;

    private MAEContainer container;

    private MavenExecutionRequestPopulator executionRequestPopulator;

    private SettingsBuilder settingsBuilder;

    private DefaultSecDispatcher securityDispatcher;

    private MAEServiceManager serviceManager;

    private transient String mavenHome;

    private transient boolean loggerAutoCreated = false;

    private MAEEmbedder embedder;

    private String[] debugLogHandles;

    private boolean modelProcessorProvided;

    private boolean mavenProvided;

    private boolean executionRequestPopulatorProvided;

    private boolean settingsBuilderProvided;

    private boolean securityDispatcherProvided;

    private boolean serviceManagerProvided;

    private boolean logHandlesConfigured;

    private boolean configProvided;

    private ContainerConfiguration containerConfiguration;

    private boolean classScanningEnabled;

    private List<MAELibraryLoader> libraryLoaders;

    private final VirtualInstance<MAEEmbedder> embedderVirtual =
        new VirtualInstance<MAEEmbedder>( MAEEmbedder.class );

    public synchronized MAEEmbedderBuilder withSettingsBuilder( final SettingsBuilder settingsBuilder )
    {
        this.settingsBuilder = settingsBuilder;
        settingsBuilderProvided = true;
        return this;
    }

    public synchronized SettingsBuilder settingsBuilder()
        throws MAEEmbeddingException
    {
        if ( settingsBuilder == null )
        {
            settingsBuilder = lookup( SettingsBuilder.class );
            settingsBuilderProvided = false;
        }
        return settingsBuilder;
    }

    public synchronized MAEEmbedderBuilder withSecurityDispatcher( final DefaultSecDispatcher securityDispatcher )
    {
        this.securityDispatcher = securityDispatcher;
        securityDispatcherProvided = true;
        return this;
    }

    public synchronized DefaultSecDispatcher securityDispatcher()
        throws MAEEmbeddingException
    {
        if ( securityDispatcher == null )
        {
            securityDispatcher = (DefaultSecDispatcher) lookup( SecDispatcher.class, "maven" );
            securityDispatcherProvided = false;
        }
        return securityDispatcher;
    }

    public synchronized MAEEmbedderBuilder withServiceManager( final MAEServiceManager serviceManager )
    {
        this.serviceManager = serviceManager;
        serviceManagerProvided = true;
        return this;
    }

    public synchronized MAEServiceManager serviceManager()
        throws MAEEmbeddingException
    {
        if ( serviceManager == null )
        {
            serviceManager = lookup( MAEServiceManager.class );
            serviceManagerProvided = true;
        }
        return serviceManager;
    }

    public synchronized MAEEmbedderBuilder withExecutionRequestPopulator( final MavenExecutionRequestPopulator executionRequestPopulator )
    {
        this.executionRequestPopulator = executionRequestPopulator;
        executionRequestPopulatorProvided = true;
        return this;
    }

    public synchronized MavenExecutionRequestPopulator executionRequestPopulator()
        throws MAEEmbeddingException
    {
        if ( executionRequestPopulator == null )
        {
            executionRequestPopulator = lookup( MavenExecutionRequestPopulator.class );
            executionRequestPopulatorProvided = false;
        }

        return executionRequestPopulator;
    }

    public synchronized MAEEmbedderBuilder withCoreClassLoader( final ClassLoader classLoader )
    {
        coreClassLoader = classLoader;
        return this;
    }

    public synchronized MAEEmbedderBuilder withCoreClassLoader( final ClassLoader root,
                                                                final Object... constituents )
        throws MalformedURLException
    {
        if ( constituents != null && constituents.length > 0 )
        {
            final Set<URL> urls = new LinkedHashSet<URL>();
            for ( final Object object : constituents )
            {
                if ( object instanceof URL )
                {
                    urls.add( (URL) object );
                }
                else if ( object instanceof CharSequence )
                {
                    urls.add( new URL( object.toString() ) );
                }
                else if ( object instanceof File )
                {
                    urls.add( ( (File) object ).toURI().toURL() );
                }
                else
                {
                    String fname;
                    ClassLoader cloader;
                    if ( object instanceof Class<?> )
                    {
                        fname = ( (Class<?>) object ).getName();
                        cloader = ( (Class<?>) object ).getClassLoader();
                    }
                    else
                    {
                        fname = object.getClass().getName();
                        cloader = object.getClass().getClassLoader();
                    }

                    fname = "/" + fname.replace( '.', '/' ) + ".class";

                    final URL resource = cloader.getResource( fname );
                    if ( resource == null )
                    {
                        throw new IllegalStateException(
                                                         "Class doesn't appear in its own classloader! ["
                                                             + object.getClass().getName() + "]" );
                    }

                    String path = resource.toExternalForm();
                    if ( path.startsWith( "jar:" ) )
                    {
                        path = path.substring( "jar:".length() );
                    }

                    final int idx = path.indexOf( '!' );
                    if ( idx > -1 )
                    {
                        path = path.substring( 0, idx );
                    }

                    urls.add( new URL( path ) );
                }
            }

            coreClassLoader = new URLClassLoader( urls.toArray( new URL[] {} ), root );
        }
        else
        {
            coreClassLoader = root;
        }

        return this;
    }

    public synchronized MAEEmbedderBuilder withClassWorld( final ClassWorld classWorld )
    {
        this.classWorld = classWorld;
        return this;
    }

    public synchronized ClassLoader coreClassLoader()
    {
        if ( coreClassLoader == null )
        {
            coreClassLoader = Thread.currentThread().getContextClassLoader();
        }

        return coreClassLoader;
    }

    public synchronized ClassWorld classWorld()
    {
        if ( classWorld == null )
        {
            classWorld = new ClassWorld( "plexus.core", coreClassLoader() );
        }

        return classWorld;
    }

    public synchronized MAEEmbedderBuilder withContainerConfiguration( final ContainerConfiguration containerConfiguration )
    {
        this.containerConfiguration = containerConfiguration;
        return this;
    }

    public synchronized ContainerConfiguration containerConfiguration()
    {
        if ( containerConfiguration == null )
        {
            containerConfiguration =
                new DefaultContainerConfiguration().setClassWorld( classWorld() ).setName( "maven" ).setClassPathScanning( classScanningEnabled ? "ON"
                                                                                                                                           : "OFF" );
        }

        return containerConfiguration;
    }

    public synchronized MAEEmbedderBuilder withClassScanningEnabled( final boolean classScanningEnabled )
    {
        this.classScanningEnabled = classScanningEnabled;
        return this;
    }

    public boolean isClassScanningEnabled()
    {
        return classScanningEnabled;
    }

    public synchronized MAEEmbedderBuilder withMaven( final Maven maven )
    {
        this.maven = maven;
        mavenProvided = true;
        return this;
    }

    public synchronized Maven maven()
        throws MAEEmbeddingException
    {
        if ( maven == null )
        {
            maven = lookup( Maven.class );
            mavenProvided = false;
        }
        return maven;
    }

    public synchronized MAEEmbedderBuilder withModelProcessor( final ModelProcessor modelProcessor )
    {
        this.modelProcessor = modelProcessor;
        modelProcessorProvided = true;
        return this;
    }

    public synchronized ModelProcessor modelProcessor()
        throws MAEEmbeddingException
    {
        if ( modelProcessor == null )
        {
            modelProcessor = lookup( ModelProcessor.class );
            modelProcessorProvided = false;
        }

        return modelProcessor;
    }

    private <T> T lookup( final Class<T> cls )
        throws MAEEmbeddingException
    {
        try
        {
            return container().lookup( cls );
        }
        catch ( final ComponentLookupException e )
        {
            throw new MAEEmbeddingException( "Failed to lookup component: %s. Reason: %s", e,
                                             cls.getName(), e.getMessage() );
        }
    }

    private <T> T lookup( final Class<T> cls, final String hint )
        throws MAEEmbeddingException
    {
        try
        {
            return container().lookup( cls, hint );
        }
        catch ( final ComponentLookupException e )
        {
            throw new MAEEmbeddingException(
                                             "Failed to lookup component: {0} with hint: {1}. Reason: {2}",
                                             e, cls.getName(), hint, e.getMessage() );
        }
    }

    public synchronized MAEEmbedderBuilder withContainer( final MAEContainer container )
    {
        this.container = container;
        resetContainer();

        return this;
    }

    public synchronized void resetContainer()
    {
        if ( !modelProcessorProvided )
        {
            modelProcessor = null;
        }
        if ( !executionRequestPopulatorProvided )
        {
            executionRequestPopulator = null;
        }
        if ( !settingsBuilderProvided )
        {
            settingsBuilder = null;
        }
        if ( !securityDispatcherProvided )
        {
            securityDispatcher = null;
        }
        if ( !serviceManagerProvided )
        {
            serviceManager = null;
        }
        if ( !mavenProvided )
        {
            maven = null;
        }
        if ( !configProvided )
        {
            config = null;
        }
        if ( container != null )
        {
            container = null;
        }
    }

    public synchronized MAEContainer container()
        throws MAEEmbeddingException
    {
        // Need to switch to using: org.codehaus.plexus.MutablePlexusContainer.addPlexusInjector(List<PlexusBeanModule>,
        // Module...)
        if ( container == null )
        {
            final ContainerConfiguration cc = containerConfiguration();

            final InstanceRegistry reg = new InstanceRegistry( instanceRegistry() );
            reg.addVirtual( new ComponentKey<MAEEmbedder>( MAEEmbedder.class ), embedderVirtual );

            MAEContainer c;
            try
            {
                c = new MAEContainer( cc, selector(), reg );
            }
            catch ( final PlexusContainerException e )
            {
                throw new MAEEmbeddingException( "Failed to initialize component container: {0}",
                                                 e, e.getMessage() );
            }

            c.setLoggerManager( new MavenLoggerManager( logger ) );

            container = c;
        }

        return container;
    }

    public synchronized ComponentSelector selector()
    {
        return configuration().getComponentSelector();
    }

    public synchronized InstanceRegistry instanceRegistry()
    {
        return configuration().getInstanceRegistry();
    }

    public MAEEmbedderBuilder withConfiguration( final MAEConfiguration config )
    {
        this.config = config;
        configProvided = true;
        return this;
    }

    public synchronized MAEConfiguration configuration()
    {
        final String[] debugLogHandles = debugLogHandles();
        if ( !logHandlesConfigured && debugLogHandles != null )
        {
            for ( final String logHandle : debugLogHandles )
            {
                final org.apache.log4j.Logger logger =
                    org.apache.log4j.Logger.getLogger( logHandle );
                logger.setLevel( Level.DEBUG );
            }

            logHandlesConfigured = true;
        }

        if ( config == null )
        {
            config = new MAEConfiguration();

            if ( shouldShowDebug() )
            {
                config.withDebug();
            }
            else
            {
                config.withoutDebug();
            }

            try
            {
                final List<MAELibraryLoader> loaders = libraryLoaders();
                final Collection<MAELibrary> libraries = loadLibraries( config, loaders );
                config.withLibraries( libraries );

                if ( debugLogHandles != null
                    && Arrays.binarySearch( debugLogHandles,
                                            MAEConfiguration.STANDARD_LOG_HANDLE_CORE ) > -1 )
                {
                    MAEEmbedder.showInfo( config, loaders, standardOut() );
                }
            }
            catch ( final IOException e )
            {
                logger.error( "Failed to query context classloader for component-overrides files. Reason: "
                                  + e.getMessage(), e );
            }

            configProvided = false;
        }

        return config;
    }

    public MAEEmbedderBuilder withServiceLibraryLoader( final boolean enabled )
    {
        boolean found = false;
        final Collection<? extends MAELibraryLoader> loaders = libraryLoadersInternal();
        for ( final MAELibraryLoader loader : new LinkedHashSet<MAELibraryLoader>( loaders ) )
        {
            if ( loader instanceof ServiceLibraryLoader )
            {
                found = true;
                if ( !enabled )
                {
                    loaders.remove( loader );
                }
                else
                {
                    break;
                }
            }
        }

        if ( enabled && !found )
        {
            withLibraryLoader( new ServiceLibraryLoader() );
        }

        return this;
    }

    public boolean isServiceLibraryLoaderUsed()
    {
        for ( final MAELibraryLoader loader : libraryLoadersInternal() )
        {
            if ( loader instanceof ServiceLibraryLoader )
            {
                return true;
            }
        }

        return false;
    }

    public MAEEmbedderBuilder withLibraryLoader( final MAELibraryLoader loader )
    {
        libraryLoadersInternal().add( loader );
        return this;
    }

    public MAEEmbedderBuilder withLibraryLoader( final MAELibraryLoader loader, final int offset )
    {
        final List<MAELibraryLoader> loaders = libraryLoadersInternal();
        if ( offset < 0 )
        {
            // idx is negative, so we can add to the size here to get the insert index.
            loaders.add( loaders.size() + offset, loader );
        }
        else
        {
            loaders.add( offset, loader );
        }

        return this;
    }

    private List<MAELibraryLoader> libraryLoadersInternal()
    {
        if ( libraryLoaders == null )
        {
            libraryLoaders =
                new ArrayList<MAELibraryLoader>(
                                                 Collections.singletonList( new ServiceLibraryLoader() ) );
        }

        return libraryLoaders;
    }

    public List<MAELibraryLoader> libraryLoaders()
    {
        final List<MAELibraryLoader> loaders = libraryLoadersInternal();

        if ( !loaders.isEmpty() && CORE_LOADER != loaders.get( 0 ) )
        {
            loaders.remove( CORE_LOADER );
        }

        loaders.add( 0, CORE_LOADER );

        return loaders;
    }

    public MAEEmbedderBuilder withVersion( final boolean showVersion )
    {
        this.showVersion = showVersion;
        return this;
    }

    public boolean showVersion()
    {
        return showVersion;
    }

    public MAEEmbedderBuilder withLogFile( final File logFile )
    {
        this.logFile = logFile;
        return this;
    }

    public File logFile()
    {
        return logFile;
    }

    public MAEEmbedderBuilder withQuietMode( final boolean quiet )
    {
        this.quiet = quiet;
        return this;
    }

    public boolean shouldBeQuiet()
    {
        return quiet;
    }

    public MAEEmbedderBuilder withDebugMode( final boolean debug )
    {
        this.debug = debug;
        return this;
    }

    public boolean shouldShowDebug()
    {
        return debug;
    }

    public MAEEmbedderBuilder withErrorMode( final boolean showErrors )
    {
        this.showErrors = showErrors;
        return this;
    }

    public boolean shouldShowErrors()
    {
        return showErrors;
    }

    public synchronized MAEEmbedderBuilder withStandardOut( final PrintStream stdout )
    {
        this.stdout = stdout;

        if ( loggerAutoCreated )
        {
            logger = null;
        }

        return this;
    }

    public PrintStream standardOut()
    {
        return stdout;
    }

    public MAEEmbedderBuilder withStandardErr( final PrintStream stderr )
    {
        this.stderr = stderr;
        return this;
    }

    public PrintStream standardErr()
    {
        return stderr;
    }

    public MAEEmbedderBuilder withStandardIn( final InputStream stdin )
    {
        this.stdin = stdin;
        return this;
    }

    public InputStream standardIn()
    {
        return stdin;
    }

    public MAEEmbedderBuilder withLogger( final Logger logger )
    {
        this.logger = logger;
        return this;
    }

    public synchronized Logger logger()
    {
        if ( logger == null )
        {
            logger = new PrintStreamLogger( stdout );
            loggerAutoCreated = true;
        }

        return logger;
    }

    public synchronized String mavenHome()
    {
        if ( mavenHome == null )
        {
            String mavenHome = System.getProperty( "maven.home" );

            if ( mavenHome != null )
            {
                try
                {
                    mavenHome = new File( mavenHome ).getCanonicalPath();
                }
                catch ( final IOException e )
                {
                    mavenHome = new File( mavenHome ).getAbsolutePath();
                }

                System.setProperty( "maven.home", mavenHome );
                this.mavenHome = mavenHome;
            }
        }

        return mavenHome;
    }

    protected synchronized void wireLogging()
    {
        if ( logFile() != null )
        {
            try
            {
                final PrintStream newOut = new PrintStream( logFile );
                withStandardOut( newOut );
            }
            catch ( final FileNotFoundException e )
            {}
        }

        logger();
    }

    protected synchronized MAEEmbedder createEmbedder()
        throws MAEEmbeddingException
    {
        final MAEEmbedder embedder =
            new MAEEmbedder( maven(), configuration(), container(), settingsBuilder(),
                             executionRequestPopulator(), securityDispatcher(), serviceManager(),
                             libraryLoaders(), standardOut(), logger(), shouldShowErrors(),
                             showVersion() );

        embedderVirtual.setInstance( embedder );

        return embedder;
    }

    public synchronized MAEEmbedder build()
        throws MAEEmbeddingException
    {
        if ( embedder == null )
        {
            logger();
            configuration();
            mavenHome();

            wireLogging();
            embedder = createEmbedder();
        }

        return embedder;
    }

    public MAEEmbedderBuilder withDebugLogHandles( final String[] debugLogHandles )
    {
        this.debugLogHandles = debugLogHandles;
        logHandlesConfigured = false;

        return this;
    }

    public String[] debugLogHandles()
    {
        return debugLogHandles;
    }

}
