package org.apache.maven.plugin.forrestdoc;

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

import org.apache.maven.jxr.java.src.JavaSrcTask;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Base class which wraps all <code>JavaSrcTask</code> functionalities.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public abstract class AbstractJavasrcMojo
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;

    // ----------------------------------------------------------------------
    // JavaSrcTask parameters
    // ----------------------------------------------------------------------

    /**
     * The source directory to be scanned.
     *
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     * @see org.apache.forrest.forrestdoc.java.src.JavaSrcTask#srcDir
     */
    protected File srcDir;

    /**
     * The output directory.
     *
     * @parameter expression="${project.build.directory}/javasrc"
     * @required
     * @readonly
     * @see org.apache.forrest.forrestdoc.java.src.JavaSrcTask#destDir
     */
    protected File outputDirectory;

    /**
     * True to apply a recursive scan.
     *
     * @parameter expression="${recurse}" default-value="true"
     * @see org.apache.forrest.forrestdoc.java.src.JavaSrcTask#recurse
     */
    private boolean recurse;

    /**
     * The title of the generated HTML report.
     *
     * @parameter expression="${title}" default-value="${project.name} ${project.version} Reference"
     * @see org.apache.forrest.forrestdoc.java.src.JavaSrcTask#title
     */
    private String windowTitle;

    /**
     * True to verbose the scan.
     *
     * @parameter expression="${verbose}" default-value="false"
     * @see org.apache.forrest.forrestdoc.java.src.JavaSrcTask#verbose
     */
    private boolean verbose;

    /**
     * Execute the <code>JavaSrcTask</code>
     *
     * @throws MojoExecutionException if any
     * @see org.apache.forrest.forrestdoc.java.src.JavaSrcTask#execute()
     */
    public void executeJavaSrcTask()
        throws MojoExecutionException
    {
        // TODO need to add a custom footer
        // @see org.apache.forrest.forrestdoc.java.src.Pass2#createPackageSummaryFiles()
        // @see org.apache.forrest.forrestdoc.java.src.Pass2#createOverviewSummaryFrame()

        JavaSrcTask task = new JavaSrcTask();

        Project antProject = new Project();
        antProject.setBaseDir( project.getBasedir() );
        task.setProject( antProject );

        task.setDestDir( outputDirectory );
        task.setSrcDir( srcDir );
        task.setTitle( windowTitle );
        task.setVerbose( verbose );
        task.setRecurse( recurse );

        try
        {
            task.execute();
        }
        catch ( BuildException e )
        {
            throw new MojoExecutionException( "BuildException: " + e.getMessage() );
        }
    }
}
