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

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.archiva.configuration.ManagedRepositoryConfiguration;
import org.apache.maven.archiva.consumers.ConsumerException;
import org.apache.maven.archiva.consumers.KnownRepositoryContentConsumer;
import org.apache.maven.archiva.indexer.filecontent.FileContentRecord;
import org.apache.maven.archiva.indexer.search.CrossRepositorySearch;
import org.apache.maven.archiva.indexer.search.SearchResults;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * @author <a href="mailto:sgargan@qualcomm.com">Stephen Gargan</a>
 */
public class DiscoverNewArtifactConsumerTest
    extends PlexusTestCase
{
    private DiscoverNewArtifactConsumer consumer;

    private MockRepositorySearch mockSearch;

    private File repoDir;

    private File dumpFile;

    private ManagedRepositoryConfiguration testRepository;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        String consumerRole = KnownRepositoryContentConsumer.class.getName();
        String searchRole = CrossRepositorySearch.class.getName();
        consumer = (DiscoverNewArtifactConsumer) lookup( consumerRole, "discover-new-artifact" );
        mockSearch = (MockRepositorySearch) lookup( searchRole, "mockSearch" );
        
        setUpMockRepository();

        dumpFile = new File( repoDir, DiscoverNewArtifactConsumer.DUMP_FILE_NAME );
        dumpFile.delete();
    }

    private void setUpMockRepository()
    {
        repoDir = new java.io.File( getBasedir(), "/target/test-consumer-repo" );
        repoDir.mkdirs();
        testRepository = new ManagedRepositoryConfiguration();
        testRepository.setName( "Test-Consumer-Repository" );
        testRepository.setId( "test-consumer-repository" );
        testRepository.setLocation( repoDir.getAbsolutePath() );
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
        dumpFile.delete();
    }

    public void testBeginScan()
        throws Exception
    {
        doTestBeginScan();
    }

    public void testScanFindsNewContent()
        throws Exception
    {
        doTestBeginScan();

        String artifactPath = "org/simple/test/testartifact/testartifact/1.0/testartifact-1.0.pom";
        consumer.processFile( artifactPath );
        mockSearch.verify( "guest", Collections.singletonList( testRepository.getId() ), artifactPath );
        verifyContentDumped( new String[] { artifactPath } );

        // process another path
        consumer.processFile( artifactPath );
        mockSearch.verify( "guest", Collections.singletonList( testRepository.getId() ), artifactPath );
        verifyContentDumped( new String[] { artifactPath, artifactPath } );
    }

    public void testScanFindsNoNewContent()
        throws Exception
    {
        doTestBeginScan();

        String artifactPath = "org/simple/test/testartifact/testartifact/1.0/testartifact-1.0.pom";

        // setup the mockSearch to find the artifact
        mockSearch.setSearchResults( createSearchResults( artifactPath ) );
        consumer.processFile( artifactPath );
        mockSearch.verify( "guest", Collections.singletonList( testRepository.getId() ), artifactPath );
        verifyContentDumped( new String[] {} );

    }

    public SearchResults createSearchResults( String artifactPath )
    {
        FileContentRecord fch = new FileContentRecord();
        fch.setFilename( artifactPath );
        fch.setRepositoryId( testRepository.getId() );
        SearchResults results = new SearchResults();
        results.addFileContentHit( fch );

        return results;
    }

    private void verifyContentDumped( String[] artifactPaths )
        throws Exception
    {
        List newArtifacts = IOUtils.readLines( new FileInputStream( dumpFile ) );
        assertEquals( artifactPaths.length, newArtifacts.size() );

        for ( int x = 0; x < newArtifacts.size(); x++ )
        {
            assertEquals( testRepository.getId() + "/" + artifactPaths[x], newArtifacts.get( x ) );
        }
    }

    private void doTestBeginScan()
        throws ConsumerException
    {
        assertFalse( dumpFile.exists() );
        consumer.beginScan( testRepository );
        assertTrue( dumpFile.exists() );
    }

}
