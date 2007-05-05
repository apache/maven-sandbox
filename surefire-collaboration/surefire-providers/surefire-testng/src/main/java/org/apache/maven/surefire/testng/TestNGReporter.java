package org.apache.maven.surefire.testng;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.surefire.Surefire;
import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.suite.SurefireTestSuite;
import org.testng.*;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * Listens for and provides and adaptor layer so that
 * TestNG tests can report their status to the current
 * {@link org.apache.maven.surefire.report.ReporterManager}.
 */
public class TestNGReporter
    implements ITestListener, ISuiteListener
{
    private ResourceBundle bundle = ResourceBundle.getBundle( Surefire.SUREFIRE_BUNDLE_NAME );

    /**
     * core Surefire reporting
     */
    protected ReporterManager reportManager;

    private Object source;

    private boolean testStarted = false;
    
    private ITestContext _finishContext;

    private boolean testSetCompleted = false;

    /**
     * Constructs a new instance that will listen to
     * test updates from a {@link TestNG} class instance.
     * <p/>
     * <p/>It is assumed that the requisite {@link TestNG#addListener(ITestListener)}
     * method call has already associated with this instance <i>before</i> the test
     * suite is run.
     *
     * @param reportManager Instance to report suite status to
     */
    public TestNGReporter( ReporterManager reportManager, SurefireTestSuite source )
    {
        this.reportManager = reportManager;

        if ( reportManager == null )
        {
            throw new IllegalArgumentException( "ReportManager passed in was null." );
        }

        this.source = source;
    }

    public void onTestStart( ITestResult result )
    {
        testStarted = true;
        
        String rawString = bundle.getString( "testStarting" );
        String group = groupString( result.getMethod().getGroups(), result.getTestClass().getName() );
        ReportEntry report = new ReportEntry( source, getUserFriendlyTestName( result ), group, rawString );
        
        reportManager.testStarting( report );
    }

    public void onTestSuccess( ITestResult result )
    {
        testStarted = false;

        String group = groupString( result.getMethod().getGroups(), result.getTestClass().getName() );
        ReportEntry report = new ReportEntry( source, getUserFriendlyTestName( result ), group, bundle.getString( "testSuccessful" ) );

        reportManager.testSucceeded( report );
    }

    public void onTestFailure( ITestResult result )
    {
        // methods run after/before suite and other similar tests don't get explicit test started calls
        // because they are considered configuration methods, but if one of them fails we need to change the
        // test count and start it in case it wasn't already started so that all failures / tests are properly
        // reported
        if (!testStarted) {
            
            onTestStart(result);
        }
        
        testStarted = false;
        String rawString = bundle.getString( "executeException" );

        ReportEntry report = new ReportEntry( source, getUserFriendlyTestName( result ), rawString,
                                              new TestNGStackTraceWriter( result ) );
        
        reportManager.testFailed( report );
    }

    private static String getUserFriendlyTestName( ITestResult result )
    {
        // This is consistent with the JUnit output
        return result.getName() + "(" + result.getTestClass().getName() + ")";
    }

    public void onTestSkipped( ITestResult result )
    {
        testStarted = false;
        ReportEntry report =
            new ReportEntry( source, getUserFriendlyTestName( result ), bundle.getString( "testSkipped" ) );

        reportManager.testSkipped( report );
    }

    public void onTestFailedButWithinSuccessPercentage( ITestResult result )
    {
        String rawString = bundle.getString( "executeException" );

        testStarted = false;
        ReportEntry report = new ReportEntry( source, getUserFriendlyTestName( result ), rawString,
                                              new TestNGStackTraceWriter( result ) );

        reportManager.testError( report );
    }

    public void onStart( ITestContext context )
    {
        String rawString = bundle.getString( "testSetStarting" );

        String group = groupString( context.getIncludedGroups(), context.getName() );
        ReportEntry report = new ReportEntry( source, context.getName(), group, rawString );

        try
        {
            reportManager.testSetStarting( report );
        }
        catch ( ReporterException e )
        {
            // TODO: remove this exception from the report manager
        }

        testSetCompleted = false;
    }

    public void onFinish( ITestContext context )
    {
        _finishContext = context;
        cleanupAfterTestsRun();
    }

    public void onFinish( ISuite suite )
    {
    }

    public void onStart( ISuite suite )
    {
    }
    
    /**
     * Should <em>always</em> be run after the entire TestNG suite has finished running so that the 
     * report set correctly captures all failures / success within the suite - especially those that happen in
     * before/after suite configuration methods that would normally report 0 tests but have to be included as a test
     * when they fail.
     */
    public void cleanupAfterTestsRun()
    {
        Method failed = TestNGExecutor.getMethod(_finishContext.getClass(), "getFailedConfigurations", 0);
        if (failed != null) {
            try {
                
                IResultMap map = (IResultMap) failed.invoke(_finishContext, new Object[0]);
                
                Iterator results = map.getAllResults().iterator();
                while (results.hasNext()) {
                    
                    ITestResult result = (ITestResult) results.next();
                    onTestFailure(result);
                }
                
            } catch (Throwable t) { t.printStackTrace(); }
        }

        // Don't execute twice
        if (testSetCompleted)
        {
            return;
        }

        String rawString = bundle.getString( "testSetCompletedNormally" );
        
        ReportEntry report =
            new ReportEntry( source, _finishContext.getName(), groupString( _finishContext.getIncludedGroups(), null ), rawString );

        reportManager.testSetCompleted( report );

        reportManager.reset();

        testSetCompleted = true;
    }
    
    /**
     * Creates a string out of the list of testng groups in the
     * form of <pre>"group1,group2,group3"</pre>.
     *
     * @param groups
     * @param defaultValue
     */
    private static String groupString( String[] groups, String defaultValue )
    {
        String retVal;
        if ( groups != null && groups.length > 0 )
        {
            StringBuffer str = new StringBuffer();
            for ( int i = 0; i < groups.length; i++ )
            {
                str.append( groups[i] );
                if ( i + 1 < groups.length )
                {
                    str.append( "," );
                }
            }
            retVal = str.toString();
        }
        else
        {
            retVal = defaultValue;
        }
        return retVal;
    }

}
