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

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import com.carrotsearch.junitbenchmarks.annotation.LabelType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.wagon.StreamingWagon;
import org.apache.maven.wagon.repository.Repository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
@RunWith( JUnit4.class )
@AxisRange( min = 0, max = 1 )
@BenchmarkMethodChart( filePrefix = "target/benchmark-result" )
@BenchmarkHistoryChart( labelWith = LabelType.CUSTOM_KEY, maxRuns = 5, filePrefix = "target/history-result" )
@BenchmarkOptions( benchmarkRounds = 2, warmupRounds = 1, concurrency = 1 )
public abstract class AbstractWagonHttpClientTest
    extends AbstractWagonClientTest
{

    @Rule
    public MethodRule benchmarkRun = new BenchmarkRule();

    static int parallelRequestNumber = Integer.parseInt( System.getProperty( "wagon.benchmark.rq.parallel" ) );

    static int requestNumber = Integer.parseInt( System.getProperty( "wagon.benchmark.rq.number" ) );

    static FileWriter resultWriter = null;


    public AbstractWagonHttpClientTest()
    {
        //
    }


    @BeforeClass
    public static void createResultFile()
        throws Exception
    {
        File resultFile = new File( "../result.txt" );
        if ( !resultFile.exists() )
        {
            resultFile.createNewFile();
        }

        resultWriter = new FileWriter( resultFile, true );
    }


    @AfterClass
    public static void close()
        throws Exception
    {
        resultWriter.flush();
    }

    //-------------------------
    // small size file get
    //-------------------------

    @Test
    public void testgetSmallFilesHttpNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        smallFileGet( false, false );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " getSmallFilesHttpNotCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );
    }


    @Test
    public void testgetSmallFilesHttpsNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();
        smallFileGet( false, true );
        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " getSmallFilesHttpsNotCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );

    }

    @Test
    public void testgetSmallFilesHttpCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();
        smallFileGet( true, false );
        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " getSmallFilesHttpCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );

    }

    @Test
    public void testgetSmallFilesHttpsCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        smallFileGet( true, true );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " getSmallFilesHttpsCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );

    }

    private void smallFileGet( boolean compressResponse, boolean ssl )
        throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "maven-metadata.xml" );
        fileGet( compressResponse, ssl, is );
        IOUtils.closeQuietly( is );
    }

    //-------------------------
    // huge size file get
    //-------------------------

    @Test
    public void testgetHugeFileHttpNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        hugeFileGet( false, false );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " getHugeFileHttpNotCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );
    }

    @Test
    public void testgetHugeFileHttpsNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        hugeFileGet( false, true );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " getHugeFileHttpsNotCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );
    }

    @Test
    public void testgetHugeFileHttpCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        hugeFileGet( true, false );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " getHugeFileHttpCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );
    }


    @Test
    public void testgetHugeFileHttpsCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        hugeFileGet( true, true );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " getHugeFileHttpsCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );
    }


    private void hugeFileGet( boolean compressResponse, boolean ssl )
        throws Exception
    {
        fileGet( compressResponse, ssl, new FileInputStream( new File( System.getProperty( "hugeFilePath" ) ) ) );
    }


    //-------------------------
    // medium size file get
    //-------------------------

    @Test
    public void testgetMediumFileHttpNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        mediumFileGet( false, false );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " testgetMediumFileHttpNotCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );
    }

    @Test
    public void testgetMediumFileHttpsNotCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        mediumFileGet( false, true );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " testgetMediumFileHttpsNotCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );
    }

    @Test
    public void testgetMediumFileHttpCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        mediumFileGet( true, false );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " testgetMediumFileHttpCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );
    }


    @Test
    public void testgetMediumFileHttpsCompressed()
        throws Exception
    {
        long start = System.currentTimeMillis();

        mediumFileGet( true, true );

        long end = System.currentTimeMillis();
        String msg = getClass().getSimpleName() + " testgetMediumFileHttpsCompressed time " + ( end - start );
        log.info( msg );
        IOUtils.write( msg + SystemUtils.LINE_SEPARATOR, resultWriter );
    }


    private void mediumFileGet( boolean compressResponse, boolean ssl )
        throws Exception
    {
        fileGet( compressResponse, ssl, new FileInputStream( new File( System.getProperty( "mediumFilePath" ) ) ) );
    }

    private void fileGet( boolean compressResponse, boolean ssl, InputStream is )
        throws Exception
    {
        GetFileServlet.compressResponse = compressResponse;

        GetFileServlet.responseContent = IOUtils.toByteArray( is );

        final TestServer testServer = new TestServer();

        testServer.ssl = ssl;

        testServer.servletsPerPath.put( "/*", GetFileServlet.class );

        testServer.start( parallelRequestNumber );

        call( ssl, testServer.port, false );

        testServer.stop();

    }


    protected void call( final boolean ssl, final int port, final boolean testcontent )
        throws Exception
    {
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();

        final String repoUrl = ( ssl ? "https" : "http" ) + "://localhost:" + port + "/";

        for ( int i = 0; i < requestNumber; i++ )
        {
            final StreamingWagon wagon = ssl ? getHttpsWagon() : getHttpWagon();
            wagon.setTimeout( 10000 );

            callables.add( new Callable<Void>()
            {
                public Void call()
                {
                    File tmpFile = null;
                    try
                    {
                        wagon.connect( new Repository( "foo", repoUrl ) );

                        tmpFile = File.createTempFile( "wagon-test", "benchmark" );
                        tmpFile.deleteOnExit();

                        wagon.get( "foo", tmpFile );

                        assertTrue( tmpFile.length() > 1 );

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

    abstract StreamingWagon getHttpWagon()
        throws Exception;

    abstract StreamingWagon getHttpsWagon()
        throws Exception;

}
