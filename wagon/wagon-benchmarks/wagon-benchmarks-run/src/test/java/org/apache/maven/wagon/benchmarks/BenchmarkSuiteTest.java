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
import org.codehaus.plexus.PlexusTestCase;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

/**
 * @author Olivier Lamy
 */
@RunWith( JUnit4.class )
@AxisRange( min = 0, max = 1 )
@BenchmarkMethodChart( filePrefix = "target/benchmark-result" )
@BenchmarkHistoryChart( labelWith = LabelType.CUSTOM_KEY, maxRuns = 5,filePrefix = "target/history-result")
@BenchmarkOptions( benchmarkRounds = 2, warmupRounds = 1, concurrency = 2 )
public class BenchmarkSuiteTest
    extends PlexusTestCase
{

    @Rule
    public MethodRule benchmarkRun = new BenchmarkRule();

    final protected Logger log = LoggerFactory.getLogger( getClass() );

    @BeforeClass
    public static void createResultFile()
        throws Exception
    {
        System.setProperty( "jub.consumers", "CONSOLE,H2" );
        System.setProperty( "jub.db.file", new File( "target/.benchmarks" ).getAbsolutePath() );
    }

    @Test
    public void ahcSuite()
        throws Throwable
    {
        JUnitCore core = new JUnitCore();
        core.run( WagonHttpAhcRunner.class );

    }

    @Test
    public void lightSuite()
        throws Throwable
    {
        JUnitCore core = new JUnitCore();
        core.run( WagonHttpClientLigthRunner.class );

    }

    @Test
    public void httpSuite()
        throws Throwable
    {
        JUnitCore core = new JUnitCore();
        core.run( WagonHttpClientRunner.class );

    }


}
