/*
 *  Copyright (C) 2010 John Casey.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
