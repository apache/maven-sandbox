package org.apache.maven.jxr.ant.doc;

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

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class AntDocTest
    extends PlexusTestCase
{
    /**
     * Call Antdoc
     *
     * @throws Exception if any.
     */
    public void testDefaultExecute()
        throws Exception
    {
        File antDocDir = new File( getBasedir(), "target/unit/antdoc-default" );
        File build = new File( getBasedir(), "src/test/resources/ant/build.xml" );

        AntDoc antdoc = (AntDoc) lookup( AntDoc.ROLE );
        assertNotNull( antdoc );
        try
        {
            antdoc.generate( build, antDocDir );
            assertTrue( "DOT exists in the path", true );
        }
        catch ( DotNotPresentInPathException e )
        {
            assertTrue( "DOT doesnt exist in the path. Ignored test", true );
            return;
        }

        // Generated files
        File generated = new File( antDocDir, "vizant.png" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( antDocDir, "vizant.svg" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( antDocDir, "target.html" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( antDocDir, "InitialXML._xml" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );

        // Copied files
        File copied = new File( antDocDir, "BrowserLauncher.jar" );
        assertTrue( copied.exists() );
        assertTrue( generated.length() > 0 );
        copied = new File( antDocDir, "nanoxml-2.1.1.jar" );
        assertTrue( copied.exists() );
        assertTrue( generated.length() > 0 );
        copied = new File( antDocDir, "TGLinkBrowser.jar" );
        assertTrue( copied.exists() );
        assertTrue( generated.length() > 0 );
        copied = new File( antDocDir, "index.html" );
        assertTrue( copied.exists() );
        assertTrue( generated.length() > 0 );
        copied = new File( antDocDir, "cover.html" );
        assertTrue( copied.exists() );
        assertTrue( generated.length() > 0 );
        copied = new File( antDocDir, "links.html" );
        assertTrue( copied.exists() );
        assertTrue( generated.length() > 0 );
        copied = new File( antDocDir, "tg.html" );
        assertTrue( copied.exists() );
        assertTrue( generated.length() > 0 );
        copied = new File( antDocDir, "main.css" );
        assertTrue( copied.exists() );
        assertTrue( generated.length() > 0 );
    }
}
