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

import org.apache.log4j.Logger;
import org.apache.maven.mae.conf.ext.ExtensionConfiguration;
import org.apache.maven.mae.conf.ext.ExtensionConfigurationException;
import org.apache.maven.mae.conf.ext.ExtensionConfigurationLoader;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.internal.container.InstanceRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMAELibrary
    implements MAELibrary
{

    private final String name;

    private final VersionProvider versionProvider;

    private final String logHandle;

    private final Logger logger;

    private final String id;

    private final ExtensionConfigurationLoader configLoader;

    private ExtensionConfiguration config;

    private final ComponentSelector selector;

    private final Set<ComponentKey<?>> exportedComponents = new HashSet<ComponentKey<?>>();

    private final Map<Class<?>, Set<ComponentKey<?>>> managementComponents =
        new HashMap<Class<?>, Set<ComponentKey<?>>>();

    private final InstanceRegistry instanceRegistry = new InstanceRegistry();

    protected AbstractMAELibrary( final String id, final String name, final VersionProvider versionProvider,
                                  final ExtensionConfigurationLoader configLoader )
    {
        this( id, name, versionProvider, id, configLoader, null );
    }

    protected AbstractMAELibrary( final String id, final String name, final VersionProvider versionProvider,
                                  final String logHandle )
    {
        this( id, name, versionProvider, logHandle, null, null );
    }

    protected AbstractMAELibrary( final String id, final String name, final VersionProvider versionProvider )
    {
        this( id, name, versionProvider, id, null, null );
    }

    protected AbstractMAELibrary( final String id, final String name, final VersionProvider versionProvider,
                                  final String logHandle, final ExtensionConfigurationLoader configLoader )
    {
        this( id, name, versionProvider, logHandle, configLoader, null );
    }

    protected AbstractMAELibrary( final String id, final String name, final VersionProvider versionProvider,
                                  final ExtensionConfigurationLoader configLoader,
                                  final ComponentSelector componentSelector )
    {
        this( id, name, versionProvider, id, configLoader, componentSelector );
    }

    protected AbstractMAELibrary( final String id, final String name, final VersionProvider versionProvider,
                                  final String logHandle, final ComponentSelector componentSelector )
    {
        this( id, name, versionProvider, logHandle, null, componentSelector );
    }

    protected AbstractMAELibrary( final String id, final String name, final VersionProvider versionProvider,
                                  final ComponentSelector componentSelector )
    {
        this( id, name, versionProvider, id, null, componentSelector );
    }

    protected AbstractMAELibrary( final String id, final String name, final VersionProvider versionProvider,
                                  final String logHandle, final ExtensionConfigurationLoader configLoader,
                                  final ComponentSelector componentSelector )
    {
        this.id = id;
        this.name = name;
        this.versionProvider = versionProvider;
        this.logHandle = logHandle;
        this.configLoader = configLoader;
        selector = componentSelector;
        logger = Logger.getLogger( logHandle );
    }

    public ComponentSelector getComponentSelector()
    {
        return selector;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public void loadConfiguration( final MAEConfiguration embConfig )
        throws ExtensionConfigurationException
    {
        if ( configLoader != null )
        {
            config = configLoader.loadConfiguration( embConfig );
        }
    }

    protected void setConfiguration( final ExtensionConfiguration config )
    {
        this.config = config;
    }

    public ExtensionConfiguration getConfiguration()
    {
        return config;
    }

    public String getLabel()
    {
        return name + ": " + versionProvider.getVersion();
    }

    public String getId()
    {
        return id;
    }

    public String getLogHandle()
    {
        return logHandle;
    }

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return versionProvider.getVersion();
    }

    protected AbstractMAELibrary withExportedComponent( final ComponentKey<?> key )
    {
        exportedComponents.add( key );
        return this;
    }

    @Override
    public Set<ComponentKey<?>> getExportedComponents()
    {
        return exportedComponents;
    }

    public Map<Class<?>, Set<ComponentKey<?>>> getManagementComponents()
    {
        return managementComponents;
    }

    public AbstractMAELibrary withManagementComponent( final ComponentKey<?> key, final Class<?>... managementTypes )
    {
        if ( managementTypes != null )
        {
            for ( final Class<?> managementType : managementTypes )
            {
                Set<ComponentKey<?>> keys = managementComponents.get( managementType );
                if ( keys == null )
                {
                    keys = new HashSet<ComponentKey<?>>();
                    managementComponents.put( managementType, keys );
                }
                keys.add( key );
            }
        }

        return this;
    }

    public Set<ComponentKey<?>> getManagementComponents( final Class<?> managementType )
    {
        return managementComponents.get( managementType );
    }

    public <T> AbstractMAELibrary withComponentInstance( final ComponentKey<T> key, final T instance )
    {
        instanceRegistry.add( key, instance );
        return this;
    }

    @Override
    public InstanceRegistry getInstanceRegistry()
    {
        return instanceRegistry;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final AbstractMAELibrary other = (AbstractMAELibrary) obj;
        if ( id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !id.equals( other.id ) )
        {
            return false;
        }
        return true;
    }

}
