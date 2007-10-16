package org.apache.maven.plugin.jxr;

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
import java.util.Iterator;
import java.util.Locale;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.StringUtils;

/**
 * Creates an html-based, cross referenced version of Java source code.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @goal javasrc
 */
public class JavasrcReport
    extends AbstractJavasrcMojo
    implements MavenReport
{
    // ----------------------------------------------------------------------
    // Report Parameters
    // ----------------------------------------------------------------------

    /**
     * Internationalization.
     *
     * @component
     */
    protected I18N i18n;

    /**
     * Specifies the destination directory where javasrc generates HTML files.
     *
     * @parameter expression="${project.reporting.outputDirectory}/javasrc"
     * @required
     */
    private File reportOutputDirectory;

    /**
     * The name of the Antlr report.
     *
     * @parameter expression="${name}" default-value="Forrestdoc"
     */
    private String name;

    /**
     * The description of the Antlr report.
     *
     * @parameter expression="${description}" default-value="Source code/Documentation management system."
     */
    private String description;

    /**
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        super.executeJavaSrcTask();
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#generate(org.codehaus.doxia.sink.Sink, java.util.Locale)
     */
    public void generate( Sink sink, Locale locale )
        throws MavenReportException
    {
        outputDirectory = reportOutputDirectory;

        try
        {
            execute();
        }
        catch ( MojoExecutionException e )
        {
            throw new MavenReportException( "An error has occurred in " + getName( Locale.ENGLISH )
                + " report generation.", e );
        }
        catch ( MojoFailureException e )
        {
            throw new MavenReportException( "An error has occurred in " + getName( Locale.ENGLISH )
                + " report generation.", e );
        }
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#canGenerateReport()
     */
    public boolean canGenerateReport()
    {
        // TODO aggregate report
        boolean canGenerate = true;
        for (Iterator it = project.getCompileSourceRoots().iterator(); it.hasNext();)
        {
            canGenerate = canGenerate && new File( (String) it.next() ).exists();
        }

        if ( !project.isExecutionRoot() )
        {
            canGenerate = false;
        }

        return canGenerate;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getCategoryName()
     */
    public String getCategoryName()
    {
        return CATEGORY_PROJECT_REPORTS;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    public String getOutputName()
    {
        return "javasrc/index";
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
     */
    public String getName( Locale locale )
    {
        if ( StringUtils.isEmpty( name ) )
        {
            return i18n.getString( "forrestdoc-report", locale, "report.name" );
        }

        return name;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
     */
    public String getDescription( Locale locale )
    {
        if ( StringUtils.isEmpty( description ) )
        {
            return i18n.getString( "forrestdoc-report", locale, "report.description" );
        }

        return description;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getReportOutputDirectory()
     */
    public File getReportOutputDirectory()
    {
        if ( reportOutputDirectory == null )
        {
            return outputDirectory;
        }

        return reportOutputDirectory;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#setReportOutputDirectory(java.io.File)
     */
    public void setReportOutputDirectory( File reportOutputDirectory )
    {
        if ( ( reportOutputDirectory != null ) && ( !reportOutputDirectory.getAbsolutePath().endsWith( "javasrc" ) ) )
        {
            this.reportOutputDirectory = new File( reportOutputDirectory, "javasrc" );
        }
        else
        {
            this.reportOutputDirectory = reportOutputDirectory;
        }
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#isExternalReport()
     */
    public boolean isExternalReport()
    {
        return true;
    }
}
