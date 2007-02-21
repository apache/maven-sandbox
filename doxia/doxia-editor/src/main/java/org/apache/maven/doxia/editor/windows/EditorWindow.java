package org.apache.maven.doxia.editor.windows;

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

import org.apache.maven.doxia.editor.Application;
import org.apache.maven.doxia.editor.EditorDocumentListener;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class EditorWindow
    extends JFrame
{
    private Application application;

    private JToolBar toolBar;

    private JTextPane textPane;

    private Document document;

    public EditorWindow( Application application )
        throws HeadlessException
    {
        this.application = application;

        // ----------------------------------------------------------------------
        // Initialize the widgets
        // ----------------------------------------------------------------------

        textPane = new JTextPane();

        document = textPane.getDocument();

        document.addDocumentListener( new EditorDocumentListener() );

        toolBar = new JToolBar();
        toolBar.add( application.getActionManager().getAction( "open-document" ) ).setText( "Open" );
        toolBar.add( application.getActionManager().getAction( "save-document" ) ).setText( "Save" );

        getContentPane().add( textPane, BorderLayout.CENTER );
        getContentPane().add( toolBar, BorderLayout.PAGE_START );
        setSize( 600, 500 );
    }


    public Document getDocument()
    {
        return document;
    }
}
