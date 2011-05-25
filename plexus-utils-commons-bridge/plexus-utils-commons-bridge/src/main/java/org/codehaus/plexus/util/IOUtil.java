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

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public final class IOUtil
{
    private IOUtil()
    {
        throw new IllegalAccessError( "Utility class" );
    }

    public static void copy( java.io.InputStream input, java.io.OutputStream output )
        throws java.io.IOException
    {
        IOUtils.copy( input, output );
    }

    public static void copy( java.io.InputStream input, java.io.OutputStream output, int bufferSize )
        throws java.io.IOException
    {
        if ( bufferSize < 0 )
        {
            throw new NegativeArraySizeException();
        }
        input.getClass();
        if ( IOUtils.copy( input, output ) > 0 )
        {
            // don't you just love recreating buggy behaviour for compatibility's sake
            fakeBufferSizeHandler( bufferSize );
        }
    }

    public static void copy( java.io.Reader input, java.io.Writer output )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( java.io.Reader input, java.io.Writer output, int bufferSize )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( java.io.InputStream input, java.io.Writer output )
        throws java.io.IOException
    {
        input.getClass();
        output.getClass();
        IOUtils.copy( input, output );
    }

    public static void copy( java.io.InputStream input, java.io.Writer output, int bufferSize )
        throws java.io.IOException
    {
        input.getClass();
        if ( bufferSize < 0 )
        {
            throw new NegativeArraySizeException();
        }
        output.getClass();
        fakeBufferSizeHandler( bufferSize );
        IOUtils.copy( input, output );
    }

    public static void copy( java.io.InputStream input, java.io.Writer output, java.lang.String encoding )
        throws java.io.IOException
    {
        input.getClass();
        encoding.getClass(); // throw NPE if null
        try
        {
            Charset.forName( encoding ); // validate charset before checking buffer size.
        }
        catch ( UnsupportedCharsetException e )
        {
            throw new UnsupportedEncodingException( e.getLocalizedMessage() );
        }
        output.getClass();
        IOUtils.copy( input, output, encoding );
    }

    public static void copy( java.io.InputStream input, java.io.Writer output, java.lang.String encoding,
                             int bufferSize )
        throws java.io.IOException
    {
        input.getClass();
        encoding.getClass(); // throw NPE if null
        try
        {
            Charset.forName( encoding ); // validate charset before checking buffer size.
        }
        catch ( UnsupportedCharsetException e )
        {
            throw new UnsupportedEncodingException( e.getLocalizedMessage() );
        }
        if ( bufferSize < 0 )
        {
            throw new NegativeArraySizeException();
        }
        output.getClass();
        fakeBufferSizeHandler( bufferSize );
        IOUtils.copy( input, output, encoding );
    }

    public static java.lang.String toString( java.io.InputStream input )
        throws java.io.IOException
    {
        return IOUtils.toString( input );
    }

    public static java.lang.String toString( java.io.InputStream input, int bufferSize )
        throws java.io.IOException
    {
        input.getClass(); // throw NPE if null
        fakeBufferSizeHandler( bufferSize );
        return IOUtils.toString( input );
    }

    public static java.lang.String toString( java.io.InputStream input, java.lang.String encoding )
        throws java.io.IOException
    {
        input.getClass(); // throw NPE if null
        encoding.getClass(); // throw NPE if null
        return IOUtils.toString( input, encoding );
    }

    public static java.lang.String toString( java.io.InputStream input, java.lang.String encoding, int bufferSize )
        throws java.io.IOException
    {
        input.getClass(); // throw NPE if null
        encoding.getClass(); // throw NPE if null
        try
        {
            Charset.forName( encoding ); // validate charset before checking buffer size.
        }
        catch ( UnsupportedCharsetException e )
        {
            throw new UnsupportedEncodingException( e.getLocalizedMessage() );
        }
        fakeBufferSizeHandler( bufferSize );
        return IOUtils.toString( input, encoding );
    }

    public static byte[] toByteArray( java.io.InputStream input )
        throws java.io.IOException
    {
        return IOUtils.toByteArray( input );
    }

    public static byte[] toByteArray( java.io.InputStream input, int bufferSize )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( java.io.Reader input, java.io.OutputStream output )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( java.io.Reader input, java.io.OutputStream output, int bufferSize )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static java.lang.String toString( java.io.Reader input )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static java.lang.String toString( java.io.Reader input, int bufferSize )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static byte[] toByteArray( java.io.Reader input )
        throws java.io.IOException
    {
        return IOUtils.toByteArray( input );
    }

    public static byte[] toByteArray( java.io.Reader input, int bufferSize )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( java.lang.String input, java.io.OutputStream output )
        throws java.io.IOException
    {
        input.getClass();
        IOUtils.write( input, output );
    }

    public static void copy( java.lang.String input, java.io.OutputStream output, int bufferSize )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( java.lang.String input, java.io.Writer output )
        throws java.io.IOException
    {
        output.getClass();
        IOUtils.write( input, output );
    }

    /**
     * @deprecated
     */
    public static void bufferedCopy( java.io.InputStream input, java.io.OutputStream output )
        throws java.io.IOException
    {
        if (input == null) throw new IOException( "stream closed" );
        output.getClass();
        IOUtils.copy( new BufferedInputStream( input ), output );
    }

    public static byte[] toByteArray( java.lang.String input )
        throws java.io.IOException
    {
        return IOUtils.toByteArray( input );
    }

    public static byte[] toByteArray( java.lang.String input, int bufferSize )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( byte[] input, java.io.Writer output )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( byte[] input, java.io.Writer output, int bufferSize )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( byte[] input, java.io.Writer output, java.lang.String encoding )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static void copy( byte[] input, java.io.Writer output, java.lang.String encoding, int bufferSize )
        throws java.io.IOException
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public static java.lang.String toString( byte[] input )
        throws java.io.IOException
    {
        return IOUtils.toString( input );
    }

    public static java.lang.String toString( byte[] input, int bufferSize )
        throws java.io.IOException
    {
        input.getClass(); // throw NPE if null
        fakeBufferSizeHandler( bufferSize );
        return IOUtils.toString( input );
    }

    public static java.lang.String toString( byte[] input, java.lang.String encoding )
        throws java.io.IOException
    {
        input.getClass(); // throw NPE if null
        encoding.getClass(); // throw NPE if null
        return IOUtils.toString( input, encoding );
    }

    public static java.lang.String toString( byte[] input, java.lang.String encoding, int bufferSize )
        throws java.io.IOException
    {
        input.getClass(); // throw NPE if null
        encoding.getClass(); // throw NPE if null
        try
        {
            Charset.forName( encoding ); // validate charset before checking buffer size.
        }
        catch ( UnsupportedCharsetException e )
        {
            throw new UnsupportedEncodingException( e.getLocalizedMessage() );
        }
        fakeBufferSizeHandler( bufferSize );
        return IOUtils.toString( input, encoding );
    }

    public static void copy( byte[] input, java.io.OutputStream output )
        throws java.io.IOException
    {
        output.getClass(); // throw NPE if null
        IOUtils.copy( new ByteArrayInputStream( input ), output );
    }

    public static void copy( byte[] input, java.io.OutputStream output, int bufferSize )
        throws java.io.IOException
    {
        output.getClass(); // throw NPE if null
        IOUtils.copy( new ByteArrayInputStream( input ), output );
    }

    public static boolean contentEquals( java.io.InputStream input1, java.io.InputStream input2 )
        throws java.io.IOException
    {
        return IOUtils.contentEquals( input1, input2 );
    }

    public static void close( java.io.InputStream inputStream )
    {
        IOUtils.closeQuietly( inputStream );
    }

    public static void close( java.io.OutputStream outputStream )
    {
        IOUtils.closeQuietly( outputStream );
    }

    public static void close( java.io.Reader reader )
    {
        IOUtils.closeQuietly( reader );
    }

    public static void close( java.io.Writer writer )
    {
        IOUtils.closeQuietly( writer );
    }

    /**
     * Throws a NegativeArraySizeException if bufferSize is negative, infinite-loops if bufferSize is zero,
     * otherwise no-op as Commons IO handles buffering for us.
     *
     * @param bufferSize the buffer size.
     * @throws IOException                if interrupted in infinite loop.
     * @throws NegativeArraySizeException if buffer size less than zero.
     */
    private static void fakeBufferSizeHandler( int bufferSize )
        throws IOException
    {
        if ( bufferSize < 0 )
        {
            throw new NegativeArraySizeException();
        }
        while ( bufferSize == 0 )
        {
            try
            {
                Thread.sleep( 1 );
            }
            catch ( InterruptedException e )
            {
                IOException ex = new IOException( e.getLocalizedMessage() );
                ex.initCause( e );
                throw ex;
            }
        }
    }

}