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

package org.apache.maven.mae.internal.container.guice;

import org.codehaus.plexus.component.annotations.Component;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.internal.container.InstanceRegistry;
import org.apache.maven.mae.internal.container.VirtualInstance;
import org.sonatype.guice.bean.reflect.LoadedClass;
import org.sonatype.guice.plexus.config.PlexusBeanModule;
import org.sonatype.guice.plexus.config.PlexusBeanSource;
import org.sonatype.guice.plexus.config.Roles;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import java.util.Map;

public class InstanceBindingModule
    implements PlexusBeanModule
{

    private final InstanceRegistry registry;

    private final ComponentSelector selector;

    private final Map<?, ?> variables;

    private SelectingTypeBinder typeBinder;

    public InstanceBindingModule( final InstanceRegistry registry, final ComponentSelector selector,
                                  final Map<?, ?> variables )
    {
        this.registry = registry;
        this.selector = selector;
        this.variables = variables;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public <T> ComponentKey<T> addInstance( final T instance )
    {
        final Component comp = instance.getClass().getAnnotation( Component.class );
        final ComponentKey<T> key = new ComponentKey( comp.role(), comp.hint() );
        if ( !registry.has( key ) )
        {
            registry.add( key, instance );

            final InstanceProvider<T> provider = new InstanceProvider( instance );

            typeBinder.hear( comp, new LoadedClass<Object>( instance.getClass() ), "External instance loaded from: "
                            + instance.getClass().getClassLoader(), provider );

            return key;
        }

        return null;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Override
    public PlexusBeanSource configure( final Binder binder )
    {
        typeBinder = new SelectingTypeBinder( selector, registry, binder );

        for ( final Map.Entry<ComponentKey<?>, Object> mapping : registry.getInstances().entrySet() )
        {
            final ComponentKey<?> key = mapping.getKey();
            final Object instance = mapping.getValue();

            if ( instance instanceof VirtualInstance )
            {
                final VirtualInstance vi = (VirtualInstance) instance;
                final Class<?> cls = vi.getVirtualClass();

                final Component comp = cls.getAnnotation( Component.class );
                if ( comp != null )
                {
                    typeBinder.hear( comp, new LoadedClass<Object>( cls ),
                                     "External instance loaded from: " + cls.getClassLoader(), vi );
                }
                else
                {
                    binder.bind( Roles.componentKey( key.getRoleClass(), key.getHint() ) )
                          .toProvider( (Provider) instance );
                }
            }
            else
            {
                final InstanceProvider provider = new InstanceProvider( instance );

                final Component comp = instance.getClass().getAnnotation( Component.class );
                if ( comp != null )
                {
                    typeBinder.hear( comp, new LoadedClass<Object>( instance.getClass() ),
                                     "External instance loaded from: " + instance.getClass().getClassLoader(), provider );
                }
                else
                {
                    binder.bind( Roles.componentKey( key.getRoleClass(), key.getHint() ) ).toProvider( provider );
                }
            }
        }

        return new XAnnotatedBeanSource( variables );
    }

    private static final class InstanceProvider<T>
        implements Provider<T>
    {
        @Inject
        private Injector injector;

        private final T instance;

        InstanceProvider( final T instance )
        {
            this.instance = instance;
        }

        @Override
        public T get()
        {
            injector.injectMembers( instance );
            return instance;
        }
    }

}
