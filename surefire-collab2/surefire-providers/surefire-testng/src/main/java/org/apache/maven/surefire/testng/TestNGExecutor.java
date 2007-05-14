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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.testng.TestNG;

/**
 * Contains utility methods for executing TestNG.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @author <a href='mailto:the[dot]mindstorm[at]gmail[dot]com'>Alex Popescu</a>
 */
public class TestNGExecutor
{
	public static final String SOURCE_DIRS_OPTION = "maven.testng.src.dir";
	public static final String PARALLEL_MODE_OPTION = "parallel";
	
    private TestNGExecutor()
    {
    }
	
	public static void run(Class[] testClasses, Map options, ExecEnv execEnv) {
		TestNG testng = initialize(execEnv, (String) options.remove(SOURCE_DIRS_OPTION));
		IConfigurator configurator = getConfigurator(execEnv.getVersion());
		configurator.configure(testng, options);
		testng.setTestClasses(testClasses);
		testng.run();
	}

	public static void run(List suiteFiles, Map options, ExecEnv execEnv) {
		TestNG testng = initialize(execEnv, (String) options.remove(SOURCE_DIRS_OPTION));
		IConfigurator configurator = getConfigurator(execEnv.getVersion());
		configurator.configure(testng, options);
		testng.setTestSuites(suiteFiles);
		testng.run();
	}
	
	private static interface IConfigurator {
		void configure(TestNG testng, Map options);
	}
	
	private static IConfigurator getConfigurator(ArtifactVersion version) {
		return null;
	}
	
	private static TestNG initialize(ExecEnv env, String sourcePath) {
		TestNG testNG = new TestNG( false );
		
		// turn off all TestNG output
		testNG.setVerbose( 0 );
		
        testNG.setListenerClasses( new ArrayList() );
	
        TestNGReporter reporter = new TestNGReporter( env.getReportManager(), env.getSuite() );
        testNG.addListener( (Object) reporter );
        if(sourcePath != null) {
        	testNG.setSourcePath(sourcePath);
        }
        // workaround for SUREFIRE-49
        // TestNG always creates an output directory, and if not set the name for the directory is "null"
        testNG.setOutputDirectory( System.getProperty( "java.io.tmpdir" ) );
        
        return testNG;
	}	
}
