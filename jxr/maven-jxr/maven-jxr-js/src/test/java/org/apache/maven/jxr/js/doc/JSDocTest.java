package org.apache.maven.jxr.js.doc;

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

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class JSDocTest
    extends PlexusTestCase
{
    /**
     * Call JSDoc
     *
     * @throws Exception if any.
     */
    public void testDefaultExecute()
        throws Exception
    {
        File jsDocDir = new File( getBasedir(), "target/unit/jsdoc-default" );

        JSDoc jsdoc = (JSDoc) lookup( JSDoc.ROLE );
        assertNotNull( jsdoc );
        jsdoc.generate( this.getClass().getClassLoader().getResource( "jsdoc" ).getFile(), jsDocDir.getAbsolutePath() );

        // Generated files
        File generated = new File( jsDocDir, "index.htm" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( jsDocDir, "Test1.htm" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
    }
}
