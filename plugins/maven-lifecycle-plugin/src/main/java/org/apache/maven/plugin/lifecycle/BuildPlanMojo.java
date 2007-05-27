package org.apache.maven.plugin.lifecycle;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.lifecycle.LifecycleLoaderException;
import org.apache.maven.lifecycle.LifecycleSpecificationException;
import org.apache.maven.lifecycle.plan.BuildPlan;
import org.apache.maven.lifecycle.plan.BuildPlanUtils;
import org.apache.maven.lifecycle.plan.BuildPlanner;
import org.apache.maven.lifecycle.plan.LifecyclePlannerException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Retrieves the build plan for the current project, and displays it to the logger's INFO log-level, or a file.
 * 
 * @goal build-plan
 */
public class BuildPlanMojo
    extends AbstractMojo
{
    /**
     * File for writing the build-plan.
     * 
     * @parameter
     */
    private File output;

    /**
     * Whether to list extended information about each mojo in the build plan. Default is false.
     * 
     * @parameter expression="${extendedInfo}" default-value="false"
     */
    private boolean extendedInfo;

    /**
     * Comma-separated list of tasks to complete in the proposed build.
     * 
     * @parameter expression="${tasks}" default-value="${package}"
     * @required
     */
    private String tasks;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private BuildPlanner buildPlanner;

    public void execute()
        throws MojoExecutionException
    {
        StringTokenizer tokens = new StringTokenizer( tasks, "," );
        List taskList = new ArrayList( tokens.countTokens() );

        while ( tokens.hasMoreTokens() )
        {
            taskList.add( tokens.nextToken().trim() );
        }

        BuildPlan buildPlan;
        try
        {
            buildPlan = buildPlanner.constructBuildPlan( taskList, project );
        }
        catch ( LifecycleLoaderException e )
        {
            throw new MojoExecutionException( "Failed to construct build plan. Reason: " + e.getMessage(), e );
        }
        catch ( LifecycleSpecificationException e )
        {
            throw new MojoExecutionException( "Failed to construct build plan. Reason: " + e.getMessage(), e );
        }
        catch ( LifecyclePlannerException e )
        {
            throw new MojoExecutionException( "Failed to construct build plan. Reason: " + e.getMessage(), e );
        }

        String listing;
        try
        {
            listing = BuildPlanUtils.listBuildPlan( buildPlan, extendedInfo );
        }
        catch ( LifecycleSpecificationException e )
        {
            throw new MojoExecutionException( "Failed to list build plan. Reason: " + e.getMessage(), e );
        }
        catch ( LifecyclePlannerException e )
        {
            throw new MojoExecutionException( "Failed to list build plan. Reason: " + e.getMessage(), e );
        }

        if ( output != null )
        {
            if ( output.getParentFile() != null )
            {
                output.getParentFile().mkdirs();
            }

            FileWriter writer = null;
            try
            {
                writer = new FileWriter( output );

                writer.write( "Project: " + project.getId() );
                writer.write( "\nTasks: " + tasks );
                writer.write( "\nBuildPlan:\n" );
                writer.write( listing );
                writer.flush();
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Failed to write build plan to: " + output + ". Reason: "
                                                  + e.getMessage(), e );
            }
            finally
            {
                IOUtil.close( writer );
            }
        }
        else
        {
            getLog().info(
                           "\n\nProject: " + project.getId() + "\nTasks: " + tasks + "\nBuild Plan:\n" + listing
                                           + "\n\n" );
        }
    }
}
