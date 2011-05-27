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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.maven.mae.internal.container.MAEContainer;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.internal.container.InstanceRegistry;
import org.apache.maven.mae.internal.container.fixture.Child;
import org.apache.maven.mae.internal.container.fixture.DefaultSingletonOwner;
import org.apache.maven.mae.internal.container.fixture.InitializedUsingRequirement;
import org.apache.maven.mae.internal.container.fixture.MapOwner;
import org.apache.maven.mae.internal.container.fixture.SingletonLiteralOwner;
import org.apache.maven.mae.internal.container.fixture.SingletonOwner;
import org.junit.Test;

import java.util.Map;

public class MAEPlexusContainerTest
{
    @Test
    public void mappedRequirementContainsNoLiteralIds()
        throws Throwable
    {
        final ContainerConfiguration config = new DefaultContainerConfiguration().setClassPathScanning( "ON" );

        final MAEContainer container =
            new MAEContainer( config, new ComponentSelector(), new InstanceRegistry() );

        final MapOwner mapOwner = container.lookup( MapOwner.class );
        final Map<String, Child> members = mapOwner.members();

        System.out.println( members );

        assertNull( members.get( "simple" + ComponentKey.LITERAL_SUFFIX ) );
    }

    @Test
    public void singletonImpliedRequirementOnComponentWithImpliedHint()
        throws Throwable
    {
        final ContainerConfiguration config = new DefaultContainerConfiguration().setClassPathScanning( "ON" );

        final MAEContainer container =
            new MAEContainer( config, new ComponentSelector(), new InstanceRegistry() );

        final DefaultSingletonOwner owner = container.lookup( DefaultSingletonOwner.class );

        assertNotNull( owner.singleton() );
    }

    @Test
    public void singletonNonLiteralRequirement()
        throws Throwable
    {
        final ContainerConfiguration config = new DefaultContainerConfiguration().setClassPathScanning( "ON" );

        final MAEContainer container =
            new MAEContainer( config, new ComponentSelector(), new InstanceRegistry() );

        final SingletonOwner owner = container.lookup( SingletonOwner.class );

        assertNotNull( owner.singleton() );
    }

    @Test
    public void singletonLiteralRequirement()
        throws Throwable
    {
        final ContainerConfiguration config = new DefaultContainerConfiguration().setClassPathScanning( "ON" );

        final MAEContainer container =
            new MAEContainer( config, new ComponentSelector(), new InstanceRegistry() );

        final SingletonLiteralOwner owner = container.lookup( SingletonLiteralOwner.class );

        assertNotNull( owner.singletonLiteral() );
    }

    @Test
    public void initializableUsingRequirement()
        throws Throwable
    {
        final ContainerConfiguration config = new DefaultContainerConfiguration().setClassPathScanning( "ON" );

        final MAEContainer container =
            new MAEContainer( config, new ComponentSelector(), new InstanceRegistry() );

        container.lookup( InitializedUsingRequirement.class );
    }

}
