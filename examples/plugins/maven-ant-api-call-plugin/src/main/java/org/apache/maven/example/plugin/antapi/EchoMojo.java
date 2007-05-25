package org.apache.maven.example.plugin.antapi;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.taskdefs.Echo;
import org.codehaus.mojo.tools.antcall.AntCaller;
import org.codehaus.mojo.tools.antcall.AntExecutionException;
import org.codehaus.mojo.tools.antcall.MojoLogAdapter;

/**
 * Echos the specified message parameter out to the build console, using Ant's Echo task.
 * 
 * @goal echo
 */
public class EchoMojo
    extends AbstractMojo
{
    /**
     * Message to echo.
     * @parameter expression="${message}"
     * @required
     */
    private String message;
    
    /**
     * Current project being built, used to supply properties and paths to Ant.
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute()
        throws MojoExecutionException
    {
        AntCaller caller = new AntCaller( new MojoLogAdapter( getLog() ) );
        
        Echo echoTask = new Echo();
        echoTask.setTaskName( "echo" );
        echoTask.setMessage( message );
        
        caller.addTask( echoTask );
        
        try
        {
            caller.executeTasks( project );
        }
        catch ( AntExecutionException e )
        {
            throw new MojoExecutionException( "Failed to echo your message to the console. Reason: " + e.getMessage(), e );
        }
    }
}
