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

package org.apache.maven.mae.conf;

import org.apache.maven.mae.MAEExecutionRequest;
import org.apache.maven.mae.conf.ext.ExtensionConfiguration;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.internal.container.InstanceRegistry;
import org.apache.maven.mae.internal.container.ServiceAuthorizer;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MAEConfiguration
{

    public static final String STANDARD_LOG_HANDLE_CORE = "core";

    public static final String STANDARD_LOG_HANDLE_LOADER = "emb-loader";

    private static final File DEFAULT_CONFIGURATION_DIRECTORY =
        new File( System.getProperty( "user.home" ), ".m2/conf" );

    private ComponentSelector componentSelector;

    private InstanceRegistry instanceRegistry;

    private Set<MAELibrary> libraries;

    private File configurationDirectory = DEFAULT_CONFIGURATION_DIRECTORY;

    private MAEExecutionRequest executionRequest;

    private InputStream stdin = System.in;

    private PrintStream stdout = System.out;

    private PrintStream stderr = System.err;

    private boolean debug;

    private boolean interactive = true;

    public MAEConfiguration()
    {
    }

    public MAEConfiguration withEMBExecutionRequest( final MAEExecutionRequest request )
    {
        executionRequest = request;
        return this;
    }

    public MAEConfiguration withStandardIn( final InputStream stdin )
    {
        this.stdin = stdin;
        return this;
    }

    public InputStream getStandardIn()
    {
        return stdin;
    }

    public MAEConfiguration withStandardOut( final PrintStream stdout )
    {
        this.stdout = stdout;
        return this;
    }

    public PrintStream getStandardOut()
    {
        return stdout;
    }

    public MAEConfiguration withStandardErr( final PrintStream stderr )
    {
        this.stderr = stderr;
        return this;
    }

    public PrintStream getStandardErr()
    {
        return stderr;
    }

    public MAEExecutionRequest getEMBExecutionRequest()
    {
        return executionRequest;
    }

    public boolean isInteractive()
    {
        return interactive;
    }

    public boolean isDebugEnabled()
    {
        return debug;
    }

    public MAEConfiguration withConfigurationDirectory( final File configurationDirectory )
    {
        this.configurationDirectory = configurationDirectory;
        return this;
    }

    public File getConfigurationDirectory()
    {
        return configurationDirectory;
    }

    public MAEConfiguration withLibraries( final Collection<MAELibrary> libraries )
    {
        for ( final MAELibrary library : libraries )
        {
            withLibrary( library );
        }
        return this;
    }

    public MAEConfiguration withLibraries( final MAELibrary... libraries )
    {
        for ( final MAELibrary library : libraries )
        {
            withLibrary( library );
        }
        return this;
    }

    public MAELibrary getLibrary( final String id )
    {
        for ( final MAELibrary library : getLibraries() )
        {
            if ( library.getId().equalsIgnoreCase( id ) )
            {
                return library;
            }
        }

        return null;
    }

    public Set<MAELibrary> getLibraries()
    {
        if ( libraries == null )
        {
            libraries = new HashSet<MAELibrary>();
        }

        return libraries;
    }

    public ComponentSelector getComponentSelector()
    {
        if ( componentSelector == null )
        {
            componentSelector = new ComponentSelector();
        }

        return componentSelector;
    }

    public synchronized MAEConfiguration withComponentSelection( final ComponentKey<?> key, final String newHint )
    {
        getComponentSelector().setSelection( key, newHint );
        return this;
    }

    public synchronized MAEConfiguration withComponentSelections( final Map<ComponentKey<?>, String> selections )
    {
        if ( selections != null )
        {
            for ( final Map.Entry<ComponentKey<?>, String> entry : selections.entrySet() )
            {
                if ( entry == null || entry.getKey() == null || entry.getValue() == null )
                {
                    continue;
                }

                getComponentSelector().setSelection( entry.getKey(), entry.getValue() );
            }
        }

        return this;
    }

    public synchronized MAEConfiguration withComponentSelections( final ComponentSelector newSelector )
    {
        if ( newSelector != null )
        {
            getComponentSelector().merge( newSelector );
        }

        return this;
    }

    public MAEConfiguration withComponentSelector( final ComponentSelector selector )
    {
        getComponentSelector().merge( selector );

        return this;
    }

    public MAEConfiguration withoutDebug()
    {
        debug = false;
        return this;
    }

    public MAEConfiguration withDebug()
    {
        debug = true;
        return this;
    }

    public MAEConfiguration interactive()
    {
        interactive = true;
        return this;
    }

    public MAEConfiguration nonInteractive()
    {
        interactive = false;
        return this;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public MAEConfiguration withLibrary( final MAELibrary library )
    {
        getLibraries().add( library );
        withComponentSelector( library.getComponentSelector() );
        withInstanceRegistry( library.getInstanceRegistry() );
        withComponentInstance( new ComponentKey<MAELibrary>( MAELibrary.class, library.getId() ), library );

        final ExtensionConfiguration configuration = library.getConfiguration();
        if ( configuration != null )
        {
            withComponentInstance( new ComponentKey<ExtensionConfiguration>( ExtensionConfiguration.class,
                                                                             library.getId() ), configuration );

            withComponentInstance( new ComponentKey( configuration.getClass() ), configuration );
        }

        return this;
    }

    public synchronized <T> MAEConfiguration withComponentInstance( final ComponentKey<T> key, final T instance )
    {
        getInstanceRegistry().add( key, instance );

        return this;
    }

    public synchronized MAEConfiguration withComponentInstance( final Object instance )
    {
        getInstanceRegistry().add( instance );
        return this;
    }

    public synchronized MAEConfiguration withInstanceRegistry( final InstanceRegistry instanceRegistry )
    {
        if ( instanceRegistry != null )
        {
            getInstanceRegistry().overrideMerge( instanceRegistry );
        }

        return this;
    }

    public synchronized InstanceRegistry getInstanceRegistry()
    {
        if ( instanceRegistry == null )
        {
            instanceRegistry = new InstanceRegistry();
        }

        final Set<ComponentKey<?>> keys = new HashSet<ComponentKey<?>>();
        for ( final MAELibrary lib : getLibraries() )
        {
            final Set<ComponentKey<?>> exports = lib.getExportedComponents();
            if ( exports != null && !exports.isEmpty() )
            {
                keys.addAll( exports );
            }
        }

        instanceRegistry.add( new ComponentKey<ServiceAuthorizer>( ServiceAuthorizer.class ),
                              new ServiceAuthorizer( keys ) );
        instanceRegistry.add( MAEConfiguration.class, this );

        return instanceRegistry;
    }

}
