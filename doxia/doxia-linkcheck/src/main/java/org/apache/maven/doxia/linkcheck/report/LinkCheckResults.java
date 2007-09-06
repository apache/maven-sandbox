package org.apache.maven.doxia.linkcheck.report;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the complete result of a LinkCheck.
 *
 * @author ltheussl
 * @version $Id$
 */
public class LinkCheckResults
{
    /** Maps filenames to lists of LinkCheckResults. */
    private Map files;

    /**
     * Constructor.
     */
    public LinkCheckResults()
    {
        files = new HashMap();
    }

    /**
     * Returns a list of {@link org.apache.maven.doxia.linkcheck.LinkCheckResult}s
     * for the given file.
     *
     * @param file the file name.
     * @return List. Null, if the file name is not found in the Map of files.
     */
    public List getUnsuccessful( String file )
    {
        List unsuccessful = null;

        if ( this.files.containsKey( file ) )
        {
            unsuccessful = (List) this.files.get( file );
        }

        return unsuccessful;
    }

    /**
     * Adds a list of {@link org.apache.maven.doxia.linkcheck.LinkCheckResult}s
     * to the Map of files.
     *
     * @param file the file name.
     * @param unsuccessful the list to add.
     */

    public void addUnsuccessful( String file, List unsuccessful )
    {
        if ( !unsuccessful.isEmpty() )
        {
            this.files.put( file, unsuccessful );
        }
    }

    /**
     * Returns the Map of files.
     *
     * @return Map.
     */
    public Map getFiles()
    {
        return files;
    }

    /**
     * Sets the Map of files.
     *
     * @param files the map to set.
     */
    public void setFiles( Map files )
    {
        this.files = files;
    }
}
