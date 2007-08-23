package org.apache.maven.doxia.editor.action;

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
import org.apache.maven.doxia.editor.io.DebugSink;
import org.apache.maven.doxia.editor.io.EditorSink;
import org.apache.maven.doxia.sink.PipelineSink;
import org.apache.maven.doxia.editor.model.DoxiaDocument;

import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class OpenDocumentAction
    extends AbstractDoxiaAction
{
    public void doAction( ActionEvent event )
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Load the model
        // ----------------------------------------------------------------------

        Doxia doxia = getApplication().getDoxia();

        EditorSink editorSink = new EditorSink();

        List pipeline = new ArrayList();
        pipeline.add( DebugSink.newInstance() );
        pipeline.add( editorSink );

        doxia.parse( new FileReader( "src/test/apt/test.apt" ), "apt", PipelineSink.newInstance( pipeline ) );

        DoxiaDocument document = editorSink.getDocument();

        getApplication().getDoxiaDocumentBuilder().loadDocument( document, getApplication().getEditorWindow().getDocument() );

        getApplication().getEditorWindow().setTitle( document.getTitle().getText() );
    }
}
