package org.codehaus.plexus.util;

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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * ExceptionUtils contains helper methods for treating Throwable objects.
 * Please note that lots of the given methods are nowadays not needed anymore
 * with Java &gt; 1.4. With Java-1.4 the Exception class itself got all
 * necessary methods to treat chained Exceptions. The original ExceptionUtils
 * got created when this was not yet the case!
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class ExceptionUtils
{
    /**
     * This method is only here for backward compat reasons.
     * It's original means was to add additional method names
     * which got checked to determine if the Throwable in question
     * is a chained exception.
     * It's not needed anymore in case of java &gt; 1.4 since
     * Throwable itself supports chains nowadays.
     *
     * @param methodName
     */
    @Deprecated
    public static void addCauseMethodName( String methodName )
    {
        // we don't need this anymore!
    }

    public static Throwable getCause( Throwable throwable )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return null;
    }

    public static Throwable getCause( Throwable throwable, String[] methodNames )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return null;
    }

    public static Throwable getRootCause( Throwable throwable )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return null;
    }

    public static int getThrowableCount( Throwable throwable )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return -1;
    }

    public static Throwable[] getThrowables( Throwable throwable )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return null;
    }

    public static int indexOfThrowable( Throwable throwable, Class type )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return -1;
    }

    public static int indexOfThrowable( Throwable throwable, Class type, int fromIndex )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return -1;
    }

    public static void printRootCauseStackTrace( Throwable t, PrintStream stream )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
    }

    public static void printRootCauseStackTrace( Throwable t )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
    }

    public static void printRootCauseStackTrace( Throwable t, PrintWriter writer )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
    }

    public static String[] getRootCauseStackTrace( Throwable t )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return null;
    }

    public static String getStackTrace( Throwable t )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return null;
    }

    public static String getFullStackTrace( Throwable t )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return null;
    }

    public static boolean isNestedThrowable( Throwable throwable )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return true;
    }

    public static String[] getStackFrames( Throwable t )
    {
        System.out.println("TODO IMPLEMENT");
        //X TODO implement
        return null;
    }

}
