package org.apache.maven.jxr.java.src.html;

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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Class HTMLOutputWriter
 *
 * @version $Id$
 */
class HTMLOutputWriter
    extends OutputStreamWriter
{
    /**
     * Constructor HTMLOutputWriter
     *
     * @param output
     */
    HTMLOutputWriter( OutputStream output )
    {
        super( output );
    }

    /**
     * @param output
     * @param charsetName
     * @throws UnsupportedEncodingException
     */
    HTMLOutputWriter( OutputStream output, String charsetName )
        throws UnsupportedEncodingException
    {
        super( output, charsetName );
    }

    /**
     * Method writeHTML
     *
     * @param c
     * @throws IOException
     */
    void writeHTML( int c )
        throws IOException
    {

        switch ( c )
        {

            case '<':
                this.write( "&lt;" );
                break;

            case '>':
                this.write( "&gt;" );
                break;

            default:
                super.write( c );
                break;
        }
    }

    /**
     * Method writeHTML
     *
     * @param s
     * @throws IOException
     */
    void writeHTML( String s )
        throws IOException
    {

        for ( int i = 0; i < s.length(); i++ )
        {
            this.writeHTML( s.charAt( i ) );
        }
    }
}
