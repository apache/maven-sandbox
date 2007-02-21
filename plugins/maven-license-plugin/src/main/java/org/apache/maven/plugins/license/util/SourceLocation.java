package org.apache.maven.plugins.license.util;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * SourceLocation, a datastorage object for passing around Source Location References. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class SourceLocation
{
    private int beginLine;

    private int endLine;

    private int beginColumn;

    private int endColumn;

    public SourceLocation( int lineBegin, int columnBegin, int lineEnd, int columnEnd )
    {
        this.beginLine = lineBegin;
        this.beginColumn = columnBegin;
        this.endLine = lineEnd;
        this.endColumn = columnEnd;
    }

    public int getEndColumn()
    {
        return endColumn;
    }

    public void setEndColumn( int endColumn )
    {
        this.endColumn = endColumn;
    }

    public int getEndLine()
    {
        return endLine;
    }

    public void setEndLine( int endLine )
    {
        this.endLine = endLine;
    }

    public int getBeginColumn()
    {
        return beginColumn;
    }

    public void setBeginColumn( int startColumn )
    {
        this.beginColumn = startColumn;
    }

    public int getBeginLine()
    {
        return beginLine;
    }

    public void setBeginLine( int startLine )
    {
        this.beginLine = startLine;
        this.endLine = Math.max( this.beginLine, this.endLine );
    }
}
