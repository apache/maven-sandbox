/*
 * Copyright 2003, 2004  The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 */
package org.apache.maven.shared.parser.java;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/** <p>Accessor class for JavaQName.</p>
 * 
 * 
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id$
 */
public class JavaQNameImpl
{
    private static final Map names = new HashMap();

    private abstract static class DefaultImpl
        implements JavaQName
    {
        public int compareTo( Object pOther )
        {
            JavaQName other = (JavaQName) pOther;
            if ( isArray() )
            {
                if ( !other.isArray() )
                {
                    return -1;
                }
                else
                {
                    return getInstanceClass().compareTo( other.getInstanceClass() );
                }
            }
            else
            {
                if ( other.isArray() )
                {
                    return 1;
                }
            }
            int result = getPackageName().compareTo( other.getPackageName() );
            if ( result != 0 )
            {
                return result;
            }
            result = getClassName().compareTo( other.getClassName() );
            if ( result != 0 )
            {
                return result;
            }
            return 0;
        }

        public int hashCode()
        {
            return getPackageName().hashCode() + getClassName().hashCode();
        }

        public boolean equals( Object pOther )
        {
            if ( pOther == null || !( pOther instanceof JavaQName ) )
            {
                return false;
            }
            return compareTo( pOther ) == 0;
        }
    }

