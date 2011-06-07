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

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.io.InputStreamFacade;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.String;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: stephenc
 * Date: 07/06/2011
 * Time: 22:39
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils
{
    public static final int ONE_KB = 1024;

    public static final int ONE_MB = ONE_KB * 1024;

    public static final int ONE_GB = ONE_MB * 1024;

    public static String FS = System.getProperty( "file.separator" );

    public FileUtils()
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String[] getDefaultExcludes()
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static List getDefaultExcludesAsList()
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String getDefaultExcludesAsString()
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String byteCountToDisplaySize( int byteCount )
    {
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize( byteCount );
    }

    public static String dirname( String name )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String filename( String name )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String basename( String name )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String basename( String s1, String s2 )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String extension( String name )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static boolean fileExists( String name )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String fileRead( String file )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String fileRead( String file, String encoding )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String fileRead( File file )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String fileRead( File file, String encoding )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void fileAppend( String file, String content )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void fileAppend( String file, String content, String encoding )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void fileWrite( String file, String content )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void fileWrite( String file, String content, String encoding )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void fileWrite( File file, String content )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void fileWrite( File file, String content, String encoding )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void fileDelete( String file )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static boolean waitFor( String file, int duration )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static boolean waitFor( File file, int duration )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static File getFile( String file )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String[] getFilesFromExtension( String file, String[] extensions )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void mkdir( String name )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static boolean contentEquals( File file1, File file2 )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static File toFile( java.net.URL url )
    {
        return org.apache.commons.io.FileUtils.toFile( url );
    }

    public static java.net.URL[] toURLs( File[] files )
        throws IOException
    {
        return org.apache.commons.io.FileUtils.toURLs( files );
    }

    public static String removeExtension( String filename )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String getExtension( String filename )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String removePath( String filename )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String removePath( String filename, char separatorChar )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String getPath( String filename )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String getPath( String filename, char separatorChar )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyFileToDirectory( String sourceFile, String destDir )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyFileToDirectoryIfModified( String sourceFile, String destDir )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyFileToDirectory( File sourceFile, File destDir )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyFileToDirectoryIfModified( File sourceFile, File destDir )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyFile( File sourceFile, File destFile )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static boolean copyFileIfModified( File sourceFile, File destFile )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyURLToFile( java.net.URL url, File file )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyStreamToFile( InputStreamFacade stream, File file )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String normalize( String filename )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static String catPath( String s1, String s2 )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static File resolveFile( File file, String string )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void forceDelete( String file )
        throws IOException
    {
        org.apache.commons.io.FileUtils.forceDelete( new File( file ) );
    }

    public static void forceDelete( File file )
        throws IOException
    {
        org.apache.commons.io.FileUtils.forceDelete( file );
    }

    public static void forceDeleteOnExit( File file )
        throws IOException
    {
        org.apache.commons.io.FileUtils.forceDeleteOnExit( file );
    }

    public static void forceMkdir( File directory )
        throws IOException
    {
        org.apache.commons.io.FileUtils.forceMkdir( directory );
    }

    public static void deleteDirectory( String directory )
        throws IOException
    {
        org.apache.commons.io.FileUtils.deleteDirectory( new File( directory ) );
    }

    public static void deleteDirectory( File directory )
        throws IOException
    {
        org.apache.commons.io.FileUtils.deleteDirectory( directory );
    }

    public static void cleanDirectory( String file )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void cleanDirectory( File file )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static long sizeOfDirectory( String directory )
    {
        return org.apache.commons.io.FileUtils.sizeOfDirectory( new File(directory) );
    }

    public static long sizeOfDirectory( File directory )
    {
        return org.apache.commons.io.FileUtils.sizeOfDirectory( directory );
    }

    public static List getFiles( File file, String include, String exclude )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static List getFiles( File file, String include, String exclude, boolean includeDirs )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static List getFileNames( File file, String include, String exclude, boolean includeDirs )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static List getFileNames( File file, String include, String exclude, boolean includeDirs, boolean includeFiles )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static List getDirectoryNames( File f, String i, String e, boolean b )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static List getDirectoryNames( File f, String i, String e, boolean b1, boolean b2 )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static List getFileAndDirectoryNames( File f, String i, String e, boolean b1,
                                                           boolean b2, boolean b3, boolean b4 )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyDirectory( File sourceDir, File destDir )
        throws IOException
    {
        org.apache.commons.io.FileUtils.copyDirectory( sourceDir, destDir );
    }

    public static void copyDirectory( File sourceDir, File destDir, String include, String exclude )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyDirectoryLayout( File sourceDir, File destDir, String[] includes, String[] excludes )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyDirectoryStructure( File sourceDir, File destDir )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyDirectoryStructureIfModified( File sourceDir, File destDir )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void rename( File oldName, File newName )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static File createTempFile( String s1, String s2, File dir )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyFile( File source, File dest, String s,
                                 FilterWrapper[] wrappers)
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static void copyFile( File source, File dest, String s,
                                 FilterWrapper[] wrappers, boolean b)
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static List loadFile( File file )
        throws IOException
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public static boolean isValidWindowsFileName( File file )
    {
        throw new UnsupportedOperationException( "TODO: Implement" );
    }

    public abstract class FilterWrapper {
        public FilterWrapper(){}
        public abstract Reader getReader(Reader reader);
    }
}
