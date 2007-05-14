package org.apache.maven.surefire.testng.conf;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.surefire.util.NestedRuntimeException;
import org.testng.TestNG;

abstract public class AbstractDirectConfigurator implements IConfigurator {
	
	public void configure(TestNG testng, Map options) {
		setOptions(testng, options);
	}

	protected void setOptions(TestNG testng, Map options) throws NestedRuntimeException {
		Map setters = getSetters();
		
		for(Iterator it = options.keySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Object val = entry.getValue();

			Setter setter = (Setter) setters.get(key);
			if(setter != null) {
				try {
					setter.invoke(testng, val);
				} 
				catch (Exception ex) {
					throw new NestedRuntimeException("Cannot set option " + key + " with value " + val, ex);
				}

			}
		}
	}
	
	protected abstract Map getSetters();
	
	public static final class Setter {
		private final String setterName;
		private final Class paramClass;
		
		public Setter(String name, Class clazz) {
			this.setterName = name;
			this.paramClass = clazz;
		}
		
		public void invoke(Object target, Object value) throws Exception {
			Method setter = target.getClass().getMethod(this.setterName, new Class[] {this.paramClass});
			if(setter != null) {
				setter.invoke(target, new Object[] {convertValue(value)});
			}
		}
		
		protected Object convertValue(Object value) {
			if(value == null) return value;
			if(this.paramClass.isAssignableFrom(value.getClass())) {
				return value;
			}
			
			if(Boolean.class.equals(this.paramClass) || boolean.class.equals(this.paramClass)) {
				return Boolean.valueOf(value.toString());
			}
			if(Integer.class.equals(this.paramClass) || int.class.equals(this.paramClass)) {
				return new Integer(value.toString());
			}
			
			return value;
		}
	}
}
