package org.apache.maven.surefire.testng.conf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.testng.TestNG;

/**
 * TestNG configurator for 5.3+ versions. TestNG exposes
 * a {@link org.testng.TestNG#configure(java.util.Map)} method.
 * All suppported TestNG options are passed in String format, except
 * <code>TestNGCommandLineArgs.LISTENER_COMMAND_OPT</code> which is <code>List<Class></code>
 * and <code>TestNGCommandLineArgs.JUNIT_DEF_OPT</code> which is a <code>Boolean</code>.
 * <p/>
 * Test classes and/or suite files are not passed along as options parameters, but
 * configured separately.
 */
public class TestNGMapConfigurator implements IConfigurator {
	private static final Map TYPE_CONVERSIONS = new HashMap() {
		{
			put("junit", Boolean.class);
			put("threadcount", String.class);
		}
	};
	
	public void configure(TestNG testng, Map options) {
		Map convertedOptions = new HashMap();
		for(Iterator it = options.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Object val = entry.getValue();
			Class valType = (Class) TYPE_CONVERSIONS.get(key);
			if(valType != null) {
				val = convert(val, valType);
			}
			convertedOptions.put("-" + key, val);
		}
		
		testng.configure(convertedOptions);
	}
	
	protected Object convert(Object val, Class type) {
		if(val == null) return null;
		if(type.isAssignableFrom(val.getClass())) return val;
		
		if( (Boolean.class.equals(type) || boolean.class.equals(type)) && String.class.equals(val.getClass())) {
			return Boolean.valueOf((String) val); 
		}
		
		if(String.class.equals(type)) {
			return val.toString();
		}
		
		return val;
	}
}