package org.apache.maven.jxr.java.src;

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

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Bean with all options supported by <code>JavaSrc</code> class.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class JavaSrcOptions
{
    /** Singleton pattern */
    private static JavaSrcOptions singleton;

    /** Specifies the text to be placed at the bottom of each output file. */
    private String bottom;

    /** Output dir, required. */
    private String destDir;

    /** Specifies the encoding of the generated HTML files. */
    private String docencoding;

    /** Specifies the title to be placed near the top of the overview summary file. */
    private String doctitle;

    /** Source file encoding name. */
    private String encoding;

    /** Specifies the footer text to be placed at the bottom of each output file. */
    private String footer;

    /** Specifies the header text to be placed at the top of each output file. */
    private String header;

    /** Specifies the text for upper left frame. */
    private String packagesheader;

    /** Specify recursive pass, true by default. */
    private boolean recurse = true;

    /** List of source dir as String, required. */
    private List srcDirs;

    /** Specifies the path of an alternate HTML stylesheet file. */
    private String stylesheetfile;

    /** Specifies the top text to be placed at the top of each output file. */
    private String top;

    /** Specify verbose information */
    private boolean verbose;

    /** Specifies the title to be placed in the HTML title tag. */
    private String windowtitle;

    // TODO add no* options a la javadoc

    private JavaSrcOptions()
    {
        // nop
    }

    /**
     * @return a singleton instance of <code>Configuration</code>.
     */
    public static JavaSrcOptions getInstance()
    {
        if ( singleton == null )
        {
            singleton = new JavaSrcOptions();
        }

        return singleton;
    }

    /**
     * @return all required and optional options
     */
    public static String getOptions()
    {
        StringBuffer sb = new StringBuffer();

        // Required options
        sb.append( "-DsrcDir=... " );
        sb.append( "-DdestDir=... " );
        sb.append( "\n" );

        // Optional options
        Field[] fields = JavaSrcOptions.class.getDeclaredFields();
        for ( int i = 0; i < fields.length; i++ )
        {
            String name = fields[i].getName();
            if ( ( name.indexOf( "class" ) != -1 ) || ( name.indexOf( "singleton" ) != -1 )
                || ( name.equals( "destDir" ) || name.equals( "srcDir" ) ) )
            {
                continue;
            }

            sb.append( "    " );
            sb.append( "[-D" );
            sb.append( fields[i].getName() );
            sb.append( "=..." );
            sb.append( "]" );

            if ( ( i + 1 ) < fields.length - 1 )
            {
                sb.append( "\n" );
            }
        }

        return sb.toString();
    }

    /**
     * Getter for the bottom
     *
     * @return the bottom
     */
    public String getBottom()
    {
        return this.bottom;
    }

    /**
     * Getter for the destDir
     *
     * @return the destDir
     */
    public String getDestDir()
    {
        return this.destDir;
    }

    /**
     * Getter for the docencoding
     *
     * @return the docencoding
     */
    public String getDocencoding()
    {
        return this.docencoding;
    }

    /**
     * Getter for the doctitle
     *
     * @return the doctitle
     */
    public String getDoctitle()
    {
        return this.doctitle;
    }

    /**
     * Getter for the encoding
     *
     * @return the encoding
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Getter for the footer
     *
     * @return the footer
     */
    public String getFooter()
    {
        return this.footer;
    }

    /**
     * Getter for the header
     *
     * @return the header
     */
    public String getHeader()
    {
        return this.header;
    }

    /**
     * Getter for the packagesheader
     *
     * @return the packagesheader
     */
    public String getPackagesheader()
    {
        return this.packagesheader;
    }

    /**
     * Getter for the String list of srcDir.
     *
     * @return the srcDir
     */
    public List getSrcDirs()
    {
        return srcDirs;
    }

    /**
     * Getter for the stylesheetfile
     *
     * @return the stylesheetfile
     */
    public String getStylesheetfile()
    {
        return this.stylesheetfile;
    }

    /**
     * Getter for the top
     *
     * @return the top
     */
    public String getTop()
    {
        return this.top;
    }

    /**
     * Getter for the windowtitle
     *
     * @return the windowtitle
     */
    public String getWindowtitle()
    {
        return this.windowtitle;
    }

    /**
     * Getter for the recurse
     *
     * @return the recurse
     */
    public boolean isRecurse()
    {
        return recurse;
    }

    /**
     * Getter for the verbose
     *
     * @return the verbose
     */
    public boolean isVerbose()
    {
        return verbose;
    }

    /**
     * Setter for the bottom
     *
     * @param bottom the bottom to set
     */
    public void setBottom( String bottom )
    {
        this.bottom = bottom;
    }

    /**
     * Setter for the destDir
     *
     * @param destDir the destDir to set
     */
    public void setDestDir( String destDir )
    {
        this.destDir = destDir;
    }

    /**
     * Setter for the docencoding
     *
     * @param docencoding the docencoding to set
     */
    public void setDocencoding( String docencoding )
    {
        this.docencoding = docencoding;
    }

    /**
     * Setter for the doctitle
     *
     * @param doctitle the doctitle to set
     */
    public void setDoctitle( String doctitle )
    {
        this.doctitle = doctitle;
    }

    /**
     * Setter for the encoding
     *
     * @param encoding the encoding to set
     */
    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    /**
     * Setter for the footer
     *
     * @param footer the footer to set
     */
    public void setFooter( String footer )
    {
        this.footer = footer;
    }

    /**
     * Setter for the header
     *
     * @param header the header to set
     */
    public void setHeader( String header )
    {
        this.header = header;
    }

    /**
     * Setter for the packagesheader
     *
     * @param packagesheader the packagesheader to set
     */
    public void setPackagesheader( String packagesheader )
    {
        this.packagesheader = packagesheader;
    }

    /**
     * Setter for the recurse
     *
     * @param recurse the recurse to set
     */
    public void setRecurse( boolean recurse )
    {
        this.recurse = recurse;
    }

    /**
     * Adder for the srcDir
     *
     * @param srcDir the srcDir to set
     */
    public void addSrcDir( String srcDir )
    {
        if ( this.srcDirs == null )
        {
            this.srcDirs = new LinkedList();
        }

        this.srcDirs.add( srcDir );
    }

    /**
     * Setter for the stylesheetfile
     *
     * @param stylesheetfile the stylesheetfile to set
     */
    public void setStylesheetfile( String stylesheetfile )
    {
        this.stylesheetfile = stylesheetfile;
    }

    /**
     * Setter for the top
     *
     * @param top the top to set
     */
    public void setTop( String top )
    {
        this.top = top;
    }

    /**
     * Setter for the verbose
     *
     * @param verbose the verbose to set
     */
    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
    }

    /**
     * Setter for the windowtitle
     *
     * @param windowtitle the windowtitle to set
     */
    public void setWindowtitle( String windowtitle )
    {
        this.windowtitle = windowtitle;
    }

    /** {@inheritDoc} */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "Configuration[" );
        buffer.append( " bottom = " ).append( bottom );
        buffer.append( " destDir = " ).append( destDir );
        buffer.append( " docencoding = " ).append( docencoding );
        buffer.append( " doctitle = " ).append( doctitle );
        buffer.append( " encoding = " ).append( encoding );
        buffer.append( " footer = " ).append( footer );
        buffer.append( " header = " ).append( header );
        buffer.append( " packagesheader = " ).append( packagesheader );
        buffer.append( " recurse = " ).append( recurse );
        buffer.append( " srcDir = " ).append( srcDirs );
        buffer.append( " stylesheetfile = " ).append( stylesheetfile );
        buffer.append( " top = " ).append( top );
        buffer.append( " verbose = " ).append( verbose );
        buffer.append( " windowtitle = " ).append( windowtitle );
        buffer.append( "]" );
        return buffer.toString();
    }
}
