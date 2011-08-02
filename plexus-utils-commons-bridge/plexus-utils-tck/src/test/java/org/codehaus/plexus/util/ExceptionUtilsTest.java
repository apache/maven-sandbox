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

import org.apache.maven.tck.FixPlexusBugs;
import org.codehaus.plexus.util.exceptionutils.TestException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.junit.matchers.JUnitMatchers;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;


/**
 * Test all public methods of {@link ExceptionUtils}.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class ExceptionUtilsTest extends Assert
{
    private static Logger logger = Logger.getLogger(ExceptionUtilsTest.class.getName());

    @Rule
    public FixPlexusBugs fixPlexusBugs = new FixPlexusBugs();

    protected static StackTraceElement[] STACKTRACE_WO_SPECIAL_METHODS =
        {
            new StackTraceElement("org.apache.maven.test.Class1", "method1", null, 101),
            new StackTraceElement("org.apache.maven.test.Class2", "method2", null, 101),
            new StackTraceElement("org.apache.maven.test.Class3", "method3", null, 101),
            new StackTraceElement("org.apache.maven.test.Class4", "method4", null, 101),
        };


    /**
     * Hack, who invokes a state-changing static method?
     * That's SICK!
     * We should deprecate this very method.
     */
    @Test
    public void testAddCauseMethodName()
    {
        ExceptionUtils.addCauseMethodName( "getNestedException" );

        //X TODO refine test!
    }




    @Test
    public void testGetCause()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );

        {
            Exception exception = new Exception( npe );
            exception.setStackTrace(STACKTRACE_WO_SPECIAL_METHODS);
            assertThat( "getCause for custom Exception is the same as before"
                      , ExceptionUtils.getCause( exception )
                      , equalTo( (Throwable) npe ) );
        }

        {
            SQLException sqlException1 = new SQLException();
            SQLException sqlException2 = new SQLException();
            sqlException1.setNextException(sqlException2);

            assertThat( "getCause for SQLException"
                      , ExceptionUtils.getCause( sqlException1 )
                      , equalTo( (Throwable) sqlException2 ) );
        }

        {
            InvocationTargetException ivte = new InvocationTargetException( npe );

            assertThat( "getCause for InvocationTargetException"
                      , ExceptionUtils.getCause( ivte )
                      , equalTo( (Throwable) npe ) );
        }

        {
            TestException testException = new TestException();
            testException.setSourceException( npe );

            assertThat( "getCause for InvocationTargetException"
                      , ExceptionUtils.getCause( testException )
                      , equalTo( (Throwable) npe ) );
        }
    }

    @Test
    public void testGetCause_MethodNames()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );

        {
            TestException testException = new TestException();
            testException.setSpecialCause( npe );
            String[] methodNames = new String[]{ "getSpecialCause" };

            assertThat( "getCause for InvocationTargetException"
                      , ExceptionUtils.getCause( testException, methodNames )
                      , equalTo( (Throwable) npe ) );
        }

        {
            TestException testException = new TestException();
            testException.setSpecialCause( npe );
            String[] methodNames = new String[]{ "getNonExistingMethod" };

            assertThat("getCause for InvocationTargetException"
                    , ExceptionUtils.getCause(testException, methodNames)
                    , nullValue());
        }
    }

    @Test
    public void testGetFullStackTrace()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );

        String fullStackTraceStart = "java.lang.NullPointerException: dooh just a random, nullpointer\n"
                     + "\tat org.codehaus.plexus.util.ExceptionUtilsTest.testGetFullStackTrace(ExceptionUtilsTest.java";

        assertThat( "getFullStackTrace start with"
                  , ExceptionUtils.getFullStackTrace( npe )
                  , JUnitMatchers.containsString(fullStackTraceStart) );
    }

    @Test
    public void testGetThrowables()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );
        SQLException sqlException = new SQLException( npe );
        TestException testException =  new TestException();
        testException.setSourceException(sqlException);

        Throwable[] expectedExceptions = new Throwable[] { testException, sqlException, npe };

        assertThat( "getThrowables"
                  , ExceptionUtils.getThrowables( testException )
                  , equalTo( expectedExceptions ) );
    }


    /**
     * @see ExceptionUtils#getRootCause(Throwable)
     */
    @Test
    public void testGetRootCause()
    {
        //X TODO refine test!
        logger.warning( "TODO implement!" );
    }

    /**
     * @see ExceptionUtils#getStackFrameList(Throwable)
     */
    @Test
    public void testGetStackFrameList()
    {
        //X TODO refine test!
        logger.warning("TODO implement!");
    }

    /**
     * @see ExceptionUtils#getStackFrames(Throwable)
     * @see ExceptionUtils#getStackFrames(String)
     */
    @Test
    public void testGetStackFrames()
    {
        //X TODO refine test!
        logger.warning("TODO implement!");
    }

    /**
     * @see ExceptionUtils#getThrowableCount(Throwable)
     */
    @Test
    public void testGetThrowableCount()
    {
        //X TODO refine test!
        logger.warning("TODO implement!");
    }

    /**
     * @see ExceptionUtils#indexOfThrowable(Throwable, Class)
     * @see ExceptionUtils#indexOfThrowable(Throwable, Class, int)
     */
    @Test
    public void testIndexOfThrowable()
    {
        //X TODO refine test!
        logger.warning("TODO implement!");
    }

    /**
     * @see ExceptionUtils#isNestedThrowable(Throwable)
     */
    @Test
    public void testIsNestedThrowable()
    {
        //X TODO refine test!
        logger.warning("TODO implement!");
    }

    /**
     * @see ExceptionUtils#printRootCauseStackTrace(Throwable)
     * @see ExceptionUtils#printRootCauseStackTrace(Throwable, java.io.PrintWriter)
     * @see ExceptionUtils#printRootCauseStackTrace(Throwable, java.io.PrintStream)
     */
    @Test
    public void testPrintRootCauseStackTrace()
    {
        //X TODO refine test!
        logger.warning("TODO implement!");
    }

}
