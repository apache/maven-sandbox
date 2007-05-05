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

import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.suite.SurefireTestSuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.TestNG;
import org.testng.internal.annotations.AnnotationConfiguration;
import org.testng.xml.XmlSuite;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * Contains utility methods for executing TestNG.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class TestNGExecutor
{
    private TestNGExecutor()
    {
    }

    static Object execute(Object target, String methodName, Object param)
    throws Exception
    {
        Method m = getMethod(target.getClass(), methodName, param != null ? 1 : 0);
        Object ret = null;

        if (m == null)
            throw new IllegalArgumentException("No method found with name <" + methodName + "> on object " + target);

        if (m.getParameterTypes().length <= 0) {

            ret = m.invoke(target, new Object[0]);
        } else if (m.getParameterTypes()[0] == boolean.class) {

            Object[] args = { param };
            if (!Boolean.class.isInstance(param))
            {
                args[0] = Boolean.valueOf(param.toString());
            }

            ret = m.invoke(target, args);
        } else if (m.getParameterTypes()[0] == String.class) {

            ret = m.invoke(target, new Object[] { param.toString() });
        }

        return ret;
    }
    
    static Method getMethod(Class clazz, String name, int argCount)
    {
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(name) && methods[i].getParameterTypes().length == argCount)
                return methods[i];
        }
        
        return null;
    }
    
    static void configureJreType(TestNG testNG, String testSourceDirectory)
    {
        try {

            String jre = System.getProperty("java.vm.version");
            Method annotType = TestNGExecutor.getMethod(testNG.getClass(), "setAnnotations", 1);
            
            if (annotType != null) {

                annotType.invoke(testNG, new Object[]{ jre.indexOf("1.4") > -1 ? "javadoc" : "jdk"});

                Method init = testNG.getClass().getDeclaredMethod("initializeListeners", new Class[0]);
                init.setAccessible(true);
                init.invoke(testNG, new Object[0]);

                init = testNG.getClass().getDeclaredMethod("initializeAnnotationFinders", new Class[0]);
                init.setAccessible(true);
                init.invoke(testNG, new Object[0]);

                init = testNG.getClass().getDeclaredMethod("initializeCommandLineSuites", new Class[0]);
                init.setAccessible(true);
                init.invoke(testNG, new Object[0]);

                init = testNG.getClass().getDeclaredMethod("initializeCommandLineSuitesParams", new Class[0]);
                init.setAccessible(true);
                init.invoke(testNG, new Object[0]);

                init = testNG.getClass().getDeclaredMethod("initializeCommandLineSuitesGroups", new Class[0]);
                init.setAccessible(true);
                init.invoke(testNG, new Object[0]);
            } else if (Class.forName("org.testng.internal.annotations.AnnotationConfiguration") != null
                    && AnnotationConfiguration.class.getMethod("getInstance", new Class[0]) != null) {

                if (jre.indexOf("1.4") > -1) {
                    AnnotationConfiguration.getInstance().initialize(AnnotationConfiguration.JVM_14_CONFIG);
                    AnnotationConfiguration.getInstance().getAnnotationFinder().addSourceDirs(new String[]{testSourceDirectory});
                } else {
                    AnnotationConfiguration.getInstance().initialize(AnnotationConfiguration.JVM_15_CONFIG);
                }
            } else {
                throw new IllegalStateException("Unable to configure TestNG jre type.");
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    static void executeTestNG( SurefireTestSuite surefireSuite, String testSourceDirectory,
                               XmlSuite suite, ReporterManager reporterManager )
    {
        TestNG testNG = new TestNG( false );

        // turn off all TestNG output
        testNG.setVerbose( 0 );

        testNG.setXmlSuites( Collections.singletonList( suite ) );

        testNG.setListenerClasses( new ArrayList() );

        TestNGReporter reporter = new TestNGReporter( reporterManager, surefireSuite );
        testNG.addListener( (ITestListener) reporter );
        testNG.addListener( (ISuiteListener) reporter );

        configureJreType(testNG, testSourceDirectory);
        
        // Set source path so testng can find javadoc annotations if not in 1.5 jvm
        if ( testSourceDirectory != null )
        {
            testNG.setSourcePath( testSourceDirectory );
        }
        
        // workaround for SUREFIRE-49
        // TestNG always creates an output directory, and if not set the name for the directory is "null"
        testNG.setOutputDirectory( System.getProperty( "java.io.tmpdir" ) );
        
        testNG.runSuitesLocally();
        
        // need to execute report end after testng has completely finished as the 
        // reporter methods don't get called in the order that would allow for capturing
        // failures that happen in before/after suite configuration methods
        
        reporter.cleanupAfterTestsRun();
    }

    static void executeTestNG( SurefireTestSuite suite, Properties config, ReporterManager reporterManager )
    {
        TestNG testNG = new TestNG( false );

        Method execMethod = getMethod(testNG.getClass(), "configureAndRun", 1);
        if (execMethod == null)
            throw new IllegalArgumentException("Unable to find method <configureAndRun(Map)> on TestNG class provided");

        TestNGReporter reporter = new TestNGReporter( reporterManager, suite );
        config.put("listener", reporter);
        config.put("suitelistener", reporter);

        try {

            execMethod.invoke(testNG, new Object[] { config });
        } catch (Throwable t)
        {
            t.printStackTrace();
            return;
        }        
    }
}
