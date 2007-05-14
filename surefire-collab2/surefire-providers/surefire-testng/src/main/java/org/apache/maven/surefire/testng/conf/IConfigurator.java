/**
 * 
 */
package org.apache.maven.surefire.testng.conf;

import java.util.Map;

import org.testng.TestNG;

public interface IConfigurator {
	void configure(TestNG testng, Map options);
}