    private static class StandardImpl
        extends DefaultImpl
    {
        private String packageName, className;

        public StandardImpl( String pPackageName, String pClassName )
        {
            checkPackageName( pPackageName );
            packageName = pPackageName;
            className = pClassName;
        }

        protected void checkPackageName( String pPackageName )
        {
            for ( StringTokenizer st = new StringTokenizer( pPackageName, "." ); st.hasMoreTokens(); )
            {
                String tok = st.nextToken();
                if ( tok.length() == 0 )
                {
                    throw new IllegalArgumentException( "Invalid package name: " + pPackageName );
                }
                for ( int i = 0; i < tok.length(); i++ )
                {
                    char c = tok.charAt( i );
                    if ( ( i == 0 && !Character.isJavaIdentifierStart( c ) )
                        || ( i > 0 && !Character.isJavaIdentifierPart( c ) ) )
                    {
                        throw new IllegalArgumentException( "Invalid package name: " + pPackageName );
                    }
                }
            }
        }

        public String getPackageName()
        {
            return packageName;
        }

        public String getClassName()
        {
            return className;
        }

        public boolean isArray()
        {
            return false;
        }

        public JavaQName getInstanceClass()
        {
            throw new IllegalStateException( "The class " + this + "is not an array class." );
        }

        public boolean isImportable()
        {
            return true;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public String toString()
        {
            String c = getClassName();
            String p = getPackageName();
            return p.length() > 0 ? ( p + "." + c ) : c;
        }

        public String getOuterClassName()
        {
            int offset = className.lastIndexOf( '$' );
            if ( offset == -1 )
            {
                return null;
            }
            else
            {
                return className.substring( 0, offset );
            }
        }

        public String getInnerClassName()
        {
            int offset = className.lastIndexOf( '$' );
            if ( offset == -1 )
            {
                return className;
            }
            else
            {
                return className.substring( offset + 1 );
            }
        }

        public boolean isInnerClass()
        {
            return className.indexOf( '$' ) > 0;
        }

        public JavaQName getObjectType()
        {
            return null;
        }

        public String getPrimitiveConversionMethod()
        {
            return null;
        }
    }

    private static class PrimitiveImpl
        extends StandardImpl
    {
        private final JavaQName objectType;

        private final String primitiveConversionMethod;

        public PrimitiveImpl( String pClassName, JavaQName pObjectType, String pConversionMethod )
        {
            super( "", pClassName );
            objectType = pObjectType;
            primitiveConversionMethod = pConversionMethod;
        }

        public boolean isImportable()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public void checkPackageName()
        {
        }

        public String toString()
        {
            return getClassName();
        }

        public JavaQName getObjectType()
        {
            return objectType;
        }

        public String getPrimitiveConversionMethod()
        {
            return primitiveConversionMethod;
        }
    }

    private static class ArrayImpl
        extends DefaultImpl
    {
        private JavaQName qName;

        public ArrayImpl( JavaQName pQName )
        {
            qName = pQName;
        }

        public String getPackageName()
        {
            return qName.getPackageName();
        }

        public String getClassName()
        {
            return qName.getClassName();
        }

        public boolean isArray()
        {
            return true;
        }

        public boolean isImportable()
        {
            return false;
        }

        public boolean isPrimitive()
        {
            return false;
        }

        public JavaQName getInstanceClass()
        {
            return qName;
        }

        public String toString()
        {
            return qName.toString() + "[]";
        }

        public String getOuterClassName()
        {
            return null;
        }

        public String getInnerClassName()
        {
            return qName.getClassName();
        }

        public boolean isInnerClass()
        {
            return false;
        }

        public JavaQName getObjectType()
        {
            return null;
        }

        public String getPrimitiveConversionMethod()
        {
            return null;
        }
    }

    /** <p>For use in return types or method parameters: The
     * void type.</p>
     */
    public static final JavaQName VOID = new PrimitiveImpl( void.class.getName(), null, null );

    /** <p>For use in return types or method parameters: The
     * boolean type.</p>
     */
    public static final JavaQName BOOLEAN = new PrimitiveImpl( boolean.class.getName(), JavaQNameImpl
        .getInstance( Boolean.class ), "booleanValue()" );

    /** <p>For use in return types or method parameters: The
     * byte type.</p>
     */
    public static final JavaQName BYTE = new PrimitiveImpl( byte.class.getName(), JavaQNameImpl
        .getInstance( Byte.class ), "byteValue" );

    /** <p>For use in return types or method parameters: The
     * short type.</p>
     */
    public static final JavaQName SHORT = new PrimitiveImpl( short.class.getName(), JavaQNameImpl
        .getInstance( Short.class ), "shortValue" );

    /** <p>For use in return types or method parameters: The
     * int type.</p>
     */
    public static final JavaQName INT = new PrimitiveImpl( int.class.getName(), JavaQNameImpl
        .getInstance( Integer.class ), "intValue" );

    /** <p>For use in return types or method parameters: The
     * long type.</p>
     */
    public static final JavaQName LONG = new PrimitiveImpl( long.class.getName(), JavaQNameImpl
        .getInstance( Long.class ), "longValue" );

    /** <p>For use in return types or method parameters: The
     * float type.</p>
     */
    public static final JavaQName FLOAT = new PrimitiveImpl( float.class.getName(), JavaQNameImpl
        .getInstance( Float.class ), "floatValue" );

    /** <p>For use in return types or method parameters: The
     * double type.</p>
     */
    public static final JavaQName DOUBLE = new PrimitiveImpl( double.class.getName(), JavaQNameImpl
        .getInstance( Double.class ), "doubleValue" );

    /** <p>For use in return types or method parameters: The
     * char type.</p>
     */
    public static final JavaQName CHAR = new PrimitiveImpl( char.class.getName(), JavaQNameImpl
        .getInstance( Character.class ), "charValue" );

    private static final JavaQName[] primitives = new JavaQName[] {
        VOID,
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR };

    private static final Class[] primitiveClasses = new Class[] {
        void.class,
        boolean.class,
        byte.class,
        short.class,
        int.class,
        long.class,
        float.class,
        double.class,
        char.class };

    public static JavaQName getInstance( Class pClass )
    {
        if ( pClass.isArray() )
        {
            return getArray( getInstance( pClass.getComponentType() ) );
        }
        if ( pClass.isPrimitive() )
        {
            for ( int i = 0; i < primitives.length; i++ )
            {
                if ( primitiveClasses[i].equals( pClass ) )
                {
                    return primitives[i];
                }
            }
            throw new IllegalArgumentException( "Unknown primitive type: " + pClass.getClass().getName() );
        }
        if ( void.class.equals( pClass ) )
        {
            return VOID;
        }

        String name = pClass.getName();
        int offset = name.lastIndexOf( '.' );
        if ( offset == -1 )
        {
            return getInstance( null, name );
        }
        else
        {
            return getInstance( name.substring( 0, offset ), name.substring( offset + 1 ) );
        }
    }

    public static JavaQName getInstance( String pPackageName, String pClassName )
    {
        if ( pClassName == null || pClassName.length() == 0 )
        {
            throw new NullPointerException( "The class name must not be null or empty." );
        }
        String name;
        if ( pPackageName == null || pPackageName.length() == 0 )
        {
            for ( int i = 0; i < primitives.length; i++ )
            {
                if ( primitiveClasses[i].getName().equals( pClassName ) )
                {
                    return primitives[i];
                }
            }
            name = pClassName;
            pPackageName = "";
        }
        else
        {
            name = pPackageName + "." + pClassName;
        }
        JavaQName result;
        synchronized ( names )
        {
            result = (JavaQName) names.get( name );
            if ( result == null )
            {
                if ( pClassName.endsWith( "[]" ) )
                {
                    String instanceClassName = pClassName.substring( 0, pClassName.length() - 2 );
                    JavaQName instanceClass = getInstance( pPackageName, instanceClassName );
                    result = new ArrayImpl( instanceClass );
                }
                else
                {
                    result = new StandardImpl( pPackageName, pClassName );
                }
                names.put( name, result );
            }
        }
        return result;
    }

    /** <p>Returns an instance with the given class name.</p>
     * @param pClassName The class name
     */
    public static JavaQName getInstance( String pClassName )
    {
        int offset = pClassName.lastIndexOf( '.' );
        if ( offset == -1 )
        {
            return getInstance( null, pClassName );
        }
        else
        {
            return getInstance( pClassName.substring( 0, offset ), pClassName.substring( offset + 1 ) );
        }
    }

    /** Returns an instance of {@link JavaQName}, which represents
     * an array. The array elements are instances of <code>pQName</code>.
     */
    public static JavaQName getArray( JavaQName pQName )
    {
        String name = pQName.toString() + "[]";
        JavaQName result;
        synchronized ( names )
        {
            result = (JavaQName) names.get( name );
            if ( result == null )
            {
                result = new ArrayImpl( pQName );
                names.put( name, result );
            }
        }
        return result;
    }

    /** <p>Returns the fully qualified name of an inner class of
     * <code>pQName</code> with the name <code>pInnerClassName</code>.</p>
     */
    public static JavaQName getInnerInstance( JavaQName pQName, String pInnerClassName )
    {
        return getInstance( pQName.getPackageName(), pQName.getClassName() + "$" + pInnerClassName );
    }
}
