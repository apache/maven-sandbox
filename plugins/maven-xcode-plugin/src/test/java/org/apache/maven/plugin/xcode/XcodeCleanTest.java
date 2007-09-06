package org.apache.maven.plugin.xcode;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.xcode.stubs.TestCounter;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

public class XcodeCleanTest
    extends AbstractMojoTestCase
{
    public void testClean()
        throws Exception
    {
        File pluginXmlFile = new File( getBasedir(), "src/test/clean-plugin-configs/min-plugin-config.xml" );

        File basedir = new File( getBasedir(), "target/test-harness/" + ( TestCounter.currentCount() + 1 ) );
        if ( basedir.exists() )
        {
            FileUtils.deleteDirectory( basedir );
        }
        assertTrue( "Prepare test base directory", basedir.mkdirs() );

        String artifactId = "plugin-test-" + ( TestCounter.currentCount() + 1 );

        File projectDir = new File( basedir, artifactId + ".xcodeproj" );
        assertTrue( "Test creation of xcodeproj dir", projectDir.mkdirs() );

        File projectFile = new File( projectDir, "project.pbxproj" );
        assertTrue( "Test creation of project file", projectFile.createNewFile() );

        File defaultUserFile = new File( projectDir, "default.pbxuser" );
        assertTrue( "Test creation of user file", defaultUserFile.createNewFile() );

        Mojo mojo = lookupMojo( "clean", pluginXmlFile );

        mojo.execute();

        assertFalse( "Test default user file was deleted", defaultUserFile.exists() );

        assertFalse( "Test project file was deleted", projectFile.exists() );

        assertFalse( "Test xcodeproj dir was deleted", projectDir.exists() );

        assertTrue( "Test project dir was not deleted", basedir.exists() );
    }
}
