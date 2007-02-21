/*
 * Copyright 2003, 2004  The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 */
package org.apache.maven.shared.parser.java;

import java.io.Serializable;


/** <p>A qualified class name, including package name. Instances
 * of JavaQName are obtained by invoking private methods of the
 * class JavaQNameImpl.</p>
 */
public interface JavaQName extends Comparable, Serializable {
	/** <p>Returns the JavaQName's package name. The empty string
	 * represents the default package.</p>
	 */
	public String getPackageName();
	
	/** <p>Returns the JavaQName's class name. This is the composition of
	 * {@link #getOuterClassName()} and {@link #getInnerClassName}.</p>
	 */
	public String getClassName();
	
	/** <p>If the class is an inner class: Returns the name of the enclosing
	 * class. Otherwise returns null.</p>
	 */
	public String getOuterClassName();
	
	/** <p>If the class is an inner class: Returns the name of the inner
	 * class. Otherwise returns {@link #getClassName()}.</p>
	 */
	public String getInnerClassName();
	
	/** <p>Returns whether the class described by the JavaQName is
	 * actually an array class. If so, you may use the
	 * <code>getInstanceClass()</code> method to determine the
	 * JavaQName of the array elements.</p>
	 */
	public boolean isArray();
	
	/** <p>If the method <code>isArray()</code> returns true,
	 * you may use this method to obtain the instance class.</p>
	 * 
	 * @throws IllegalStateException This JavaQName is no array,
	 *   and <code>isArray()</code> returns false.
	 */
	public JavaQName getInstanceClass();
	
	/** <p>Returns whether this is a primitive class. Primitive classes
	 * are {@link JavaQNameImpl#VOID}, {@link JavaQNameImpl#BOOLEAN},
	 * {@link JavaQNameImpl#BYTE}, {@link JavaQNameImpl#SHORT},
	 * {@link JavaQNameImpl#INT}, {@link JavaQNameImpl#LONG},
	 * {@link JavaQNameImpl#FLOAT}, {@link JavaQNameImpl#DOUBLE}, and
	 * {@link JavaQNameImpl#CHAR}.</p>
	 */
	public boolean isPrimitive();
	
	/** <p>Returns whether this class may be imported. For instance,
	 * this is not the case for primitive classes.</p>
	 */
	public boolean isImportable();
	
	/** <p>Returns whether this class is an inner class.</p>
	 */
	public boolean isInnerClass();
	
	/** If the class is primitive: Returns the corresponding
	 * object type.
	 */
	public JavaQName getObjectType();

	/** If the class is primitive: Returns the name of the
	 * corresponding object classes method for converting
	 * the object into a primitive value.
	 */
	public String getPrimitiveConversionMethod();
}
