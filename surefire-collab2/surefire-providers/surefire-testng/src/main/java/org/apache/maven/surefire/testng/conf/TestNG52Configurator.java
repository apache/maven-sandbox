package org.apache.maven.surefire.testng.conf;

import java.util.HashMap;
import java.util.Map;

/**
 * TestNG 5.2 configurator.
 * 
 * Allowed options:
 * -groups
 * -excludedgroups
 * -junit (boolean)
 * -threadcount (int)
 * -parallel (String)
 * 
 * Not supported yet:
 * -setListenerClasses(List<Class>) or setListeners(List<Object>)
 */
public class TestNG52Configurator extends AbstractDirectConfigurator {
	private static final Map ALLOWED_OPTS = new HashMap() {
		{
			put("groups", new Setter("setGroups", String.class));
			put("excludedgroups", new Setter("setExcludedGroups", String.class));
			put("parallel", new Setter("setParallel", String.class));
			put("junit", new Setter("setJUnit", Boolean.class));
			put("threadcount", new Setter("setThreadCount", int.class));
		}
	};
	
	protected Map getSetters() {
		return ALLOWED_OPTS;
	}
}