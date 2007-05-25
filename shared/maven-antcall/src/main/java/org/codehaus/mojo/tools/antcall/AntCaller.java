package org.codehaus.mojo.tools.antcall;

/*
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
 *
 */

import java.io.File;
import java.io.PrintStream;

import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * Convenience api used to call Ant APIs directly from Java code.
 * 
 * @author <a href="mailto:jdcasey@apache.org">John Casey</a>
 * @author <a href="mailto:kenney@apache.org">Kenney Westerhof</a>
 */
public class AntCaller
{
    private Project antProject;

    private Target tasks;

    private DefaultLogger antLogger;

    private final AntCallLogger logger;

    private String messageLevel = AntCallLogger.MESSAGE_LEVEL_INFO;

    private PrintStream outStream = System.out;

    private PrintStream errStream = System.err;

    // private InputStream inStream = System.in;

    public AntCaller( AntCallLogger logger )
    {
        this.logger = logger;
    }

    public boolean hasTasks()
    {
        if ( tasks == null )
        {
            return false;
        }

        Task[] taskArray = tasks.getTasks();

        return taskArray != null && taskArray.length > 0;
    }

    public void addTask( Task task )
    {
        checkOrCreateProjectAndTarget();

        task.setProject( antProject );
        tasks.addTask( task );
    }

    public void setProjectBasedir( File basedir )
    {
        checkOrCreateProjectAndTarget();

        antProject.setBaseDir( basedir );
    }

    private void checkOrCreateProjectAndTarget()
    {
        if ( antProject == null )
        {
            antProject = new Project();
        }

        if ( tasks == null )
        {
            tasks = new Target();
            tasks.setProject( antProject );
        }
    }

    public void setMessageLevel( String messageLevel )
    {
        this.messageLevel = messageLevel;
    }

    public String getProjectProperty( String property )
    {
        return antProject.getProperty( property );
    }

    public void setOutputStream( PrintStream printStream )
    {
        this.outStream = printStream;
    }

    public PrintStream getOutputStream()
    {
        return this.outStream;
    }

    public void setErrorStream( PrintStream printStream )
    {
        this.errStream = printStream;
    }

    public PrintStream getErrorStream()
    {
        return this.errStream;
    }

    // public void setInputStream( InputStream inStream )
    // {
    // this.inStream = inStream;
    // }
    //    
    // public InputStream getInputStream()
    // {
    // return this.inStream;
    // }

    private void initializeLogger()
    {
        int idx = AntCallLogger.MESSAGE_LEVELS.indexOf( messageLevel );

        if ( idx < 0 )
        {
            throw new IllegalArgumentException( "Invalid message level: \'" + messageLevel + "\'" );
        }
        else
        {
            if ( antLogger == null )
            {
                antLogger = new DefaultLogger();
                antLogger.setOutputPrintStream( outStream );
                antLogger.setErrorPrintStream( errStream );
            }

            switch ( idx )
            {
                case ( 0 ):
                {
                    antLogger.setMessageOutputLevel( Project.MSG_VERBOSE );
                    break;
                }
                case ( 1 ):
                {
                    antLogger.setMessageOutputLevel( Project.MSG_DEBUG );
                    break;
                }
                case ( 3 ):
                {
                    antLogger.setMessageOutputLevel( Project.MSG_WARN );
                    break;
                }
                case ( 4 ):
                {
                    antLogger.setMessageOutputLevel( Project.MSG_ERR );
                    break;
                }
                case ( 5 ):
                {
                    antLogger.setMessageOutputLevel( -1 );
                    break;
                }
                default:
                {
                    antLogger.setMessageOutputLevel( Project.MSG_INFO );
                }
            }
        }
    }

    public void executeTasks( MavenProject mavenProject ) throws AntExecutionException
    {
        if ( !hasTasks() )
        {
            logger.warn( "No tasks defined. Skipping Ant-API execution." );
            return;
        }

        try
        {
            PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper( antProject );

            propertyHelper.setNext( new AntPropertyHelper( mavenProject, logger ) );

            initializeLogger();

            antProject.addBuildListener( antLogger );

            File basedir = mavenProject.getBasedir();
            if ( basedir != null && basedir.exists() && basedir.isDirectory() )
            {
                antProject.setBaseDir( mavenProject.getBasedir() );
            }

            logger.debug( "Executing tasks: " );

            Task[] taskArray = tasks.getTasks();
            for ( int i = 0; i < taskArray.length; i++ )
            {
                if ( taskArray[i].getTaskName() != null )
                    logger.debug( taskArray[i].getTaskName() );
                else
                    logger.debug( "Unknown Task" );
            }

            tasks.execute();

            logger.debug( "Executed tasks" );
        }
        catch ( Exception e )
        {
            throw new AntExecutionException( "Error executing ant tasks", e );
        }
    }
}
