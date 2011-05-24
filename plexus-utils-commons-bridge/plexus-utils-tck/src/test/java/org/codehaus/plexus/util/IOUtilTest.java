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

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.maven.tck.TckMatchers.isClassWithOnlyPrivateConstructors;
import static org.apache.maven.tck.TckMatchers.isUtilityClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IOUtilTest
{

    @Test
    public void isAUtilityClass()
        throws Exception
    {
        assertThat( IOUtil.class, isUtilityClass() );
    }

    @Test
    public void closeReaderWithNull() throws Exception {
        IOUtil.close( (Reader)null );
    }

    @Test
    public void closeWriterWithNull() throws Exception {
        IOUtil.close( (Writer)null );
    }

    @Test
    public void closeInputStreamWithNull() throws Exception {
        IOUtil.close( (InputStream)null );
    }

    @Test
    public void closeOutputStreamWithNull() throws Exception {
        IOUtil.close( (OutputStream) null );
    }

    @Test
    public void closeReaderWithIOE() throws Exception {
        IOUtil.close( new BufferedReader( new StringReader( "" ) ) {
            @Override
            public void close()
                throws IOException
            {
                super.close();
                throw new IOException( "don't bomb out" );
            }
        });
    }

    @Test
    public void closeWriterWithIOE() throws Exception {
        IOUtil.close( new BufferedWriter( new StringWriter(  ) ) {
            @Override
            public void close()
                throws IOException
            {
                super.close();
                throw new IOException( "don't bomb out" );
            }
        });
    }

    @Test
    public void closeInputStreamWithIOE() throws Exception {
        IOUtil.close( new BufferedInputStream( new ByteArrayInputStream( new byte[0] ) ){
            @Override
            public void close()
                throws IOException
            {
                super.close();
                throw new IOException( "don't bomb out" );
            }
        });
    }

    @Test
    public void closeOutputStreamWithIOE() throws Exception {
        IOUtil.close( new BufferedOutputStream( new ByteArrayOutputStream(  ) ){
            @Override
            public void close()
                throws IOException
            {
                super.close();
                throw new IOException( "don't bomb out" );
            }
        });
    }

    @Test
    public void closeReaderCloses() throws Exception {
        final AtomicBoolean closed = new AtomicBoolean( false );
        IOUtil.close( new BufferedReader( new StringReader( "" ) ) {
            @Override
            public void close()
                throws IOException
            {
                closed.set( true );
                super.close();
            }
        });
        assertThat( closed.get(), is( true ) );
    }

    @Test
    public void closeWriterCloses() throws Exception {
        final AtomicBoolean closed = new AtomicBoolean( false );
        IOUtil.close( new BufferedWriter( new StringWriter(  ) ) {
            @Override
            public void close()
                throws IOException
            {
                closed.set( true );
                super.close();
            }
        });
        assertThat( closed.get(), is( true ) );
    }

    @Test
    public void closeInputStreamCloses() throws Exception {
        final AtomicBoolean closed = new AtomicBoolean( false );
        IOUtil.close( new BufferedInputStream( new ByteArrayInputStream( new byte[0] ) ){
            @Override
            public void close()
                throws IOException
            {
                closed.set( true );
                super.close();
            }
        });
        assertThat( closed.get(), is( true ) );
    }

    @Test
    public void closeOutputStreamCloses() throws Exception {
        final AtomicBoolean closed = new AtomicBoolean( false );
        IOUtil.close( new BufferedOutputStream( new ByteArrayOutputStream(  ) ){
            @Override
            public void close()
                throws IOException
            {
                closed.set( true );
                super.close();
            }
        });
        assertThat( closed.get(), is( true ) );
    }

}
