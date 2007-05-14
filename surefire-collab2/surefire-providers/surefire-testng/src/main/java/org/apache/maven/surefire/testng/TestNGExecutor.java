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

import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.surefire.testng.conf.IConfigurator;
import org.apache.maven.surefire.testng.conf.TestNG4751Configurator;
import org.apache.maven.surefire.testng.conf.TestNG52Configurator;
import org.apache.maven.surefire.testng.conf.TestNGMapConfigurator;
import org.apache.maven.surefire.util.NestedRuntimeException;
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
	
	private static IConfigurator getConfigurator(ArtifactVersion version) {
		try {
			VersionRange range = VersionRange.createFromVersionSpec("[4.7,5.1]");
			if(range.containsVersion(version)) {
				return new TestNG4751Configurator();
			}
			range = VersionRange.createFromVersion("5.2");
			if(range.containsVersion(version)) {
				return new TestNG52Configurator();
			}
			range = VersionRange.createFromVersionSpec( "[5.3,)" );
			if(range.containsVersion(version)) {
				return new TestNGMapConfigurator();
			}
			
			throw new NestedRuntimeException("Unknown TestNG version " + version);
		}
		catch(InvalidVersionSpecificationException invsex) {
			throw new NestedRuntimeException("", invsex);
		}
	}
		
	
	private static TestNG initialize(ExecEnv env, String sourcePath) {
		TestNG testNG = new TestNG( false );
		
		// turn off all TestNG output
		testNG.setVerbose( 0 );
		
        TestNGReporter reporter = new TestNGReporter( env.getReportManager(), env.getSuite() );
        testNG.addListener( (Object) reporter );
        // TODO: we should have the Profile so that we can decide if this is needed or not
        if(sourcePath != null) {
        	testNG.setSourcePath(sourcePath);
        }
        // workaround for SUREFIRE-49
        // TestNG always creates an output directory, and if not set the name for the directory is "null"
        testNG.setOutputDirectory( System.getProperty( "java.io.tmpdir" ) );
        
        return testNG;
	}
}
