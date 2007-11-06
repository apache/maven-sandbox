package org.apache.maven.plugin.uml;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.site.decoration.Body;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.Skin;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.jxr.java.doc.GenerateUMLDoc;
import org.apache.maven.jxr.java.doc.UmlDocException;
import org.apache.maven.jxr.util.DotUtil.DotNotPresentInPathException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.PathTool;
import org.codehaus.plexus.util.StringUtils;

/**
 * Generates UML class diagram for the <code>Java code</code> of the project using the
 * <a href="http://www.graphviz.org/">Graphviz Tool</a>.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @goal uml
 */
public class UmlReport
    extends AbstractMavenReport
{
    // ----------------------------------------------------------------------
    // Report Parameters
    // ----------------------------------------------------------------------

    /**
     * The Maven Project Object.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Specifies the destination directory where the report will be generated.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Specifies the destination report directory where the report will be generated.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    private File reportOutputDirectory;

    /**
     * Renderer component.
     *
     * @component
     */
    private Renderer siteRenderer;

    /**
     * Internationalization component.
     *
     * @component
     */
    private I18N i18n;

    /**
     * Local Repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * ArtifactResolver component.
     *
     * @component
     */
    private ArtifactResolver resolver;

    /**
     * ArtifactFactory component.
     *
     * @component
     */
    private ArtifactFactory factory;

    /**
     * The name of the UML report.
     *
     * @parameter expression="${name}" default-value="UML Class Diagram"
     */
    private String name;

    /**
     * The description of the UML report.
     *
     * @parameter expression="${description}" default-value="Generate an UML class diagram from Java source code."
     */
    private String description;

    // ----------------------------------------------------------------------
    // UML parameters
    // ----------------------------------------------------------------------

    /**
     * The output file of the class diagram.
     * <br/>
     * <b>Note:</b> The extension of the output file will be used to auto-detect the wanted format for Graphviz.
     * See <a href="http://www.graphviz.org/doc/info/output.html">Output Formats</a> supported by Graphviz.
     *
     * @parameter expression="${project.reporting.outputDirectory}/images/class-diagram.svg"
     * @required
     */
    private File output;

    /**
     * Sets the path of the Graphviz Dot executable to use.
     * <br/>
     * See <a href="http://www.graphviz.org/">Graphviz</a>.
     *
     * @parameter expression="${dotExecutable}"
     */
    private File dotExecutable;

    /**
     * Specifies the encoding name of the source files.
     *
     * @parameter expression="${encoding}"
     */
    private String encoding;

    /**
     * The class diagram encoding.
     *
     * @parameter expression="${encoding}" default-value="ISO-8859-1"
     */
    private String diagramEncoding;

    /**
     * The UML class diagram label.
     *
     * @parameter expression="${diagramLabel}"
     * default-value="UML Class Diagram of ${project.name}.&#xa;Copyright &#169; {inceptionYear}-{currentYear} {organizationName}. All Rights Reserved."
     */
    private String diagramLabel;

    /**
     * Relative path or URI to the generated Javadoc directory.
     *
     * @parameter expression="${javadocPath}" default-value="../apidocs/"
     */
    private String javadocPath;

    /**
     * Relative path or URI to the generated Java Xref directory.
     *
     * @parameter expression="${javasrcPath}" default-value="../javasrc/"
     */
    private String javasrcPath;

    /**
     * True to verbose the scan.
     *
     * @parameter expression="${verbose}" default-value="false"
     */
    private boolean verbose;

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
     * @parameter expression="${show}" default-value="protected"
     */
    private String show;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public String getDescription( Locale locale )
    {
        if ( StringUtils.isEmpty( description ) )
        {
            return i18n.getString( "uml-report", locale, "report.description" );
        }

        return description;
    }

    /** {@inheritDoc} */
    public String getName( Locale locale )
    {
        if ( StringUtils.isEmpty( name ) )
        {
            return i18n.getString( "uml-report", locale, "report.name" );
        }

        return name;
    }

    /** {@inheritDoc} */
    public String getOutputName()
    {
        return "uml";
    }

    /** {@inheritDoc} */
    public File getReportOutputDirectory()
    {
        return reportOutputDirectory;
    }

    /** {@inheritDoc} */
    public void setReportOutputDirectory( File reportOutputDirectory )
    {
        this.reportOutputDirectory = reportOutputDirectory;
    }

    /** {@inheritDoc} */
    public boolean canGenerateReport()
    {
        boolean canGenerate = true;
        for ( Iterator it = project.getCompileSourceRoots().iterator(); it.hasNext(); )
        {
            canGenerate = canGenerate && new File( (String) it.next() ).exists();
        }

        return canGenerate;
    }

    /** {@inheritDoc} */
    public void execute()
        throws MojoExecutionException
    {
        // TODO Comes from maven-project-info-plugin AbstractProjectInfoReport class: MPIR-74
        if ( !canGenerateReport() )
        {
            return;
        }

        try
        {
            DecorationModel model = new DecorationModel();
            model.setBody( new Body() );
            Map attributes = new HashMap();
            attributes.put( "outputEncoding", "UTF-8" );
            attributes.put( "project", project );
            Locale locale = Locale.getDefault();
            SiteRenderingContext siteContext = siteRenderer.createContextForSkin( getSkinArtifactFile(), attributes,
                                                                                  model, getName( locale ), locale );

            RenderingContext context = new RenderingContext( outputDirectory, getOutputName() + ".html" );

            SiteRendererSink sink = new SiteRendererSink( context );
            generate( sink, locale );

            outputDirectory.mkdirs();

            Writer writer = new FileWriter( new File( outputDirectory, getOutputName() + ".html" ) );

            siteRenderer.generateDocument( writer, sink, siteContext );

            siteRenderer.copyResources( siteContext, new File( project.getBasedir(), "src/site/resources" ),
                                        outputDirectory );
        }
        catch ( RendererException e )
        {
            throw new MojoExecutionException( "An error has occurred in " + getName( Locale.ENGLISH )
                + " report generation.", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "An error has occurred in " + getName( Locale.ENGLISH )
                + " report generation.", e );
        }
        catch ( MavenReportException e )
        {
            throw new MojoExecutionException( "An error has occurred in " + getName( Locale.ENGLISH )
                + " report generation.", e );
        }
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    protected Renderer getSiteRenderer()
    {
        return siteRenderer;
    }

    /** {@inheritDoc} */
    protected String getOutputDirectory()
    {
        return outputDirectory.getAbsolutePath();
    }

    /** {@inheritDoc} */
    protected MavenProject getProject()
    {
        return project;
    }

    /** {@inheritDoc} */
    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        executeUml();

        try
        {
            generateReport( locale );
        }
        catch ( IOException e )
        {
            throw new MavenReportException( "IOException: " + e.getMessage(), e );
        }
    }

    // ----------------------------------------------------------------------
    // private methods
    // ----------------------------------------------------------------------

    /**
     * Execute the <code>UML</code>.
     *
     * @throws MavenReportException if any
     */
    private void executeUml()
        throws MavenReportException
    {
        GenerateUMLDoc generator;
        try
        {
            generator = new GenerateUMLDoc( new File( this.project.getBuild().getSourceDirectory() ), output );
        }
        catch ( IllegalArgumentException e )
        {
            throw new MavenReportException( "IllegalArgumentException: " + e.getMessage(), e );
        }

        if ( this.dotExecutable != null )
        {
            generator.setDotExecutable( this.dotExecutable );
        }
        if ( StringUtils.isNotEmpty( this.encoding ) )
        {
            generator.setEncoding( this.encoding );
        }
        generator.setVerbose( this.verbose );
        if ( this.show != null ) // could be empty
        {
            generator.setShow( this.show );
        }
        if ( StringUtils.isNotEmpty( this.javadocPath ) )
        {
            File javadoc = new File( output.getParentFile(), this.javadocPath );
            if ( javadoc.exists() )
            {
                generator.setJavadocPath( this.javadocPath );
            }
        }
        if ( StringUtils.isNotEmpty( this.javasrcPath ) )
        {
            File javasrc = new File( output.getParentFile(), this.javasrcPath );
            if ( javasrc.exists() )
            {
                generator.setJavasrcPath( this.javasrcPath );
            }
        }
        if ( StringUtils.isNotEmpty( this.diagramEncoding ) )
        {
            generator.setDiagramEncoding( this.diagramEncoding );
        }
        if ( StringUtils.isNotEmpty( getDiagramLabel() ) )
        {
            generator.setDiagramLabel( getDiagramLabel() );
        }

        try
        {
            generator.generateUML();
        }
        catch ( DotNotPresentInPathException e )
        {
            throw new MavenReportException( "DotNotPresentInPathException: " + e.getMessage(), e );
        }
        catch ( UmlDocException e )
        {
            throw new MavenReportException( "UmlDocException: " + e.getMessage(), e );
        }
    }

    /**
     * Method that sets the diagram label text that will be displayed on the bottom of the
     * UML class diagram.
     *
     * @return a String that contains the text that will be displayed at the bottom of the class diagram
     */
    private String getDiagramLabel()
    {
        int actualYear = Calendar.getInstance().get( Calendar.YEAR );
        String year = String.valueOf( actualYear );

        String inceptionYear = project.getInceptionYear();

        String theBottom = StringUtils.replace( this.diagramLabel, "{currentYear}", year );

        if ( inceptionYear != null )
        {
            if ( inceptionYear.equals( year ) )
            {
                theBottom = StringUtils.replace( theBottom, "{inceptionYear}-", "" );
            }
            else
            {
                theBottom = StringUtils.replace( theBottom, "{inceptionYear}", inceptionYear );
            }
        }
        else
        {
            theBottom = StringUtils.replace( theBottom, "{inceptionYear}-", "" );
        }

        if ( project.getOrganization() == null )
        {
            theBottom = StringUtils.replace( theBottom, " {organizationName}", "" );
        }
        else
        {
            if ( StringUtils.isNotEmpty( project.getOrganization().getName() ) )
            {
                theBottom = StringUtils.replace( theBottom, "{organizationName}", project.getOrganization().getName() );
            }
            else
            {
                theBottom = StringUtils.replace( theBottom, " {organizationName}", "" );
            }
        }

        return theBottom;
    }

    /**
     * Generate the UML report.
     *
     * @param locale the wanted locale
     * @throws IOException if any
     */
    private void generateReport( Locale locale )
        throws IOException
    {
        getSink().head();
        getSink().text( i18n.getString( "uml-report", locale, "report.name" ) );
        getSink().head_();

        getSink().body();

        getSink().section1();
        getSink().sectionTitle1();
        getSink().text( i18n.getString( "uml-report", locale, "report.name" ) );
        getSink().sectionTitle1_();

        getSink().paragraph();
        getSink().rawText( i18n.getString( "uml-report", locale, "report.general.intro1" ) );
        getSink().paragraph_();
        getSink().paragraph();
        getSink().text( i18n.getString( "uml-report", locale, "report.general.intro2" ) );
        getSink().paragraph_();

        getSink().paragraph();

        String outDirPath = getReportOutputDirectory().getAbsolutePath().replace( "\\", "/" );
        String outPath = this.output.getAbsolutePath().replace( "\\", "/" );
        String relative = PathTool.getRelativePath( outPath, outDirPath );
        String link = "." + PathTool.calculateLink( StringUtils.replace( outPath, outDirPath, "" ), relative );

        if ( !FileUtils.extension( output.getAbsolutePath() ).toLowerCase().equals( "svg" ) )
        {
            String img = "<a href=\"" + link + "\" target=\"_blank\"><img src=\"" + link + "\" title=\""
                + getDiagramLabel() + "\" width=\"800\" title=\"" + diagramLabel + "\"/></a>";
            getSink().rawText( img );
        }
        else
        {
            SVGConverter converter = new SVGConverter();
            converter.setWidth( 800 );
            converter.setSources( new String[] { output.getAbsolutePath() } );
            converter.setDst( new File( output.getParentFile(), "small-" + output.getName() + ".png" ) );
            converter.setDestinationType( DestinationType.PNG );

            // For graphviz <  2.14, see https://mailman.research.att.com/pipermail/graphviz-interest/2007q2/004505.html
            InputStream is = null;
            OutputStream os = null;
            try
            {
                is = new FileInputStream( output );
                String content = IOUtil.toString( is );
                IOUtil.close( is );
                content = StringUtils.replace( content, "font-weight:regular;", "" );

                os = new FileOutputStream( output );
                IOUtil.copy( content, os );
            }
            finally
            {
                IOUtil.close( is );
                IOUtil.close( os );
            }

            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
                Thread.currentThread().setContextClassLoader( UmlReport.class.getClassLoader() );

                converter.execute();
            }
            catch ( SVGConverterException e )
            {
                throw new IOException( "SVGConverterException: " + e.getMessage() );
            }
            finally
            {
                Thread.currentThread().setContextClassLoader( currentClassLoader );
            }

            String smalllink = StringUtils.replace( link, output.getName(), "small-" + output.getName() + ".png" );

            String img = "<a href=\"" + link + "\" target=\"_blank\"><img src=\"" + smalllink + "\" title=\""
                + getDiagramLabel() + "\" width=\"800\" title=\"" + diagramLabel + "\"/></a>";
            getSink().rawText( img );
        }

        getSink().paragraph_();
        getSink().section1_();

        getSink().body_();
        getSink().flush();
        getSink().close();
    }

    /**
     * TODO Comes from maven-project-info-plugin AbstractProjectInfoReport class: MPIR-74
     *
     * @return
     * @throws MojoExecutionException
     */
    private File getSkinArtifactFile()
        throws MojoExecutionException
    {
        Skin skin = Skin.getDefaultSkin();

        String version = skin.getVersion();
        Artifact artifact;
        try
        {
            if ( version == null )
            {
                version = Artifact.RELEASE_VERSION;
            }
            VersionRange versionSpec = VersionRange.createFromVersionSpec( version );
            artifact = factory.createDependencyArtifact( skin.getGroupId(), skin.getArtifactId(), versionSpec, "jar",
                                                         null, null );

            resolver.resolve( artifact, project.getRemoteArtifactRepositories(), localRepository );
        }
        catch ( InvalidVersionSpecificationException e )
        {
            throw new MojoExecutionException( "The skin version '" + version + "' is not valid: " + e.getMessage() );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Unable to find skin", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "The skin does not exist: " + e.getMessage() );
        }

        return artifact.getFile();
    }
}
