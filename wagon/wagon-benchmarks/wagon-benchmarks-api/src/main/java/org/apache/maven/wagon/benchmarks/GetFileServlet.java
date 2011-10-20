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
import org.eclipse.jetty.http.gzip.GzipResponseWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * @author Olivier Lamy
 */
public class GetFileServlet
    extends HttpServlet
{

    public static boolean compressResponse;

    public static byte[] responseContent;

    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( compressResponse )
        {
            res.addHeader( "Content-Encoding", "gzip" );
            res.getOutputStream().write( compressStringWithGZIP( responseContent ) );
        }
        else
        {
            res.getOutputStream().write( responseContent );
            res.getOutputStream().flush();
        }
    }

    public static byte[] compressStringWithGZIP( byte[] unCompress )
        throws IOException
    {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream( buffer );
        out.write( unCompress );
        out.finish();
        ByteArrayInputStream bais = new ByteArrayInputStream( buffer.toByteArray() );
        return IOUtils.toByteArray( bais );

    }

}
