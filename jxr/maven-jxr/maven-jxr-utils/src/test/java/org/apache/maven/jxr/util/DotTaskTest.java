package org.apache.maven.jxr.util;

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

import junit.framework.TestCase;

import org.apache.maven.jxr.util.DotTask.DotNotPresentInPathBuildException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class DotTaskTest
    extends TestCase
{
    /**
     * Call Dot task
     *
     * @throws Exception if any.
     */
    public void testDefaultExecute()
        throws Exception
    {
        final String basedir = new File( "" ).getAbsolutePath();

        File in = new File( basedir, "src/test/resources/dot/target.dot" );
        File out = new File( basedir, "target/unit/dot-default/" );

        Project antProject = new Project();
        antProject.setBasedir( basedir );

        DotTask task = new DotTask();
        task.setProject( antProject );
        task.setIn( in );
        task.setDestDir( out );
        try
        {
            task.execute();
            assertTrue( "DOT exists in the path", true );
        }
        catch ( DotNotPresentInPathBuildException e )
        {
            assertTrue( "DOT doesnt exist in the path. Ignored test", true );
            return;
        }
        catch ( BuildException e )
        {
            if ( e.getMessage().indexOf( "Execute failed" ) != -1 )
            {
                assertTrue( "Uncatch error:" + e.getMessage(), false );
            }

            assertEquals( "Error when calling dot.", e.getMessage() );
        }

        // Generated files
        File generated = new File( out, "target.dot." + DotTask.DEFAULT_OUTPUT_FORMAT );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
    }

    /**
     * Call Dot task
     *
     * @throws Exception if any.
     */
    public void testErrorExecute()
        throws Exception
    {
        final String basedir = new File( "" ).getAbsolutePath();

        File in = new File( basedir, "src/test/resources/dot/graph.dot" );
        File out = new File( basedir, "target/unit/dot-default/" );

        Project antProject = new Project();
        antProject.setBasedir( basedir );

        DotTask task = new DotTask();
        task.setProject( antProject );
        task.setIn( in );
        task.setDestDir( out );
        try
        {
            task.execute();
            assertTrue( "DOT exists in the path", true );
            assertTrue( "Doesnt handle dot error", false );
        }
        catch ( DotNotPresentInPathBuildException e )
        {
            assertTrue( "DOT doesnt exist in the path. Ignored test", true );
            return;
        }
        catch ( BuildException e )
        {
            if ( e.getMessage().indexOf( "Execute failed" ) != -1 )
            {
                assertTrue( "Uncatch error:" + e.getMessage(), false );
            }

            assertEquals( "Error when calling dot.", e.getMessage() );
        }

        // Generated files
        File generated = new File( out, "graph.dot." + DotTask.DEFAULT_OUTPUT_FORMAT  );
        assertFalse( generated.exists() );
        assertFalse( generated.length() > 0 );
    }
}
