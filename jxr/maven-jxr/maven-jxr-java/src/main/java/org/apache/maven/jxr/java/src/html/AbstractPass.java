package org.apache.maven.jxr.java.src.html;

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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.apache.maven.jxr.java.src.GenerateHTMLJavaSrc;
import org.apache.maven.jxr.java.src.JavaSrcOptions;

/**
 * Abstract class for Pass.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
abstract class AbstractPass
{
    protected static final String DEFAULT_EXCLUDES = "**/*~,**/#*#,**/.#*,**/%*%,**/._*,**/CVS,**/CVS/**,"
        + "**/.cvsignore,**/SCCS,**/SCCS/**,**/vssver.scc,**/.svn,**/.svn/**,**/.DS_Store";

    protected static final String DEFAULT_DOCENCODING = "ISO-8859-1";

    private JavaSrcOptions options;

    /**
     * Default constructor
     *
     * @param options object
     */
    AbstractPass( JavaSrcOptions options )
    {
        if ( options == null )
        {
            throw new IllegalArgumentException( "options could not be null" );
        }
        this.options = options;
    }

    /**
     * @return the output dir
     * @see JavaSrcOptions#getDestDir()
     */
    String getDestDir()
    {
        return this.options.getDestDir();
    }

    /**
     * @return a String list of source dir.
     * @see JavaSrcOptions#getSrcDirs()
     */
    List getSrcDirs()
    {
        return this.options.getSrcDirs();
    }

    /**
     * Getter for the javasrc options
     *
     * @return the options
     */
    JavaSrcOptions getOptions()
    {
        return this.options;
    }

    /**
     * Returns the path to the top level of the source hierarchy from the files
     * of a given class.
     *
     * @param packageName the package to get the backup path for
     * @return
     * @returns the path from the package to the top level, as a string
     */
    static String getBackupPath( String packageName )
    {
        StringTokenizer st = new StringTokenizer( packageName, "." );
        String backup = "";
        int dirs = 0;

        dirs = st.countTokens();
        for ( int j = 0; j < dirs; j++ )
        {
            backup = backup + "../";
        }

        return backup;
    }

    static void println( String description )
    {
        System.out.print( "\n" );
        System.out.print( description );
    }

    /**
     * Method createDirs
     *
     * @param f
     */
    static void createDirs( File f )
    {
        String parentDir = f.getParent();
        File directory = new File( parentDir );

        if ( !directory.exists() )
        {
            directory.mkdirs();
        }
    }

    /**
     * @return an HTML comment with generated time.
     */
    static String getGeneratedBy()
    {
        return "<!-- Generated by Maven JXR " + GenerateHTMLJavaSrc.VERSION + " on "
            + new GregorianCalendar( TimeZone.getDefault() ).getTime().toString() + " -->";
    }
}
