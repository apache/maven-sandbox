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
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public void closeReaderWithNull()
        throws Exception
    {
        IOUtil.close( (Reader) null );
    }

    @Test
    public void closeWriterWithNull()
        throws Exception
    {
        IOUtil.close( (Writer) null );
    }

    @Test
    public void closeInputStreamWithNull()
        throws Exception
    {
        IOUtil.close( (InputStream) null );
    }

    @Test
    public void closeOutputStreamWithNull()
        throws Exception
    {
        IOUtil.close( (OutputStream) null );
    }

    @Test
    public void closeReaderWithIOE()
        throws Exception
    {
        IOUtil.close( new BufferedReader( new StringReader( "" ) )
        {
            @Override
            public void close()
                throws IOException
            {
                super.close();
                throw new IOException( "don't bomb out" );
            }
        } );
    }

    @Test
    public void closeWriterWithIOE()
        throws Exception
    {
        IOUtil.close( new BufferedWriter( new StringWriter() )
        {
            @Override
            public void close()
                throws IOException
            {
                super.close();
                throw new IOException( "don't bomb out" );
            }
        } );
    }

    @Test
    public void closeInputStreamWithIOE()
        throws Exception
    {
        IOUtil.close( new BufferedInputStream( new ByteArrayInputStream( new byte[0] ) )
        {
            @Override
            public void close()
                throws IOException
            {
                super.close();
                throw new IOException( "don't bomb out" );
            }
        } );
    }

    @Test
    public void closeOutputStreamWithIOE()
        throws Exception
    {
        IOUtil.close( new BufferedOutputStream( new ByteArrayOutputStream() )
        {
            @Override
            public void close()
                throws IOException
            {
                super.close();
                throw new IOException( "don't bomb out" );
            }
        } );
    }

    @Test
    public void closeReaderCloses()
        throws Exception
    {
        final AtomicBoolean closed = new AtomicBoolean( false );
        IOUtil.close( new BufferedReader( new StringReader( "" ) )
        {
            @Override
            public void close()
                throws IOException
            {
                closed.set( true );
                super.close();
            }
        } );
        assertThat( closed.get(), is( true ) );
    }

    @Test
    public void closeWriterCloses()
        throws Exception
    {
        final AtomicBoolean closed = new AtomicBoolean( false );
        IOUtil.close( new BufferedWriter( new StringWriter() )
        {
            @Override
            public void close()
                throws IOException
            {
                closed.set( true );
                super.close();
            }
        } );
        assertThat( closed.get(), is( true ) );
    }

    @Test
    public void closeInputStreamCloses()
        throws Exception
    {
        final AtomicBoolean closed = new AtomicBoolean( false );
        IOUtil.close( new BufferedInputStream( new ByteArrayInputStream( new byte[0] ) )
        {
            @Override
            public void close()
                throws IOException
            {
                closed.set( true );
                super.close();
            }
        } );
        assertThat( closed.get(), is( true ) );
    }

    @Test
    public void closeOutputStreamCloses()
        throws Exception
    {
        final AtomicBoolean closed = new AtomicBoolean( false );
        IOUtil.close( new BufferedOutputStream( new ByteArrayOutputStream() )
        {
            @Override
            public void close()
                throws IOException
            {
                closed.set( true );
                super.close();
            }
        } );
        assertThat( closed.get(), is( true ) );
    }

    @Test
    public void toByteArrayFromString()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toByteArray( probe ), is( probe.getBytes() ) );
    }

    @Test
    public void toByteArrayFromReader()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toByteArray( new StringReader( probe ) ), is( probe.getBytes() ) );
    }

    @Test
    public void toByteArrayFromInputStream()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toByteArray( new DontCloseByteArrayInputStream( IOUtil.toByteArray( probe ) ) ),
                    is( probe.getBytes() ) );
    }

    @Test( expected = NullPointerException.class )
    public void toByteArrayNullString()
        throws Exception
    {
        IOUtil.toByteArray( (String) null );
    }

    @Test( expected = NullPointerException.class )
    public void toByteArrayNullReader()
        throws Exception
    {
        IOUtil.toByteArray( (Reader) null );
    }

    @Test( expected = NullPointerException.class )
    public void toByteArrayNullInputStream()
        throws Exception
    {
        IOUtil.toByteArray( (InputStream) null );
    }

    @Test( expected = IOException.class )
    public void contentEqualNullNull()
        throws Exception
    {
        IOUtil.contentEquals( null, null );
    }

    @Test( expected = IOException.class )
    public void contentEqualNonNullNull()
        throws Exception
    {
        IOUtil.contentEquals( new DontCloseByteArrayInputStream( new byte[0] ), null );
    }

    @Test( expected = IOException.class )
    public void contentEqualNullNonNull()
        throws Exception
    {
        IOUtil.contentEquals( new DontCloseByteArrayInputStream( new byte[0] ), null );
    }

    @Test
    public void contentEqualEmptyEmpty()
        throws Exception
    {
        assertThat(
            IOUtil.contentEquals( new DontCloseByteArrayInputStream( new byte[0] ), new DontCloseByteArrayInputStream( new byte[0] ) ),
            is( true ) );
    }

    @Test
    public void contentEqualNonEmptyEmpty()
        throws Exception
    {
        assertThat(
            IOUtil.contentEquals( new DontCloseByteArrayInputStream( new byte[1] ), new DontCloseByteArrayInputStream( new byte[0] ) ),
            is( false ) );
    }

    @Test
    public void contentEqualEmptyNonEmpty()
        throws Exception
    {
        assertThat(
            IOUtil.contentEquals( new DontCloseByteArrayInputStream( new byte[0] ), new DontCloseByteArrayInputStream( new byte[1] ) ),
            is( false ) );
    }

    @Test
    public void contentEqualNonEmptyNonEmpty()
        throws Exception
    {
        assertThat(
            IOUtil.contentEquals( new DontCloseByteArrayInputStream( new byte[1] ), new DontCloseByteArrayInputStream( new byte[1] ) ),
            is( true ) );
    }

    @Test
    public void contentEqualMostlySame()
        throws Exception
    {
        assertThat( IOUtil.contentEquals( new DontCloseByteArrayInputStream( new byte[]{ 1, 2, 3, 4, 5, 6 } ),
                                          new DontCloseByteArrayInputStream( new byte[]{ 1, 2, 3, 4, 5, 7 } ) ), is( false ) );
    }

    @Test
    public void contentEqualLargeSame()
        throws Exception
    {
        assertThat( IOUtil.contentEquals( new DontCloseByteArrayInputStream( new byte[8192] ),
                                          new DontCloseByteArrayInputStream( new byte[8192] ) ), is( true ) );
    }

    @Test
    public void contentEqualLargeDifferent()
        throws Exception
    {
        byte[] buf = new byte[8192];
        buf[8191] = 1;
        assertThat( IOUtil.contentEquals( new DontCloseByteArrayInputStream( new byte[8192] ), new DontCloseByteArrayInputStream( buf ) ),
                    is( false ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringNullByteArray()
        throws Exception
    {
        IOUtil.toString( (byte[]) null );
    }

    @Test
    public void toStringEmptyByteArray()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0] ), is( "" ) );
    }

    @Test
    public void toStringByteArray()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes() ).getBytes(), is( probe.getBytes() ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringNullByteArrayNegBufSz()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, -1 );
    }

    @Test( expected = NegativeArraySizeException.class )
    public void toStringEmptyByteArrayNegBufSz()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], -1 ), is( "" ) );
    }

    @Test( expected = NegativeArraySizeException.class )
    public void toStringByteArrayNegBufSz()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes(), -1 ), is( probe ) );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void toStringNullByteArrayZeroBufSz()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, 0 );
    }

    @Test( timeout = 150 )
    public void toStringEmptyByteArrayZeroBufSz()
        throws Exception
    {
        final AtomicBoolean finished = new AtomicBoolean( false );
        Thread worker = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    IOUtil.toString( new byte[0], 0 );
                }
                catch ( IOException e )
                {
                    // ignore
                }
                finished.set( true );
            }
        };
        worker.start();
        worker.join( 100 );
        worker.interrupt();
        assertThat( "We have an infinite loop", finished.get(), is( false ) );
    }

    @Test( timeout = 150 )
    public void toStringByteArrayZeroBufSz()
        throws Exception
    {
        final AtomicBoolean finished = new AtomicBoolean( false );
        Thread worker = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    String probe = "A string \u2345\u00ef";
                    IOUtil.toString( probe.getBytes(), 0 );
                }
                catch ( IOException e )
                {
                    // ignore
                }
                finished.set( true );
            }
        };
        worker.start();
        worker.join( 100 );
        worker.interrupt();
        assertThat( "We have an infinite loop", finished.get(), is( false ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringNullByteArrayPosBufSz()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, 1 );
    }

    @Test
    public void toStringEmptyByteArrayPosBufSz()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], 1 ), is( "" ) );
    }

    @Test
    public void toStringByteArrayPosBufSz()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes(), 1 ).getBytes(), is( probe.getBytes() ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringNullByteArrayNullEncoding()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, null );
    }

    @Test( expected = NullPointerException.class )
    public void toStringEmptyByteArrayNullEncoding()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], null ), is( "" ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringByteArrayNullEncoding()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes(), null ).getBytes(), is( probe.getBytes() ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringNullByteArrayJunkEncoding()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, "junk" );
    }

    @Test( expected = UnsupportedEncodingException.class )
    public void toStringEmptyByteArrayJunkEncoding()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], "junk" ), is( "" ) );
    }

    @Test( expected = UnsupportedEncodingException.class )
    public void toStringByteArrayJunkEncoding()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes(), "junk" ).getBytes(), is( probe.getBytes() ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringNullByteArrayValidEncoding()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, "utf-16" );
    }

    @Test
    public void toStringEmptyByteArrayValidEncoding()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], "utf-16" ), is( "" ) );
    }

    @Test
    public void toStringByteArrayValidEncoding()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes( "utf-16" ), "utf-16" ).getBytes( "utf-8" ),
                    is( probe.getBytes( "utf-8" ) ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringNullByteArrayNullEncodingNegBufSz()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, null, -1 );
    }

    @Test( expected = NullPointerException.class )
    public void toStringEmptyByteArrayNullEncodingNegBufSz()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], null, -1 ), is( "" ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringByteArrayNullEncodingNegBufSz()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes(), null, -1 ).getBytes(), is( probe.getBytes() ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringNullByteArrayJunkEncodingNegBufSz()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, "junk", -1 );
    }

    @Test( expected = UnsupportedEncodingException.class )
    public void toStringEmptyByteArrayJunkEncodingNegBufSz()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], "junk", -1 ), is( "" ) );
    }

    @Test( expected = UnsupportedEncodingException.class )
    public void toStringByteArrayJunkEncodingNegBufSz()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes(), "junk", -1 ).getBytes(), is( probe.getBytes() ) );
    }

    @Test( expected = NullPointerException.class )
    public void toStringNullByteArrayValidEncodingNegBufSz()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, "utf-16", -1 );
    }

    @Test( expected = NegativeArraySizeException.class )
    public void toStringEmptyByteArrayValidEncodingNegBufSz()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], "utf-16", -1 ), is( "" ) );
    }

    @Test( expected = NegativeArraySizeException.class )
    public void toStringByteArrayValidEncodingNegBufSz()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes( "utf-16" ), "utf-16", -1 ).getBytes( "utf-8" ),
                    is( probe.getBytes( "utf-8" ) ) );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void toStringNullByteArrayNullEncodingZeroBufSz()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, null, 0 );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void toStringEmptyByteArrayNullEncodingZeroBufSz()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], null, 0 ), is( "" ) );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void toStringByteArrayNullEncodingZeroBufSz()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes(), null, 0 ).getBytes(), is( probe.getBytes() ) );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void toStringNullByteArrayJunkEncodingZeroBufSz()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, "junk", 0 );
    }

    @Test( expected = UnsupportedEncodingException.class, timeout = 150 )
    public void toStringEmptyByteArrayJunkEncodingZeroBufSz()
        throws Exception
    {
        assertThat( IOUtil.toString( new byte[0], "junk", 0 ), is( "" ) );
    }

    @Test( expected = UnsupportedEncodingException.class, timeout = 150 )
    public void toStringByteArrayJunkEncodingZeroBufSz()
        throws Exception
    {
        String probe = "A string \u2345\u00ef";
        assertThat( IOUtil.toString( probe.getBytes(), "junk", 0 ).getBytes(), is( probe.getBytes() ) );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void toStringNullByteArrayValidEncodingZeroBufSz()
        throws Exception
    {
        IOUtil.toString( (byte[]) null, "utf-16", 0 );
    }

    @Test( timeout = 150 )
    public void toStringEmptyByteArrayValidEncodingZeroBufSz()
        throws Exception
    {
        final AtomicBoolean finished = new AtomicBoolean( false );
        Thread worker = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    IOUtil.toString( new byte[0], "utf-16", 0 );
                }
                catch ( IOException e )
                {
                    // ignore
                }
                finished.set( true );
            }
        };
        worker.start();
        worker.join( 100 );
        worker.interrupt();
        assertThat( "We have an infinite loop", finished.get(), is( false ) );
    }

    @Test( timeout = 150 )
    public void toStringByteArrayValidEncodingZeroBufSz()
        throws Exception
    {
        final AtomicBoolean finished = new AtomicBoolean( false );
        Thread worker = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    String probe = "A string \u2345\u00ef";
                    IOUtil.toString( probe.getBytes(), "utf-16", 0 );
                }
                catch ( IOException e )
                {
                    // ignore
                }
                finished.set( true );
            }
        };
        worker.start();
        worker.join( 100 );
        worker.interrupt();
        assertThat( "We have an infinite loop", finished.get(), is( false ) );
    }

    @Test( expected = NullPointerException.class )
    public void copyNullByteArrayNullOutputStream()
        throws Exception
    {
        IOUtil.copy( (byte[]) null, (OutputStream) null );
    }

    @Test( expected = NullPointerException.class )
    public void copyNullByteArrayValidOutputStream()
        throws Exception
    {
        IOUtil.copy( (byte[]) null, new DontCloseByteArrayOutputStream() );
    }

    @Test( expected = NullPointerException.class )
    public void copyEmptyByteArrayNullOutputStream()
        throws Exception
    {
        IOUtil.copy( new byte[0], (OutputStream) null );
    }

    @Test
    public void copyEmptyByteArrayValidOutputStream()
        throws Exception
    {
        IOUtil.copy( new byte[0], new DontCloseByteArrayOutputStream());
    }

    @Test
    public void copyByteArrayValidOutputStream()
        throws Exception
    {
        ByteArrayOutputStream outputStream = new DontCloseByteArrayOutputStream();
        byte[] input = { 1, 2, 3, 4, 5, 6 };
        IOUtil.copy( input, outputStream );
        assertThat( outputStream.toByteArray(), is( input ) );
    }

    @Test( expected = NullPointerException.class )
    public void copyNullByteArrayNullOutputStreamNegBufSz()
        throws Exception
    {
        IOUtil.copy( (byte[]) null, (OutputStream) null, -1 );
    }

    @Test( expected = NullPointerException.class )
    public void copyNullByteArrayValidOutputStreamNegBufSz()
        throws Exception
    {
        IOUtil.copy( (byte[]) null, new DontCloseByteArrayOutputStream(), -1 );
    }

    @Test( expected = NullPointerException.class )
    public void copyEmptyByteArrayNullOutputStreamNegBufSz()
        throws Exception
    {
        IOUtil.copy( new byte[0], (OutputStream) null, -1 );
    }

    @Test
    public void copyEmptyByteArrayValidOutputStreamNegBufSz()
        throws Exception
    {
        IOUtil.copy( new byte[0], new DontCloseByteArrayOutputStream(), -1 );
    }

    @Test
    public void copyByteArrayValidOutputStreamNegBufSz()
        throws Exception
    {
        ByteArrayOutputStream outputStream = new DontCloseByteArrayOutputStream();
        byte[] input = { 1, 2, 3, 4, 5, 6 };
        IOUtil.copy( input, outputStream, -1 );
        assertThat( outputStream.toByteArray(), is( input ) );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyNullByteArrayNullOutputStreamZeroBufSz()
        throws Exception
    {
        IOUtil.copy( (byte[]) null, (OutputStream) null, 0 );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyNullByteArrayValidOutputStreamZeroBufSz()
        throws Exception
    {
        IOUtil.copy( (byte[]) null, new DontCloseByteArrayOutputStream(), 0 );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyEmptyByteArrayNullOutputStreamZeroBufSz()
        throws Exception
    {
        IOUtil.copy( new byte[0], (OutputStream) null, 0 );
    }

    @Test( timeout = 150 )
    public void copyEmptyByteArrayValidOutputStreamZeroBufSz()
        throws Exception
    {
        IOUtil.copy( new byte[0], new DontCloseByteArrayOutputStream(), 0 );
    }

    @Test( timeout = 150 )
    public void copyByteArrayValidOutputStreamZeroBufSz()
        throws Exception
    {
        ByteArrayOutputStream outputStream = new DontCloseByteArrayOutputStream();
        byte[] input = { 1, 2, 3, 4, 5, 6 };
        IOUtil.copy( input, outputStream, 0 );
        assertThat( outputStream.toByteArray(), is( input ) );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyNullByteArrayNullOutputStreamPosBufSz()
        throws Exception
    {
        IOUtil.copy( (byte[]) null, (OutputStream) null, 1 );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyNullByteArrayValidOutputStreamPosBufSz()
        throws Exception
    {
        IOUtil.copy( (byte[]) null, new DontCloseByteArrayOutputStream(), 1 );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyEmptyByteArrayNullOutputStreamPosBufSz()
        throws Exception
    {
        IOUtil.copy( new byte[0], (OutputStream) null, 1 );
    }

    @Test( timeout = 150 )
    public void copyEmptyByteArrayValidOutputStreamPosBufSz()
        throws Exception
    {
        IOUtil.copy( new byte[0], new DontCloseByteArrayOutputStream(), 1 );
    }

    @Test( timeout = 150 )
    public void copyByteArrayValidOutputStreamPosBufSz()
        throws Exception
    {
        ByteArrayOutputStream outputStream = new DontCloseByteArrayOutputStream();
        byte[] input = { 1, 2, 3, 4, 5, 6 };
        IOUtil.copy( input, outputStream, 1 );
        assertThat( outputStream.toByteArray(), is( input ) );
    }
    @Test( expected = NullPointerException.class )
    public void copyNullInputStreamNullOutputStream()
        throws Exception
    {
        IOUtil.copy( (InputStream) null, (OutputStream) null );
    }

    @Test( expected = NullPointerException.class )
    public void copyNullInputStreamValidOutputStream()
        throws Exception
    {
        IOUtil.copy( (InputStream) null, new DontCloseByteArrayOutputStream() );
    }

    @Test
    public void copyEmptyInputStreamNullOutputStream()
        throws Exception
    {
        IOUtil.copy( new DontCloseByteArrayInputStream( new byte[0] ), (OutputStream) null );
    }

    @Test
    public void copyEmptyInputStreamValidOutputStream()
        throws Exception
    {
        IOUtil.copy( new DontCloseByteArrayInputStream( new byte[0] ), new DontCloseByteArrayOutputStream());
    }

    @Test
    public void copyInputStreamValidOutputStream()
        throws Exception
    {
        ByteArrayOutputStream outputStream = new DontCloseByteArrayOutputStream();
        byte[] input = { 1, 2, 3, 4, 5, 6 };
        IOUtil.copy( new DontCloseByteArrayInputStream( input ), outputStream );
        assertThat( outputStream.toByteArray(), is( input ) );
    }

    @Test( expected = NegativeArraySizeException.class )
    public void copyNullInputStreamNullOutputStreamNegBufSz()
        throws Exception
    {
        IOUtil.copy( (InputStream) null, (OutputStream) null, -1 );
    }

    @Test( expected = NegativeArraySizeException.class )
    public void copyNullInputStreamValidOutputStreamNegBufSz()
        throws Exception
    {
        IOUtil.copy( (InputStream) null, new DontCloseByteArrayOutputStream(), -1 );
    }

    @Test( expected = NegativeArraySizeException.class )
    public void copyEmptyInputStreamNullOutputStreamNegBufSz()
        throws Exception
    {
        IOUtil.copy( new DontCloseByteArrayInputStream(  new byte[0]), (OutputStream) null, -1 );
    }

    @Test(expected = NegativeArraySizeException.class)
    public void copyEmptyInputStreamValidOutputStreamNegBufSz()
        throws Exception
    {
        IOUtil.copy( new DontCloseByteArrayInputStream(new byte[0]), new DontCloseByteArrayOutputStream(), -1 );
    }

    @Test(expected = NegativeArraySizeException.class)
    public void copyInputStreamValidOutputStreamNegBufSz()
        throws Exception
    {
        ByteArrayOutputStream outputStream = new DontCloseByteArrayOutputStream();
        byte[] input = { 1, 2, 3, 4, 5, 6 };
        IOUtil.copy( new DontCloseByteArrayInputStream( input ), outputStream, -1 );
        assertThat( outputStream.toByteArray(), is( input ) );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyNullInputStreamNullOutputStreamZeroBufSz()
        throws Exception
    {
        IOUtil.copy( (InputStream) null, (OutputStream) null, 0 );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyNullInputStreamValidOutputStreamZeroBufSz()
        throws Exception
    {
        IOUtil.copy( (InputStream) null, new ByteArrayOutputStream(), 0 );
    }

    @Test( timeout = 150 )
    public void copyEmptyInputStreamNullOutputStreamZeroBufSz()
        throws Exception
    {
        IOUtil.copy( new DontCloseByteArrayInputStream( new byte[0]), (OutputStream) null, 0 );
    }

    @Test( timeout = 150 )
    public void copyEmptyInputStreamValidOutputStreamZeroBufSz()
        throws Exception
    {
        IOUtil.copy( new DontCloseByteArrayInputStream( new byte[0]), new DontCloseByteArrayOutputStream(), 0 );
    }

    @Test( timeout = 150 )
    public void copyInputStreamValidOutputStreamZeroBufSz()
        throws Exception
    {
        final AtomicBoolean finished = new AtomicBoolean( false );
        Thread worker = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    ByteArrayOutputStream outputStream = new DontCloseByteArrayOutputStream();
                    byte[] input = { 1, 2, 3, 4, 5, 6 };
                    IOUtil.copy( new DontCloseByteArrayInputStream( input), outputStream, 0 );
                }
                catch ( IOException e )
                {
                    // ignore
                }
                finished.set( true );
            }
        };
        worker.start();
        worker.join( 100 );
        worker.interrupt();
        assertThat( "We have an infinite loop", finished.get(), is( false ) );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyNullInputStreamNullOutputStreamPosBufSz()
        throws Exception
    {
        IOUtil.copy( (InputStream) null, (OutputStream) null, 1 );
    }

    @Test( expected = NullPointerException.class, timeout = 150 )
    public void copyNullInputStreamValidOutputStreamPosBufSz()
        throws Exception
    {
        IOUtil.copy( (InputStream) null, new ByteArrayOutputStream(), 1 );
    }

    @Test( timeout = 150 )
    public void copyEmptyInputStreamNullOutputStreamPosBufSz()
        throws Exception
    {
        IOUtil.copy( new DontCloseByteArrayInputStream( new byte[0] ), (OutputStream) null, 1 );
    }

    @Test( timeout = 150 )
    public void copyEmptyInputStreamValidOutputStreamPosBufSz()
        throws Exception
    {
        IOUtil.copy( new DontCloseByteArrayInputStream( new byte[0]), new DontCloseByteArrayOutputStream(), 1 );
    }

    @Test( timeout = 150 )
    public void copyInputStreamValidOutputStreamPosBufSz()
        throws Exception
    {
        ByteArrayOutputStream outputStream = new DontCloseByteArrayOutputStream();
        byte[] input = { 1, 2, 3, 4, 5, 6 };
        IOUtil.copy( new DontCloseByteArrayInputStream( input ), outputStream, 1 );
        assertThat( outputStream.toByteArray(), is( input ) );
    }

    private static class DontCloseByteArrayInputStream
        extends ByteArrayInputStream
    {
        public DontCloseByteArrayInputStream( byte[] input )
        {
            super( input );
        }

        @Override
        public void close()
            throws IOException
        {
            throw new UnsupportedOperationException( "should not be called" );
        }
    }

    private static class DontCloseByteArrayOutputStream
        extends ByteArrayOutputStream
    {
        @Override
        public void close()
            throws IOException
        {
            throw new UnsupportedOperationException( "should not be called" );
        }
    }
}
