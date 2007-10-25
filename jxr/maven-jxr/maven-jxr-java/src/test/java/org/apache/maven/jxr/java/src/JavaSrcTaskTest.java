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
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class JavaSrcTaskTest
    extends TestCase
{
    private static final String BASEDIR = new File( "" ).getAbsolutePath();

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        File srcDir = new File( BASEDIR, "target/unit/src" );
        if ( !srcDir.exists() )
        {
            // MSANDBOX-38: to compare results before and after Antlr generation
            Project antProject = new Project();
            antProject.setBasedir( BASEDIR );

            Copy copy = new Copy();
            copy.setProject( antProject );
            copy.setTodir( srcDir );
            FileSet set = new FileSet();
            set.setDir( new File( BASEDIR, "src/main/java" ) );
            set.setIncludes( "**/*.java" );
            copy.addFileset( set );
            set = new FileSet();
            set.setDir( new File( BASEDIR, "target/generated-sources/antlr" ) );
            set.setIncludes( "**/*.java" );
            copy.addFileset( set );
            copy.execute();
        }
    }

    /**
     * Call JavaSrc task
     *
     * @throws Exception if any.
     */
    public void testDefaultExecute()
        throws Exception
    {
        File srcDir = new File( BASEDIR, "target/unit/src" );
        File destDir = new File( BASEDIR, "target/unit/jxrdoc-default" );

        Project antProject = new Project();
        antProject.setBasedir( BASEDIR );

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
        File srcDir = new File( BASEDIR, "target/unit/src" );
        File destDir = new File( BASEDIR, "target/unit/jxrdoc-null" );

        Project antProject = new Project();
        antProject.setBasedir( BASEDIR );

        JavaSrcTask task = new JavaSrcTask();
        task.setProject( antProject );

        task.setSrcDir( null );
        task.setDestDir( destDir );
        try
        {
            task.execute();
            assertTrue( "Doesnt handle null src dir", false );
        }
        catch ( BuildException e )
        {
            assertTrue( true );
        }

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
