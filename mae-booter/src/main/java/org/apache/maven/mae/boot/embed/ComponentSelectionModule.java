package org.apache.maven.mae.boot.embed;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

// FIXME: Test this! It's definitely not complete.
public class ComponentSelectionModule
    implements Module
{

    private final ComponentSelector selector;

    public ComponentSelectionModule( final ComponentSelector selector )
    {
        this.selector = selector;
    }

    @Override
    public void configure( final Binder binder )
    {
        // binder.bindListener( Matchers.any(), new SysoutTypeListener() );
        binder.bindListener( Matchers.any(), new ComponentSelectingListener( selector, binder ) );
    }

    @SuppressWarnings( "unused" )
    private static final class SysoutTypeListener
        implements TypeListener
    {

        @Override
        public <I> void hear( final TypeLiteral<I> type, final TypeEncounter<I> encounter )
        {
            Set<InjectionPoint> injectionPoints = InjectionPoint.forInstanceMethodsAndFields( type.getRawType() );
            if ( injectionPoints != null && !injectionPoints.isEmpty() )
            {
                System.out.println( type.getRawType().getName() );

                for ( InjectionPoint ip : injectionPoints )
                {
                    Dependency<?> dep = ip.getDependencies().get( 0 );
                    System.out.printf( "%s --> %s", ip.getMember(), dep.getKey() );
                }
            }

            Component comp = type.getRawType().getAnnotation( Component.class );
            if ( comp != null )
            {
                ComponentKey<I> found = new ComponentKey<I>( comp );

                System.out.printf( "%s [Provider: %s]\n", found, encounter.getProvider( found.componentKey() ) );
            }
            else
            {
                System.out.printf( "RAW: %s\n", type.getRawType().getName() );
            }
        }

    }

    private static final class ComponentSelectingListener
        implements TypeListener
    {

        private final ComponentSelector selector;

        private final Binder binder;

        public ComponentSelectingListener( final ComponentSelector selector, final Binder binder )
        {
            this.selector = selector;
            this.binder = binder;
        }

        @SuppressWarnings( { "unchecked", "rawtypes" } )
        @Override
        public <I> void hear( final TypeLiteral<I> type, final TypeEncounter<I> encounter )
        {
            Map<Field, Provider<?>> requirementFields = new HashMap<Field, Provider<?>>();

            // Component comp = type.getRawType().getAnnotation( Component.class );
            // Set<InjectionPoint> injectionPoints = InjectionPoint.forInstanceMethodsAndFields( type.getRawType() );
            // if ( comp != null || ( injectionPoints != null && !injectionPoints.isEmpty() ) )
            // {
            // encounter.register( null )
            // }

            for ( Field field : type.getRawType().getDeclaredFields() )
            {
                Requirement req = field.getAnnotation( Requirement.class );
                if ( req != null )
                {
                    // FIXME: Collections!
                    if ( !field.getType().equals( req.role() ) )
                    {
                        // System.out.printf( "Found collection? %s\n", field );
                    }
                    else
                    {
                        ComponentKey key = new ComponentKey( req, field );

                        Key componentKey = key.componentKey();
                        if ( key.isLiteral() )
                        {
                            componentKey = key.rawComponentKey();
                            // System.out.printf( "%s ------> %s\n", field, componentKey );
                        }
                        else if ( selector.hasOverride( key ) )
                        {
                            binder.bind( key.literalComponentKey() ).toProvider( encounter.getProvider( key.componentKey() ) );
                            componentKey = selector.getOverride( key ).componentKey();
                            // System.out.printf( "%s ------> %s\n", field, componentKey );
                        }

                        requirementFields.put( field, encounter.getProvider( componentKey ) );
                    }
                }
            }

            if ( !requirementFields.isEmpty() )
            {
                final ComponentSelectionInjector<I> injector = new ComponentSelectionInjector<I>( requirementFields );
                encounter.register( new MembersInjector<I>()
                {
                    @Override
                    public void injectMembers( final I instance )
                    {
                        injector.inject( instance );
                    }
                } );

                encounter.register( new InjectionListener<I>()
                {
                    @Override
                    public void afterInjection( final I instance )
                    {
                        injector.inject( instance );
                    }
                } );
            }
        }
    }

    private static final class ComponentSelectionInjector<T>
    {
        private final Map<Field, Provider<?>> fields;

        public ComponentSelectionInjector( final Map<Field, Provider<?>> fields )
        {
            this.fields = fields;
        }

        public void inject( final T instance )
        {
            for ( Map.Entry<Field, Provider<?>> entry : fields.entrySet() )
            {
                Field field = entry.getKey();
                Provider<?> provider = entry.getValue();
                boolean acc = field.isAccessible();
                if ( !acc )
                {
                    field.setAccessible( true );
                }

                try
                {
                    Object value = provider.get();
                    System.out.printf( "Setting %s to: %s\n", field, value );
                    field.set( instance, value );
                }
                catch ( IllegalArgumentException e )
                {
                    throw new RuntimeException( e );
                }
                catch ( IllegalAccessException e )
                {
                    throw new RuntimeException( e );
                }
                finally
                {
                    field.setAccessible( acc );
                }

                // FieldSetter setter = new FieldSetter( entry.getKey(), entry.getValue(), instance );
                // AccessController.doPrivileged( setter );
            }
        }

    }

    @SuppressWarnings( "unused" )
    private static final class FieldSetter<T>
        implements PrivilegedAction<T>
    {

        private final Field field;

        private final Provider<?> provider;

        private final T instance;

        FieldSetter( final Field field, final Provider<?> provider, final T instance )
        {
            this.field = field;
            this.provider = provider;
            this.instance = instance;

        }

        @Override
        public T run()
        {
            try
            {
                field.set( instance, provider.get() );
            }
            catch ( IllegalArgumentException e )
            {
                throw new RuntimeException( e );
            }
            catch ( IllegalAccessException e )
            {
                throw new RuntimeException( e );
            }

            return null;
        }

    }

}
