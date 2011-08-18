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

package org.apache.maven.mae.app;

import org.apache.log4j.Logger;
import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.boot.embed.MAEEmbedder;
import org.apache.maven.mae.boot.embed.MAEEmbedderBuilder;
import org.apache.maven.mae.conf.MAEConfiguration;
import org.apache.maven.mae.conf.MAELibrary;
import org.apache.maven.mae.conf.VersionProvider;
import org.apache.maven.mae.conf.ext.ExtensionConfiguration;
import org.apache.maven.mae.conf.ext.ExtensionConfigurationException;
import org.apache.maven.mae.conf.loader.InstanceLibraryLoader;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.internal.container.InstanceRegistry;
import org.apache.maven.mae.internal.container.VirtualInstance;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link MAEApplication} implementation that provides support for loading a full Maven component
 * environment, complete with {@link MAELibrary}'s, {@link ComponentSelector} and {@link InstanceRegistry}.
 * This class supervises the assembly of the environment, giving the application developer an easy
 * way to inject the behavior he needs.
 * 
 * @author John Casey
 */
public abstract class AbstractMAEApplication
    implements MAEApplication
{

    private final List<MAELibrary> additionalLibraries = new ArrayList<MAELibrary>();

    private final InstanceRegistry instanceRegistry = new InstanceRegistry();

    private transient boolean loaded = false;

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    protected AbstractMAEApplication()
    {
        withLibrary( this );
        withComponentInstance( new ComponentKey( getClass() ), this );
    }

    /**
     * Programmatically add a new {@link MAELibrary} instance, beyond those that are automatically
     * detected via the /META-INF/services/org.apache.maven.mae.conf.MAELibrary files on the
     * classpath.
     */
    protected final AbstractMAEApplication withLibrary( final MAELibrary library )
    {
        additionalLibraries.add( library );
        return this;
    }

    /**
     * {@inheritDoc}
     * @see org.apache.maven.mae.app.MAEApplication#load()
     */
    @Override
    public MAEApplication load()
        throws MAEException
    {
        return doLoad();
    }

    /**
     * Carry out the application loading process. This means:
     * <br/>
     * <ul>
     *   <li>Create a new {@link MAEEmbedderBuilder}</li>
     *   <li>Add to that an {@link InstanceLibraryLoader} to handle libraries that were 
     *       programmatically added here</li>
     *   <li>Call {@link AbstractMAEApplication#beforeLoading()}</li>
     *   <li>Call {@link AbstractMAEApplication#configureBuilder(MAEEmbedderBuilder)} to allow 
     *       fine-tuning of the {@link MAEEmbedderBuilder} instance</li>
     *   <li>Call {@link MAEEmbedderBuilder#build} to create an instance of {@link MAEEmbedder}</li>
     *   <li>For each instance in the {@link InstanceRegistry}, lookup via {@link MAEEmbedder#container()}
     *       to ensure injectable component dependencies are filled</li>
     *   <li>Call {@link AbstractMAEApplication#afterLoading()}</li>
     *   <li>Set the loaded flag, which will prevent this process from repeating for an application
     *       that has already been loaded</li>
     * </ul>
     */
    private synchronized final MAEApplication doLoad()
        throws MAEException
    {
        if ( loaded )
        {
            return this;
        }

        final MAEEmbedderBuilder builder = new MAEEmbedderBuilder().withLibraryLoader( new InstanceLibraryLoader( additionalLibraries ) );

        configureBuilder( builder );

        MAEEmbedder embedder = builder.build();
        for ( final ComponentKey<?> key : getInstanceRegistry().getInstances().keySet() )
        {
            try
            {
                builder.container().lookup( key.getRoleClass(), key.getHint() );
            }
            catch ( final ComponentLookupException e )
            {
                throw new MAEException( "Forced member-injection for registered instance: %s failed. Reason: %s", e,
                                        key, e.getMessage() );
            }
        }

        afterLoading( embedder );

        loaded = true;

        return this;
    }

    /**
     * Register a new, external component instance for injection into other components, or to
     * have components injected into it.
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    protected final void withComponentInstance( final Object instance )
    {
        getInstanceRegistry().add( new ComponentKey( instance.getClass() ), instance );
    }

    /**
     * Register a new {@link VirtualInstance}, which allows the component environment to bind its
     * requirements without actually having access to the component instance. The instance itself
     * will be injected into the {@link VirtualInstance} later.
     */
    protected final <C> void withVirtualComponent( final Class<C> virtualClass )
    {
        getInstanceRegistry().addVirtual( new VirtualInstance<C>( virtualClass ) );
    }

    /**
     * Set the actual instance on a {@link VirtualInstance} that was registered previously.
     */
    protected final <C, T extends C> void setVirtualInstance( final Class<C> virtualKey, final T instance )
    {
        getInstanceRegistry().setVirtualInstance( virtualKey, instance );
    }

    /**
     * Register a new, external component instance to make it available for injection, or to allow
     * other components to be injected into it.
     */
    protected final <C> void withComponentInstance( final ComponentKey<C> componentKey, final C instance )
    {
        getInstanceRegistry().add( componentKey, instance );
    }

    /**
     * Register a new {@link VirtualInstance}, which allows the component environment to bind its
     * requirements without actually having access to the component instance. The instance itself
     * will be injected into the {@link VirtualInstance} later.
     */
    protected final <C> void withVirtualComponent( final ComponentKey<C> virtualKey )
    {
        getInstanceRegistry().addVirtual( virtualKey, new VirtualInstance<C>( virtualKey.getRoleClass() ) );
    }

    /**
     * Set the actual instance on a {@link VirtualInstance} that was registered previously.
     */
    protected final <C, T extends C> void setVirtualInstance( final ComponentKey<C> virtualKey, final T instance )
    {
        getInstanceRegistry().setVirtualInstance( virtualKey, instance );
    }

    /**
     * Fine-tune the {@link MAEEmbedderBuilder} instance before it is used to create the 
     * {@link MAEEmbedder} that will be used to load the application components.
     */
    protected void configureBuilder( final MAEEmbedderBuilder builder )
        throws MAEException
    {
    }

    /**
     * Hook allowing application developers access to the {@link MAEEmbedder} just after the registered
     * external component instances have been injected, but before loading is considered complete.
     */
    protected void afterLoading(MAEEmbedder embedder)
        throws MAEException
    {
    }

    @Override
    public Logger getLogger()
    {
        return Logger.getLogger( getLogHandle() );
    }

    @Override
    public ExtensionConfiguration getConfiguration()
    {
        return null;
    }

    @Override
    public ComponentSelector getComponentSelector()
    {
        return null;
    }

    @Override
    public Set<ComponentKey<?>> getExportedComponents()
    {
        return null;
    }

    @Override
    public Set<ComponentKey<?>> getManagementComponents( final Class<?> managementType )
    {
        return null;
    }

    @Override
    public Map<Class<?>, Set<ComponentKey<?>>> getManagementComponents()
    {
        return null;
    }

    @Override
    public String getLabel()
    {
        return getName();
    }

    @Override
    public String getLogHandle()
    {
        return getId();
    }

    @Override
    public void loadConfiguration( final MAEConfiguration embConfig )
        throws ExtensionConfigurationException
    {
    }

    @Override
    public final InstanceRegistry getInstanceRegistry()
    {
        return instanceRegistry;
    }

    @Override
    public String getVersion()
    {
        final VersionProvider provider = getVersionProvider();
        if ( provider == null )
        {
            throw new IllegalStateException( "Your application booter: " + getClass().getName()
                            + " must implement either getVersion() or getVersionProvider()." );
        }

        return provider.getVersion();
    }

    protected VersionProvider getVersionProvider()
    {
        return null;
    }

}
