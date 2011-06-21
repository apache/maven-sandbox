/*
 * Copyright 2011 Red Hat, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.maven.mae.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.maven.mae.project.ProjectLoader;
import org.apache.maven.mae.project.testutil.TestFixture;
import org.apache.maven.project.MavenProject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ProjectLoaderTest
{

    private static TestFixture fixture;

    @Test
    public void retrieveReactorProjects()
        throws Exception
    {
        final ProjectLoader projectManager = fixture.projectManager();

        final File dir = fixture.getTestFile( "projects", "multi-module" );
        final MavenProject project =
            projectManager.buildProjectInstance( new File( dir, "pom.xml" ), fixture.newSession() );

        final Set<String> projectIds = projectManager.retrieveReactorProjectIds( project.getFile() );

        System.out.println( projectIds );

        assertTrue( "parent project missing", projectIds.contains( "test:parent:1" ) );
        assertTrue( "child1 project missing", projectIds.contains( "test:child1:1" ) );
        assertTrue( "child2 project missing", projectIds.contains( "test:child2:1" ) );
    }

    @Test
    public void buildInstanceFromFile()
        throws Exception
    {
        final File pom = fixture.getTestFile( "projects", "simple.pom.xml" );
        final MavenProject project = fixture.projectManager().buildProjectInstance( pom, fixture.newSession() );

        assertEquals( pom, project.getFile() );
        assertEquals( "test", project.getGroupId() );
        assertEquals( "project", project.getArtifactId() );
        assertEquals( "1", project.getVersion() );
    }

    @Test
    public void buildInstanceFromCoords()
        throws Exception
    {
        final MavenProject project =
            fixture.projectManager().buildProjectInstance( "test", "found-dep", "1", fixture.newSession() );

        assertEquals( "test", project.getGroupId() );
        assertEquals( "found-dep", project.getArtifactId() );
        assertEquals( "1", project.getVersion() );
    }

    @BeforeClass
    public static void setup()
        throws Exception
    {
        fixture = TestFixture.getInstance();
    }

    @AfterClass
    public static void shutdown()
        throws IOException
    {
        if ( fixture != null )
        {
            fixture.shutdown();
        }
    }

}
