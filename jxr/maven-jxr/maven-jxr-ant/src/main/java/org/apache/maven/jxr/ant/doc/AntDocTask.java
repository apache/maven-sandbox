package org.apache.maven.jxr.ant.doc;

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
import java.io.IOException;

import org.apache.maven.jxr.util.DotUtil.DotNotPresentInPathException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * <a href="http://ant.apache.org/">Ant</a> task to generate Ant code documentation.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class AntDocTask
    extends Task
{
    /** An ant file */
    private File antFile;

    /** Destination directory */
    private File destDir;

    /** Graphviz Dot executable file */
    private File dotExecutable;

    /** Verbose mode */
    private boolean verbose;

    /** Terminate Ant build */
    private boolean failOnError;

    /**
     * Set the ant file to generate documentation.
     *
     * @param f Path to the Ant file.
     */
    public void setAntFile( File f )
    {
        this.antFile = f;
    }

    /**
     * Set the destination directory.
     *
     * @param d Path to the directory.
     */
    public void setDestDir( File d )
    {
        this.destDir = d;
    }

    /**
     * Set the the dotExecutable to set
     *
     * @param dotExecutable the dotExecutable to set
     */
    public void setDotExecutable( File dotExecutable )
    {
        this.dotExecutable = dotExecutable;
    }

    /**
     * Set verbose mode.
     *
     * @param b true to verbose mode.
     */
    public void setVerbose( boolean b )
    {
        this.verbose = b;
    }

    /**
     * Set fail on an error.
     *
     * @param b true to fail on an error.
     */
    public void setFailonerror( boolean b )
    {
        this.failOnError = b;
    }

    /** {@inheritDoc} */
    public void init()
        throws BuildException
    {
        super.init();
    }

    /** {@inheritDoc} */
    public String getTaskName()
    {
        return "antdoc";
    }

    /** {@inheritDoc} */
    public String getDescription()
    {
        return "Generate Ant documentation";
    }

    /** {@inheritDoc} */
    public void execute()
        throws BuildException
    {
        try
        {
            GenerateHTMLDoc generator = new GenerateHTMLDoc();
            generator.setVerbose( this.verbose );
            if ( this.dotExecutable != null )
            {
                generator.generate( this.dotExecutable, this.antFile, this.destDir );
            }
            else
            {
                generator.generate( this.antFile, this.destDir );
            }
        }
        catch ( IllegalArgumentException e )
        {
            if ( !failOnError )
            {
                throw new BuildException( "IllegalArgumentException: " + e.getMessage(), e, getLocation() );
            }

            log( "IllegalArgumentException: " + e.getMessage(), Project.MSG_ERR );
        }
        catch ( DotNotPresentInPathException e )
        {
            if ( !failOnError )
            {
                throw new BuildException( "DotNotPresentInPathException: " + e.getMessage(), e, getLocation() );
            }

            log( "Dot is not present in the path: " + e.getMessage(), Project.MSG_ERR );
        }
        catch ( IOException e )
        {
            if ( !failOnError )
            {
                throw new BuildException( "IOException: " + e.getMessage(), e, getLocation() );
            }

            log( "IOException: " + e.getMessage(), Project.MSG_ERR );
        }
        catch ( AntDocException e )
        {
            if ( !failOnError )
            {
                throw new BuildException( "AntDocException: " + e.getMessage(), e, getLocation() );
            }

            log( "AntDocException: " + e.getMessage(), Project.MSG_ERR );
        }
    }
}
