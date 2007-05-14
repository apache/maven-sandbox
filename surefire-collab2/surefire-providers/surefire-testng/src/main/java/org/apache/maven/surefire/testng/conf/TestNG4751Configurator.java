package org.apache.maven.surefire.testng.conf;

import java.util.HashMap;
import java.util.Map;

/**
 * TestNG 4.7 and 5.1 configurator.
 * 
 * Allowed options:
 * -groups
 * -excludedgroups
 * -junit (boolean)
 * -threadcount (int)
 * -parallel (boolean)
 * 
 * Not supported yet:
 * -setListenerClasses(List<Class>) or setListeners(List<Object>)
 */
public class TestNG4751Configurator extends AbstractDirectConfigurator {
	private static final Map ALLOWED_OPTS = new HashMap() {
		{
			put("groups", new Setter("setGroups", String.class));
			put("excludedgroups", new Setter("setExcludedGroups", String.class));
			put("parallel", new Setter("setParallel", boolean.class));
			put("junit", new Setter("setJUnit", Boolean.class));
			put("threadcount", new Setter("setThreadCount", int.class));
		}
	};
	
	protected Map getSetters() {
		return ALLOWED_OPTS;
	}
	
}