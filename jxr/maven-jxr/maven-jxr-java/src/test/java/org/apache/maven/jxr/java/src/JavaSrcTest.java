package org.apache.maven.jxr.java.src;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class JavaSrcTest
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
     * Call JavaSrc
     *
     * @throws Exception if any.
     */
    public void testDefaultExecute()
        throws Exception
    {
        File srcDir = new File( getBasedir(), "target/unit/src" );
        File destDir = new File( getBasedir(), "target/unit/jxrdoc-default" );

        JavaSrc javaSrc = (JavaSrc) lookup( JavaSrc.ROLE );
        assertNotNull( javaSrc );

        JavaSrcOptions options = new JavaSrcOptions();
        options.setBottom( "bottom" );
        options.setDocencoding( "ISO-8859-1" );
        options.setDoctitle( "doctitle" );
        options.setEncoding( "ISO-8859-1" );
        options.setFooter( "footer" );
        options.setHeader( "header" );
        options.setPackagesheader( "packagesheader" );
        options.setRecurse( true );
        options.setTop( "top" );
        options.setVerbose( false );
        options.setWindowtitle( "windowtitle" );

        javaSrc.generate( srcDir, destDir, options );

        // Generated files
        File generated = new File( destDir, "index.html" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( destDir, "allclasses-frame.html" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( destDir, "overview-frame.html" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( destDir, "overview-summary.html" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( destDir, "styles.css" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        // Generated src files
        generated = new File( destDir, "org/apache/maven/jxr/java/src/JavaSrcTask.def" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( destDir, "org/apache/maven/jxr/java/src/JavaSrcTask_java.html" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( destDir, "org/apache/maven/jxr/java/src/JavaSrcTask_java_ref.html" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
    }
}
