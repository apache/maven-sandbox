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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.maven.doxia.linkcheck.validation.FileLinkValidator;
import org.apache.maven.doxia.linkcheck.validation.LinkValidatorManager;
import org.apache.maven.doxia.linkcheck.validation.MailtoLinkValidator;
import org.apache.maven.doxia.linkcheck.validation.OfflineHTTPLinkValidator;
import org.apache.maven.doxia.linkcheck.validation.OnlineHTTPLinkValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The main bean to be called whenever a set of documents should have their links checked.
 * 
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @version $Id$
 */
public final class LinkCheck
{
    /** Log. */
    private static final Log LOG = LogFactory.getLog( LinkCheck.class );

    /** The vm line separator. */
    public static final String EOL = System.getProperty( "line.separator" );

    /** FilenameFilter. */
    private static final FilenameFilter CUSTOM_FF = new LinkCheck.CustomFilenameFilter();

    /** One MegaByte. */
    private static final long MEG = 1024 * 1024;

    /** basedir. */
    private File basedir;

    /** cache. */
    private String cache;

    /** excludes. */
    private String[] excludes = null;

    /** method. */
    private String method;

    /** filesToCheck. */
    private List filesToCheck = null;

    /** LinkValidatorManager. */
    private LinkValidatorManager lvm = null;

    /** online mode. */
    private boolean online;

    /** Output file for xml document. */
    private File output;

    /** Output encoding for the xml document. */
    private String outputEncoding;

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
     * Get the base directory for the change log generator.
     *
     * @return the base directory
     */
    public File getBasedir()
    {
        return this.basedir;
    }

    /**
     * Set the base directory for the change log generator.
     *
     * @param base the base directory
     */
    public void setBasedir( File base )
    {
        this.basedir = base;
    }

    /**
     * Returns the cacheFile.
     *
     * @return String
     */
    public String getCache()
    {
        return this.cache;
    }

    /**
     * Sets the cacheFile.
     *
     * @param cacheFile The cacheFile to set
     */
    public void setCache( String cacheFile )
    {
        this.cache = cacheFile;
    }

    /**
     * Returns the excludes.
     *
     * @return String[]
     */
    public String[] getExcludes()
    {
        return this.excludes;
    }

    /**
     * Sets the excludes, a String[] with exclude locations.
     *
     * @param excl The excludes to set
     */
    public void setExcludes( String[] excl )
    {
        this.excludes = excl;
    }

    /**
     * The http method to use.
     *
     * @return the method
     */
    public String getMethod()
    {
        return this.method;
    }

    /**
     * The http method to use. Currently supported are "get" and "head".
     * 
     * @param meth the method to set.
     */
    public void setMethod( String meth )
    {
        this.method = meth;
    }


    /**
     * Returns a list of {@link org.apache.maven.doxia.linkcheck.FileToCheck files} that have been checked.
     * This is only available after {@link #doExecute()} has been called.
     *
     * @return the list of files.
     */
    public List getFiles()
    {
        return this.filesToCheck;
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

        if ( this.excludes != null )
        {
            this.lvm.setExcludes( excludes );
        }

        this.lvm.addLinkValidator( new FileLinkValidator() );

        if ( isOnline() )
        {
            this.lvm.addLinkValidator( new OnlineHTTPLinkValidator() );
        }
        else
        {
            this.lvm.addLinkValidator( new OfflineHTTPLinkValidator() );
        }

        this.lvm.addLinkValidator( new MailtoLinkValidator() );
    }

    /**
     * Set the output file for the log.
     *
     * @param file the output file.
     */
    public void setOutput( File file )
    {
        this.output = file;
    }

    /**
     * Returns the outputEncoding.
     *
     * @return String
     */
    public String getOutputEncoding()
    {
        return this.outputEncoding;
    }

    /**
     * Sets the outputEncoding.
     *
     * @param encoding The outputEncoding to set.
     */
    public void setOutputEncoding( String encoding )
    {
        this.outputEncoding = encoding;
    }

