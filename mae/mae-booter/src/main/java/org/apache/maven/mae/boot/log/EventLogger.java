/*
 * Copyright 2010 Red Hat, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.maven.mae.boot.log;

import org.apache.maven.execution.BuildFailure;
import org.apache.maven.execution.BuildSuccess;
import org.apache.maven.execution.BuildSummary;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EventLogger
    implements ExecutionListener
{
    private final Logger logger;

    private static final int LINE_LENGTH = 72;

    public EventLogger( final Logger logger )
    {
        if ( logger == null )
        {
            throw new IllegalArgumentException( "logger missing" );
        }

        this.logger = logger;
    }

    private static String chars( final char c, final int count )
    {
        final StringBuilder buffer = new StringBuilder( count );

        for ( int i = count; i > 0; i-- )
        {
            buffer.append( c );
        }

        return buffer.toString();
    }

    private static String getFormattedTime( final long time )
    {
        String pattern = "s.SSS's'";

        if ( time / 60000L > 0 )
        {
            pattern = "m:s" + pattern;

            if ( time / 3600000L > 0 )
            {
                pattern = "H:m" + pattern;
            }
        }

        final DateFormat fmt = new SimpleDateFormat( pattern );
        fmt.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

        return fmt.format( new Date( time ) );
    }

    public void projectDiscoveryStarted( final ExecutionEvent event )
    {
        if ( logger.isInfoEnabled() )
        {
            logger.info( "Scanning for projects..." );
        }
    }

    public void sessionStarted( final ExecutionEvent event )
    {
        if ( logger.isInfoEnabled() && event.getSession().getProjects().size() > 1 )
        {
            logger.info( chars( '-', LINE_LENGTH ) );

            logger.info( "Reactor Build Order:" );

            logger.info( "" );

            for ( final MavenProject project : event.getSession().getProjects() )
            {
                logger.info( project.getName() );
            }
        }
    }

    public void sessionEnded( final ExecutionEvent event )
    {
        if ( logger.isInfoEnabled() )
        {
            if ( event.getSession().getProjects().size() > 1 )
            {
                logReactorSummary( event.getSession() );
            }

            logResult( event.getSession() );

            logStats( event.getSession() );

            logger.info( chars( '-', LINE_LENGTH ) );
        }
    }

    private void logReactorSummary( final MavenSession session )
    {
        logger.info( chars( '-', LINE_LENGTH ) );

        logger.info( "Reactor Summary:" );

        logger.info( "" );

        final MavenExecutionResult result = session.getResult();

        for ( final MavenProject project : session.getProjects() )
        {
            final StringBuilder buffer = new StringBuilder( 128 );

            buffer.append( project.getName() );

            buffer.append( ' ' );
            while ( buffer.length() < LINE_LENGTH - 21 )
            {
                buffer.append( '.' );
            }
            buffer.append( ' ' );

            final BuildSummary buildSummary = result.getBuildSummary( project );

            if ( buildSummary == null )
            {
                buffer.append( "SKIPPED" );
            }
            else if ( buildSummary instanceof BuildSuccess )
            {
                buffer.append( "SUCCESS [" );
                buffer.append( getFormattedTime( buildSummary.getTime() ) );
                buffer.append( "]" );
            }
            else if ( buildSummary instanceof BuildFailure )
            {
                buffer.append( "FAILURE [" );
                buffer.append( getFormattedTime( buildSummary.getTime() ) );
                buffer.append( "]" );
            }

            logger.info( buffer.toString() );
        }
    }

    private void logResult( final MavenSession session )
    {
        logger.info( chars( '-', LINE_LENGTH ) );

        if ( session.getResult().hasExceptions() )
        {
            logger.info( "BUILD FAILURE" );
        }
        else
        {
            logger.info( "BUILD SUCCESS" );
        }
    }

    private void logStats( final MavenSession session )
    {
        logger.info( chars( '-', LINE_LENGTH ) );

        final Date finish = new Date();

        final long time = finish.getTime() - session.getRequest().getStartTime().getTime();

        final String wallClock = session.getRequest().isThreadConfigurationPresent() ? " (Wall Clock)" : "";

        logger.info( "Total time: " + getFormattedTime( time ) + wallClock );

        logger.info( "Finished at: " + finish );

        System.gc();

        final Runtime r = Runtime.getRuntime();

        final long MB = 1024 * 1024;

        logger.info( "Final Memory: " + ( r.totalMemory() - r.freeMemory() ) / MB + "M/" + r.totalMemory() / MB + "M" );
    }

    public void projectSkipped( final ExecutionEvent event )
    {
        if ( logger.isInfoEnabled() )
        {
            logger.info( chars( ' ', LINE_LENGTH ) );
            logger.info( chars( '-', LINE_LENGTH ) );

            logger.info( "Skipping " + event.getProject().getName() );
            logger.info( "This project has been banned from the build due to previous failures." );

            logger.info( chars( '-', LINE_LENGTH ) );
        }
    }

    public void projectStarted( final ExecutionEvent event )
    {
        if ( logger.isInfoEnabled() )
        {
            logger.info( chars( ' ', LINE_LENGTH ) );
            logger.info( chars( '-', LINE_LENGTH ) );

            logger.info( "Building " + event.getProject().getName() + " " + event.getProject().getVersion() );

            logger.info( chars( '-', LINE_LENGTH ) );
        }
    }

    public void mojoSkipped( final ExecutionEvent event )
    {
        if ( logger.isWarnEnabled() )
        {
            logger.warn( "Goal " + event.getMojoExecution().getGoal()
                            + " requires online mode for execution but Maven is currently offline, skipping" );
        }
    }

    public void mojoStarted( final ExecutionEvent event )
    {
        if ( logger.isInfoEnabled() )
        {
            final StringBuilder buffer = new StringBuilder( 128 );

            buffer.append( "--- " );
            append( buffer, event.getMojoExecution() );
            append( buffer, event.getProject() );
            buffer.append( " ---" );

            logger.info( "" );
            logger.info( buffer.toString() );
        }
    }

    public void forkStarted( final ExecutionEvent event )
    {
        if ( logger.isInfoEnabled() )
        {
            final StringBuilder buffer = new StringBuilder( 128 );

            buffer.append( ">>> " );
            append( buffer, event.getMojoExecution() );
            append( buffer, event.getProject() );
            buffer.append( " >>>" );

            logger.info( "" );
            logger.info( buffer.toString() );
        }
    }

    public void forkSucceeded( final ExecutionEvent event )
    {
        if ( logger.isInfoEnabled() )
        {
            final StringBuilder buffer = new StringBuilder( 128 );

            buffer.append( "<<< " );
            append( buffer, event.getMojoExecution() );
            append( buffer, event.getProject() );
            buffer.append( " <<<" );

            logger.info( "" );
            logger.info( buffer.toString() );
        }
    }

    private void append( final StringBuilder buffer, final MojoExecution me )
    {
        buffer.append( me.getArtifactId() ).append( ':' ).append( me.getVersion() );
        buffer.append( ':' ).append( me.getGoal() );
        if ( me.getExecutionId() != null )
        {
            buffer.append( " (" ).append( me.getExecutionId() ).append( ')' );
        }
    }

    private void append( final StringBuilder buffer, final MavenProject project )
    {
        buffer.append( " @ " ).append( project.getArtifactId() );
    }

    public void forkedProjectStarted( final ExecutionEvent event )
    {
        if ( logger.isInfoEnabled() && event.getMojoExecution().getForkedExecutions().size() > 1 )
        {
            logger.info( chars( ' ', LINE_LENGTH ) );
            logger.info( chars( '>', LINE_LENGTH ) );

            logger.info( "Forking " + event.getProject().getName() + " " + event.getProject().getVersion() );

            logger.info( chars( '>', LINE_LENGTH ) );
        }
    }

    public void forkFailed( final ExecutionEvent event )
    {
    }

    public void forkedProjectFailed( final ExecutionEvent event )
    {
    }

    public void forkedProjectSucceeded( final ExecutionEvent event )
    {
    }

    public void mojoFailed( final ExecutionEvent event )
    {
    }

    public void mojoSucceeded( final ExecutionEvent event )
    {
    }

    public void projectFailed( final ExecutionEvent event )
    {
    }

    public void projectSucceeded( final ExecutionEvent event )
    {
    }

}
