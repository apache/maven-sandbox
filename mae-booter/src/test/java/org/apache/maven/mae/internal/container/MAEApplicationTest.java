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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.app.AbstractMAEApplication;
import org.apache.maven.mae.boot.embed.MAEEmbedderBuilder;
import org.apache.maven.mae.internal.container.fixture.ContainerOwner;
import org.apache.maven.mae.internal.container.fixture.DefaultSingletonOwner;
import org.apache.maven.mae.internal.container.fixture.SingletonLiteralOwner;
import org.apache.maven.mae.internal.container.fixture.SingletonOwner;
import org.junit.Test;

public class MAEApplicationTest
{

    @Test
    // @Ignore
    public void injectContainerIntoExternalInstance()
        throws Exception
    {
        ContainerOwner owner = new ContainerOwner();
        new TestApplication().withInstance( owner ).load();

        assertThat( owner.container, notNullValue() );
    }

    @Test
    // @Ignore
    public void loadBare()
        throws Exception
    {
        new TestApplication().load();
    }

    @Test
    public void loadTwoApplications()
        throws MAEException
    {
        ContainerOwner owner = new ContainerOwner();
        new TestApplication().load();

        assertThat( "Container holder should not have a container instance after unrelated application is loaded.",
                    owner.container, nullValue() );

        new TestApplication().withInstance( owner ).load();

        assertThat( "Container holder should have a container instance after application with its registered instance is loaded.",
                    owner.container, notNullValue() );
    }

    // @Test
    // public void mappedRequirementContainsNoLiteralIds()
    // throws Throwable
    // {
    // final ContainerConfiguration config = new DefaultContainerConfiguration().setClassPathScanning( "ON" );
    //
    // final MAEContainer container = new MAEContainer( config, new ComponentSelector(), new InstanceRegistry() );
    //
    // final MapOwner mapOwner = container.lookup( MapOwner.class );
    // final Map<String, Child> members = mapOwner.members();
    //
    // System.out.println( members );
    //
    // assertNull( members.get( "simple" + ComponentKey.LITERAL_SUFFIX ) );
    // }

    @Test
    public void singletonImpliedRequirementOnComponentWithImpliedHint()
        throws Throwable
    {
        ContainerOwner owner = new ContainerOwner();
        new TestApplication().load();
        final DefaultSingletonOwner single = owner.container.lookup( DefaultSingletonOwner.class );

        assertThat( single.singleton(), notNullValue() );
    }

    @Test
    public void singletonNonLiteralRequirement()
        throws Throwable
    {
        ContainerOwner owner = new ContainerOwner();
        new TestApplication().load();
        final SingletonOwner single = owner.container.lookup( SingletonOwner.class );

        assertThat( single.singleton(), notNullValue() );
    }

    @Test
    public void singletonLiteralRequirement()
        throws Throwable
    {
        ContainerOwner owner = new ContainerOwner();
        new TestApplication().load();
        final SingletonLiteralOwner single = owner.container.lookup( SingletonLiteralOwner.class );

        assertThat( single.singletonLiteral(), notNullValue() );
    }

    //
    // @Test
    // public void initializableUsingRequirement()
    // throws Throwable
    // {
    // final ContainerConfiguration config = new DefaultContainerConfiguration().setClassPathScanning( "ON" );
    //
    // final MAEContainer container = new MAEContainer( config, new ComponentSelector(), new InstanceRegistry() );
    //
    // container.lookup( InitializedUsingRequirement.class );
    // }

    private static final class TestApplication
        extends AbstractMAEApplication
    {
        private final String name;

        TestApplication()
        {
            name = new Exception().getStackTrace()[1].getMethodName();
        }

        public final TestApplication withInstance( final Object instance )
        {
            withComponentInstance( instance );
            return this;
        }

        @Override
        public String getId()
        {
            return name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        protected void configureBuilder( final MAEEmbedderBuilder builder )
            throws MAEException
        {
            builder.withClassScanningEnabled( true );
            super.configureBuilder( builder );
        }

    }

}
