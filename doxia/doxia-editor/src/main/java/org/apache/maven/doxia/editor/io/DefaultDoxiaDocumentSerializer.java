package org.apache.maven.doxia.editor.io;

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

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.editor.Application;
import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import javax.swing.text.Document;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultDoxiaDocumentSerializer
    extends AbstractLogEnabled
    implements DoxiaDocumentSerializer
{
    private Application application;

    private Doxia doxia;

    // ----------------------------------------------------------------------
    // DoxiaDocumentSerializer Implementation
    // ----------------------------------------------------------------------

    public void serialize( Document document, String sinkId )
        throws ParserNotFoundException, ParseException, IOException
    {
        XdocSink sink = new XdocSink( new FileWriter( "/tmp/document.xdoc" ) );

        DocumentParser.document.set( application.getEditorWindow().getDocument() );

        List list = new ArrayList();
        list.add( DebugSink.newInstance() );
//        list.add( new WellformednessCheckingSink() );
        list.add( sink );

        try
        {
            doxia.parse( null, "doxia-document", PipelineSink.newInstance( list ) );
        }
        finally
        {
            DocumentParser.document.set( null );
        }
    }
}
