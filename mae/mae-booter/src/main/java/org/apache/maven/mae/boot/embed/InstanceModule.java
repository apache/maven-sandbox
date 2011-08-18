package org.apache.maven.mae.boot.embed;

import java.util.Map;

import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.InstanceRegistry;
import org.apache.maven.mae.internal.container.VirtualInstance;
import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.guice.bean.reflect.DeferredClass;
import org.sonatype.guice.bean.reflect.LoadedClass;
import org.sonatype.guice.plexus.config.Roles;
import org.sonatype.guice.plexus.config.Strategies;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.binder.ScopedBindingBuilder;

public class InstanceModule
    implements Module
{

    private final InstanceRegistry registry;

    public InstanceModule( final InstanceRegistry reg )
    {
        this.registry = reg;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @Override
    public void configure( final Binder binder )
    {
        for ( final Map.Entry<ComponentKey<?>, Object> mapping : registry )
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
                    hear( binder, comp, new LoadedClass<Object>( cls ),
                          "External instance loaded from: " + cls.getClassLoader(), vi );
                }
                else
                {
                    binder.bind( Roles.componentKey( key.getRoleClass(), key.getHint() ) ).toProvider( (Provider) instance );
                }
            }
            else
            {
                InstanceProvider provider = new InstanceProvider( instance );

                final Component comp = instance.getClass().getAnnotation( Component.class );
                if ( comp != null )
                {
                    hear( binder, comp, new LoadedClass<Object>( instance.getClass() ),
                          "External instance loaded from: " + instance.getClass().getClassLoader(), provider );
                }
                else
                {
                    binder.bind( Roles.componentKey( key.getRoleClass(), key.getHint() ) ).toProvider( provider );
                }
            }
        }
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private void hear( final Binder binder, final Component component, final DeferredClass<?> clazz,
                       final String source, final Provider<?> provider )
    {
        final Key roleKey = Roles.componentKey( component );
        final String strategy = component.instantiationStrategy();

        final ScopedBindingBuilder sbb =
            binder.withSource( source == null ? component.description() : source ).bind( roleKey ).toProvider( provider );

        if ( Strategies.LOAD_ON_START.equals( strategy ) )
        {
            sbb.asEagerSingleton();
        }
        else if ( !Strategies.PER_LOOKUP.equals( strategy ) )
        {
            sbb.in( Scopes.SINGLETON );
        }
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
