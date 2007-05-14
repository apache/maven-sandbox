package org.apache.maven.surefire.testng.conf;

import java.util.Map;

import org.testng.TestNG;

/**
 * TestNG configurator for 5.3+ versions. TestNG exposes
 * a {@link org.testng.TestNG#configure(java.util.Map)} method.
 * All suppported TestNG options are passed in String format, except
 * <code>TestNGCommandLineArgs.LISTENER_COMMAND_OPT</code> which is <code>List<Class></code>.
 * <p/>
 * Test classes and/or suite files are not passed along as options parameters, but
 * configured separately.
 */
public class TestNGMapConfigurator implements IConfigurator {

	public void configure(TestNG testng, Map options) {
		
	}
	
}