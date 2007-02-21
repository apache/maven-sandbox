package org.apache.maven.doxia.editor;

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

import org.apache.maven.doxia.editor.model.DoxiaAttribute;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class EditorDocumentListener
    implements DocumentListener
{
    // ----------------------------------------------------------------------
    // DocumentListener Implementation
    // ----------------------------------------------------------------------

    public void changedUpdate( DocumentEvent e )
    {
//        System.out.println( "EditorDocumentListener.changedUpdate" );
//
//        System.out.println( "e.getOffset() = " + e.getOffset() );
//        System.out.println( "e.getLength() = " + e.getLength() );
//        System.out.println( "e.getType() = " + e.getType() );
//
//        Element element = ( (StyledDocument) e.getDocument() ).getCharacterElement( e.getOffset() );
//
//        Object type = element.getAttributes().getAttribute( DoxiaAttribute.TYPE );
//
//        System.out.println( "type = " + type );
    }

    public void insertUpdate( DocumentEvent e )
    {
//        System.out.println( "EditorDocumentListener.insertUpdate" );
//
//        System.out.println( "e.getOffset() = " + e.getOffset() );
//        System.out.println( "e.getLength() = " + e.getLength() );
//        System.out.println( "e.getType() = " + e.getType() );
    }

    public void removeUpdate( DocumentEvent e )
    {
//        System.out.println( "EditorDocumentListener.removeUpdate" );
//
//        System.out.println( "e.getOffset() = " + e.getOffset() );
//        System.out.println( "e.getLength() = " + e.getLength() );
//        System.out.println( "e.getType() = " + e.getType() );
    }
}
