package org.example.consumer;

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

import org.apache.maven.archiva.configuration.ManagedRepositoryConfiguration;
import org.apache.maven.archiva.consumers.KnownRepositoryContentConsumer;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;
import org.easymock.EasyMock;

/**
 * <code>SimpleArtifactConsumerTest</code>
 */
public class SimpleArtifactConsumerTest
    extends PlexusTestCase
{
    private SimpleArtifactConsumer consumer;

    private File repoDir;

    private ManagedRepositoryConfiguration testRepository;

    private Logger mockLogger;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        String consumerRole = KnownRepositoryContentConsumer.class.getName();
        consumer = (SimpleArtifactConsumer) lookup( consumerRole, "simple-artifact-consumer" );

        setUpMockRepository();
        setupMockLogger();
    }

    private void setupMockLogger()
    {       
        mockLogger = (Logger) EasyMock.createNiceMock(Logger.class); 
        consumer.enableLogging( mockLogger );
    }

    private void setUpMockRepository()
    {
        repoDir = new java.io.File( getBasedir(), "/target/test-consumer-repo" );
        repoDir.mkdirs();
        repoDir.deleteOnExit();

        testRepository = new ManagedRepositoryConfiguration();
        testRepository.setName( "Test-Consumer-Repository" );
        testRepository.setId( "test-consumer-repository" );
        testRepository.setLocation( repoDir.getAbsolutePath() );
    }

    public void testBeginScan()
        throws Exception
    {
        mockLogger.info( "Beginning scan of repository [test-consumer-repository]" );
        EasyMock.expectLastCall();
        EasyMock.replay(mockLogger);

        consumer.beginScan( testRepository );

        EasyMock.verify();
    }

    public void testProcessFile()
        throws Exception
    {
        mockLogger.info( "Beginning scan of repository [test-consumer-repository]" );
        EasyMock.expectLastCall();
        mockLogger.info( "Processing entry [org/simple/test/testartifact/testartifact/1.0/testartifact-1.0.pom]"
            + " from repository [test-consumer-repository]" );
        EasyMock.expectLastCall();
        mockLogger.info( "Processing entry [org/simple/test/testartifact/testartifact/1.1/testartifact-1.1.pom]"
            + " from repository [test-consumer-repository]" );
        EasyMock.expectLastCall();
        EasyMock.replay(mockLogger);

        consumer.beginScan( testRepository );
        consumer.processFile( "org/simple/test/testartifact/testartifact/1.0/testartifact-1.0.pom" );
        consumer.processFile( "org/simple/test/testartifact/testartifact/1.1/testartifact-1.1.pom" );
        EasyMock.verify(mockLogger);
    }

}
