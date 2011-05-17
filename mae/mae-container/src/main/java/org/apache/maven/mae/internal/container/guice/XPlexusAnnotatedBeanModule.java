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

import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.internal.container.InstanceRegistry;
import org.sonatype.guice.bean.binders.SpaceModule;
import org.sonatype.guice.bean.reflect.ClassSpace;
import org.sonatype.guice.bean.scanners.ClassSpaceVisitor;
import org.sonatype.guice.plexus.config.PlexusBeanModule;
import org.sonatype.guice.plexus.config.PlexusBeanSource;
import org.sonatype.guice.plexus.scanners.PlexusTypeVisitor;
import org.sonatype.inject.BeanScanning;

import com.google.inject.Binder;

import java.util.Map;

/**
 * {@link PlexusBeanModule} that registers Plexus beans by scanning classes for runtime annotations.
 */
public final class XPlexusAnnotatedBeanModule
    implements PlexusBeanModule
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private final ClassSpace space;

    private final Map<?, ?> variables;

    private final ComponentSelector componentSelector;

    private final InstanceRegistry instanceRegistry;

    private final BeanScanning scanning;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Creates a bean source that scans the given class space for Plexus annotations using the given scanner.
     * 
     * @param componentSelector
     * @param space The local class space
     * @param variables The filter variables
     * @param scanning
     */
    public XPlexusAnnotatedBeanModule( final ComponentSelector componentSelector,
                                       final InstanceRegistry instanceRegistry, final ClassSpace space,
                                       final Map<?, ?> variables, BeanScanning scanning )
    {
        this.componentSelector = componentSelector;
        this.instanceRegistry = instanceRegistry;
        this.space = space;
        this.variables = variables;
        this.scanning = scanning;
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public PlexusBeanSource configure( final Binder binder )
    {
        if ( null != space && scanning != BeanScanning.OFF )
        {
            new PlexusSpaceModule( componentSelector, instanceRegistry, space, scanning ).configure( binder );
        }

        return new XAnnotatedBeanSource( variables );
    }

    // ----------------------------------------------------------------------
    // Implementation types
    // ----------------------------------------------------------------------

    private static final class PlexusSpaceModule
        extends SpaceModule
    {
        private final InstanceRegistry instanceRegistry;

        private final ComponentSelector componentSelector;

        PlexusSpaceModule( final ComponentSelector componentSelector, final InstanceRegistry instanceRegistry,
                           final ClassSpace space, final BeanScanning scanning )
        {
            super( space, scanning );
            this.componentSelector = componentSelector;
            this.instanceRegistry = instanceRegistry;
        }

        @Override
        protected ClassSpaceVisitor visitor( final Binder binder )
        {
            return new PlexusTypeVisitor( new SelectingTypeBinder( componentSelector, instanceRegistry, binder ) );
        }
    }
}
