package org.apache.maven.doxia.linkcheck;

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
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.doxia.linkcheck.model.LinkcheckFile;
import org.apache.maven.doxia.linkcheck.model.LinkcheckFileResult;
import org.apache.maven.doxia.linkcheck.model.LinkcheckModel;
import org.apache.maven.doxia.linkcheck.model.io.xpp3.LinkcheckModelXpp3Writer;
import org.apache.maven.doxia.linkcheck.validation.FileLinkValidator;
import org.apache.maven.doxia.linkcheck.validation.LinkValidationItem;
import org.apache.maven.doxia.linkcheck.validation.LinkValidationResult;
import org.apache.maven.doxia.linkcheck.validation.LinkValidatorManager;
import org.apache.maven.doxia.linkcheck.validation.MailtoLinkValidator;
import org.apache.maven.doxia.linkcheck.validation.OfflineHTTPLinkValidator;
import org.apache.maven.doxia.linkcheck.validation.OnlineHTTPLinkValidator;
import org.codehaus.plexus.util.IOUtil;

/**
 * The main bean to be called whenever a set of documents should have their links checked.
 *
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public final class LinkCheck
{
    /** Log. */
    private static final Log LOG = LogFactory.getLog( LinkCheck.class );

    /** FilenameFilter. */
    private static final FilenameFilter CUSTOM_FF = new LinkCheck.CustomFilenameFilter();

    /** One MegaByte. */
    private static final long MEG = 1024 * 1024;

    /** The basedir to check. */
    private File basedir;

    /** Linkcheck Cache. */
    private File linkCheckCache;

    /** excluded links. */
    private String[] excludedLinks = null;

    /** HTTP method to use. Currently supported are "get" and "head". */
    private String httpMethod;

    /** Internal LinkValidatorManager. */
    private LinkValidatorManager lvm = null;

    /** Online mode. */
    private boolean online;

    /** Report output file for xml document. */
    private File reportOutput;

    /** Report output encoding for the xml document, UTF-8 by default. */
    private String reportOutputEncoding = "UTF-8";

    /** The base URL for links that start with '/'. */
    private String baseURL;

    /** The level to report, used in toXML(). */
    private int reportLevel = LinkcheckFileResult.WARNING_LEVEL;

    /** The linkcheck model */
    private LinkcheckModel model = new LinkcheckModel();

    /**
     * The current report level. Defaults to LinkCheckResult.WARNING.
     *
     * @return int
     */
    public int getReportLevel()
    {
        return this.reportLevel;
    }

    /**
     * Set the report level.
     *
     * @param level the level to set.
     */
    public void setReportLevel( int level )
    {
        this.reportLevel = level;
    }

    /**
     * Whether links are checked in online mode.
     *
     * @return online
     */
    public boolean isOnline()
    {
        return this.online;
    }

    /**
     * Set the online mode.
     *
     * @param onLine online mode.
     */
    public void setOnline( boolean onLine )
    {
        this.online = onLine;
    }

    /**
     * Get the base directory for the files to be linkchecked.
     *
     * @return the base directory
     */
    public File getBasedir()
    {
        return this.basedir;
    }

    /**
     * Set the base directory for the files to be linkchecked.
     *
     * @param base the base directory
     */
    public void setBasedir( File base )
    {
        this.basedir = base;
    }

    /**
     * Returns the cache File.
     *
     * @return File
     */
    public File getLinkCheckCache()
    {
        return this.linkCheckCache;
    }

    /**
     * Sets the cache File.
     *
     * @param cacheFile The cacheFile to set. Set this to null to ignore storing the cache.
     */
    public void setLinkCheckCache( File cacheFile )
    {
        this.linkCheckCache = cacheFile;
    }

    /**
     * Returns the excluded links.
     *
     * @return String[]
     */
    public String[] getExcludedLinks()
    {
        return this.excludedLinks;
    }

    /**
     * Sets the excluded links, a String[] with excluded locations.
     *
     * @param excl The excludes to set
     */
    public void setExcludedLinks( String[] excl )
    {
        this.excludedLinks = excl;
    }

    /**
     * The HTTP method to use. Currently supported are "GET" and "HEAD".
     * <dl>
     * <dt>HTTP GET</dt>
     * <dd>
     * The HTTP GET method is defined in section 9.3 of
     * <a HREF="http://www.ietf.org/rfc/rfc2616.txt">RFC2616</a>:
     * <blockquote>
     * The GET method means retrieve whatever information (in the form of an
     * entity) is identified by the Request-URI.
     * </blockquote>
     * </dd>
     * <dt>HTTP HEAD</dt>
     * <dd>
     * The HTTP HEAD method is defined in section 9.4 of
     * <a HREF="http://www.ietf.org/rfc/rfc2616.txt">RFC2616</a>:
     * <blockquote>
     * The HEAD method is identical to GET except that the server MUST NOT
     * return a message-body in the response.
     * </blockquote>
     * </dd>
     * </dl>
     *
     * @return the method
     */
    public String getHttpMethod()
    {
        return this.httpMethod;
    }

    /**
     * The HTTP method to use. Currently supported are "GET" and "HEAD".
     * <dl>
     * <dt>HTTP GET</dt>
     * <dd>
     * The HTTP GET method is defined in section 9.3 of
     * <a HREF="http://www.ietf.org/rfc/rfc2616.txt">RFC2616</a>:
     * <blockquote>
     * The GET method means retrieve whatever information (in the form of an
     * entity) is identified by the Request-URI.
     * </blockquote>
     * </dd>
     * <dt>HTTP HEAD</dt>
     * <dd>
     * The HTTP HEAD method is defined in section 9.4 of
     * <a HREF="http://www.ietf.org/rfc/rfc2616.txt">RFC2616</a>:
     * <blockquote>
     * The HEAD method is identical to GET except that the server MUST NOT
     * return a message-body in the response.
     * </blockquote>
     * </dd>
     * </dl>
     *
     * @param meth the method to set.
     */
    public void setHttpMethod( String meth )
    {
        this.httpMethod = meth;
    }

    /**
     * Sets the LinkValidatorManager.
     *
     * @param validator the LinkValidatorManager to set
     */
    public void setLinkValidatorManager( LinkValidatorManager validator )
    {
        this.lvm = validator;
    }

    /**
     * Returns the LinkValidatorManager.
     * If this hasn't been set before with {@link #setLinkValidatorManager(LinkValidatorManager)}
     * a default LinkValidatorManager will be returned.
     *
     * @return the LinkValidatorManager
     */
    public LinkValidatorManager getLinkValidatorManager()
    {
        if ( this.lvm == null )
        {
            initDefaultLinkValidatorManager();
        }

        return this.lvm;
    }

    /**
     * Intializes the current LinkValidatorManager to a default value.
     */
    private void initDefaultLinkValidatorManager()
    {
        this.lvm = new LinkValidatorManager();

        if ( this.excludedLinks != null )
        {
            this.lvm.setExcludes( excludedLinks );
        }

        this.lvm.addLinkValidator( new FileLinkValidator() );

        if ( isOnline() )
        {
            OnlineHTTPLinkValidator olv = new OnlineHTTPLinkValidator();

            if ( this.baseURL != null )
            {
                olv.setBaseURL( baseURL );
            }

            this.lvm.addLinkValidator( olv );
        }
        else
        {
            this.lvm.addLinkValidator( new OfflineHTTPLinkValidator() );
        }

        this.lvm.addLinkValidator( new MailtoLinkValidator() );
    }

    /**
     * Set the output file for the results.
     * If this is null, no output will be written.
     *
     * @param file the output file.
     */
    public void setReportOutput( File file )
    {
        this.reportOutput = file;
    }

    /**
     * Returns the output file.
     *
     * @return File
     */
    public File getReportOutput()
    {
        return this.reportOutput;
    }

    /**
     * Returns the outputEncoding.
     *
     * @return String
     */
    public String getReportOutputEncoding()
    {
        return this.reportOutputEncoding;
    }

    /**
     * Sets the outputEncoding.
     *
     * @param encoding The outputEncoding to set.
     */
    public void setReportOutputEncoding( String encoding )
    {
        this.reportOutputEncoding = encoding;
    }

    /**
     * The base URL.
     *
     * @return the base URL.
     */
    public String getBaseURL()
    {
        return this.baseURL;
    }

    /**
     * Sets the base URL. This is pre-pended to links that start with '/'.
     *
     * @param url the base URL.
     */
    public void setBaseURL( String url )
    {
        this.baseURL = url;
    }

    /**
     * The model.
     *
     * @return the model.
     */
    public LinkcheckModel getModel()
    {
        return model;
    }

    /**
     * Recurses through the given base directory and adds/checks
     * files to the model that pass through the current filter.
     *
     * @param base the base directory to traverse.
     */
    private void findAndCheckFiles( File base )
    {
        File[] f = base.listFiles( CUSTOM_FF );

        if ( f != null )
        {
            File file;
            for ( int i = 0; i < f.length; i++ )
            {
                file = f[i];

                if ( file.isDirectory() )
                {
                    findAndCheckFiles( file );
                }
                else
                {
                    if ( LOG.isDebugEnabled() )
                    {
                        LOG.debug( " File - " + file );
                    }

                    String fileRelativePath = file.getAbsolutePath();
                    if ( fileRelativePath.startsWith( this.basedir.getAbsolutePath() ) )
                    {
                        fileRelativePath = fileRelativePath.substring( this.basedir.getAbsolutePath().length() + 1 );
                    }
                    fileRelativePath = fileRelativePath.replace( '\\', '/' );

                    LinkcheckFile linkcheckFile = new LinkcheckFile();
                    linkcheckFile.setAbsolutePath( file.getAbsolutePath() );
                    linkcheckFile.setRelativePath( fileRelativePath );

                    check( linkcheckFile );

                    model.addFile( linkcheckFile );

                    if ( model.getFiles().size() % 100 == 0 )
                    {
                        LOG.info( "Found " + model.getFiles().size() + " files so far." );
                    }
                }
            }

            file = null;
        }

        f = null;
    }

    /**
     * Validates a linkcheck file.
     *
     * @param linkcheckFile the linkcheckFile object to validate
     */
    private void check( LinkcheckFile linkcheckFile )
    {
        linkcheckFile.setSuccessful( 0 );

        linkcheckFile.setUnsuccessful( 0 );

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "Validating " + linkcheckFile.getRelativePath() );
        }

        final Set hrefs;

        try
        {
            hrefs = LinkMatcher.match( new File( linkcheckFile.getAbsolutePath() ) );
        }
        catch ( Throwable t )
        {
            // We catch Throwable, because there is a chance that the domReader will throw
            // a stack overflow exception for some files

            if ( LOG.isDebugEnabled() )
            {
                LOG.error( "Received: [" + t + "] in page [" + linkcheckFile.getRelativePath() + "]", t );
            }
            else
            {
                LOG.error( "Received: [" + t + "] in page [" + linkcheckFile.getRelativePath() + "]" );
            }

            LinkcheckFileResult lcr = new LinkcheckFileResult();

            lcr.setStatus( "PARSE FAILURE" );

            lcr.setTarget( "N/A" );

            linkcheckFile.addResult( lcr );

            return;
        }

        String href;
        LinkcheckFileResult lcr;
        LinkValidationItem lvi;
        LinkValidationResult result;

        for ( Iterator iter = hrefs.iterator(); iter.hasNext(); )
        {
            href = (String) iter.next();

            lcr = new LinkcheckFileResult();
            lvi = new LinkValidationItem( new File( linkcheckFile.getAbsolutePath() ), href );
            result = lvm.validateLink( lvi );
            lcr.setTarget( href );
            lcr.setErrorMessage( result.getErrorMessage() );

            switch ( result.getStatus() )
            {
                case LinkcheckFileResult.VALID_LEVEL:
                    linkcheckFile.setSuccessful( linkcheckFile.getSuccessful() + 1 );

                    lcr.setStatus( LinkcheckFileResult.VALID );

                    // At some point we won't want to store valid links. The tests require that we do at present.
                    linkcheckFile.addResult( lcr );

                    break;
                case LinkcheckFileResult.ERROR_LEVEL:
                    linkcheckFile.setUnsuccessful( linkcheckFile.getUnsuccessful() + 1 );

                    lcr.setStatus( LinkcheckFileResult.ERROR );

                    linkcheckFile.addResult( lcr );

                    break;
                case LinkcheckFileResult.WARNING_LEVEL:
                    linkcheckFile.setUnsuccessful( linkcheckFile.getUnsuccessful() + 1 );

                    lcr.setStatus( LinkcheckFileResult.WARNING );

                    linkcheckFile.addResult( lcr );

                    break;
                case LinkcheckFileResult.UNKNOWN_LEVEL:
                default:
                    linkcheckFile.setUnsuccessful( linkcheckFile.getUnsuccessful() + 1 );

                    lcr.setStatus( LinkcheckFileResult.UNKNOWN );

                    linkcheckFile.addResult( lcr );

                    break;
            }
        }

        href = null;
        lcr = null;
        lvi = null;
        result = null;
    }

    /**
     * Execute task.
     */
    public void doExecute()
    {
        if ( this.basedir == null )
        {
            LOG.error( "No base directory specified!" );

            throw new NullPointerException( "The basedir can't be null!" );
        }

        if ( this.reportOutput == null )
        {
            LOG.warn( "No output file specified! Results will not be written!" );
        }

        model = new LinkcheckModel();
        model.setModelEncoding( reportOutputEncoding );
        model.setFiles( new LinkedList() );

        displayMemoryConsumption();

        LinkValidatorManager validator = getLinkValidatorManager();
        validator.loadCache( this.linkCheckCache );

        displayMemoryConsumption();

        LOG.info( "Begin to check links in files..." );

        findAndCheckFiles( this.basedir );

        LOG.info( "Links checked." );

        displayMemoryConsumption();

        try
        {
            createDocument();
        }
        catch ( IOException e )
        {
            LOG.error( "Could not write to output file, results will be lost!", e );
        }

        validator.saveCache( this.linkCheckCache );

        displayMemoryConsumption();
    }

    /**
     * Writes some memory data to the log (if debug enabled).
     */
    private void displayMemoryConsumption()
    {
        if ( LOG.isDebugEnabled() )
        {
            Runtime r = Runtime.getRuntime();
            LOG.debug( "Memory: " + ( r.totalMemory() - r.freeMemory() ) / MEG + "M/" + r.totalMemory() / MEG + "M" );
        }
    }

    /**
     * Create the XML document from the currently available details.
     *
     * @throws IOException if any
     */
    private void createDocument()
        throws IOException
    {
        if ( this.reportOutput == null )
        {
            return;
        }

        File dir = this.reportOutput.getParentFile();
        if ( dir != null )
        {
            dir.mkdirs();
        }

        FileWriter writer = null;
        LinkcheckModelXpp3Writer xpp3Writer = new LinkcheckModelXpp3Writer();
        try
        {
            writer = new FileWriter( this.reportOutput );
            xpp3Writer.write( writer, getModel() );
        }
        finally
        {
            IOUtil.close( writer );
        }

        dir = null;
    }

    /** Custom FilenameFilter used to search html files */
    static class CustomFilenameFilter
        implements FilenameFilter
    {
        /** {@inheritDoc} */
        public boolean accept( File dir, String name )
        {
            File n = new File( dir, name );

            if ( n.isDirectory() )
            {
                return true;
            }

            if ( name.toLowerCase().endsWith( ".html" ) || name.toLowerCase().endsWith( ".htm" ) )
            {
                return true;
            }

            return false;
        }
    }
}
