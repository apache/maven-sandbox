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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class JSDocTaskTest
    extends PlexusTestCase
{
    /**
     * Call JSDoc task
     *
     * @throws Exception if any.
     */
    public void testDefaultExecute()
        throws Exception
    {
        File jsDocDir = new File( getBasedir(), "target/unit/jsdoc-default" );

        Project antProject = new Project();
        antProject.setBasedir( getBasedir() );

        JSDocTask task = new JSDocTask();
        task.setProject( antProject );
        task.init();
        task.setJSDir( this.getClass().getClassLoader().getResource( "jsdoc" ).getFile() );
        task.setDestDir( jsDocDir.getAbsolutePath() );
        task.execute();

        // Generated files
        File generated = new File( jsDocDir, "index.htm" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
        generated = new File( jsDocDir, "Test1.htm" );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
    }

    /**
     * Call JSDoc task
     *
     * @throws Exception if any.
     */
    public void testNullExecute()
        throws Exception
    {
        File jsDocDir = new File( getBasedir(), "target/unit/jsdoc-null" );

        Project antProject = new Project();
        antProject.setBasedir( getBasedir() );

        JSDocTask task = new JSDocTask();
        task.setProject( antProject );
        task.init();

        task.setJSDir( null );
        task.setDestDir( jsDocDir.getAbsolutePath() );
        try
        {
            task.execute();
            assertTrue( "Doesnt handle null JS dir", false );
        }
        catch ( BuildException e )
        {
            assertTrue( true );
        }

        task.setJSDir( this.getClass().getClassLoader().getResource( "jsdoc" ).getFile() );
        task.setDestDir( null );
        try
        {
            task.execute();
            assertTrue( "Doesnt handle null dest dir", false );
        }
        catch ( BuildException e )
        {
            assertTrue( true );
        }
    }
}
