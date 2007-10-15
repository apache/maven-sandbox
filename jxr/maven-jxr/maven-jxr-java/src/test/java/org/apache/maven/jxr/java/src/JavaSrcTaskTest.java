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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class JavaSrcTaskTest
    extends TestCase
{
    /**
     * Call JavaSrc task
     *
     * @throws Exception if any.
     */
    public void testDefaultExecute()
        throws Exception
    {
        final String basedir = new File( "" ).getAbsolutePath();

        File srcDir = new File( basedir, "src/main/java" );
        File destDir = new File( basedir, "target/unit/jxrdoc-default" );

        Project antProject = new Project();
        antProject.setBasedir( basedir );

        JavaSrcTask task = new JavaSrcTask();
        task.setProject( antProject );
        task.setSrcDir( srcDir );
        task.setDestDir( destDir );
        task.execute();

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

    /**
     * Call JavaSrc task
     *
     * @throws Exception if any.
     */
    public void testNullExecute()
        throws Exception
    {
        final String basedir = new File( "" ).getAbsolutePath();

        File srcDir = new File( basedir, "src/main/java" );
        File destDir = new File( basedir, "target/unit/jxrdoc-null" );

        Project antProject = new Project();
        antProject.setBasedir( basedir );

        JavaSrcTask task = new JavaSrcTask();
        task.setProject( antProject );

        task.setSrcDir( null );
        task.setDestDir( destDir );
        task.execute();

        task.setSrcDir( srcDir );
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
