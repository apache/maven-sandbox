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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.apache.maven.jxr.util.DotTask;
import org.apache.maven.jxr.util.DotTask.DotNotPresentInPathBuildException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import org.apache.tools.ant.taskdefs.XSLTProcess.Param;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import com.sun.tools.javadoc.Main;

/**
 * Generate UML diagram from Java source directory.
 * <br/>
 * <b>Note</b>: <a href="http://www.graphviz.org/">Graphviz</a> program should be in the path.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class GenerateUMLDoc
{
    /** Source directory */
    private File srcDir;

    /** Output file of the diagram */
    private File out;

    /** Temp javadoc xml file */
    private File javadocXml;

    /** Temp xsl file */
    private File xml2dot;

    /** Temp generated dot file */
    private File dot;

    /** Specify verbose information */
    private boolean verbose;

    /**
     * Default constructor.
     *
     * @param srcDir not null
     * @param out not null
     * @throws IllegalArgumentException if any
     */
    public GenerateUMLDoc( File srcDir, File out )
        throws IllegalArgumentException
    {
        if ( srcDir == null )
        {
            throw new IllegalArgumentException( "Missing mandatory attribute 'srcDir'." );
        }
        if ( !srcDir.exists() || srcDir.isFile() )
        {
            throw new IllegalArgumentException( "Input '" + srcDir + "' not found or not a directory." );
        }

        if ( out == null )
        {
            throw new IllegalArgumentException( "Missing mandatory attribute 'out'." );
        }
        if ( out.exists() && out.isDirectory() )
        {
            throw new IllegalArgumentException( out + " is a directory." );
        }
        if ( !out.exists() && !out.getParentFile().exists() && !out.getParentFile().mkdirs() )
        {
            throw new IllegalArgumentException( "Cannot create the parent directory of " + out );
        }

        this.srcDir = srcDir;
        this.out = out;
    }

    /**
     * Generate the documentation
     *
     * @throws IOException if any
     * @throws BuildException if any
     * @throws DotNotPresentInPathBuildException if any
     */
    public void generateUML()
        throws IOException, BuildException, DotNotPresentInPathBuildException
    {
        // 1. Generate Javadoc xml
        generateJavadocXML();

        // 2. Generate dot image
        generateJavadocDot();

        // 3. Generate UML image
        generateUmlImage();
    }

    /**
     * Getter for the destDir
     *
     * @return the destDir
     */
    public File getOut()
    {
        return this.out;
    }

    /**
     * Getter for the srcDir
     *
     * @return the srcDir
     */
    public File getSrcDir()
    {
        return this.srcDir;
    }

    /**
     * Getter for the verbose
     *
     * @return the verbose
     */
    public boolean isVerbose()
    {
        return this.verbose;
    }

    /**
     * Setter for the destDir
     *
     * @param destDir the destDir to set
     */
    public void setOut( File destDir )
    {
        this.out = destDir;
    }

    /**
     * Setter for the srcDir
     *
     * @param srcDir the srcDir to set
     */
    public void setSrcDir( File srcDir )
    {
        this.srcDir = srcDir;
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

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * @return the javadoc output xml file
     */
    private File getJavadocXml()
    {
        if ( this.javadocXml == null )
        {
            this.javadocXml = new File( getOut().getParentFile(), "javadoc.xml" );
            if ( !isVerbose() )
            {
                this.javadocXml.deleteOnExit();
            }
        }

        return this.javadocXml;
    }

    /**
     * @return a temp file for dot file.
     */
    private File getDot()
    {
        if ( this.dot == null )
        {
            this.dot = new File( getOut().getParentFile(), "javadoc.dot" );
            if ( !isVerbose() )
            {
                this.dot.deleteOnExit();
            }
        }

        return this.dot;
    }

    /**
     * @return a minimal Ant project.
     */
    private Project getAntProject()
    {
        Project antProject = new Project();
        antProject.setBasedir( new File( "" ).getAbsolutePath() );

        return antProject;
    }

    /**
     * @return xsl temp file.
     * @throws IOException if any
     */
    private File getXml2dot()
        throws IOException
    {
        if ( this.xml2dot == null )
        {
            this.xml2dot = new File( getOut().getParentFile(), "xml2dot.xsl" );
            if ( !isVerbose() )
            {
                this.xml2dot.deleteOnExit();
            }

            InputStream is = getClass().getClassLoader().getResourceAsStream(
                                                                              GenerateUMLDoc.class.getPackage()
                                                                                  .getName().replace( ".", "/" )
                                                                                  + "/xml2dot.xsl" );
            if ( is == null )
            {
                throw new IOException( "This resource doesn't exist." );
            }

            FileOutputStream w = new FileOutputStream( this.xml2dot );

            IOUtil.copy( is, w );

            IOUtil.close( is );

            IOUtil.close( w );
        }

        return this.xml2dot;
    }

    /**
     * Call javadoc tool with the XMLDoclet
     *
     * @throws IOException if Javadoc error
     */
    private void generateJavadocXML()
        throws IOException
    {
        final String defaultExcludes = "**/*~,**/#*#,**/.#*,**/%*%,**/._*,**/CVS,**/CVS/**,"
            + "**/.cvsignore,**/SCCS,**/SCCS/**,**/vssver.scc,**/.svn,**/.svn/**,**/.DS_Store";

        List args = new LinkedList();
        args.add( "-package" );
        args.add( "-sourcepath" );
        args.add( srcDir.getAbsolutePath() );
        args.add( "-o" );
        args.add( getJavadocXml().getAbsolutePath() );
        List packages = FileUtils.getDirectoryNames( srcDir, null, defaultExcludes, false );
        for ( Iterator it = packages.iterator(); it.hasNext(); )
        {
            String p = (String) it.next();

            if ( StringUtils.isEmpty( p ) )
            {
                continue;
            }

            if ( FileUtils.getFileNames( new File( srcDir, p ), "*.java", "", false ).isEmpty() )
            {
                continue;
            }

            args.add( StringUtils.replace( p, File.separator, "." ) );
        }

        StringWriter err = new StringWriter();
        StringWriter warn = new StringWriter();
        StringWriter notice = new StringWriter();
        int exit = Main.execute( "javadoc", new PrintWriter( err ), new PrintWriter( warn ), new PrintWriter( notice ),
                                 XMLDoclet.class.getName(), (String[]) args.toArray( new String[0] ) );

        if ( exit != 0 )
        {
            throw new IOException( "Error when calling Javadoc: " + err );
        }
    }

    /**
     * Apply XSLT to generate dot file from the Javadoc xml
     *
     * @throws BuildException if any
     * @throws IOException if any
     */
    private void generateJavadocDot()
        throws BuildException, IOException
    {
        String now = new GregorianCalendar( TimeZone.getDefault() ).getTime().toString();

        XSLTProcess xsltTask = new XSLTProcess();
        xsltTask.setProject( getAntProject() );
        xsltTask.setTaskName( "xslt" );
        xsltTask.init();
        xsltTask.setIn( getJavadocXml() );
        xsltTask.setOut( getDot() );
        xsltTask.setStyle( getXml2dot().getAbsolutePath() );
        Param param = xsltTask.createParam();
        param.setProject( getAntProject() );
        param.setName( "now" );
        param.setExpression( now );
        xsltTask.execute();
    }

    /**
     * Call Graphviz dot to generate images.
     *
     * @throws BuildException if any
     * @throws DotNotPresentInPathBuildException if any
     */
    private void generateUmlImage()
        throws BuildException, DotNotPresentInPathBuildException
    {
        String outputPath = getOut().getAbsolutePath();
        String format;
        if ( outputPath.lastIndexOf( "." ) != -1 )
        {
            format = outputPath.substring( outputPath.lastIndexOf( "." ) + 1 );
        }
        else
        {
            format = "png";
        }

        DotTask dotTask = new DotTask();
        dotTask.setProject( getAntProject() );
        dotTask.setTaskName( "dot" );
        dotTask.init();
        dotTask.setIn( getDot() );
        dotTask.setOut( getOut() );
        dotTask.setFormat( format );
        dotTask.execute();
    }
}
