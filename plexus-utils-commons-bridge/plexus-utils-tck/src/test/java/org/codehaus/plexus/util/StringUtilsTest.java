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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;


/**
 * Test the {@link org.codehaus.plexus.util.StringUtils} class.
 *
 * We don't need to test this
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class StringUtilsTest extends Assert
{

    @Rule
    public FixPlexusBugs fixPlexusBugs = new FixPlexusBugs();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();


    @Test(expected = NullPointerException.class)
    public void testAbbreviate_NPE()
    {
        assertThat( StringUtils.abbreviate( null, 10 )
                , CoreMatchers.<Object>nullValue() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAbbreviate_MinLength()
    {
        assertThat( StringUtils.abbreviate( "This is a longtext", 3 )
                  , is( "T" ) );
    }

    @Test
    public void testAbbreviate()
    {
        assertThat( StringUtils.abbreviate( "This is a longtext", 10 )
                  , is( "This is..." ) );

        assertThat( StringUtils.abbreviate( "This is a longtext", 50 )
                  , is( "This is a longtext" ) );
    }

    @Test(expected = NullPointerException.class)
    public void testAbbreviate_Offset_NPE()
    {
        assertThat( StringUtils.abbreviate( null, 10, 20 )
                , CoreMatchers.<Object>nullValue() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAbbreviate_Offset_MinLength()
    {
        assertThat( StringUtils.abbreviate( "This is a longtext", 10, 3 )
                  , is( "T" ) );
    }

    @Test
    public void testAbbreviate_Offset()
    {
        assertThat( StringUtils.abbreviate( "This is a longtext", 5, 10 )
                  , is( "...is a..." ) );

        assertThat( StringUtils.abbreviate( "This is a longtext", 10, 20 )
                  , is( "This is a longtext" ) );

        assertThat( StringUtils.abbreviate( "This is a longtext", 50, 20 )
                  , is( "This is a longtext" ) );
    }

    @Test( expected = NullPointerException.class )
    public void testAddAndDeHump_NPE()
    {
        StringUtils.addAndDeHump( null );
    }

    @Test
    public void testAddAndDeHump()
    {
        assertThat( StringUtils.addAndDeHump( "lalala" )
                  , is( "lalala" ) );

        assertThat( StringUtils.addAndDeHump( "LaLaLa" )
                  , is( "la-la-la" ) );

        assertThat( StringUtils.addAndDeHump( "ALLUPPER" )
                  , is( "a-l-l-u-p-p-e-r" ) );

    }

    @Test
    public void testCapitalise()
    {
        assertThat( StringUtils.capitalise( null )
                , nullValue() );

        assertThat( StringUtils.capitalise( "startBig" )
                , is( "StartBig" ) );
    }

    @Test
    public void testCapitaliseAllWords()
    {
        assertThat( StringUtils.capitaliseAllWords( null )
                , nullValue() );

        assertThat( StringUtils.capitaliseAllWords( "start all big" )
                , is( "Start All Big" ) );
    }

    @Test( expected = NullPointerException.class )
    public void testCapitalizeFirstLetter_NPE()
    {
        assertThat( StringUtils.capitalizeFirstLetter( null )
                , nullValue() );
    }

    @Test
    public void testCapitalizeFirstLetter()
    {
        assertThat( StringUtils.capitalizeFirstLetter( "start all big" )
                , is( "Start all big" ) );
    }

    @Test( expected = NullPointerException.class )
    public void testCenter_NPE()
    {
        StringUtils.center( null, 20 );
    }

    @Test
    public void testCenter()
    {
        assertThat( StringUtils.center( "centerMe", 20 )
                , is( "      centerMe      " ) );

        assertThat( StringUtils.center( "centerMe", 4 )
                , is( "centerMe" ) );

        assertThat( StringUtils.center( "        centerMe", 20 )
                , is( "          centerMe  " ) );
    }
}
