package org.apache.maven.wagon.benchmarks;
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

import org.apache.commons.io.IOUtils;
import org.apache.maven.wagon.StreamingWagon;
import org.apache.maven.wagon.repository.Repository;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Olivier Lamy
 */
public abstract class AbstractWagonHttpClientTest
    extends AbstractWagonClientTest
{

    static int parallelRequestNumber = Integer.parseInt( System.getProperty( "wagon.benchmark.rq.parallel" ) );

    static int requestNumber = Integer.parseInt( System.getProperty( "wagon.benchmark.rq.number" ) );

    @Test
    public void getSmallFilesHttpNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        smallFileGet( false, false );

        long end = System.currentTimeMillis();
        log.info( getClass() + " getSmallFilesHttpNotCompressed time " + ( end - start ) );
    }

    @Test
    public void getSmallFilesHttpsNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();
        smallFileGet( false, true );
        long end = System.currentTimeMillis();
        log.info( getClass() + "getSmallFilesHttpsNotCompressed time " + ( end - start ) );

    }

    @Test
    public void getSmallFilesHttpCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();
        smallFileGet( true, false );
        long end = System.currentTimeMillis();
        log.info( getClass() + "getSmallFilesHttpCompressed time " + ( end - start ) );

    }

    @Test
    public void getSmallFilesHttpsCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        smallFileGet( true, true );

        long end = System.currentTimeMillis();
        log.info( getClass() + "getSmallFilesHttpsCompressed time " + ( end - start ) );

    }

    private void smallFileGet( boolean compressResponse, boolean ssl )
        throws Exception
    {
        GetFileServlet.compressResponse = compressResponse;

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "maven-metadata.xml" );
        GetFileServlet.responseContent = IOUtils.toByteArray( is );

        final TestServer testServer = new TestServer();

        testServer.ssl = ssl;

        testServer.servletsPerPath.put( "/*", GetFileServlet.class );

        testServer.start( parallelRequestNumber );

        call( ssl, testServer.port, true );

        testServer.stop();

    }

    @Test
    public void getHugeFileHttpNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        hugeFileGet( false, false );

        long end = System.currentTimeMillis();
        log.info( getClass() + " getHugeFilesHttpNotCompressed time " + ( end - start ) );
    }

    @Test
    public void getHugeFileHttpsNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        hugeFileGet( false, true );

        long end = System.currentTimeMillis();
        log.info( getClass() + " getHugeFileHttpsNotCompressed time " + ( end - start ) );
    }

    @Test
    public void getHugeFileHttpCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        hugeFileGet( true, false );

        long end = System.currentTimeMillis();
        log.info( getClass() + " getHugeFileHttpCompressed time " + ( end - start ) );
    }


    @Test
    public void getHugeFileHttpsCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        hugeFileGet( true, true );

        long end = System.currentTimeMillis();
        log.info( getClass() + " getHugeFileHttpsCompressed time " + ( end - start ) );
    }


    private void hugeFileGet( boolean compressResponse, boolean ssl )
        throws Exception
    {
        GetFileServlet.compressResponse = compressResponse;

        File f = new File( "src/test/apache-maven-3.0.3-bin.zip" );
        FileInputStream fileInputStream = new FileInputStream( f );

        GetFileServlet.responseContent = IOUtils.toByteArray( fileInputStream );

        final TestServer testServer = new TestServer();

        testServer.ssl = ssl;

        testServer.servletsPerPath.put( "/*", GetFileServlet.class );

        testServer.start( parallelRequestNumber );

        call( ssl, testServer.port, false );

        testServer.stop();

        long end = System.currentTimeMillis();
    }


    protected void call( boolean ssl, final int port, final boolean testcontent )
        throws Exception
    {
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();

        for ( int i = 0; i < requestNumber; i++ )
        {
            final StreamingWagon wagon = ssl ? getHttpsWagon() : getHttpWagon();
            wagon.setTimeout( 10000 );

            callables.add( new Callable<Void>()
            {
                public Void call()
                {
                    try
                    {
                        wagon.connect( new Repository( "foo", "http://localhost:" + port + "/" ) );

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        wagon.getToStream( "foo", baos );
                        if ( testcontent )
                        {
                            assertTrue( baos.toString().contains( "20110821162420" ) );
                        }
                    }
                    catch ( Exception e )
                    {
                        throw new RuntimeException( e.getMessage(), e );
                    }
                    return null;
                }
            } );

        }

        ExecutorService executorService = Executors.newFixedThreadPool( parallelRequestNumber );

        executorService.invokeAll( callables );

        executorService.shutdown();
        executorService.awaitTermination( 10, TimeUnit.SECONDS );
    }
}
