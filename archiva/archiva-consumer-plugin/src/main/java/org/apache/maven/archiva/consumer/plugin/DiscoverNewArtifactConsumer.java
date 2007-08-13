package org.apache.maven.archiva.consumer.plugin;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.archiva.consumers.AbstractMonitoredConsumer;
import org.apache.maven.archiva.consumers.KnownRepositoryContentConsumer;
import org.apache.maven.archiva.consumers.ConsumerException;
import org.apache.maven.archiva.configuration.ArchivaConfiguration;
import org.apache.maven.archiva.configuration.FileTypes;
import org.apache.maven.archiva.repository.layout.BidirectionalRepositoryLayoutFactory;
import org.apache.maven.archiva.repository.layout.BidirectionalRepositoryLayout;
import org.apache.maven.archiva.repository.layout.LayoutException;
import org.apache.maven.archiva.model.ArchivaRepository;
import org.apache.maven.archiva.model.ArchivaArtifact;
import org.apache.maven.archiva.indexer.search.CrossRepositorySearch;
import org.apache.maven.archiva.indexer.search.SearchResults;
import org.apache.maven.archiva.indexer.search.SearchResultLimits;
import org.apache.maven.archiva.indexer.search.SearchResultHit;
import org.codehaus.plexus.registry.RegistryListener;
import org.codehaus.plexus.registry.Registry;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author <a href="mailto:oching@apache.org">Maria Odea Ching</a>
 * @plexus.component role="org.apache.maven.archiva.consumers.KnownRepositoryContentConsumer"
 * role-hint="discover-new-artifact"
 * instantiation-strategy="per-lookup"
 */
public class DiscoverNewArtifactConsumer
    extends AbstractMonitoredConsumer
    implements KnownRepositoryContentConsumer, RegistryListener, Initializable
{
    /**
     * @plexus.configuration default-value="discover-new-artifact"
     */
    private String id;

    /**
     * @plexus.configuration default-value="Discover new artifacts in the repository."
     */
    private String description;

    /**
     * @plexus.requirement
     */
    private FileTypes filetypes;

    /**
     * @plexus.requirement role-hint="default"
     */
    private CrossRepositorySearch repoSearch;

    /**
     * @plexus.requirement
     */
    private BidirectionalRepositoryLayoutFactory layoutFactory;

    /**
     * @plexus.requirement
     */
    private ArchivaConfiguration configuration;

    private List propertyNameTriggers = new ArrayList();

    private List includes = new ArrayList();

    private ArchivaRepository repository;

    private List newArtifacts = new ArrayList();

    private BidirectionalRepositoryLayout repositoryLayout;

    public String getId()
    {
        return this.id;
    }

    public String getDescription()
    {
        return this.description;
    }

    public boolean isPermanent()
    {
        return false;
    }

    public List getExcludes()
    {
        return null;
    }

    public List getIncludes()
    {
        return this.includes;
    }

    public void beginScan( ArchivaRepository repository )
        throws ConsumerException
    {
        if ( !repository.isManaged() )
        {
            throw new ConsumerException( "Consumer requires managed repository." );
        }

        this.repository = repository;

        try
        {
            this.repositoryLayout = layoutFactory.getLayout( this.repository.getLayoutType() );
        }
        catch ( LayoutException e )
        {
            throw new ConsumerException(
                "Unable to initialize consumer due to unknown repository layout: " + e.getMessage(), e );
        }
    }

    public void processFile( String path )
        throws ConsumerException
    {
        // @todo needs to be tested!
        SearchResults results =
            repoSearch.searchForTerm( repository.getId() + "/" + path, new SearchResultLimits( 0 ) );
        List hits = results.getHits();
        boolean found = false;
        for ( Iterator iter = hits.iterator(); iter.hasNext(); )
        {
            SearchResultHit hit = (SearchResultHit) iter.next();
            if ( ( repository.getId() + "/" + path ).equalsIgnoreCase( hit.getUrl() ) )
            {
                found = true;
                break;
            }
        }

        if ( found )
        {
            try
            {
                ArchivaArtifact artifact = this.repositoryLayout.toArtifact( path );
                newArtifacts.add( artifact );
            }
            catch ( LayoutException e )
            {
                // Not an artifact.
            }
        }
    }

    public void completeScan()
    {
        // @todo dump into file
        for ( Iterator iter = newArtifacts.iterator(); iter.hasNext(); )
        {
            ArchivaArtifact artifact = (ArchivaArtifact) iter.next();
            //System.out.println( "\n %%%%%% NEW ARTIFACT == " + artifact.getGroupId() + ":" +
            //  artifact.getArtifactId() + ":" + artifact.getVersion() + ":" + artifact.getType() );
        }

        /* do nothing */
    }

    public void afterConfigurationChange( Registry registry, String propertyName, Object propertyValue )
    {
        if ( propertyNameTriggers.contains( propertyName ) )
        {
            initIncludes();
        }
    }

    public void beforeConfigurationChange( Registry registry, String propertyName, Object propertyValue )
    {
        /* do nothing */
    }

    private void initIncludes()
    {
        includes.clear();

        includes.addAll( filetypes.getFileTypePatterns( FileTypes.INDEXABLE_CONTENT ) );
    }

    public void initialize()
        throws InitializationException
    {
        propertyNameTriggers = new ArrayList();
        propertyNameTriggers.add( "repositoryScanning" );
        propertyNameTriggers.add( "fileTypes" );
        propertyNameTriggers.add( "fileType" );
        propertyNameTriggers.add( "patterns" );
        propertyNameTriggers.add( "pattern" );

        configuration.addChangeListener( this );

        initIncludes();
    }

}
