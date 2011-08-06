package org.codehaus.plexus.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

/**
 * Expand will unpack the given zip archive.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class Expand
{
    /**
     * Source file which should get expanded
     */
    private File source;

    /**
     * destination directory
     */
    private File dest;

    /**
     * if the unpackaging should get performed if the destination already exists.
     */
    private boolean overwrite = false;


    /**
     * The zip archive which should get expanded.
     *
     * @param sourceArchive
     */
    public void setSrc( File sourceArchive )
    {
        this.source = sourceArchive;
    }

    /**
     * Set the destination directory into which the archive should get expanded.
     * The directory will get created if it doesn't yet exist
     * while executing the expand.
     *
     * @param destinationDirectory
     */
    public void setDest( File destinationDirectory )
    {
        this.dest = destinationDirectory;
    }

    /**
     * If the destination directory should get overwritten if the content
     * already exists.
     * @param overwrite
     */
    public void setOverwrite( boolean overwrite )
    {
        this.overwrite = overwrite;
    }

    /**
     * Actually perform the unpacking of the source archive
     * into the destination directory.
     *
     * @throws Exception
     */
    public void execute() throws Exception
    {
        //X TODO implement
        throw new RuntimeException( "not yet implemented!" );
    }


}
