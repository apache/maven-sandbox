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

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;

/**
 * Utilities methods to play with <a href="http://www.graphviz.org/">Graphviz</a> program.
 * <br/>
 * <b>Note:</b> Graphviz <code>dot</code> executable should be in the path for the success of these methods.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class DotUtil
{
    /** Default output format when calling dot executable */
    public static final String DEFAULT_OUTPUT_FORMAT = "svg";

    /**
     * Execute Graphviz dot.
     *
     * @param input the input dot file, not null.
     * @param output the output generated file, could be null.
     * @throws CommandLineException if any.
     * @throws IllegalArgumentException if any.
     * @throws DotNotPresentInPathException if any.
     */
    public static void executeDot( File input, File output )
        throws CommandLineException, DotNotPresentInPathException
    {
        if ( output == null )
        {
            throw new IllegalArgumentException( "Output parameters could not be null" );
        }
        if ( output.exists() && output.isDirectory() )
        {
            throw new IllegalArgumentException( "Output file '" + output + "' is a dir." );
        }

        if ( !output.getParentFile().exists() && !output.getParentFile().mkdirs() )
        {
            throw new IllegalArgumentException( "Parent output file '" + output.getParentFile()
                + "' could not be created." );
        }

        process( null, input, null, output );
    }

    /**
     * Execute Graphviz dot.
     *
     * @param input the input dot file, not null.
     * @param format the wanted format, could be null.
     * @param output the output generated file, could be null.
     * @throws CommandLineException if any.
     * @throws IllegalArgumentException if any.
     * @throws DotNotPresentInPathException if any.
     */
    public static void executeDot( File input, String format, File output )
        throws CommandLineException, DotNotPresentInPathException
    {
        if ( output == null )
        {
            throw new IllegalArgumentException( "Output parameters could not be null" );
        }
        if ( output.exists() && output.isDirectory() )
        {
            throw new IllegalArgumentException( "Output file '" + output + "' is a dir." );
        }

        if ( !output.getParentFile().exists() && !output.getParentFile().mkdirs() )
        {
            throw new IllegalArgumentException( "Parent output file '" + output.getParentFile()
                + "' could not be created." );
        }

        process( null, input, format, output );
    }

    /**
     * Execute Graphviz dot.
     *
     * @param exe the dot executable, could be null.
     * @param input the input dot file, not null.
     * @param output the output generated file, could be null.
     * @throws CommandLineException if any.
     * @throws IllegalArgumentException if any.
     * @throws DotNotPresentInPathException if any.
     */
    public static void executeDot( File exe, File input, File output )
        throws CommandLineException, DotNotPresentInPathException
    {
        if ( exe == null )
        {
            throw new IllegalArgumentException( "Exe parameters could not be null" );
        }
        if ( !exe.exists() || !exe.isFile() )
        {
            throw new IllegalArgumentException( "Exe file '" + input + "' not found or not a file." );
        }

        if ( output == null )
        {
            throw new IllegalArgumentException( "Output parameters could not be null" );
        }
        if ( output.exists() && output.isDirectory() )
        {
            throw new IllegalArgumentException( "Output file '" + output + "' is a dir." );
        }

        if ( !output.getParentFile().exists() && !output.getParentFile().mkdirs() )
        {
            throw new IllegalArgumentException( "Parent output file '" + output.getParentFile()
                + "' could not be created." );
        }

        process( exe, input, null, output );
    }

    /**
     * Execute Graphviz dot.
     *
     * @param exe the dot executable, could be null.
     * @param input the input dot file, not null.
     * @param format the wanted format, could be null.
     * @param output the output generated file, could be null.
     * @throws CommandLineException if any.
     * @throws IllegalArgumentException if any.
     * @throws DotNotPresentInPathException if any.
     */
    public static void executeDot( File exe, File input, String format, File output )
        throws CommandLineException, DotNotPresentInPathException
    {
        if ( exe == null )
        {
            throw new IllegalArgumentException( "Exe parameters could not be null" );
        }
        if ( !exe.exists() || !exe.isFile() )
        {
            throw new IllegalArgumentException( "Exe file '" + input + "' not found or not a file." );
        }

        if ( output == null )
        {
            throw new IllegalArgumentException( "Output parameters could not be null" );
        }
        if ( output.exists() && output.isDirectory() )
        {
            throw new IllegalArgumentException( "Output file '" + output + "' is a dir." );
        }

        if ( !output.getParentFile().exists() && !output.getParentFile().mkdirs() )
        {
            throw new IllegalArgumentException( "Parent output file '" + output.getParentFile()
                + "' could not be created." );
        }

        process( exe, input, format, output );
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * Execute Graphviz dot.
     *
     * @param exe the dot executable, could be null.
     * @param input the input dot file, not null.
     * @param format the wanted format, could be null.
     * @param output the output generated file, could be null.
     * @throws CommandLineException if any.
     * @throws IllegalArgumentException if any.
     * @throws DotNotPresentInPathException if any.
     */
    private static void process( File exe, File input, String format, File output )
        throws CommandLineException, DotNotPresentInPathException
    {
        // Input checks
        if ( input == null )
        {
            throw new IllegalArgumentException( "Input parameter is mandatory." );
        }
        if ( !input.exists() || !input.isFile() )
        {
            throw new IllegalArgumentException( "Input file '" + input + "' not found or not a file." );
        }

        Commandline cmd = new Commandline();
        cmd.setWorkingDirectory( input.getParentFile() );
        if ( exe == null )
        {
            // in the path
            verifyDotInPath();
            cmd.setExecutable( "dot" );
        }
        else
        {
            cmd.setExecutable( exe.getAbsolutePath() );
        }

        if ( StringUtils.isEmpty( format ) )
        {
            if ( output.getAbsolutePath().lastIndexOf( "." ) != -1 )
            {
                format = output.getAbsolutePath().substring( output.getAbsolutePath().lastIndexOf( "." ) + 1 );
            }
            else
            {
                format = DEFAULT_OUTPUT_FORMAT;
            }
        }
        format = format.toLowerCase();

        if ( output == null )
        {
            output = new File( input.getParentFile(), input.getName() + "." + format );
        }
        else
        {
            String ouputPath = output.getAbsolutePath();
            if ( ( ouputPath.lastIndexOf( "." ) != -1 )
                && ( !format.equals( ouputPath.substring( ouputPath.lastIndexOf( "." ) + 1 ).toLowerCase() ) ) )
            {
                output = new File( ouputPath + "." + format );
            }
        }

        cmd.createArg().setValue( "-T" + format );
        cmd.createArg().setValue( "-o" );
        cmd.createArg().setValue( output.getAbsolutePath() );
        cmd.createArg().setValue( input.getAbsolutePath() );

        CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();

        int exitCode = CommandLineUtils.executeCommandLine( cmd, new DefaultConsumer(), err );

        if ( exitCode != 0 )
        {
            String cmdLine = CommandLineUtils.toString( cmd.getCommandline() ).replaceAll( "'", "" );

            StringBuffer msg = new StringBuffer( "Exit code: " + exitCode + " - " + err.getOutput() );
            msg.append( '\n' );
            msg.append( "Command line was:" + cmdLine );
            throw new CommandLineException( msg.toString() );
        }
    }

    /**
     * Call <code>dot -V</code> to see if <code>dot</code> executable is in the path.
     *
     * @throws CommandLineException if any
     * @throws DotNotPresentInPathException if any.
     */
    private static void verifyDotInPath()
        throws CommandLineException, DotNotPresentInPathException
    {
        Commandline cmd = new Commandline();
        cmd.setExecutable( "dot" );
        cmd.createArg().setValue( "-V" );

        CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();

        int exitCode = CommandLineUtils.executeCommandLine( cmd, new DefaultConsumer(), err );

        if ( exitCode != 0 )
        {
            throw new DotNotPresentInPathException( "Dot is not present in the PATH. Install it first" );
        }
    }

    /**
     * Signals that the dot executable is not present in the path.
     */
    public static class DotNotPresentInPathException
        extends RuntimeException
    {
        static final long serialVersionUID = 2381457101638341000L;

        /**
         * Constructs am exception with no descriptive information.
         */
        public DotNotPresentInPathException()
        {
            super();
        }

        /**
         * Constructs an exception with the given descriptive message.
         *
         * @param message
         */
        public DotNotPresentInPathException( String message )
        {
            super( message );
        }

        /**
         * Constructs an exception with the given message and exception as
         * a root cause.
         *
         * @param message
         * @param cause
         */
        public DotNotPresentInPathException( String message, Throwable cause )
        {
            super( message, cause );
        }
    }
}
