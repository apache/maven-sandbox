package org.apache.maven.mae.boot.embed;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.guice.plexus.config.Hints;
import org.sonatype.guice.plexus.config.Roles;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
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
        binder.bindListener( Matchers.any(), new ComponentSelectingListener( selector ) );
    }

    private static final class ComponentSelectingListener
        implements TypeListener
    {

        private final ComponentSelector selector;

        public ComponentSelectingListener( final ComponentSelector selector )
        {
            this.selector = selector;
        }

        @Override
        public <I> void hear( final TypeLiteral<I> type, final TypeEncounter<I> encounter )
        {
            List<Field> requirementFields = new ArrayList<Field>();
            for ( Field field : type.getRawType().getDeclaredFields() )
            {
                if ( field.getAnnotation( Requirement.class ) != null )
                {
                    requirementFields.add( field );
                }
            }

            if ( !requirementFields.isEmpty() )
            {
                encounter.register( new ComponentSelectionInjector<I>( selector, requirementFields ) );
            }
        }
    }

    private static final class ComponentSelectionInjector<T>
        implements MembersInjector<T>
    {
        // FIXME: This is NOT being injected!!
        @Inject
        private Injector injector;

        private final ComponentSelector selector;

        private final List<Field> fields;

        public ComponentSelectionInjector( final ComponentSelector selector, final List<Field> fields )
        {
            this.selector = selector;
            this.fields = fields;
        }

        @SuppressWarnings( { "rawtypes", "unchecked" } )
        @Override
        public void injectMembers( final T instance )
        {
            for ( Field field : fields )
            {
                Requirement req = field.getAnnotation( Requirement.class );
                Class role = req.role();
                if ( role == null )
                {
                    role = field.getType();
                }

                // FIXME: What about req.hints() ???
                String hint = req.hint();
                if ( hint == null )
                {
                    hint = Hints.DEFAULT_HINT;
                }

                ComponentKey key;
                if ( ComponentKey.isLiteral( hint ) )
                {
                    key = new ComponentKey( role, ComponentKey.getLiteralHint( hint ) );
                }
                else if ( selector.hasOverride( role, hint ) )
                {
                    key = selector.getOverride( role, hint );
                }
                else
                {
                    key = new ComponentKey( role, hint );
                }

                set( field, instance, key );
            }
        }

        @SuppressWarnings( { "rawtypes", "unchecked" } )
        private void set( final Field field, final Object instance, final ComponentKey key )
        {
            Key componentKey = Roles.componentKey( key.getRoleClass(), key.getHint() );
            Object value = injector.getInstance( componentKey );
            try
            {
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
        }

    }

}
