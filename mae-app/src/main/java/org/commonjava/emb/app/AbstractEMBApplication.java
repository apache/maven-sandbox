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

package org.commonjava.emb.app;

import org.apache.log4j.Logger;
import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.boot.embed.EMBEmbedderBuilder;
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

public abstract class AbstractEMBApplication
    implements EMBApplication
{

    private final List<MAELibrary> additionalLibraries = new ArrayList<MAELibrary>();

    private final InstanceRegistry instanceRegistry = new InstanceRegistry();

    private transient boolean loaded = false;

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    protected AbstractEMBApplication()
    {
        withLibrary( this );
        withComponentInstance( new ComponentKey( getClass() ), this );
    }

    protected final AbstractEMBApplication withLibrary( final MAELibrary library )
    {
        additionalLibraries.add( library );
        return this;
    }

    @Override
    public EMBApplication load()
        throws MAEException
    {
        return doLoad();
    }

    private synchronized EMBApplication doLoad()
        throws MAEException
    {
        if ( loaded )
        {
            return this;
        }

        final EMBEmbedderBuilder builder = new EMBEmbedderBuilder().withLibraryLoader( new InstanceLibraryLoader( additionalLibraries ) );

        beforeLoading();
        configureBuilder( builder );

        builder.build();
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

        afterLoading();

        loaded = true;

        return this;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    protected final void withComponentInstance( final Object instance )
    {
        getInstanceRegistry().add( new ComponentKey( instance.getClass() ), instance );
    }

    protected final <C> void withVirtualComponent( final Class<C> virtualClass )
    {
        getInstanceRegistry().addVirtual( new VirtualInstance<C>( virtualClass ) );
    }

    protected final <C, T extends C> void setVirtualInstance( final Class<C> virtualKey, final T instance )
    {
        getInstanceRegistry().setVirtualInstance( virtualKey, instance );
    }

    protected final <C> void withComponentInstance( final ComponentKey<C> componentKey, final C instance )
    {
        getInstanceRegistry().add( componentKey, instance );
    }

    protected final <C> void withVirtualComponent( final ComponentKey<C> virtualKey )
    {
        getInstanceRegistry().addVirtual( virtualKey, new VirtualInstance<C>( virtualKey.getRoleClass() ) );
    }

    protected final <C, T extends C> void setVirtualInstance( final ComponentKey<C> virtualKey, final T instance )
    {
        getInstanceRegistry().setVirtualInstance( virtualKey, instance );
    }

    protected void configureBuilder( final EMBEmbedderBuilder builder )
        throws MAEException
    {
    }

    protected void beforeLoading()
        throws MAEException
    {
    }

    protected void afterLoading()
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
