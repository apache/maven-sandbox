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
import org.junit.rules.MethodRule;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.Properties;

import static org.apache.maven.tck.TckMatchers.hasDefaultConstructor;
import static org.apache.maven.tck.TckMatchers.isFinalClass;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class PropertyUtilsTest
{
    @Rule
    public FixPlexusBugs fixPlexusBugs = new FixPlexusBugs();

    @Retention( RetentionPolicy.RUNTIME )
    @Target( ElementType.METHOD )
    @interface NeedsTemporaryFolder
    {
    }

    @Rule
    public MethodRule folderProvider = new MethodRule()
    {
        // keep the tests fast and only interface with the filesystem if needed
        public Statement apply( Statement base, FrameworkMethod method, Object target )
        {
            if ( method.getAnnotation( NeedsTemporaryFolder.class ) == null )
            {
                return base;
            }
            folder = new TemporaryFolder();
            return folder.apply( base, method, target );
        }
    };

    public TemporaryFolder folder;

    @Test
    public void isNotUtilityClass()
        throws Exception
    {
        assertThat( PropertyUtils.class, allOf( hasDefaultConstructor(), not( isFinalClass() ) ) );
    }

    @Test
    public void loadNullInputStream()
        throws Exception
    {
        assertThat( PropertyUtils.loadProperties( (InputStream) null ), is( new Properties() ) );
    }

    @Test
    public void loadNullURL()
        throws Exception
    {
        assertThat( PropertyUtils.loadProperties( (URL) null ), nullValue( Properties.class ) );
    }

    @Test
    public void loadNullFile()
        throws Exception
    {
        assertThat( PropertyUtils.loadProperties( (File) null ), nullValue( Properties.class ) );
    }

    @Test
    public void loadEmptyInputStream()
        throws Exception
    {
        assertThat( PropertyUtils.loadProperties( new ByteArrayInputStream( new byte[0] ) ), is( new Properties() ) );
    }

    @Test
    @NeedsTemporaryFolder
    public void loadEmptyFile()
        throws Exception
    {
        assertThat( PropertyUtils.loadProperties( folder.newFile( "empty" ) ), is( new Properties() ) );
    }

    @Test
    @NeedsTemporaryFolder
    public void loadEmptyURL()
        throws Exception
    {
        assertThat( PropertyUtils.loadProperties( folder.newFile( "empty" ).toURI().toURL() ), is( new Properties() ) );
    }

    @Test
    public void loadValidInputStream()
        throws Exception
    {
        Properties value = new Properties();
        value.setProperty( "a", "b" );
        assertThat( PropertyUtils.loadProperties( new ByteArrayInputStream( "a=b".getBytes( "ISO-8859-1" ) ) ),
                    is( value ) );
    }

    @Test
    @NeedsTemporaryFolder
    public void loadValidFile()
        throws Exception
    {
        File valid = folder.newFile( "valid" );
        Properties value = new Properties();
        value.setProperty( "a", "b" );
        value.store( new FileOutputStream( valid ), "a test" );
        assertThat( PropertyUtils.loadProperties( valid ), is( value ) );
    }

    @Test
    @NeedsTemporaryFolder
    public void loadValidURL()
        throws Exception
    {
        File valid = folder.newFile( "valid" );
        Properties value = new Properties();
        value.setProperty( "a", "b" );
        value.store( new FileOutputStream( valid ), "a test" );
        assertThat( PropertyUtils.loadProperties( valid.toURI().toURL() ), is( value ) );
    }


}
