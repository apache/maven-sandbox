/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.mae.internal.container;

/**
 * Use {@link org.sonatype.guice.bean.locators.ComponentKey} instead.
 */
@Deprecated
public class ComponentKey<T>
    extends org.sonatype.guice.bean.locators.ComponentKey<T>
{

    public ComponentKey( final Class<T> role, final String hint )
    {
        super( role, hint );
    }

    public ComponentKey( final Class<T> role )
    {
        super( role );
    }

    public static boolean isLiteral( final String value )
    {
        return org.sonatype.guice.bean.locators.ComponentKey.isLiteral( value );
    }

    public static String getLiteralHint( final String value )
    {
        return org.sonatype.guice.bean.locators.ComponentKey.getLiteralHint( value );
    }

}
