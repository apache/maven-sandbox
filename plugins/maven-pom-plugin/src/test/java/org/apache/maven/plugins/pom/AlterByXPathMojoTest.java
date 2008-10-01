package org.apache.maven.plugins.pom;

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

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.plugins.pom.AlterByXPathMojo;
import org.apache.maven.plugins.pom.util.XMLTool;

/**
 * Test a pom mojo.
 *
 * @author <a href="mailto:jmcconnell@apache.org">Jesse McConnell</a>
 * @version $Id:$
 */
public class AlterByXPathMojoTest
    extends AbstractMojoTestCase
{
    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    /**
     * Tests a pom alteration by xpath
     *
     * @throws Exception
     */
    public void testAlterByXPath()
        throws Exception
    {
        String pluginPom = getBasedir() + "/src/test/resources/unit/alter-by-xpath-test/plugin-pom.xml";

        // safety
        FileUtils.copyDirectory( new File( getBasedir(), "src/test/resources/unit/alter-by-xpath-test" ),
                                 new File( getBasedir(), "target/test-files/unit/alter-by-xpath-test" ), null, "**/.svn,**/.svn/**" );

        AlterByXPathMojo mojo = (AlterByXPathMojo) lookupMojo( "alter-by-xpath", pluginPom );
        
        assertNotNull( mojo );

        mojo.execute();
        
        XMLTool xmlTool = new XMLTool( "project", new File( getBasedir() + "/target/test-files/unit/alter-by-xpath-test/alter-by-xpath-pom.xml" ) );
        
        assertEquals( "1.1-SNAPSHOT", xmlTool.getElement("/project/dependencies/dependency[artifactId[.='commons-collections']]/version").getText());
    } 
}
