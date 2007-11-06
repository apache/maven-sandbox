package org.apache.maven.jxr.ant.doc.vizant;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Vizant processor.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class Vizant
{
    private File antfile;

    private File outfile;

    private VizProjectLoader loader;

    private VizPrinter printer;

    /**
     * Default constructor.
     */
    public Vizant()
    {
        loader = getLoader();
        printer = getPrinter();
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * @param antfile
     * @throws IOException if any
     * @throws IllegalArgumentException if any
     */
    public void setAntfile( File antfile )
        throws IOException, IllegalArgumentException
    {
        if ( antfile == null )
        {
            throw new IllegalArgumentException( "antfile could not be null" );
        }
        if ( !antfile.exists() || !antfile.isFile() )
        {
            throw new IllegalArgumentException( "antfile attribute should exist and should be a file." );
        }

        this.antfile = antfile;
        loader.setInputStream( new FileInputStream( antfile ) );
    }

    /**
     * Setter for the outfile
     *
     * @param outfile the outfile to set
     * @throws IllegalArgumentException if any
     */
    public void setOutfile( File outfile )
        throws IllegalArgumentException
    {
        if ( outfile == null )
        {
            throw new IllegalArgumentException( "outfile could not be null" );
        }
        if ( outfile.exists() && outfile.isDirectory() )
        {
            throw new IllegalArgumentException( "outfile could not be a directory" );
        }

        this.outfile = outfile;
    }

    /**
     * Setter for the graphid in printer
     *
     * @param graphid the graphid to set in printer
     */
    public void setGraphid( String graphid )
    {
        printer.setGraphid( graphid );
    }

    /**
     * Setter for the targetName in printer
     *
     * @param targetName the targetName to set in printer
     */
    public void setFrom( String targetName )
    {
        printer.setFrom( targetName );
    }

    /**
     * Setter for the targetName in printer
     *
     * @param targetName the targetName to set in printer
     */
    public void setTo( String targetName )
    {
        printer.setTo( targetName );
    }

    /**
     * Setter for the noclustor in printer
     *
     * @param noclustor the noclustor to set in printer
     */
    public void setNocluster( boolean noclustor )
    {
        printer.setNocluster( noclustor );
    }

    /**
     * @param uniqueref true to use uniqure reference
     */
    public void setUniqueref( boolean uniqueref )
    {
        loader.uniqueRef( uniqueref );
    }

    /**
     * @param opt true to ignore ant
     */
    public void setIgnoreant( boolean opt )
    {
        loader.ignoreAnt( opt );
    }

    /**
     * @param opt true to ignore antcall
     */
    public void setIgnoreantcall( boolean opt )
    {
        loader.ignoreAntcall( opt );
    }

    /**
     * @param opt true to ignore depends
     */
    public void setIgnoredepends( boolean opt )
    {
        loader.ignoreDepends( opt );
    }

    /**
     * @param attrstmt the attrstmt to add
     */
    public void addConfiguredAttrstmt( VizAttrStmt attrstmt )
    {
        attrstmt.checkConfiguration();
        printer.addAttributeStatement( attrstmt );
    }

    /**
     * @param subgraph the subgraph to add
     */
    public void addSubgraph( VizSubgraph subgraph )
    {
        subgraph.setPrinter( printer );
    }

    /**
     * Process Vizant
     *
     * @throws IOException if any
     * @throws IllegalArgumentException if any
     */
    public void execute()
        throws IOException, IllegalArgumentException
    {
        checkConfiguration();
        loadProjects();
        writeDotToOutfile();
    }

    // ----------------------------------------------------------------------
    // private
    // ----------------------------------------------------------------------

    /**
     * @throws IllegalArgumentException if any
     */
    private void checkConfiguration()
        throws IllegalArgumentException
    {
        if ( antfile == null )
        {
            throw new IllegalArgumentException( "antfile attribute is required" );
        }
        if ( !antfile.exists() || !antfile.isFile() )
        {
            throw new IllegalArgumentException( "antfile attribute should exist and should be a file." );
        }
        if ( outfile == null )
        {
            throw new IllegalArgumentException( "outfile could not be null" );
        }
        if ( outfile.exists() && outfile.isDirectory() )
        {
            throw new IllegalArgumentException( "outfile could not be a directory" );
        }
    }

    private VizPrinter getPrinter()
    {
        return new VizPrinter();
    }

    private VizProjectLoader getLoader()
    {
        return new VizProjectLoaderImpl();
    }

    private void loadProjects()
    {
        Enumeration enumList = loader.getProjects().elements();
        while ( enumList.hasMoreElements() )
        {
            printer.addProject( (VizProject) enumList.nextElement() );
        }
    }

    private void writeDotToOutfile()
        throws IOException
    {
        if ( !outfile.getParentFile().exists() && !outfile.getParentFile().mkdirs() )
        {
            throw new IllegalArgumentException( "Cannot create outfile parent dir." );
        }

        VizFileWriter out = null;
        try
        {
            out = new VizFileWriter( outfile );
            print( out );
        }
        finally
        {
            if ( out != null )
            {
                out.close();
            }
        }
    }

    private void print( VizWriter out )
    {
        printer.setWriter( out );
        printer.print();
    }

    private class VizFileWriter
        implements VizWriter
    {
        private PrintWriter out = null;

        /**
         * @param outfile
         * @throws IOException if any
         */
        public VizFileWriter( File outfile )
            throws IOException
        {
            out = new PrintWriter( new BufferedWriter( new FileWriter( outfile ) ) );
        }

        /** {@inheritDoc} */
        public void print( String str )
        {
            out.print( str );
        }

        /** {@inheritDoc} */
        public void println( String str )
        {
            out.println( str );
        }

        /**
         * Close out
         */
        public void close()
        {
            if ( out != null )
            {
                out.flush();
                out.close();
            }
        }
    }
}
