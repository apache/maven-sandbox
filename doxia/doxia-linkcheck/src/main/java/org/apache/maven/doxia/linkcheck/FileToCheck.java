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

import org.apache.maven.doxia.linkcheck.validation.LinkValidationItem;
import org.apache.maven.doxia.linkcheck.validation.LinkValidationResult;
import org.apache.maven.doxia.linkcheck.validation.LinkValidatorManager;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A file to be checked.
 *
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @version $Id$
 */
public final class FileToCheck
{
    /** Log for debug output. */
    private static final Log LOG = LogFactory.getLog( FileToCheck.class );

    /** Unknown validation status. Initialized as null. */
    public static final String STATUS_UNKNOWN = null;

    /** Validation status ok. */
    public static final String STATUS_OK = "OK";

    /** The base of this FileToCheck. */
    private String base;

    /** The File object of this FileToCheck. */
    private File fileToCheck;

    /** A list of links found in this FileToCheck. */
    private List links = new LinkedList();

    /** A message. */
    private String message = "";

    /** The status. */
    private String status = STATUS_OK;

    /** successful. */
    private int successful;

    /** unsuccessful. */
    private int unsuccessful;

    /**
     * Returns the message.
     *
     * @return String
     */
    public String getMessage()
    {
        return this.message;
    }

    /**
     * Returns the fileName.
     *
     * @return String
     */
    public String getName()
    {
        String fileName = this.fileToCheck.getAbsolutePath();
        if ( fileName.startsWith( this.base ) )
        {
            fileName = fileName.substring( this.base.length() + 1 );
        }

        fileName = fileName.replace( '\\', '/' );
        return fileName;
    }

    /**
     * Returns the list of results.
     *
     * @return List
     */
    public List getResults()
    {
        return this.links;
    }

    /**
     * Returns the status.
     *
     * @return int
     */
    // TODO: replace by LinkValidationResult status.
    public String getStatus()
    {
        return this.status;
    }

    /**
     * Returns the successful.
     *
     * @return int
     */
    public int getSuccessful()
    {
        return this.successful;
    }

    /**
     * Returns the unsuccessful.
     *
     * @return int
     */
    public int getUnsuccessful()
    {
        return this.unsuccessful;
    }

    /**
     * Constructor: initializes the basedir and fileToCheck.
     *
     * @param baseFile The base file.
     * @param file The file to check.
     */
    public FileToCheck( File baseFile, File file )
    {
        this.base = baseFile.getAbsolutePath();
        this.fileToCheck = file;
    }

    /**
     * Validates this fileToCheck.
     *
     * @param lvm The LinkValidatorManager to use.
     * @throws Exception if something goes wrong.
     */
    public void check( LinkValidatorManager lvm ) throws Exception
    {
        this.successful = 0;

        this.unsuccessful = 0;

        this.status = STATUS_OK;

        this.message = "";

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "Validating " + getName() );
        }

        try
        {
            final Set hrefs;

            try
            {
                hrefs = LinkMatcher.match( this.fileToCheck );
            }
            catch ( Throwable t )
            {
                // We catch Throwable, because there is a chance that the domReader will throw
                // a stack overflow exception for some files

                if ( LOG.isDebugEnabled() )
                {
                    LOG.error( "Received: [" + t + "] in page [" + getName() + "]", t );
                }
                else
                {
                    LOG.error( "Received: [" + t + "] in page [" + getName() + "]" );
                }

                LinkCheckResult lcr = new LinkCheckResult();

                lcr.setStatus( "PARSE FAILURE" );

                lcr.setTarget( "N/A" );

                addResult( lcr );

                return;
            }

            String href;

            LinkCheckResult lcr;

            LinkValidationItem lvi;

            LinkValidationResult result;

            for ( Iterator iter = hrefs.iterator(); iter.hasNext(); )
            {
                href = (String) iter.next();

                lcr = new LinkCheckResult();

                lvi = new LinkValidationItem( this.fileToCheck, href );

                result = lvm.validateLink( lvi );

                lcr.setTarget( href );

                lcr.setErrorMessage( result.getErrorMessage() );

                switch ( result.getStatus() )
                {
                    case LinkValidationResult.VALID:
                        this.successful++;

                        lcr.setStatus( "valid" );

                        addResult( lcr ); // At some point we won't want to store valid links. The tests require that
                        // we do at present

                        break;
                    case LinkValidationResult.ERROR:
                        this.unsuccessful++;

                        lcr.setStatus( "error" );

                        addResult( lcr );

                        break;
                    case LinkValidationResult.WARNING:
                        this.unsuccessful++;

                        lcr.setStatus( "warning" );

                        addResult( lcr );

                        break;
                    case LinkValidationResult.UNKNOWN:
                    default:
                        this.unsuccessful++;

                        lcr.setStatus( "unknown" );

                        addResult( lcr );

                        break;
                }
            }

            href = null;

            lcr = null;

            lvi = null;

            result = null;

        }
        catch ( Exception e )
        {
            LOG.error( this.message );

            throw e;
        }
    }

    /**
     * Returns an XML representation of the current linkcheck result.
     * This is only available after {@link #check(LinkValidatorManager)} has been called.
     *
     * @return the XML linkcheck result as a string.
     */
    public String toXML()
    {
        StringBuffer buf = new StringBuffer();

        buf.append( "  <file>" + LinkCheck.EOL );

        buf.append( "    <name><![CDATA[" + getName() + "]]></name>" + LinkCheck.EOL );

        buf.append( "    <successful>" + getSuccessful() + "</successful>" + LinkCheck.EOL );

        buf.append( "    <unsuccessful>" + getUnsuccessful() + "</unsuccessful>" + LinkCheck.EOL );

        Iterator iter = getResults().iterator();

        LinkCheckResult result;

        while ( iter.hasNext() )
        {
            result = (LinkCheckResult) iter.next();

            buf.append( result.toXML() );
        }

        buf.append( "  </file>" + LinkCheck.EOL );

        return buf.toString();
    }

    /**
     * Adds the given LinkCheckResult to the list of results.
     *
     * @param lcr the LinkCheckResult to add.
     */
    private void addResult( LinkCheckResult lcr )
    {
        this.links.add( lcr );
    }
}
