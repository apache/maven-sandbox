/**
 * Copyright (c) 2009 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.apache.maven.mae.internal.container.guice;

import org.codehaus.plexus.component.annotations.Component;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.internal.container.InstanceRegistry;
import org.sonatype.guice.bean.reflect.ClassSpace;
import org.sonatype.guice.bean.reflect.DeferredClass;
import org.sonatype.guice.plexus.config.PlexusBeanMetadata;
import org.sonatype.guice.plexus.config.PlexusBeanModule;
import org.sonatype.guice.plexus.config.PlexusBeanSource;
import org.sonatype.guice.plexus.scanners.PlexusXmlScanner;

import com.google.inject.Binder;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * {@link PlexusBeanModule} that collects {@link PlexusBeanMetadata} by scanning XML resources.
 */
public final class XPlexusXmlBeanModule
    implements PlexusBeanModule
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private final ClassSpace space;

    private final Map<?, ?> variables;

    private final URL plexusXml;

    private final ComponentSelector componentSelector;

    private final boolean root;

    private final InstanceRegistry instanceRegistry;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Creates a bean source that scans all the surrounding class spaces for XML resources.
     * 
     * @param space
     *            The main class space
     * @param variables
     *            The filter variables
     * @param plexusXml
     *            The plexus.xml URL
     */
    public XPlexusXmlBeanModule( final ComponentSelector componentSelector, final InstanceRegistry instanceRegistry,
                                 final ClassSpace space, final Map<?, ?> variables, final URL plexusXml )
    {
        this.componentSelector = componentSelector;
        this.instanceRegistry = instanceRegistry;
        this.space = space;
        this.variables = variables;
        this.plexusXml = plexusXml;
        root = true;
    }

    /**
     * Creates a bean source that only scans the local class space for XML resources.
     * 
     * @param space
     *            The local class space
     * @param variables
     *            The filter variables
     */
    public XPlexusXmlBeanModule( final ComponentSelector componentSelector, final InstanceRegistry instanceRegistry,
                                 final ClassSpace space, final Map<?, ?> variables )
    {
        this.componentSelector = componentSelector;
        this.instanceRegistry = instanceRegistry;
        this.space = space;
        this.variables = variables;
        plexusXml = null;
        root = false;
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public PlexusBeanSource configure( final Binder binder )
    {
        final Map<String, PlexusBeanMetadata> metadataMap = new HashMap<String, PlexusBeanMetadata>();
        final PlexusXmlScanner scanner = new PlexusXmlScanner( variables, plexusXml, metadataMap );
        final Map<Component, DeferredClass<?>> components = scanner.scan( space, root );
        final SelectingTypeBinder plexusTypeBinder =
            new SelectingTypeBinder( componentSelector, instanceRegistry, binder );
        for ( final Entry<Component, DeferredClass<?>> entry : components.entrySet() )
        {
            plexusTypeBinder.hear( entry.getKey(), entry.getValue(), space );
        }

        return new PlexusXmlBeanSource( metadataMap );
    }

    // ----------------------------------------------------------------------
    // Implementation types
    // ----------------------------------------------------------------------

    private static final class PlexusXmlBeanSource
        implements PlexusBeanSource
    {
        private Map<String, PlexusBeanMetadata> metadataMap;

        PlexusXmlBeanSource( final Map<String, PlexusBeanMetadata> metadataMap )
        {
            this.metadataMap = metadataMap;
        }

        public PlexusBeanMetadata getBeanMetadata( final Class<?> implementation )
        {
            if ( null == metadataMap )
            {
                return null;
            }
            final PlexusBeanMetadata metadata = metadataMap.remove( implementation.getName() );
            if ( metadataMap.isEmpty() )
            {
                metadataMap = null;
            }
            return metadata;
        }
    }
}
