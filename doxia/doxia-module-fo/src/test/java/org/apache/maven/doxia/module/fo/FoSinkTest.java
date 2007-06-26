package org.apache.maven.doxia.module.fo;

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

import java.io.File;
import java.io.Reader;
import java.io.FileWriter;
import java.io.Writer;


import org.apache.maven.doxia.module.apt.AptParser;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.AbstractSinkTestCase;

/**
 * <code>FO Sink</code> Test case.
 */
public class FoSinkTest extends AbstractSinkTestCase
{

    public void testConvertFO2PDF() throws Exception
    {
        // first create fo file from apt
        new AptParser().parse( getTestReader(), createSink() );

        // then generate PDF
        fo2pdf( "test" );
    }

    public void testAggregateMode() throws Exception
    {
        AptParser parser = new AptParser();
        Reader source = getTestReader();
        FoSink fosink = new FoSink( getFOTestWriter( "aggregate" ), true );
        fosink.beginDocument();
        parser.parse( source, fosink );
        // re-use the same source
        source = getTestReader();
        parser.parse( source, fosink );
        fosink.endDocument();

        // then generate PDF
        fo2pdf( "aggregate" );
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "fo";
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        // fo parser?
        return new AptParser();
    }

    /** {@inheritDoc} */
    protected Sink createSink() throws Exception
    {
        return new FoSink( getTestWriter() );
    }

    private void fo2pdf( String baseName ) throws Exception
    {
        File outputDirectory = new File( getBasedirFile(), "target/output" );
        File resourceDirectory = new File( getBasedirFile(), "target/test-classes" );
        File foFile = new File( outputDirectory, baseName + "." + outputExtension() );
        File pdfFile = new File( outputDirectory, baseName + ".pdf" );
        FoTestUtils.convertFO2PDF( foFile, pdfFile, resourceDirectory.getCanonicalPath() );
    }

    private Writer getFOTestWriter( String baseName )
        throws Exception
    {
        File outputDirectory = new File( getBasedirFile(), "target/output" );

        if ( !outputDirectory.exists() )
        {
            outputDirectory.mkdirs();
        }

        return new FileWriter( new File( outputDirectory, baseName + "." + outputExtension() ) );
    }


}
