package org.apache.maven.doxia.linkcheck.validation;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @version $Id$
 */

public class LinkValidatorManager implements Serializable
{
    /** serialVersionUID. */
    private static final long serialVersionUID = 2467928182206500945L;

    /** Log for debug output. */
    private static final Log LOG = LogFactory.getLog( LinkValidatorManager.class );

    /** validators. */
    private List validators = new LinkedList();

    /** excludes. */
    private String[] excludes = new String[0];

    /** cache. */
    private Map cache = new HashMap();

    /**
     * Returns the list of validators.
     *
     * @return List
     */
    public List getValidators()
    {
        return this.validators;
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
     * Sets the excludes.
     *
     * @param excl The excludes to set.
     */
    public void setExcludes( String[] excl )
    {
        this.excludes = excl;
    }

    /**
     * Adds a LinkValidator to this manager.
     *
     * @param lv The LinkValidator to add.
     */
    public void addLinkValidator( LinkValidator lv )
    {
        this.validators.add( lv );
    }

    /**
     * Validates the links of the given LinkValidationItem.
     *
     * @param lvi The LinkValidationItem to validate.
     * @return A LinkValidationResult.
     * @throws Exception if something goes wrong.
     */
    public LinkValidationResult validateLink( LinkValidationItem lvi ) throws Exception
    {
        {
            LinkValidationResult status = getCachedResult( lvi );

            if ( status != null )
            {
                return status;
            }
        }

        for ( int i = 0; i < this.excludes.length; i++ )
        {
            if ( this.excludes[i] != null && lvi.getLink().startsWith( this.excludes[i] ) )
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "Excluded " + lvi.getLink() );
                }

                return new LinkValidationResult( LinkValidationResult.VALID, false, "" );
            }
        }

        Iterator iter = this.validators.iterator();

        LinkValidator lv;

        Object resourceKey;

        LinkValidationResult lvr;

        while ( iter.hasNext() )
        {
            lv = (LinkValidator) iter.next();

            resourceKey = lv.getResourceKey( lvi );

            if ( resourceKey != null )
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( lv.getClass().getName() + " - Checking link " + lvi.getLink() );
                }

                lvr = lv.validateLink( lvi );

                if ( lvr.getStatus() == LinkValidationResult.NOTMINE )
                {
                    continue;
                }

                setCachedResult( resourceKey, lvr );

                return lvr;
            }
        }

        lv = null;

        resourceKey = null;

        lvr = null;

        LOG.error( "Unable to validate link : " + lvi.getLink() );

        return new LinkValidationResult( LinkValidationResult.UNKNOWN, false, "No validator found for this link" );
    }

    /**
     * Loads a cache file.
     *
     * @param cacheFilename The name of the cache file.
     */
    public void loadCache( String cacheFilename )
    {
        try
        {
            File f = new File( cacheFilename );

            if ( f.exists() )
            {
                ObjectInputStream is = new ObjectInputStream( new FileInputStream( cacheFilename ) );

                this.cache = (Map) is.readObject();

                is.close();
            }
        }
        catch ( InvalidClassException e )
        {
            LOG.warn( "Your cache is incompatible with this new release of linkcheck. It will be recreated." );
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to load the cache: " + cacheFilename, t );
        }
    }

    /**
     * Saves a cache file.
     *
     * @param cacheFilename The name of the cache file.
     */
    public void saveCache( String cacheFilename )
    {
        try
        {
            // Remove non-persistent items from cache
            Map persistentCache = new HashMap();

            Iterator iter = this.cache.keySet().iterator();

            Object resourceKey;

            while ( iter.hasNext() )
            {
                resourceKey = iter.next();

                if ( ( (LinkValidationResult) this.cache.get( resourceKey ) ).isPersistent() )
                {
                    persistentCache.put( resourceKey, this.cache.get( resourceKey ) );

                    if ( LOG.isDebugEnabled() )
                    {
                        LOG.debug( "[" + resourceKey + "] with result [" + this.cache.get( resourceKey )
                                        + "] is stored in the cache." );
                    }
                }
            }

            File cacheFile = new File( cacheFilename );

            File dir = cacheFile.getParentFile();

            if ( dir != null )
            {
                dir.mkdirs();
            }

            ObjectOutputStream os = new ObjectOutputStream( new FileOutputStream( cacheFilename ) );

            os.writeObject( persistentCache );

            os.close();

            persistentCache = null;

            iter = null;

            resourceKey = null;

            cacheFile = null;

            dir = null;

            os = null;
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to save the cache: " + cacheFilename, t );
        }
    }

    /**
     * Returns a LinkValidationResult for the given LinkValidationItem
     * if it has been cached from a previous run, returns null otherwise.
     *
     * @param lvi The LinkValidationItem.
     * @return LinkValidationResult
     */
    public LinkValidationResult getCachedResult( LinkValidationItem lvi )
    {
        Iterator iter = getValidators().iterator();

        LinkValidator lv;

        Object resourceKey;

        while ( iter.hasNext() )
        {
            lv = (LinkValidator) iter.next();

            resourceKey = lv.getResourceKey( lvi );

            if ( resourceKey != null && this.cache.containsKey( resourceKey ) )
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "The cache returns for [" + resourceKey + "] the result ["
                                            + this.cache.get( resourceKey ) + "]." );
                }

                return (LinkValidationResult) this.cache.get( resourceKey );
            }
        }

        lv = null;

        resourceKey = null;

        return null;
    }

    /**
     * Puts the given LinkValidationResult into the cache.
     *
     * @param resourceKey The key to retrieve the result.
     * @param lvr the LinkValidationResult to cache.
     */
    public void setCachedResult( Object resourceKey, LinkValidationResult lvr )
    {
        this.cache.put( resourceKey, lvr );
    }
}
