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
import java.io.UnsupportedEncodingException;

/**
 * Class LineOutputWriter
 *
 * @version $Id$
 */
class LineOutputWriter
    extends HTMLOutputWriter
{
    /**
     * The max number of line number digits.
     * Sadly, the pipe pattern doesn't let us use the real value
     * discovered at runtime.
     * No class file should have more than 9999 lines anyway.
     */
    private static final int MAX_LINE_NUMBER_DIGITS = 5;

    /** Field _lineNumber */
    private int _lineNumber;

    /** Field _firstLine */
    private boolean _firstLine;

    /**
     * Constructor LineOutputWriter
     *
     * @param output
     */
    LineOutputWriter( OutputStream output )
    {
        super( output );

        _lineNumber = 1;
        _firstLine = true;
    }

    /**
     * Constructor LineOutputWriter
     *
     * @param output
     */
    LineOutputWriter( OutputStream output, String charsetName )
        throws UnsupportedEncodingException
    {
        super( output, charsetName );

        _lineNumber = 1;
        _firstLine = true;
    }

    /** {@inheritDoc} */
    public void write( int c )
        throws IOException
    {
        if ( _firstLine )
        {
            _firstLine = false;

            writeLineNumber();
        }

        super.write( c );

        if ( c == '\n' )
        {
            writeLineNumber();
        }
    }

    /** {@inheritDoc} */
    public void writeHTML( int c )
        throws IOException
    {
        if ( _firstLine )
        {
            _firstLine = false;

            writeLineNumber();
        }

        super.writeHTML( c );

        if ( c == '\n' )
        {
            writeLineNumber();
        }
    }

    /** {@inheritDoc} */
    public void write( String s )
        throws IOException
    {
        int length = s.length();

        for ( int i = 0; i < length; i++ )
        {
            this.write( s.charAt( i ) );
        }
    }

    /** {@inheritDoc} */
    public void writeHTML( String s )
        throws IOException
    {
        int length = s.length();

        for ( int i = 0; i < length; i++ )
        {
            this.writeHTML( s.charAt( i ) );
        }
    }

    /**
     * Method writeLineNumber
     *
     * @throws IOException
     */
    void writeLineNumber()
        throws IOException
    {
        String lineString = Integer.toString( _lineNumber );
        String spacing = "";

        for ( int i = 0; i < MAX_LINE_NUMBER_DIGITS - lineString.length(); i++ )
        {
            spacing += " ";
        }

        // add linenum tag
        lineString = "<a href=\"#" + _lineNumber + "\" name=\"" + lineString + "\" class=\"linenum\">" + lineString;
        lineString += "</a>";
        lineString += spacing;

        this.write( lineString );

        _lineNumber++;
    }
}
