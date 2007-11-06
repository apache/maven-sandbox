package org.apache.maven.jxr.java.doc;

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
import org.codehaus.plexus.util.StringUtils;

/**
 * <a href="http://ant.apache.org/">Ant</a> task to generate UML diagram.
 * <br/>
 * <b>Note</b>: <a href="http://www.graphviz.org/">Graphviz</a> program should be in the path.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class UmlDocTask
    extends Task
{
    /** Java source directory */
    private File srcDir;

    /** Output file of the diagram*/
    private File out;

    /** Source file encoding name. */
    private String encoding;

    /**
     * Specifies the access level for classes and members to show in the generated class diagram.
     * Possible values are:
     * <ul>
     * <li>public: shows only public classes and members</li>
     * <li>protected: shows only public and protected classes and members</li>
     * <li>package: shows all classes and members not marked private</li>
     * <li>private: shows all classes and members</li>
     * <li>"" (i.e. empty): nothing</li>
     * </ul>
     *
     * Default value is protected.
     */
    private String show;

    /** Relative path or URI to the generated Javadoc directory. */
    private String javadocPath;

    /** Relative path or URI to the generated Java Xref directory. */
    private String javasrcPath;

    /** The class diagram encoding. */
    private String diagramEncoding;

    /** The class diagram label */
    private String diagramLabel;

    /** Specify verbose information */
    private boolean verbose;

    /** Terminate Ant build */
    private boolean failOnError;

    /**
     * Set fail on an error.
     *
     * @param b true to fail on an error.
     */
    public void setFailonerror( boolean b )
    {
        this.failOnError = b;
    }

    /**
     * Set the class diagram encoding.
     *
     * @param diagramEncoding the class diagram encoding.
     */
    public void setDiagramEncoding( String diagramEncoding )
    {
        this.diagramEncoding = diagramEncoding;
    }

    /**
     * Set the class diagram label.
     *
     * @param diagramLabel the class diagram label.
     */
    public void setDiagramLabel( String diagramLabel )
    {
        this.diagramLabel = diagramLabel;
    }

    /**
     * Set the source file encoding name.
     *
     * @param encoding the source file encoding name.
     */
    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    /**
     * Set the generated Javadoc directory.
     *
     * @param javadocPath the relative path or URI to the generated javadoc directory
     */
    public void setJavadocPath( String javadocPath )
    {
        this.javadocPath = javadocPath;
    }

    /**
     * Set the generated JavaSrc source directory.
     *
     * @param javasrcPath the relative path or URI to the generated javasrc directory
     */
    public void setJavasrcPath( String javasrcPath )
    {
        this.javasrcPath = javasrcPath;
    }

    /**
     * Set the destination file.
     *
     * @param f Path to the generated UML file.
     */
    public void setOut( File f )
    {
        this.out = f;
    }

    /**
     * Set the access level for classes and members. Possible values are:
     * <ul>
     * <li>public: shows only public classes and members</li>
     * <li>protected: shows only public and protected classes and members</li>
     * <li>package: shows all classes and members not marked private</li>
     * <li>private: shows all classes and members</li>
     * <li>"" (i.e. empty): nothing</li>
     * </ul>
     *
     * @param show new access level.
     */
    public void setShow( String show )
    {
        this.show = show;
    }

    /**
     * Set the Java source directory.
     *
     * @param d Path to the directory.
     */
    public void setSrcDir( File d )
    {
        this.srcDir = d;
    }

    /**
     * Setter for the verbose
     *
     * @param verbose the verbose to set
     */
    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
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
        return "umldoc";
    }

    /** {@inheritDoc} */
    public String getDescription()
    {
        return "Generate UML documentation";
    }

    /** {@inheritDoc} */
    public void execute()
        throws BuildException
    {
        try
        {
            GenerateUMLDoc generator = new GenerateUMLDoc( getSrcDir(), getOut() );
            if ( StringUtils.isNotEmpty( this.encoding ) )
            {
                generator.setEncoding( this.encoding );
            }
            generator.setVerbose( this.verbose );
            if ( this.show != null )
            {
                generator.setShow( this.show );
            }
            if ( StringUtils.isNotEmpty( this.javadocPath ) )
            {
                generator.setJavadocPath( this.javadocPath );
            }
            if ( StringUtils.isNotEmpty( this.javasrcPath ) )
            {
                generator.setJavasrcPath( this.javasrcPath );
            }
            if ( StringUtils.isNotEmpty( this.diagramEncoding ) )
            {
                generator.setDiagramEncoding( this.diagramEncoding );
            }
            if ( StringUtils.isNotEmpty( this.diagramLabel ) )
            {
                generator.setDiagramLabel( this.diagramLabel );
            }
            generator.generateUML();
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
        catch ( UmlDocException e )
        {
            if ( !failOnError )
            {
                throw new BuildException( "UmlDocException: " + e.getMessage(), e, getLocation() );
            }

            log( "UmlDocException: " + e.getMessage(), Project.MSG_ERR );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * @return the source dir.
     */
    private File getSrcDir()
    {
        return this.srcDir;
    }

    /**
     * @return the output file.
     */
    private File getOut()
    {
        return this.out;
    }
}
