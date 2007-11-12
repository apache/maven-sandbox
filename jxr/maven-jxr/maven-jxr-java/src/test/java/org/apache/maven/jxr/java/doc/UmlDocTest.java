package org.apache.maven.jxr.java.doc;

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

import org.apache.maven.jxr.util.DotUtil.DotNotPresentInPathException;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.PathTool;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class UmlDocTest
    extends PlexusTestCase
{
    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        File srcDir = new File( getBasedir(), "target/unit/src" );
        if ( !srcDir.exists() )
        {
            FileUtils.copyDirectory( new File( getBasedir(), "src/main/java" ), srcDir, "**/*.java", null );
            FileUtils.copyDirectory( new File( getBasedir(), "target/generated-sources/antlr" ), srcDir, "**/*.java",
                                     null );
        }
    }

    /**
     * Call UmlDoc
     *
     * @throws Exception if any.
     */
    public void testDefaultGenerate()
        throws Exception
    {
        File out = new File( getBasedir(), "target/unit/umldoc-default/umlDefault.svg" );
        File srcDir = new File( getBasedir(), "src/test/resources/javasrc" );

        UmlDoc umldoc = (UmlDoc) lookup( UmlDoc.ROLE );
        assertNotNull( umldoc );
        try
        {
            umldoc.generate( srcDir, out );
            assertTrue( "DOT exists in the path", true );
        }
        catch ( DotNotPresentInPathException e )
        {
            assertTrue( "DOT doesnt exist in the path. Ignored test", true );
            return;
        }

        // Generated files
        assertTrue( out.exists() );
        assertTrue( out.length() > 0 );
    }

    /**
     * Call UmlDoc
     *
     * @throws Exception if any.
     */
    public void testLinkExecute()
        throws Exception
    {
        File out = new File( getBasedir(), "target/unit/umldoc-default/umlDefault.svg" );
        File srcDir = new File( getBasedir(), "src/test/resources/javasrc" );

        UmlDoc umldoc = (UmlDoc) lookup( UmlDoc.ROLE );
        assertNotNull( umldoc );
        // All tests passed...
        umldoc.setJavasrcPath( PathTool.getRelativePath( "./target/unit/src" ) + "/target/unit/jxrdoc-default/" );
        try
        {
            umldoc.generate( srcDir, out );
            assertTrue( "DOT exists in the path", true );
        }
        catch ( DotNotPresentInPathException e )
        {
            assertTrue( "DOT doesnt exist in the path. Ignored test", true );
            return;
        }

        // Generated files
        assertTrue( out.exists() );
        assertTrue( out.length() > 0 );
    }
}
