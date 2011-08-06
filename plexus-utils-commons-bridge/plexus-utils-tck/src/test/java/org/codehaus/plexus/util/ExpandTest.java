package org.codehaus.plexus.util;

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

import org.apache.maven.tck.FixPlexusBugs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;



/**
 * This will test the plexus utility class {@link Expand}.
 *
 * Most of this stuff will be obsolete because java-1.4.2
 * introduced a java.util.zip package which works like a charm.
 *
 * We of course need to implement this class due to compatibility
 * reasons.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class ExpandTest extends Assert
{

    private static Logger logger = Logger.getLogger(ExceptionUtilsTest.class.getName());

    private static final String TEST_ZIP_LOCATION = "/expand/expand_test.zip";
    private static final String TEST_ZIP_TARGET = "target/expand_test_target/";

    private static final String TEST_UNZIPPED_FILE = "expand_test/test_file.txt";
    private static final String TEST_UNZIPPED_CONTENT = "TestContent";

    @Rule
    public FixPlexusBugs fixPlexusBugs = new FixPlexusBugs();


    private File getSourceFile()
    {
        URL zipFileUrl = getClass().getResource( TEST_ZIP_LOCATION );

        assertNotNull( zipFileUrl );

        return new File( zipFileUrl.getFile() );
    }

    /**
     * Create a clean target directory for unzipping.
     * If it did exist, then clean it first.
     *
     * @return
     */
    private File getTestTargetDir() throws IOException
    {
        File targetDir = new File( TEST_ZIP_TARGET );

        if ( targetDir.exists() )
        {
            FileUtils.cleanDirectory( targetDir );
        }
        else
        {
            targetDir.mkdirs();
        }

        return targetDir;
    }

    @Test
    public void testSetDest_No_NPE()
    {
        Expand expand = new Expand();
        expand.setDest( null );
    }

    @Test
    public void testSetSrc_No_NPE()
    {
        Expand expand = new Expand();
        expand.setSrc(null);
    }

    @Test
    public void testExecute() throws Exception
    {
        Expand expand = new Expand();

        File source = getSourceFile();
        expand.setSrc( source );

        File targetDir = getTestTargetDir();
        expand.setDest( targetDir );

        expand.execute();

        verifyExpandedFileAndContent( targetDir,  TEST_UNZIPPED_CONTENT );
    }

    @Test
    public void testExecuteIntoNonexistingDirectory() throws Exception
    {
        Expand expand = new Expand();

        File source = getSourceFile();
        expand.setSrc( source );

        File nonexisingDir = new File( getTestTargetDir(), "nonexisting_dir" );

        if ( nonexisingDir.exists() )
        {
            FileUtils.deleteDirectory( nonexisingDir );
        }

        expand.setDest( nonexisingDir );

        expand.execute();

        verifyExpandedFileAndContent( nonexisingDir,  TEST_UNZIPPED_CONTENT );
    }

    @Test
    public void testExecuteNonexistingSource() throws Exception
    {
        Expand expand = new Expand();

        File nonexistingSource = new File( "target/expand_test_target/nonexisting_source_file.nixda" );
        expand.setSrc( nonexistingSource );

        File targetDir = getTestTargetDir();
        expand.setDest( targetDir );

        try
        {

            expand.execute();
            fail( "expand with notexiting source must throw Exception!" );
        }
        catch ( Exception e )
        {
            Throwable cause = ExceptionUtils.getCause( e );

            assertTrue( "cause must be a FileNotFoundException", cause instanceof FileNotFoundException );
        }

    }

    private File verifyExpandedFile( File targetDir )
    {
        assertThat( "target directory must exist"
                  , targetDir.exists()
                  , is( true) );

        File expandedFile = new File( targetDir, TEST_UNZIPPED_FILE );

        assertThat( "expanded file must exist: " + expandedFile.getAbsolutePath()
                  , expandedFile.exists()
                  , is( true) );

        return expandedFile;
    }

    private File verifyExpandedFileAndContent( File targetDir, String expectedContent )
            throws FileNotFoundException
    {
        File expandedFile = verifyExpandedFile( targetDir );

        assertNotNull(expandedFile);

        java.util.Scanner scanner = new java.util.Scanner( expandedFile ).useDelimiter("\n");
        String text = scanner.next();

        assertThat( "expanded file content must match"
                  , text
                  , is( expectedContent) );

        return expandedFile;
    }



}
