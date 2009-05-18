package org.apache.maven.shared.plugin.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.util.DirectoryScanner;

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
 * Use the builder pattern to configure and execute a DirectoryScanner. <code>iterateOn*</code> methods allow to iterate
 * on the scanner result
 * 
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public class ScannerBuilder
{
    private DirectoryScanner scanner;

    private List includes = new ArrayList();

    private List excludes = new ArrayList();

    public ScannerBuilder( File basedir )
    {
        scanner = new DirectoryScanner();
        scanner.setBasedir( basedir );
    }

    public ScannerBuilder include( String include )
    {
        this.includes.add( include );
        return this;
    }

    public ScannerBuilder include( String[] includes )
    {
        for ( int i = 0; i < includes.length; i++ )
        {
            this.includes.add( includes[i] );
        }
        return this;
    }

    public ScannerBuilder exclude( String exclude )
    {
        this.excludes.add( exclude );
        return this;
    }

    public ScannerBuilder exclude( String[] excludes )
    {
        for ( int i = 0; i < excludes.length; i++ )
        {
            this.excludes.add( excludes[i] );
        }
        return this;
    }

    public ScannerBuilder scan()
    {
        scanner.setIncludes( (String[]) includes.toArray( new String[includes.size()] ) );
        scanner.setExcludes( (String[]) excludes.toArray( new String[excludes.size()] ) );
        scanner.scan();
        return this;
    }

    public ScannerBuilder addDefaultExcludes()
    {
        scanner.addDefaultExcludes();
        return this;
    }

    public ScannerBuilder setCaseSensitive( boolean isCaseSensitive )
    {
        scanner.setCaseSensitive( isCaseSensitive );
        return this;
    }

    public ScannerBuilder setFollowSymlinks( boolean followSymlinks )
    {
        scanner.setFollowSymlinks( followSymlinks );
        return this;
    }

    /**
     * @return Iterator&tl;File&gt;
     */
    public Iterator iterateOnFiles()
    {
        String[] paths = scanner.getIncludedFiles();
        List files = new ArrayList( paths.length );
        for ( int i = 0; i < paths.length; i++ )
        {
            files.add( new File( scanner.getBasedir(), paths[i] ) );
        }
        return files.iterator();
    }

    /**
     * @return Iterator&tl;File&gt;
     */
    public Iterator iterateOnDirectories()
    {
        String[] paths = scanner.getIncludedDirectories();
        List files = new ArrayList( paths.length );
        for ( int i = 0; i < paths.length; i++ )
        {
            files.add( new File( scanner.getBasedir(), paths[i] ) );
        }
        return files.iterator();
    }

    /**
     * @return Iterator&tl;String&gt;
     */
    public Iterator iterateOnPaths()
    {
        String[] paths = scanner.getIncludedFiles();
        return Arrays.asList( paths ).iterator();
    }

    public String[] getDeselectedDirectories()
    {
        return scanner.getDeselectedDirectories();
    }

    public String[] getDeselectedFiles()
    {
        return scanner.getDeselectedFiles();
    }

    public String[] getExcludedDirectories()
    {
        return scanner.getExcludedDirectories();
    }

    public String[] getExcludedFiles()
    {
        return scanner.getExcludedFiles();
    }

    public String[] getIncludedDirectories()
    {
        return scanner.getIncludedDirectories();
    }

    public String[] getIncludedFiles()
    {
        return scanner.getIncludedFiles();
    }

    public String[] getNotIncludedDirectories()
    {
        return scanner.getNotIncludedDirectories();
    }

    public String[] getNotIncludedFiles()
    {
        return scanner.getNotIncludedFiles();
    }

    public boolean isEverythingIncluded()
    {
        return scanner.isEverythingIncluded();
    }

}
