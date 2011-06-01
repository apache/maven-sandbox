package org.apache.maven.tck;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.allOf;

/**
 * Common {@link Matcher}s used in the TCK.
 */
public class TckMatchers
{
    /**
     * A matcher which checks that the class is a Utility class (i.e. is final and has only private constructors).
     *
     * @return A matcher which checks that the class is a Utility class (i.e. is final and has only private constructors).
     */
    public static Matcher<Class<?>> isUtilityClass()
    {
        return allOf( isFinalClass(), isClassWithOnlyPrivateConstructors() );
    }

    /**
     * A matcher which checks that the class has only private constructors.
     *
     * @return A matcher which checks that the class has only private constructors.
     */
    public static Matcher<Class<?>> isClassWithOnlyPrivateConstructors()
    {
        return new IsClassWithOnlyPrivateConstructors();
    }

    /**
     * A matcher which checks that the class is final.
     *
     * @return A matcher which checks that the class is final.
     */
    public static Matcher<Class<?>> isFinalClass()
    {
        return new IsFinalClass();
    }

    /**
     * A matcher which checks that the class has the default constructor.
     *
     * @return A matcher which checks that the class has the default constructor.
     */
    public static Matcher<Class<?>> hasDefaultConstructor()
    {
        return new HasDefaultConstructor();
    }

    /**
     * A matcher which checks that a {@link Task} will take longer than the supplied number of ms to complete.
     *
     * @param ms the duration to complete within.
     * @return A matcher which checks that a {@link Task} will take longer than the supplied number of ms to
     *         complete.
     */
    public static Matcher<Task> runsForLongerThan( long ms )
    {
        return new RunsForLongerThan( ms );
    }

    private static class HasDefaultConstructor
        extends BaseMatcher<Class<?>>
    {
        public boolean matches( Object item )
        {
            Class<?> clazz = (Class<?>) item;
            try
            {
                Constructor<?> constructor = clazz.getConstructor();
                return Modifier.isPublic( constructor.getModifiers() );
            }
            catch ( NoSuchMethodException e )
            {
                return false;
            }
        }

        public void describeTo( Description description )
        {
            description.appendText( "a class with the default constructor" );
        }
    }

    private static class IsClassWithOnlyPrivateConstructors
        extends BaseMatcher<Class<?>>
    {
        public boolean matches( Object item )
        {
            Class<?> clazz = (Class<?>) item;
            for ( Constructor c : clazz.getConstructors() )
            {
                if ( !Modifier.isPrivate( c.getModifiers() ) )
                {
                    return false;
                }
            }
            return true;
        }

        public void describeTo( Description description )
        {
            description.appendText( "a class with only private constructors" );
        }
    }

    private static class IsFinalClass
        extends BaseMatcher<Class<?>>
    {
        public boolean matches( Object item )
        {
            Class<?> clazz = (Class<?>) item;
            return Modifier.isFinal( clazz.getModifiers() );
        }

        public void describeTo( Description description )
        {
            description.appendText( "a final class" );
        }
    }

    private static class RunsForLongerThan
        extends BaseMatcher<Task>
    {

        private final long duration;

        public RunsForLongerThan( long duration )
        {
            this.duration = duration;
        }

        public boolean matches( Object item )
        {
            final Task task = Task.class.cast( item );
            final AtomicBoolean didNotFinish = new AtomicBoolean( true );
            final Thread worker = new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        task.task();
                        didNotFinish.set( false );
                    }
                    catch ( Exception t )
                    {
                        // ignore
                    }
                }
            };
            try
            {
                worker.start();
                worker.join( 100 );
            }
            catch ( InterruptedException e )
            {
                // ignore
            }
            finally
            {
                worker.interrupt();
            }
            return didNotFinish.get();
        }

        public void describeTo( Description description )
        {
            description.appendText( "takes longer than " ).appendValue( duration ).appendText( "ms to complete" );
        }
    }
}
