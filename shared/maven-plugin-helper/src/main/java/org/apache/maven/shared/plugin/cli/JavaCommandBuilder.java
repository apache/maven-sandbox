package org.apache.maven.shared.plugin.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.plugin.classloader.ProjectClassLoader;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineTimeOutException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.shell.Shell;

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

/**
 * Use the builder pattern to configure and execute a class on a forked JVM.
 * 
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public class JavaCommandBuilder
{
    public static final String OS_NAME = System.getProperty( "os.name" ).toLowerCase( Locale.US );

    private String jvm = System.getProperty( "java.home" );

    private String className;

    private int timeOut;

    private String scope;

    private List args = new ArrayList();

    private List jvmArgs = new ArrayList();

    private List classpath = new ArrayList();

    private Properties systemProperties = new Properties();

    private Properties env = new Properties();

    private Log log;

    public JavaCommandBuilder( String className, Log log )
    {
        this.className = className;
        this.log = log;
    }

    public JavaCommandBuilder withJVM( String jvm )
    {
        if ( !StringUtils.isEmpty( jvm ) )
        {
            this.jvm = jvm;
        }
        return this;
    }

    public JavaCommandBuilder withinScope( MavenProject project, String scope, boolean includeSources )
        throws MojoExecutionException
    {
        File outputDirectory = new File( project.getBuild().getOutputDirectory() );
        classpath.add( outputDirectory );
        if ( includeSources )
        {
            List sourceRoots = project.getCompileSourceRoots();
            for ( Iterator iterator = sourceRoots.iterator(); iterator.hasNext(); )
            {
                String sourceRoot = (String) iterator.next();
                classpath.add( new File( sourceRoot ) );
            }
        }

        if ( scope.equals( Artifact.SCOPE_TEST ) )
        {
            File testOutputDirectory = new File( project.getBuild().getTestOutputDirectory() );
            classpath.add( testOutputDirectory );
            if ( includeSources )
            {
                List testSourceRoots = project.getTestCompileSourceRoots();
                for ( Iterator iterator = testSourceRoots.iterator(); iterator.hasNext(); )
                {
                    String testSourceRoot = (String) iterator.next();
                    classpath.add( new File( testSourceRoot ) );
                }
            }
        }

        List artifacts = ProjectClassLoader.getArtifactsByScope( project, scope );
        for ( Iterator iterator = artifacts.iterator(); iterator.hasNext(); )
        {
            Artifact artifact = (Artifact) iterator.next();
            if ( !artifact.isResolved() )
            {
                throw new IllegalStateException( "Artifact is not resolved. \n"
                    + "Plugin must declare @requiresDependencyResolution " + scope );
            }
            classpath.add( artifact.getFile() );
        }
        return this;
    }

    public JavaCommandBuilder withClasspath( File path )
    {
        classpath.add( path );
        return this;
    }

    public JavaCommandBuilder withJvmArgs( String arg )
    {
        jvmArgs.add( arg );
        return this;
    }

    public JavaCommandBuilder arg( String arg )
    {
        args.add( arg );
        return this;
    }

    public JavaCommandBuilder arg( boolean condition, String arg )
    {
        if ( condition )
        {
            args.add( arg );
        }
        return this;
    }

    public JavaCommandBuilder systemProperty( String name, String value )
    {
        systemProperties.setProperty( name, value );
        return this;
    }

    public JavaCommandBuilder environment( String name, String value )
    {
        env.setProperty( name, value );
        return this;
    }

    /**
     * @return <code>true</code> if the command execute successfully, <code>false</code> on timeOut.
     * @throws MojoExecutionException
     */
    public boolean execute()
        throws MojoExecutionException
    {
        List command = new ArrayList();
        command.addAll( jvmArgs );
        command.add( "-classpath" );
        List path = new ArrayList();
        for ( Iterator iterator = classpath.iterator(); iterator.hasNext(); )
        {
            File file = (File) iterator.next();
            path.add( StringUtils.quoteAndEscape( file.getAbsolutePath(), '"' ) );
        }
        command.add( StringUtils.join( path.iterator(), File.pathSeparator ) );

        if ( systemProperties != null )
        {
            for ( Iterator iterator = systemProperties.entrySet().iterator(); iterator.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) iterator.next();
                command.add( "-D" + entry.getKey() + "=" + entry.getValue() );
            }
        }
        command.add( className );
        command.addAll( args );
        String[] arguments = (String[]) command.toArray( new String[command.size()] );
        return executeJava( arguments );
    }

    private boolean executeJava( String[] arguments )
        throws MojoExecutionException, ForkedProcessExecutionException
    {
        // On windows, the default Shell will fall into command line length limitation issue
        // On Unixes, not using a Shell breaks the classpath as escaped path are not resolved.
        Commandline cmd = ( Os.isFamily( Os.FAMILY_WINDOWS ) ) ? new Commandline( new JavaShell() ) : new Commandline();
        cmd.setExecutable( getJavaCommand() );
        cmd.addArguments( arguments );
        if ( env != null )
        {
            for ( Iterator iterator = env.entrySet().iterator(); iterator.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) iterator.next();
                cmd.addEnvironment( (String) entry.getKey(), (String) entry.getValue() );
            }
        }

        log.debug( "Execute command :\n" + cmd.toString() );
        int status;
        try
        {
            if ( timeOut > 0 )
            {
                status = CommandLineUtils.executeCommandLine( cmd, out, err, timeOut );
            }
            else
            {
                status = CommandLineUtils.executeCommandLine( cmd, out, err );
            }

        }
        catch ( CommandLineTimeOutException e )
        {
            if ( timeOut > 0 )
            {
                log.warn( "Forked JVM has been killed on time-out after " + timeOut + " seconds" );
                return false;
            }
            throw new MojoExecutionException( "Time-out on command line execution :\n" + cmd.toString() );
        }
        catch ( CommandLineException e )
        {
            throw new MojoExecutionException( "Failed to execute command line :\n" + cmd.toString() );
        }

        if ( status != 0 )
        {
            throw new ForkedProcessExecutionException( "Command [[\n" + cmd.toString() + "\n]] failed with status "
                + status );
        }
        return true;
    }

    private String getJavaCommand()
        throws MojoExecutionException
    {
        // does-it exists ? is-it a directory or a path to a java executable ?
        File jvmFile = new File( jvm );
        if ( !jvmFile.exists() )
        {
            throw new MojoExecutionException( "the configured jvm " + jvm
                + " doesn't exists please check your environnement" );
        }
        if ( jvmFile.isDirectory() )
        {
            // it's a directory we construct the path to the java executable
            return jvmFile.getAbsolutePath() + File.separator + "bin" + File.separator + "java";
        }
        return jvm;
    }

    /**
     * A plexus-util StreamConsumer to redirect messages to plugin log
     */
    protected StreamConsumer out = new StreamConsumer()
    {
        public void consumeLine( String line )
        {
            log.info( line );
        }
    };

    /**
     * A plexus-util StreamConsumer to redirect errors to plugin log
     */
    protected StreamConsumer err = new StreamConsumer()
    {
        public void consumeLine( String line )
        {
            log.error( line );
        }
    };

    /**
     * plexus-util hack to run a command WITHOUT a shell. On windows, the shell is restricted to ~4000 characters and
     * break on long classpath created from maven dependencies. Running the JVM as a native process works fine on this
     * platform, even with whitespaces in files path escaped.
     * 
     * @see PLXUTILS-107
     */
    private class JavaShell
        extends Shell
    {
        protected List getRawCommandLine( String executable, String[] arguments )
        {
            List commandLine = new ArrayList();
            if ( executable != null )
            {
                commandLine.add( executable );
            }
            for ( int i = 0; i < arguments.length; i++ )
            {
                commandLine.add( arguments[i] );
            }
            return commandLine;
        }
    }
}
