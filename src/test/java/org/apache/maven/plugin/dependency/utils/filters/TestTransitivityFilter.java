package org.apache.maven.plugin.dependency.utils.filters;

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
/**
 * 
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.dependency.testUtils.DependencyArtifactStubFactory;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.testing.SilentLog;

/**
 * @author brianf
 * 
 */
public class TestTransitivityFilter
    extends TestCase
{
    Set artifacts = new HashSet();

    Set directArtifacts = new HashSet();

    Log log = new SilentLog();

    protected void setUp()
        throws Exception
    {
        super.setUp();

        DependencyArtifactStubFactory fact = new DependencyArtifactStubFactory( null, false );
        artifacts = fact.getScopedArtifacts();

        directArtifacts = fact.getReleaseAndSnapshotArtifacts();

        artifacts.addAll( directArtifacts );
    }

    public void testAll()
    {
        TransitivityFilter filter = new TransitivityFilter( directArtifacts, false );

        Set result = filter.filter( artifacts, log );

        assertEquals( 7, result.size() );
    }

    public void testExclude()
    {
        TransitivityFilter filter = new TransitivityFilter( directArtifacts, false );
        assertFalse( filter.isExcludeTransitive() );
        filter.setExcludeTransitive( true );
        assertTrue( filter.isExcludeTransitive() );
        Set result = filter.filter( artifacts, log );

        assertEquals( 2, result.size() );

        Iterator iter = result.iterator();
        while ( iter.hasNext() )
        {
            Artifact artifact = (Artifact) iter.next();
            assertTrue( artifact.getArtifactId().equals( "release" ) || artifact.getArtifactId().equals( "snapshot" ) );
        }
    }

}