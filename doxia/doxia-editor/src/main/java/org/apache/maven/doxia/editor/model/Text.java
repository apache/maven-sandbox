package org.apache.maven.doxia.editor.model;

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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Text
    extends LeafElement
{
    private String text;

    private boolean bold;

    private boolean italic;

    private boolean monospaced;

    public Text( String text, boolean bold, boolean italic, boolean monospaced )
    {
        this.text = text;
        this.bold = bold;
        this.italic = italic;
        this.monospaced = monospaced;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public boolean isBold()
    {
        return bold;
    }

    public void setBold( boolean bold )
    {
        this.bold = bold;
    }

    public boolean isItalic()
    {
        return italic;
    }

    public void setItalic( boolean italic )
    {
        this.italic = italic;
    }

    public boolean isMonospaced()
    {
        return monospaced;
    }

    public void setMonospaced( boolean monospaced )
    {
        this.monospaced = monospaced;
    }
}