    /**
     * Recurses through the given base directory and adds
     * files to the given list that pass through the current filter.
     *
     * @param allFiles the list to fill
     * @param base the base directory to traverse.
     */
    public void findFiles( List allFiles, File base )
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
                    findFiles( allFiles, file );
                }
                else
                {
                    if ( LOG.isDebugEnabled() )
                    {
                        LOG.debug( " File - " + file );
                    }

                    allFiles.add( new FileToCheck( this.basedir, file ) );

                    if ( allFiles.size() % 100 == 0 )
                    {
                        LOG.info( "Found " + allFiles.size() + " files so far." );
                    }
                }
            }

            file = null;
        }

        f = null;
    }

    /**
     * Execute task.
     *
     * @throws IOException if there are problems
     */
    public void doExecute() throws IOException
    {
        if ( this.output == null )
        {
            throw new NullPointerException( "output must be set" );
        }

        if ( LOG.isDebugEnabled() )
        {
            displayMemoryConsumption();
        }

        LinkValidatorManager validator = getLinkValidatorManager();

        this.filesToCheck = new LinkedList();

        validator.loadCache( this.cache );

        List files = new LinkedList();

        LOG.debug( "Locating all files to be checked..." );

        findFiles( files, this.basedir );

        LOG.debug( "Located all files to be checked." );

        LOG.info( "Found " + files.size() + " files to check." );

        displayMemoryConsumption();

        LOG.info( "Begin to check links in files..." );

        Iterator fileIter = files.iterator();

        FileToCheck flc;

        while ( fileIter.hasNext() )
        {
            flc = (FileToCheck) fileIter.next();

            try
            {
                this.filesToCheck.add( flc );
                flc.check( validator );
            }
            catch ( Exception e )
            {
                LOG.error( "Error while checking : " + flc.getName(), e );
            }
        }

        flc = null;

        LOG.info( "Links checked." );

        displayMemoryConsumption();

        createDocument();

        validator.saveCache( this.cache );

        displayMemoryConsumption();
    }

    /**
     * Returns an XML representation of the current linkcheck result.
     *
     * @return the XML linkcheck result as a string.
     */
    public String toXML()
    {
        StringBuffer buf = new StringBuffer();

        buf.append( "<linkcheck>" + EOL );

        FileToCheck ftc;

        for ( Iterator iter = getFiles().iterator(); iter.hasNext(); )
        {
            ftc = (FileToCheck) iter.next();
            buf.append( ftc.toXML() );
        }

        ftc = null;

        buf.append( "</linkcheck>" + EOL );

        return buf.toString();
    }

    /**
     * Writes some memory data to the log (if debug enabled).
     */
    private void displayMemoryConsumption()
    {
        if ( LOG.isDebugEnabled() )
        {
            Runtime r = Runtime.getRuntime();
            LOG.debug( "Memory: " + ( r.totalMemory() - r.freeMemory() ) / MEG + "M/" + r.totalMemory() / MEG
                            + "M" );
        }
    }

    /**
     * Create the XML document from the currently available details.
     *
     * @throws FileNotFoundException
     *             when the output file previously provided does not exist.
     * @throws UnsupportedEncodingException
     *             when the platform doesn't support the current encoding.
     */
    private void createDocument() throws FileNotFoundException, UnsupportedEncodingException

    {
        File dir = this.output.getParentFile();

        if ( dir != null )
        {
            dir.mkdirs();
        }

        PrintWriter out;

        String encoding = getOutputEncoding();

        if ( encoding == null )
        {
            OutputStreamWriter osw = new OutputStreamWriter( new FileOutputStream( this.output ) );
            out = new PrintWriter( osw );
            encoding = osw.getEncoding();
        }
        else
        {
            out = new PrintWriter( new OutputStreamWriter( new FileOutputStream( this.output ), encoding ) );
        }

        StringBuffer buffer = new StringBuffer();

        buffer.append( "<?xml version=\"1.0\" encoding=\"" ).append( encoding ).append( "\" ?>" + EOL );

        out.write( buffer.toString() );

        out.write( toXML() );

        out.close();

        out = null;

        buffer = null;

        dir = null;
    }

    /** Custom FilenameFilter used to search html files */
    static class CustomFilenameFilter implements FilenameFilter
    {
        /** {@inheritDoc} */
        public boolean accept( File dir, String name )
        {
            File n = new File( dir, name );

            if ( n.isDirectory() )
            {
                return true;
            }

            if ( name.endsWith( ".html" ) || name.endsWith( ".htm" ) )
            {
                return true;
            }

            return false;
        }
    }

}
