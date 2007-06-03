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

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.suite.SurefireTestSuite;
import org.apache.maven.surefire.testng.conf.Configurator;
import org.apache.maven.surefire.testng.conf.TestNG4751Configurator;
import org.apache.maven.surefire.testng.conf.TestNG52Configurator;
import org.apache.maven.surefire.testng.conf.TestNGMapConfigurator;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.testng.TestNG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contains utility methods for executing TestNG.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @author <a href='mailto:the[dot]mindstorm[at]gmail[dot]com'>Alex Popescu</a>
 */
public class TestNGExecutor
{
    private TestNGExecutor()
    {
    }

    public static void run( Class[] testClasses, String testSourceDirectory, Map options, ArtifactVersion version,
                            String classifier, ReporterManager reportManager, SurefireTestSuite suite )
        throws TestSetFailedException
    {
        TestNG testng = new TestNG( false );
        Configurator configurator = getConfigurator( version );
        configurator.configure( testng, options );
        postConfigure( testng, testSourceDirectory, (String) options.get("listener"), classifier, reportManager, suite );
        testng.setTestClasses( testClasses );
        testng.run();
    }

    public static void run( List suiteFiles, String testSourceDirectory, Map options, ArtifactVersion version,
                            String classifier, ReporterManager reportManager, SurefireTestSuite suite )
        throws TestSetFailedException
    {
        TestNG testng = new TestNG( false );
        Configurator configurator = getConfigurator( version );
        configurator.configure( testng, options );
        postConfigure( testng, testSourceDirectory, (String) options.get("listener"), classifier, reportManager, suite );

        testng.setTestSuites( suiteFiles );
        testng.run();
    }

    private static Configurator getConfigurator( ArtifactVersion version ) throws TestSetFailedException
    {
        try
        {
            VersionRange range = VersionRange.createFromVersionSpec( "[4.7,5.1]" );
            if ( range.containsVersion( version ) )
            {
                return new TestNG4751Configurator();
            }
            range = VersionRange.createFromVersion( "5.2" );
            if ( range.containsVersion( version ) )
            {
                return new TestNG52Configurator();
            }
            range = VersionRange.createFromVersionSpec( "[5.3,)" );
            if ( range.containsVersion( version ) )
            {
                return new TestNGMapConfigurator();
            }

            throw new TestSetFailedException( "Unknown TestNG version " + version );
        }
        catch ( InvalidVersionSpecificationException invsex )
        {
            throw new TestSetFailedException( "Bug in plugin. Please report it with the attached stacktrace", invsex );
        }
    }


    private static void postConfigure( TestNG testNG, String sourcePath, String listenerClasses, 
                                       String classifier, ReporterManager reportManager, SurefireTestSuite suite )
        throws TestSetFailedException
    {
        // turn off all TestNG output
        testNG.setVerbose( 0 );

        TestNGReporter reporter = new TestNGReporter( reportManager, suite );
        testNG.addListener( (Object) reporter );
        testNG.setListenerClasses(loadListenerClasses(listenerClasses));
        
        // FIXME: use classifier to decide if we need to pass along the source dir (onyl for JDK14)
        if ( sourcePath != null )
        {
            testNG.setSourcePath( sourcePath );
        }

        // workaround for SUREFIRE-49
        // TestNG always creates an output directory, and if not set the name for the directory is "null"
        testNG.setOutputDirectory( System.getProperty( "java.io.tmpdir" ) );
    }
    
    private static List loadListenerClasses(String listenerClasses) throws TestSetFailedException
    {
        if (listenerClasses == null || "".equals(listenerClasses.trim())) {
            return Collections.emptyList();
        }
        
        List classes = new ArrayList();
        String[] classNames = listenerClasses.split(" *, *");
        for(int i = 0; i < classNames.length; i++) 
        {
            try 
            {
                classes.add(Class.forName(classNames[i]));
            }
            catch(Exception ex) 
            {
                throw new TestSetFailedException("Cannot find listener class " + classNames[i], ex);
            }              
        }
        
        return classes;
    }
}
