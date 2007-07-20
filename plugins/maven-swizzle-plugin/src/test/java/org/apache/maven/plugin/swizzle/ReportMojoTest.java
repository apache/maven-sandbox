package org.apache.maven.plugin.swizzle;

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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Author: John Tolentino
 */
public class ReportMojoTest
    extends AbstractMojoTestCase
{
    private ReportMojo mojo;

    public void testSimple()
        throws Exception
    {
        File pom = new File( getBasedir(), "target/test-classes/unit/simple.xml" );
        mojo = (ReportMojo) lookupMojo( "generate", pom );

        assertNotNull( mojo );
    }

    public void testResolvedIssuesConfiguration()
        throws Exception
    {
        testConfiguration( "resolved-issues-configuration-test.xml", "ResolvedIssuesExpectedResult.txt",
                           getBasedir() + "/target/test-classes/unit/ResolvedIssuesActualResult.txt" );
    }

    public void testVotesConfiguration()
        throws Exception
    {
        testConfiguration( "votes-configuration-test.xml", "VotesExpectedResult.txt",
                           getBasedir() + "/target/test-classes/unit/VotesActualResult.txt" );
    }

    public void testReleaseConfiguration()
        throws Exception
    {
        testConfiguration( "release-configuration-test.xml", "ReleaseExpectedResult.txt",
                           getBasedir() + "/target/test-classes/unit/ReleaseActualResult.txt" );
    }

    public void testCustomTemplateConfiguration()
        throws Exception
    {
        testConfiguration( "custom-template-configuration-test.xml", "MyResolvedIssuesExpectedResult.txt",
                           getBasedir() + "/target/test-classes/unit/MyResolvedIssuesActualResult.txt" );
    }

    public void testReportGenerationExceptionHandling()
        throws Exception
    {
        File pom = new File( getBasedir(), "target/test-classes/unit/blank-template-configuration-test.xml" );

        File actualFile = new File( getBasedir() + "/target/test-classes/unit/exception.txt" );

        if ( actualFile.exists() )
        {
            actualFile.delete();
        }

        mojo = (ReportMojo) lookupMojo( "generate", pom );

        assertNotNull( mojo );

        try
        {
            mojo.execute();
            assertTrue( "Was expecting the mojo to throw an exception", false );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( true );
        }
    }

    public void testBlankOutputPathHandling()
        throws Exception
    {
        File pom = new File( getBasedir(), "target/test-classes/unit/blank-output-file-configuration-test.xml" );

        mojo = (ReportMojo) lookupMojo( "generate", pom );

        assertNotNull( mojo );

        try
        {
            mojo.execute();
            assertTrue( "Was expecting the mojo to throw an exception", false );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( true );
        }

    }

    public void testIOExceptionHandling()
        throws Exception
    {
        File pom = new File( getBasedir(), "target/test-classes/unit/custom-template-configuration-test.xml" );

        File actualFile = new File( getBasedir() + "/target/test-classes/unit/MyResolvedIssuesActualResult.txt" );

        if ( actualFile.exists() )
        {
            actualFile.delete();
        }

        actualFile.createNewFile();
        actualFile.setReadOnly();

        mojo = (ReportMojo) lookupMojo( "generate", pom );

        assertNotNull( mojo );

        try
        {
            mojo.execute();
            assertTrue( "Was expecting the mojo to throw an exception", false );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( true );
        }
    }

    private void testConfiguration( String configurationFile, String expectedResult, String actualResult )
        throws Exception
    {
        File pom = new File( getBasedir(), "target/test-classes/unit/" + configurationFile );

        File expectedFile = new File( getBasedir() + "/target/test-classes/unit/" + expectedResult );
        File actualFile = new File( actualResult );

        if ( actualFile.exists() )
        {
            actualFile.delete();
        }

        mojo = (ReportMojo) lookupMojo( "generate", pom );

        assertNotNull( mojo );

        mojo.execute();

        assertNotNull( expectedFile );
        assertNotNull( actualFile );

        assertTrue( actualFile.exists() );
        assertTrue( expectedFile.exists() );

        String expectedString = fileToString( expectedFile.getAbsolutePath() );
        String actualString = fileToString( actualFile.getAbsolutePath() );

        assertEquals( expectedString, actualString );
    }

    private static String fileToString( String inputFilename )
        throws IOException
    {
        BufferedReader br = new BufferedReader( new FileReader( inputFilename ) );
        StringBuffer text = new StringBuffer();

        try
        {
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                text.append( line );
            }
        }
        finally
        {
            br.close();
        }
        return text.toString();
    }

}
