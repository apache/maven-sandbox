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

package org.apache.maven.mae.internal.container;

import static org.codehaus.plexus.util.StringUtils.isBlank;

public class ComponentKey<T>
{

    public static final String DEFAULT_HINT = "default".intern();

    public static final String LITERAL_SUFFIX = "_";

    private final Class<T> roleClass;

    private final String hint;

    public ComponentKey( final Class<T> role, final String hint )
    {
        roleClass = role;
        this.hint = isBlank( hint ) || DEFAULT_HINT.equals( hint ) ? DEFAULT_HINT : hint.intern();
    }

    public ComponentKey( final Class<T> role )
    {
        roleClass = role;
        hint = DEFAULT_HINT;
    }

    public String getRole()
    {
        return roleClass.getName();
    }

    public String getHint()
    {
        return hint;
    }

    public String key()
    {
        return roleClass + ( DEFAULT_HINT.equals( hint ) ? "" : "#" + hint );
    }

    @Override
    public String toString()
    {
        return key();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + hint.hashCode();
        result = prime * result + roleClass.getName().hashCode();
        return result;
    }

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

        final ComponentKey<?> other = ComponentKey.class.cast( obj );
        if ( !hint.equals( other.hint ) )
        {
            return false;
        }

        if ( !roleClass.getName().equals( other.roleClass.getName() ) )
        {
            return false;
        }

        return true;
    }

    public Class<T> getRoleClass()
    {
        return roleClass;
    }

    public T castValue( final Object instance )
    {
        return instance == null ? null : roleClass.cast( instance );
    }

    public static boolean isLiteral( final String value )
    {
        return value != null && value.length() > 1 && value.endsWith( LITERAL_SUFFIX );
    }

    public static String getLiteralHint( final String value )
    {
        return isLiteral( value ) ? value.substring( 0, value.length() - 1 ) : value;
    }

}
