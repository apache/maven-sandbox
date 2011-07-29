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

import java.util.Set;

import org.sonatype.guice.bean.locators.ComponentKey;

public class ServiceAuthorizer
{

    private final Set<ComponentKey<?>> authorizedKeys;

    public ServiceAuthorizer( final Set<ComponentKey<?>> authorizedKeys )
    {
        this.authorizedKeys = authorizedKeys;
    }

    public <T> boolean isAvailable( final Class<T> serviceType )
    {
        return authorizedKeys.contains( new ComponentKey<T>( serviceType ) );
    }

    public <T> boolean isAvailable( final Class<T> serviceType, final String hint )
    {
        return authorizedKeys.contains( new ComponentKey<T>( serviceType, hint ) );
    }

}
