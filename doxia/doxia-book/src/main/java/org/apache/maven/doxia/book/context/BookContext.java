package org.apache.maven.doxia.book.context;

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

import org.apache.maven.doxia.book.model.BookModel;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BookContext
{
    private BookModel book;

    private Map files;

    private File outputDirectory;

    private BookIndex index;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static class BookFile
    {
        private File file;

        private String parserId;

        public BookFile( File file, String parserId )
        {
            this.file = file;
            this.parserId = parserId;
        }

        public File getFile()
        {
            return file;
        }

        public String getParserId()
        {
            return parserId;
        }
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public BookModel getBook()
    {
        return book;
    }

    public void setBook( BookModel book )
    {
        this.book = book;
    }

    public Map getFiles()
    {
        if ( files == null )
        {
            files = new HashMap();
        }

        return files;
    }

    public void setFiles( Map files )
    {
        this.files = files;
    }

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    public BookIndex getIndex()
    {
        return index;
    }

    public void setIndex( BookIndex index )
    {
        this.index = index;
    }
}
