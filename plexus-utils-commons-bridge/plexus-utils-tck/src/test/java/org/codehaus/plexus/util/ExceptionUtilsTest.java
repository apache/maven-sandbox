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
import org.codehaus.plexus.util.exceptionutils.TestExceptionWithDetail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.junit.matchers.JUnitMatchers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
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
                    , ExceptionUtils.getCause( testException, methodNames )
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
                  , JUnitMatchers.containsString( fullStackTraceStart ) );
    }

    @Test
    public void testGetThrowables()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );
        SQLException sqlException = new SQLException( npe );
        TestException testException =  new TestException();
        testException.setSourceException( sqlException );

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
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );
        SQLException sqlException = new SQLException( npe );
        TestException testException =  new TestException();
        testException.setSourceException( sqlException );

        assertThat( "getRootCause"
                  , ExceptionUtils.getRootCause(testException)
                  , equalTo( (Throwable) npe ) );

        assertThat( "getRootCause"
                  , ExceptionUtils.getRootCause( sqlException )
                  , equalTo( (Throwable) npe ) );

        assertThat("getRootCause"
                , ExceptionUtils.getRootCause(npe)
                , nullValue() );

        try
        {
            ExceptionUtils.getRootCause( null );
            fail( "getRootCause(null) NPE expected" );
        }
        catch ( NullPointerException e )
        {
            //nothing to do, Exception was expected
        }
    }

    /**
     * @see ExceptionUtils#getStackFrameList(Throwable)
     */
    @Test
    public void testGetStackFrameList()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );

        List<String> exceptionFrames = ExceptionUtils.getStackFrameList( npe );
        assertNotNull( exceptionFrames );
        assertTrue( exceptionFrames.size() > 1 );
        assertThat( "exceptionFrame", exceptionFrames.get( 0 )
                  , JUnitMatchers.containsString( "at org.codehaus.plexus.util.ExceptionUtilsTest."
                                                  + "testGetStackFrameList(ExceptionUtilsTest.java" ) );

        // NPE safe test
        try
        {
            ExceptionUtils.getStackFrameList( null );
            fail( "getStackFrameList(null) NPE expected" );
        }
        catch ( NullPointerException e )
        {
            //nothing to do, Exception was expected
        }
    }

    /**
     * @see ExceptionUtils#getStackTrace(Throwable)
     */
    @Test
    public void testGetStackTrace()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );

        String stackTrace = ExceptionUtils.getStackTrace( npe );
        assertNotNull(stackTrace);
        assertTrue( "wrong stacktrace: " + stackTrace,
                    stackTrace.startsWith( "java.lang.NullPointerException: dooh just a random, nullpointer\n" +
                        "\tat org.codehaus.plexus.util.ExceptionUtilsTest.testGetStackTrace(ExceptionUtilsTest.java" ));

        // NPE safe test
        try
        {
            ExceptionUtils.getStackTrace((Throwable) null);
            fail( "getStackTrace(null) NPE expected" );
        }
        catch ( NullPointerException e )
        {
            //nothing to do, Exception was expected
        }
    }

    /**
     * @see ExceptionUtils#getStackFrames(Throwable)
     */
    @Test
    public void testGetStackFrames()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );

        String[] stackFrames = ExceptionUtils.getStackFrames( npe );
        assertNotNull( stackFrames );
        assertTrue( stackFrames.length > 3 );

        assertEquals( "java.lang.NullPointerException: " + npe.getMessage(), stackFrames[0] );
        assertThat( "stackFrames", stackFrames[1]
                  , JUnitMatchers.containsString( "at org.codehaus.plexus.util.ExceptionUtilsTest."
                                                  + "testGetStackFrames(ExceptionUtilsTest.java" ) );

        // NPE safe test
        try
        {
            ExceptionUtils.getStackFrames((Throwable) null);
            fail( "getStackFrames(null) NPE expected" );
        }
        catch ( NullPointerException e )
        {
            //nothing to do, Exception was expected
        }
    }

    /**
     * @see ExceptionUtils#getStackFrames(String)
     */
    @Test
    public void testGetStackFrames_String()
    {
        String stackTrace = "java.lang.NullPointerException: mymessage\n" +
                "\tat org.codehaus.plexus.util.ExceptionUtilsTest.testGetStackTrace(ExceptionUtilsTest.java:237)\n" +
                "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n" +
                "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n" +
                "\tat java.lang.reflect.Method.invoke(Method.java:597)\n" +
                "\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)\n" +
                "\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\n" +
                "\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)\n" +
                "\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\n" +
                "\tat org.junit.runners.BlockJUnit4ClassRunner.runNotIgnored(BlockJUnit4ClassRunner.java:79)\n" +
                "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:71)\n" +
                "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:49)\n" +
                "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)\n" +
                "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)\n" +
                "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)\n" +
                "\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)\n" +
                "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)\n" +
                "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:236)\n" +
                "\tat org.junit.runner.JUnitCore.run(JUnitCore.java:157)\n" +
                "\tat com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:71)\n" +
                "\tat com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:199)\n" +
                "\tat com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:62)";

        String[] stackFrames = ExceptionUtils.getStackFrames( stackTrace );
        assertNotNull( stackFrames );
        assertEquals(23, stackFrames.length);

        assertEquals( "java.lang.NullPointerException: mymessage", stackFrames[0] );
        assertThat( "stackFrames", stackFrames[1]
                  , JUnitMatchers.containsString( "at org.codehaus.plexus.util.ExceptionUtilsTest."
                                                  + "testGetStackTrace(ExceptionUtilsTest.java" ) );

        try
        {
            ExceptionUtils.getStackFrames( (String) null );
            fail( "getStackFrames(null) NPE expected" );
        }
        catch ( NullPointerException e )
        {
            //nothing to do, Exception was expected
        }
    }

    /**
     * @see ExceptionUtils#getThrowableCount(Throwable)
     */
    @Test
    public void testGetThrowableCount()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );
        SQLException sqlException = new SQLException( npe );
        TestException testException =  new TestException();
        testException.setSourceException( sqlException );

        assertThat( "getThrowableCount"
                  , ExceptionUtils.getThrowableCount( npe )
                  , is( 1 ));

        assertThat( "getThrowableCount"
                  , ExceptionUtils.getThrowableCount( sqlException )
                  , is( 2 ));

        assertThat( "getThrowableCount"
                  , ExceptionUtils.getThrowableCount( testException )
                  , is( 3 ));

        // NPE safe test
        // this method should NOT throw a NPE on a null argument!
        ExceptionUtils.getThrowableCount( null );
    }

    /**
     * @see ExceptionUtils#indexOfThrowable(Throwable, Class)
     * @see ExceptionUtils#indexOfThrowable(Throwable, Class, int)
     */
    @Test
    public void testIndexOfThrowable()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );
        SQLException sqlException = new SQLException( npe );
        TestException testException =  new TestException();
        testException.setSourceException( sqlException );

        assertThat("indexOfThrowable"
                , ExceptionUtils.indexOfThrowable(npe, NullPointerException.class)
                , is(0));

        assertThat( "indexOfThrowable for non contained Exception type"
                  , ExceptionUtils.indexOfThrowable( npe, SQLException.class )
                  , is( -1 ));


        assertThat( "indexOfThrowable"
                  , ExceptionUtils.indexOfThrowable( testException, NullPointerException.class )
                  , is( 2 ));

        assertThat( "indexOfThrowable for non contained Exception type"
                  , ExceptionUtils.indexOfThrowable( testException, SQLException.class )
                  , is( 1 ));

        assertThat( "indexOfThrowable"
                  , ExceptionUtils.indexOfThrowable( testException, TestException.class )
                  , is( 0 ));


        // tests for indexOfThrowable with start index param
        assertThat( "indexOfThrowable"
                  , ExceptionUtils.indexOfThrowable( testException, NullPointerException.class, 2 )
                  , is( 2 ));

        assertThat( "indexOfThrowable"
                  , ExceptionUtils.indexOfThrowable( testException, SQLException.class, 2 )
                  , is( -1 ));

        try
        {
            ExceptionUtils.indexOfThrowable( testException, TestException.class, 3 );
            fail( "indexOfThrowable with too large inces" );
        }
        catch ( IndexOutOfBoundsException e )
        {
            //nothing to do, Exception was expected
        }

        // NPE safe tests
        try
        {
            ExceptionUtils.indexOfThrowable( null, TestException.class );
            fail( "indexOfThrowable(null, Exception.class) NPE expected" );
        }
        catch ( IndexOutOfBoundsException e )
        {
            //nothing to do, Exception was expected
        }
        assertThat( "indexOfThrowable for null Exception type"
                  , ExceptionUtils.indexOfThrowable(npe, null)
                  , is(-1));
    }

    /**
     * Most probably this only ever returns false on null in JDK > 1.4
     * Because Throwable itself nowadays has a getCause() method which
     * is in the method list...
     *
     * @see ExceptionUtils#isNestedThrowable(Throwable)
     */
    @Test
    public void testIsNestedThrowable()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );
        SQLException sqlException = new SQLException( npe );
        TestException testException =  new TestException();
        testException.setSourceException( sqlException );

        assertThat( "isNestedThrowable"
                  , ExceptionUtils.isNestedThrowable( null )
                  , is( false ) );

        assertThat("isNestedThrowable"
                , ExceptionUtils.isNestedThrowable(npe)
                , is(true));

        assertThat( "isNestedThrowable"
                  , ExceptionUtils.isNestedThrowable( sqlException )
                  , is( true ) );

        assertThat( "isNestedThrowable"
                  , ExceptionUtils.isNestedThrowable( new InvocationTargetException( npe ) )
                  , is( true ) );

        assertThat( "isNestedThrowable"
                  , ExceptionUtils.isNestedThrowable( new TestExceptionWithDetail() )
                  , is( true ) );

        assertThat( "isNestedThrowable"
                  , ExceptionUtils.isNestedThrowable( new Exception() )
                  , is( true ) );

        assertThat( "isNestedThrowable"
                  , ExceptionUtils.isNestedThrowable( new Throwable() )
                  , is( true ) );
    }

    /**
     * @see ExceptionUtils#printRootCauseStackTrace(Throwable)
     * @see ExceptionUtils#printRootCauseStackTrace(Throwable, java.io.PrintWriter)
     * @see ExceptionUtils#printRootCauseStackTrace(Throwable, java.io.PrintStream)
     */
    @Test
    public void testPrintRootCauseStackTrace()
    {
        NullPointerException npe = new NullPointerException( "dooh just a random, nullpointer" );
        SQLException sqlException = new SQLException( npe );
        TestException testException =  new TestException();
        testException.setSourceException( sqlException );

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        PrintStream outStream = new PrintStream( bao );
        PrintStream originalErr = System.err;

        try
        {
            System.setErr( outStream );
            ExceptionUtils.printRootCauseStackTrace( npe );

            assertThat( "stackFrames"
                      , bao.toString()
                      , JUnitMatchers.containsString( "java.lang.NullPointerException: dooh just a random, nullpointer"
                                                      + "\n\tat org.codehaus.plexus.util.ExceptionUtilsTest."
                                                      + "testPrintRootCauseStackTrace(ExceptionUtilsTest.java:" ) );
        }
        finally
        {
            System.setErr( originalErr );
        }

        //X TODO A FEW THINGS STILL MISSING! will continue later today...
    }

}
