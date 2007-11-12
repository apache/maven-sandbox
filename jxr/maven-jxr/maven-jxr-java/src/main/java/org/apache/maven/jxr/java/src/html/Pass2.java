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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.maven.jxr.java.src.JavaSrcOptions;
import org.apache.maven.jxr.java.src.symtab.ReferenceTypes;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * Cross-reference generation pass.
 * <p/>
 * For each package (i.e for each output directory):
 * <ul>
 * <li>Load reference.txt</li>
 * <li>Sort references</li>
 * <li>Generate HTML one public class at a time</li>
 * </ul>
 *
 * @version $Id$
 */
public class Pass2
    extends AbstractPass
{
    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( Pass2.class );

    ArrayList packageNames;

    Hashtable packageClasses;

    // ----------------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------------

    /**
     * Constructor Pass2
     *
     * @param conf object
     */
    public Pass2( JavaSrcOptions conf )
    {
        super( conf );

        packageNames = new ArrayList();
        packageClasses = new Hashtable();
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * @throws IOException if any
     */
    public void run()
        throws IOException
    {
        File outDir = new File( getDestDir() );

        if ( log.isDebugEnabled() )
        {
            log.debug( "run(String[]) - File outDir=" + outDir );
        }

        walkDirectories( null, outDir );
        Collections.sort( packageNames, stringComparator() );

        // Create package files
        // I.e. generate HTML list of classes in each package
        if ( log.isDebugEnabled() )
        {
            log.debug( "run(String[]) - Writing package files" );
        }
        createPackageFiles();
        createPackageSummaryFiles();

        // Create the HTML for the package list
        createOverviewFrame();

        // Create main frames
        createIndex();
        createAllClassesFrame();
        createOverviewSummaryFrame();
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Method walkDirectories
     *
     * @param packageName
     * @param outDir
     * @throws IOException
     */
    private void walkDirectories( String packageName, File outDir )
        throws IOException
    {
        File refFile = new File( outDir, "references.txt" );

        if ( refFile.exists() )
        {
            // packageNames.add(packageName);
            // processRefFile(packageName, refFile);
            processRefFile( packageName, refFile );

            HashMap classes = (HashMap) packageClasses.get( packageName );

            if ( classes.size() > 0 )
            {
                packageNames.add( packageName );
            }
        }

        File[] entries = outDir.listFiles();
        for ( int i = 0; i < entries.length; i++ )
        {
            if ( entries[i].isDirectory() )
            {
                String newPackageName = ( packageName == null ) ? entries[i].getName() : packageName + "."
                    + entries[i].getName();

                walkDirectories( newPackageName, entries[i] );
            }
        }
    }

    /**
     * Method processRefFile
     *
     * @param packageName
     * @param refFile
     * @throws IOException
     */
    private void processRefFile( String packageName, File refFile )
        throws IOException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "processRefFile(String, File) - File refFile=" + refFile );
        }

        HashMap classes = (HashMap) packageClasses.get( packageName );

        if ( classes == null )
        {
            classes = new HashMap();
        }

        // load the entire file
        String line;
        FileInputStream fis = new FileInputStream( refFile );
        InputStreamReader isr;
        if ( StringUtils.isNotEmpty( getOptions().getEncoding() ) )
        {
            isr = new InputStreamReader( fis, getOptions().getEncoding() );
        }
        else
        {
            isr = new InputStreamReader( fis );
        }
        BufferedReader br = new BufferedReader( isr );
        Vector v = new Vector();

        while ( ( line = br.readLine() ) != null )
        {
            v.addElement( line );
        }

        IOUtil.close( br );
        IOUtil.close( isr );
        IOUtil.close( fis );

        String[] lines = new String[v.size()];
        v.copyInto( lines );

        // sort the lines
        Arrays.sort( lines, stringComparator() );

        // process one referentFileClass (=one source file) at a time
        BufferedWriter bw = null;
        String prevReferentFileClass = null;
        String prevReferentTag = null;

        for ( int i = 0; i < lines.length; i++ )
        {
            line = lines[i];

            if ( line.charAt( 0 ) == '#' )
            {
                continue;
            }

            ReferenceEntry ref = new ReferenceEntry( line );

            if ( !ref.getReferentFileClass().equals( prevReferentFileClass ) )
            {
                // close current section, if any
                if ( prevReferentTag != null )
                {
                    closeSection( bw, prevReferentTag );
                }

                // close current output file, if any
                if ( bw != null )
                {
                    closeOutputFile( bw, prevReferentFileClass );
                }

                // open new output file
                bw = openOutputFile( packageName, ref );
                prevReferentFileClass = ref.getReferentFileClass();
                prevReferentTag = null;
            }

            if ( !classes.containsKey( ref.getReferentClass() ) )
            {
                classes.put( ref.getReferentClass(), ref.getReferentFileClass() );
            }

            if ( !ref.getReferentTag().equals( prevReferentTag ) )
            {
                // write close-section stuff, if any
                if ( prevReferentTag != null )
                {
                    closeSection( bw, prevReferentTag );
                }

                // write new heading based on new referent type
                prevReferentTag = ref.getReferentTag();

                openSection( bw, packageName, ref );
            }

            // write link for this reference
            if ( !ref.getReferringMethod().equals( "?" ) )
            {
                writeLink( bw, packageName, ref );
            }
        }

        // close the last output file
        if ( bw != null )
        {
            closeOutputFile( bw, prevReferentFileClass );
        }

        if ( log.isDebugEnabled() )
        {
            log.debug( "processRefFile(String, File) - class list for " + packageName + " is " + classes );
        }

        packageClasses.put( packageName, classes );
    }

    /**
     * Method closeOutputFile
     *
     * @param bw
     * @param referentFileClass
     * @throws IOException
     */
    private void closeOutputFile( BufferedWriter bw, String referentFileClass )
        throws IOException
    {
        bw.write( "</BODY></HTML>" );
        IOUtil.close( bw );

        if ( log.isDebugEnabled() )
        {
            log.debug( "closeOutputFile(BufferedWriter, String) - close output file" );
        }
    }

    /**
     * Method openOutputFile
     *
     * @param packageName
     * @param ref
     * @return
     * @throws IOException
     */
    private BufferedWriter openOutputFile( String packageName, ReferenceEntry ref )
        throws IOException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "openOutputFile(String, Reference) - Reference ref=" + ref.getReferentFileClass() );
        }

        File rootDir = new File( getDestDir() );
        String relPath = ( packageName == null ) ? ref.getReferentFileClass() : packageName
            .replace( '.', File.separatorChar )
            + File.separatorChar + ref.getReferentFileClass();

        relPath += "_java_ref.html";

        File outFile = new File( rootDir, relPath );
        FileOutputStream fos = new FileOutputStream( outFile );
        OutputStreamWriter fw;
        if ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) )
        {
            fw = new OutputStreamWriter( fos, getOptions().getDocencoding() );
        }
        else
        {
            fw = new OutputStreamWriter( fos );
        }
        BufferedWriter result = new BufferedWriter( fw );

        result.write( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
            + "\"http://www.w3.org/TR/html4/loose.dtd\">\n" );
        result.write( "<HTML>" );
        result.write( "<HEAD>\n" );
        result.write( getGeneratedBy() + "\n" );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions().getDocencoding()
                                                                                   : DEFAULT_DOCENCODING );
        result.write( "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=" + encoding + "\">\n" );
        result.write( "<TITLE>" + packageName + "." + ref.getReferentFileClass() + " References</title>\n" );
        result.write( "<LINK REL=\"stylesheet\" TYPE=\"text/css\" " + "HREF=\"" + getBackupPath( packageName )
            + "styles.css\" TITLE=\"Style\">\n" );
        result.write( "</HEAD>\n" );
        result.write( "<BODY>\n" );

        return result;
    }

    /**
     * Method closeSection
     *
     * @param bw
     * @param referentTag
     * @throws IOException
     */
    private void closeSection( BufferedWriter bw, String referentTag )
        throws IOException
    {
        //bw.write( "</p>" );

        if ( log.isDebugEnabled() )
        {
            log.debug( "closeSection(BufferedWriter, String) - close section for referent " + referentTag );
        }
    }

    /**
     * Method openSection
     *
     * @param bw
     * @param referentPackage
     * @param ref
     * @throws IOException
     */
    private void openSection( BufferedWriter bw, String referentPackage, ReferenceEntry ref )
        throws IOException
    {
        if ( ref.getReferentType().equals( ReferenceTypes.CLASS_REF ) )
        {
            bw.write( "<P CLASS=\"classReflist\">" );

            String nameString = "<P CLASS=\"classReflistHeader\">Class: <A NAME=\"" + ref.getReferentTag()
                + "\" HREF=\"" + ref.getReferentFileClass() + "_java.html#" + ref.getReferentTag() + "\">"
                + ref.getReferentClass() + "</A></P>";

            bw.write( nameString );
        }
        else if ( ref.getReferentType().equals( ReferenceTypes.METHOD_REF ) )
        {
            bw.write( "<P CLASS=\"methodReflist\">" );

            String nameString = "<p class=\"methodReflistHeader\">Method: <A NAME=\"" + ref.getReferentTag()
                + "\" HREF=\"" + ref.getReferentFileClass() + "_java.html#" + ref.getReferentTag() + "\">"
                + ref.getReferentTag() + "</A></P>";

            bw.write( nameString );
        }
        else if ( ref.getReferentType().equals( ReferenceTypes.VARIABLE_REF ) )
        {
            bw.write( "<P CLASS=\"variableReflist\">" );

            String nameString = "<P CLASS=\"variableReflistHeader\">Variable: <A NAME=\"" + ref.getReferentTag()
                + "\" HREF=\"" + ref.getReferentFileClass() + "_java.html#" + ref.getReferentTag() + "\">"
                + ref.getReferentTag() + "</A></P>";

            bw.write( nameString );
        }
        else
        {
            bw.write( "<P>open section " + ref.getReferentType() + "</P>" );
        }

        if ( log.isDebugEnabled() )
        {
            log.debug( "openSection(BufferedWriter, String, Reference) - open section for referent="
                + ref.getReferentTag() );
        }
    }

    /**
     * Method writeLink
     *
     * @param bw
     * @param referentPackage
     * @param ref
     * @throws IOException
     */
    private void writeLink( BufferedWriter bw, String referentPackage, ReferenceEntry ref )
        throws IOException
    {
        String linkFilename = sourceName( referentPackage, ref );

        if ( ref.getReferentType().equals( ReferenceTypes.CLASS_REF ) )
        {
            String linkString = "<P CLASS=\"classRefItem\"><A HREF=\"" + linkFilename + "#"
                + ref.getReferringLineNumber() + "\">" + ref.getReferringPackage() + "." + ref.getReferringClass()
                + "." + ref.getReferringMethod() + " (" + ref.getReferringFile() + ":" + ref.getReferringLineNumber()
                + ")</A></P>\n";

            bw.write( linkString );
        }
        else if ( ref.getReferentType().equals( ReferenceTypes.METHOD_REF ) )
        {
            String linkString = "<P CLASS=\"methodRefItem\"><A HREF=\"" + linkFilename + "#"
                + ref.getReferringLineNumber() + "\">" + ref.getReferringPackage() + "." + ref.getReferringClass()
                + "." + ref.getReferringMethod() + " (" + ref.getReferringFile() + ":" + ref.getReferringLineNumber()
                + ")</A></P>\n";

            bw.write( linkString );
        }
        else if ( ref.getReferentType().equals( ReferenceTypes.VARIABLE_REF ) )
        {
            String linkString = "<P CLASS=\"variableRefItem\"><A HREF=\"" + linkFilename + "#"
                + ref.getReferringLineNumber() + "\">" + ref.getReferringPackage() + "." + ref.getReferringClass()
                + "." + ref.getReferringMethod() + " (" + ref.getReferringFile() + ":" + ref.getReferringLineNumber()
                + ")</A></P>\n";

            bw.write( linkString );
        }
        else
        {
            bw.write( "<P>link for a " + ref.getReferentType() + "</P>" );
        }
    }

    /**
     * Return path to referring X_java.html file, relative to referent directory.
     *
     * @param referentPackage
     * @param ref
     * @return
     */
    private String sourceName( String referentPackage, ReferenceEntry ref )
    {
        String result = getBackupPath( referentPackage ) + ref.getReferringPackage().replace( '.', '/' ) + '/'
            + ref.getReferringClass() + "_java.html";

        return result;
    }

    /**
     * Method createPackageFiles
     *
     * @throws IOException if any
     */
    private void createPackageFiles()
        throws IOException
    {
        String packageName;
        String fileName;
        File file;
        PrintWriter pw;

        // String className;
        int totalClassCount = 0;
        Iterator packageIter = packageNames.iterator();

        while ( packageIter.hasNext() )
        {
            packageName = (String) packageIter.next();

            List classes = orderedPackageClasses( packageName );

            if ( log.isDebugEnabled() )
            {
                log.debug( "createPackageFiles() - " + packageName + " has " + classes.size() + " classes" );
            }

            totalClassCount += classes.size();
            fileName = getDestDir() + File.separatorChar + packageName.replace( '.', File.separatorChar )
                + File.separatorChar + "classList.html";
            file = new File( fileName );

            createDirs( file );

            FileOutputStream fos = new FileOutputStream( file );
            OutputStreamWriter fw;
            if ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) )
            {
                fw = new OutputStreamWriter( fos, getOptions().getDocencoding() );
            }
            else
            {
                fw = new OutputStreamWriter( fos );
            }
            pw = new PrintWriter( fw );

            pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
                + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
            pw.println( "<HTML>" );
            pw.println( "<HEAD>" );
            pw.println( getGeneratedBy() );
            String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions().getDocencoding()
                                                                                       : DEFAULT_DOCENCODING );
            pw.println( "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=" + encoding + "\">" );
            if ( StringUtils.isNotEmpty( getOptions().getWindowtitle() ) )
            {
                pw.println( "<TITLE>" + packageName + " (" + getOptions().getWindowtitle() + ")</TITLE>" );
                pw.println( "<META NAME=\"keywords\" CONTENT=\"" + packageName + "\">" );
            }
            else
            {
                pw.println( "<TITLE>" + packageName + "</TITLE>" );
            }
            pw.println( "<LINK REL=\"stylesheet\" TYPE=\"text/css\" " + "HREF=\"" + getBackupPath( packageName )
                + "styles.css\" TITLE=\"Style\">" );
            pw.println( "</HEAD>" );

            pw.println( "" );

            pw.println( "<BODY BGCOLOR=\"white\">" );
            pw.println( "<FONT size=\"+1\" CLASS=\"FrameTitleFont\">" );
            pw.println( "<A HREF=\"package-summary.html\" TARGET=\"classFrame\">" + packageName + "</A></FONT>" );
            pw.println( "<TABLE BORDER=\"0\" WIDTH=\"100%\" SUMMARY=\"\">" );
            pw.println( "<TR>" );
            pw.println( "<TD NOWRAP><FONT size=\"+1\" CLASS=\"FrameHeadingFont\">" );
            pw.println( "Classes</FONT>&nbsp;" );
            pw.println( "<FONT CLASS=\"FrameItemFont\">" );
            pw.println( "<BR>" );

            Iterator iter = classes.iterator();
            while ( iter.hasNext() )
            {
                ClassFileEntry cf = (ClassFileEntry) iter.next();
                String className = cf.getClassName();
                String fileClassName = cf.getFileName();
                String anchor = className;

                pw.println( "<A HREF=\"" + fileClassName + "_java.html#" + anchor + "\" TITLE=\"" + className
                    + "\" TARGET=\"classFrame\">" + className + "</A>" );
                pw.println( "<BR>" );
            }
            pw.println( "</FONT>" );
            pw.println( "</TD>" );
            pw.println( "</TR>" );
            pw.println( "</TABLE>" );

            pw.println( "</BODY>" );
            pw.println( "</HTML>" );
            IOUtil.close( pw );
        }

        println( totalClassCount + " classes total" );
        println( "" );
    }

    /**
     * Return alphabetized list of all classes in a package, including inner classes.
     *
     * @param packageName
     * @return
     */
    private List orderedPackageClasses( String packageName )
    {
        HashMap hm = (HashMap) packageClasses.get( packageName );

        // Hmmm, is this supposed to be easier than using Hashtable.keys()?
        Set es = hm.entrySet();
        Iterator iter = es.iterator();
        List result = new ArrayList();

        while ( iter.hasNext() )
        {
            Map.Entry me = (Map.Entry) iter.next();
            ClassFileEntry cf = new ClassFileEntry( (String) me.getKey(), (String) me.getValue() );

            result.add( cf );
        }

        Collections.sort( result, classFileComparator() );

        return result;
    }

    /**
     * Method orderedAllClasses
     *
     * @return
     */
    private List orderedAllClasses()
    {
        List result = new ArrayList();
        Iterator packageIter = packageNames.iterator();

        while ( packageIter.hasNext() )
        {
            String packageName = (String) packageIter.next();
            HashMap hm = (HashMap) packageClasses.get( packageName );
            String packageFileName = "";

            if ( packageName.indexOf( '.' ) != -1 )
            {
                packageFileName = packageName.replace( '.', '/' );
                packageFileName += '/';
            }

            // Hmmm, is this supposed to be easier than using Hashtable.keys()?
            Set es = hm.entrySet();
            Iterator iter = es.iterator();

            while ( iter.hasNext() )
            {
                Map.Entry me = (Map.Entry) iter.next();
                ClassFileEntry cf = new ClassFileEntry( (String) me.getKey(), packageFileName + (String) me.getValue() );

                result.add( cf );
            }
        }

        Collections.sort( result, classFileComparator() );

        return result;
    }

    /**
     * Method stringComparator
     *
     * @return
     */
    private Comparator stringComparator()
    {
        return new Comparator()
        {
            /** {@inheritDoc} */
            public int compare( Object o1, Object o2 )
            {
                return ( (String) o1 ).compareTo( (String) o2 );
            }

            /** {@inheritDoc} */
            public boolean equals( Object o )
            {
                return false;
            }
        };
    }

    /**
     * Method classFileComparator
     *
     * @return
     */
    private Comparator classFileComparator()
    {
        return new Comparator()
        {
            /** {@inheritDoc} */
            public int compare( Object o1, Object o2 )
            {
                ClassFileEntry cf1 = (ClassFileEntry) o1;
                ClassFileEntry cf2 = (ClassFileEntry) o2;

                return cf1.getClassName().compareTo( cf2.getClassName() );
            }

            /** {@inheritDoc} */
            public boolean equals( Object o )
            {
                return false;
            }
        };
    }

    /**
     * Method createIndex
     *
     * @throws IOException if any
     */
    private void createIndex()
        throws IOException
    {
        String fileName = getDestDir() + File.separatorChar + "index.html";
        File file = new File( fileName );

        createDirs( file );

        FileOutputStream fos = new FileOutputStream( file );
        OutputStreamWriter fw;
        if ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) )
        {
            fw = new OutputStreamWriter( fos, getOptions().getDocencoding() );
        }
        else
        {
            fw = new OutputStreamWriter( fos );
        }

        PrintWriter pw = new PrintWriter( fw );

        pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" "
            + "\"http://www.w3.org/TR/html4/frameset.dtd\">" );
        pw.println( "<HTML>" );
        pw.println( "<HEAD>" );
        pw.println( getGeneratedBy() );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions().getDocencoding()
                                                                                   : DEFAULT_DOCENCODING );
        pw.println( "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=" + encoding + "\">" );
        if ( StringUtils.isNotEmpty( getOptions().getWindowtitle() ) )
        {
            pw.println( "<TITLE>" + getOptions().getWindowtitle() + "</TITLE>" );
        }
        else
        {
            pw.println( "<TITLE>JavaSrc</TITLE>" );
        }
        pw.println( "<SCRIPT TYPE=\"text/javascript\">" );
        pw.println( "<!--" );
        pw.println( "    targetPage = \"\" + window.location.search;" );
        pw.println( "    if (targetPage != \"\" && targetPage != \"undefined\")" );
        pw.println( "       targetPage = targetPage.substring(1);" );
        pw.println( "    function loadFrames() {" );
        pw.println( "        if (targetPage != \"\" && targetPage != \"undefined\")" );
        pw.println( "             top.classFrame.location = top.targetPage;" );
        pw.println( "    }" );
        pw.println( "//-->" );
        pw.println( "</SCRIPT>" );
        pw.println( "</HEAD>" );

        pw.println( "" );

        pw.println( "<FRAMESET COLS=\"20%,80%\" TITLE=\"\" ONLOAD=\"top.loadFrames()>\">" );
        pw.println( "  <FRAMESET ROWS=\"30%,70%\" TITLE=\"\" ONLOAD=\"top.loadFrames()>\">" );
        pw.println( "    <FRAME SRC=\"overview-frame.html\" NAME=\"packageListFrame\" TITLE=\"All Packages\">" );
        pw.println( "    <FRAME SRC=\"allclasses-frame.html\" NAME=\"packageFrame\" "
            + "TITLE=\"All classes and interfaces (except non-static nested types\">" );
        pw.println( "  </FRAMESET>" );
        pw.println( "  " );
        pw.println( "  <FRAMESET ROWS=\"*\">" );
        pw.println( "    <FRAME SRC=\"overview-summary.html\" NAME=\"classFrame\" "
            + "TITLE=\"Package, class and interface descriptions\" SCROLLING=\"yes\">" );
        pw.println( "    <NOFRAMES>" );
        pw.println( "      <H2>Frame Alert</H2>" );
        pw.println( "      <P>" );
        pw.println( "      This document is designed to be viewed using the frames feature. "
            + "If you see this message, you are using a non-frame-capable web client." );
        pw.println( "      <BR>" );
        pw.println( "      Link to<A HREF=\"overview-summary.html\">Non-frame version.</A>" );
        pw.println( "    </NOFRAMES>" );
        pw.println( "  </FRAMESET>" );
        pw.println( "</FRAMESET>" );
        pw.println( "</HTML>" );
        IOUtil.close( pw );
    }

    /**
     * Create the HTML for the list of all packages.
     *
     * @throws IOException if any
     */
    private void createOverviewFrame()
        throws IOException
    {
        String packageName;
        String packageFileName;
        String fileName = getDestDir() + File.separatorChar + "overview-frame.html";
        File file = new File( fileName );

        createDirs( file );

        FileOutputStream fos = new FileOutputStream( file );
        OutputStreamWriter fw;
        if ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) )
        {
            fw = new OutputStreamWriter( fos, getOptions().getDocencoding() );
        }
        else
        {
            fw = new OutputStreamWriter( fos );
        }
        PrintWriter pw = new PrintWriter( fw );

        Iterator iter = packageNames.iterator();

        pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
            + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
        pw.println( "<HTML>" );
        pw.println( "<HEAD>" );
        pw.println( getGeneratedBy() );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions().getDocencoding()
                                                                                   : DEFAULT_DOCENCODING );
        pw.println( "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=" + encoding + "\">" );
        if ( StringUtils.isNotEmpty( getOptions().getWindowtitle() ) )
        {
            pw.println( "<TITLE>Overview (" + getOptions().getWindowtitle() + ")</TITLE>" );
            pw.println( "<META NAME=\"keywords\" CONTENT=\"Overview, " + getOptions().getWindowtitle() + "\">" );
        }
        else
        {
            pw.println( "<TITLE>Overview</TITLE>" );
        }
        pw.println( "<LINK REL=\"stylesheet\" TYPE=\"text/css\" HREF=\"styles.css\" TITLE=\"Style\">" );
        pw.println( "</HEAD>" );

        pw.println( "" );

        pw.println( "<BODY BGCOLOR=\"white\">" );
        pw.println( "<TABLE BORDER=\"0\" WIDTH=\"100%\" SUMMARY=\"\">" );
        pw.println( "<TR>" );
        pw.println( "<TH ALIGN=\"left\" NOWRAP>" );
        pw.println( "<FONT size=\"+1\" CLASS=\"FrameTitleFont\">" );
        if ( StringUtils.isNotEmpty( getOptions().getPackagesheader() ) )
        {
            pw.println( "<B>" + getOptions().getPackagesheader() + "</B>" );
        }
        pw.println( "</FONT></TH>" );
        pw.println( "</TR>" );
        pw.println( "</TABLE>" );

        pw.println( "" );

        pw.println( "<TABLE BORDER=\"0\" WIDTH=\"100%\" SUMMARY=\"\">" );
        pw.println( "<TR>" );
        pw.println( "<TD NOWRAP>" );
        pw.println( "<FONT CLASS=\"FrameItemFont\">" );
        pw.println( "<A HREF=\"allclasses-frame.html\" TARGET=\"packageFrame\">All Classes</A>" );
        pw.println( "</FONT><P>" );
        pw.println( "<FONT size=\"+1\" CLASS=\"FrameHeadingFont\">Packages</FONT>" );
        pw.println( "<BR>" );
        while ( iter.hasNext() )
        {
            packageName = (String) iter.next();
            packageFileName = packageName.replace( '.', '/' ) + '/' + "classList.html";

            pw.println( "<FONT CLASS=\"FrameItemFont\">" );
            pw.println( "<A HREF=\"" + packageFileName + "\" TARGET=\"packageFrame\">" + packageName + "</A>" );
            pw.println( "</FONT>" );
            pw.println( "<BR>" );
        }
        pw.println( "</TD>" );
        pw.println( "</TR>" );
        pw.println( "</TABLE>" );

        pw.println( "" );

        pw.println( "<P>" );
        pw.println( "&nbsp;" );
        pw.println( "</BODY>" );
        pw.println( "</HTML>" );

        IOUtil.close( pw );
    }

    /**
     * Method createAllClassesFrame
     *
     * @throws IOException if any
     */
    private void createAllClassesFrame()
        throws IOException
    {
        String fileName = getDestDir() + File.separatorChar + "allclasses-frame.html";
        File file = new File( fileName );

        createDirs( file );

        FileOutputStream fos = new FileOutputStream( file );
        OutputStreamWriter fw;
        if ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) )
        {
            fw = new OutputStreamWriter( fos, getOptions().getDocencoding() );
        }
        else
        {
            fw = new OutputStreamWriter( fos );
        }
        PrintWriter pw = new PrintWriter( fw );

        pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
            + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
        pw.println( "<HTML>" );
        pw.println( "<HEAD>" );
        pw.println( getGeneratedBy() );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions().getDocencoding()
                                                                                   : DEFAULT_DOCENCODING );
        pw.println( "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=" + encoding + "\">" );
        if ( StringUtils.isNotEmpty( getOptions().getWindowtitle() ) )
        {
            pw.println( "<TITLE>All classes (" + getOptions().getWindowtitle() + ")</TITLE>" );
        }
        else
        {
            pw.println( "<TITLE>All classes</TITLE>" );
        }
        pw.println( "<LINK REL=\"stylesheet\" TYPE=\"text/css\" HREF=\"styles.css\" TITLE=\"Style\">" );
        pw.println( "</HEAD>" );

        pw.println( "" );

        pw.println( "<BODY BGCOLOR=\"white\">" );
        pw.println( "<FONT size=\"+1\" CLASS=\"FrameHeadingFont\">" );
        pw.println( "<B>All Classes</B></FONT>" );
        pw.println( "<BR>" );
        pw.println( "" );
        pw.println( "<TABLE BORDER=\"0\" WIDTH=\"100%\" SUMMARY=\"\">" );
        pw.println( "<TR>" );
        pw.println( "<TD NOWRAP><FONT CLASS=\"FrameItemFont\">" );
        Iterator iter = orderedAllClasses().iterator();
        while ( iter.hasNext() )
        {
            ClassFileEntry cf = (ClassFileEntry) iter.next();
            String className = cf.getClassName();
            String fileClassName = cf.getFileName();
            String anchor = className;

            // TODO add <I> for interface
            pw.println( "<A HREF=\"" + fileClassName + "_java.html#" + anchor + "\" TITLE=\"" + className + "\" "
                + "TARGET=\"classFrame\">" + className + "</A>" );
            pw.println( "<BR>" );
        }
        pw.println( "</FONT>" );
        pw.println( "</TD>" );
        pw.println( "</TR>" );
        pw.println( "</TABLE>" );

        pw.println( "" );

        pw.println( "</BODY>" );
        pw.println( "</HTML>" );
        IOUtil.close( pw );
    }

    /**
     * Method createPackageFiles
     *
     * @throws IOException if any
     */
    private void createPackageSummaryFiles()
        throws IOException
    {
        String packageName;
        String fileName;
        File file;
        PrintWriter pw;

        // String className;
        int totalClassCount = 0;
        Iterator packageIter = packageNames.iterator();

        while ( packageIter.hasNext() )
        {
            packageName = (String) packageIter.next();

            List classes = orderedPackageClasses( packageName );

            JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
            if ( StringUtils.isNotEmpty( getOptions().getEncoding() ) )
            {
                javaDocBuilder.setEncoding( getOptions().getEncoding() );
            }
            for ( Iterator it = getSrcDirs().iterator(); it.hasNext(); )
            {
                String srcDir = (String) it.next();
                File packageDir = new File( srcDir, packageName.replace( '.', File.separatorChar ) + File.separatorChar );
                if ( packageDir.exists() )
                {
                    javaDocBuilder.addSourceTree( packageDir );
                }
            }

            if ( log.isDebugEnabled() )
            {
                log.debug( "createPackageSummaryFiles() - " + packageName + " has " + classes.size() + " classes" );
            }

            totalClassCount += classes.size();
            fileName = getDestDir() + File.separatorChar + packageName.replace( '.', File.separatorChar )
                + File.separatorChar + "package-summary.html";
            file = new File( fileName );

            createDirs( file );

            FileOutputStream fos = new FileOutputStream( file );
            OutputStreamWriter fw;
            if ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) )
            {
                fw = new OutputStreamWriter( fos, getOptions().getDocencoding() );
            }
            else
            {
                fw = new OutputStreamWriter( fos );
            }
            pw = new PrintWriter( fw );

            pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
                + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
            pw.println( "<HTML>" );
            pw.println( "<HEAD>" );
            pw.println( getGeneratedBy() );
            String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions().getDocencoding()
                                                                                       : DEFAULT_DOCENCODING );
            pw.println( "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=" + encoding + "\">" );
            if ( StringUtils.isNotEmpty( getOptions().getWindowtitle() ) )
            {
                pw.println( "<TITLE>" + packageName + " (" + getOptions().getWindowtitle() + ")</TITLE>" );
                pw.println( "META NAME=\"keywords\" CONTENT=\"package " + packageName + "\">" );
            }
            else
            {
                pw.println( "<TITLE>" + packageName + " Summary</TITLE>" );
            }
            pw.println( "<LINK REL=\"stylesheet\" TYPE=\"text/css\" HREF=\"" + getBackupPath( packageName )
                + "styles.css\" TITLE=\"Style\">" );
            pw.println( "<SCRIPT TYPE=\"text/javascript\">" );
            pw.println( "<!--" );
            pw.println( "function windowTitle()" );
            pw.println( "{" );
            if ( StringUtils.isNotEmpty( getOptions().getWindowtitle() ) )
            {
                pw.println( "    parent.document.title=\"" + packageName + " (" + getOptions().getWindowtitle()
                    + ")\";" );
            }
            else
            {
                pw.println( "    parent.document.title=\"" + packageName + " Summary\";" );
            }
            pw.println( "}" );
            pw.println( "//-->" );
            pw.println( "</SCRIPT>" );
            pw.println( "</HEAD>" );

            pw.println( "" );

            pw.println( "<BODY BGCOLOR=\"white\" ONLOAD=\"windowTitle();\">" );

            pw.println( "" );
            pw.println( "<!-- ========= START OF TOP NAVBAR ======= -->" );
            if ( StringUtils.isNotEmpty( getOptions().getTop() ) )
            {
                pw.println( getOptions().getTop() );
                pw.println( "<HR>" );
            }
            createPackageSummaryFilesExtras( pw, getBackupPath( packageName ), "package-summary.html", true );
            pw.println( "<!-- ========= END OF TOP NAVBAR ========= -->" );

            pw.println( "<HR>" );
            pw.println( "<H2>Package " + packageName + "</H2>" );
            pw.println( "" );
            pw.println( "<TABLE BORDER=\"1\" WIDTH=\"100%\" CELLPADDING=\"3\" CELLSPACING=\"0\" SUMMARY=\"\">" );
            pw.println( "<TR BGCOLOR=\"#CCCCFF\" CLASS=\"TableHeadingColor\">" );
            pw.println( "<TH ALIGN=\"left\" COLSPAN=\"2\"><FONT SIZE=\"+2\">" );
            pw.println( "<B>Class Summary</B></FONT></TH>" );
            pw.println( "</TR>" );

            Iterator iter = classes.iterator();

            while ( iter.hasNext() )
            {
                ClassFileEntry cf = (ClassFileEntry) iter.next();
                String className = cf.getClassName();
                String fileClassName = cf.getFileName();
                String anchor = className;

                pw.println( "<TR BGCOLOR=\"white\" CLASS=\"TableRowColor\">" );
                pw.println( "<TD WIDTH=\"15%\">" );
                pw.println( "<B><A HREF=\"" + fileClassName + "_java.html#" + anchor + "\" TITLE=\"" + className
                    + "\">" + className + "</A></B>" );
                pw.println( "</TD>" );

                JavaClass clazz = null;
                JavaClass[] clazzes = javaDocBuilder.getClasses();
                for ( int i = 0; i < clazzes.length; i++ )
                {
                    if ( clazzes[i].getName().equals( className ) )
                    {
                        clazz = clazzes[i];
                        break;
                    }
                }
                if ( clazz == null )
                {
                    pw.println( "<TD></TD>" );
                }
                else
                {
                    String comment = clazz.getComment();
                    if ( StringUtils.isEmpty( comment ) )
                    {
                        pw.println( "<TD></TD>" );
                    }
                    else
                    {
                        // TODO display only the first line
                        pw.println( "<TD>" + comment + "</TD>" );
                    }
                }
                pw.println( "</TR>" );
            }
            pw.println( "</TBODY>" );
            pw.println( "</TABLE>" );

            pw.println( "" );
            pw.println( "<P>&nbsp;</P><HR>" );

            pw.println( "<!-- ======= START OF BOTTOM NAVBAR ====== -->" );
            createPackageSummaryFilesExtras( pw, getBackupPath( packageName ), "package-summary.html", false );
            pw.println( "<!-- ======= END OF BOTTOM NAVBAR ====== -->" );

            if ( StringUtils.isNotEmpty( getOptions().getBottom() ) )
            {
                pw.println( "<HR>" );
                pw.println( getOptions().getBottom() );
            }
            pw.println( "</BODY>" );
            pw.println( "</HTML>" );
            IOUtil.close( pw );
        }
    }

    private void createPackageSummaryFilesExtras( PrintWriter pw, String root, String current, boolean top )
    {
        if ( top )
        {
            pw.println( "<A NAME=\"navbar_top\"><!-- --></A>" );
            pw.println( "<A HREF=\"#skip-navbar_top\" TITLE=\"Skip navigation links\"></A>" );
        }
        else
        {
            pw.println( "<A NAME=\"navbar_bottom\"><!-- --></A>" );
            pw.println( "<A HREF=\"#skip-navbar_bottom\" TITLE=\"Skip navigation links\"></A>" );
        }
        pw.println( "<TABLE BORDER=\"0\" WIDTH=\"100%\" CELLPADDING=\"1\" CELLSPACING=\"0\" SUMMARY=\"\">" );
        pw.println( "<TR>" );
        pw.println( "<TD COLSPAN=\"2\" BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        if ( top )
        {
            pw.println( "<A NAME=\"navbar_top_firstrow\"><!-- --></A>" );
        }
        else
        {
            pw.println( "<A NAME=\"navbar_bottom_firstrow\"><!-- --></A>" );
        }
        pw.println( "<TABLE BORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"3\" SUMMARY=\"\">" );
        pw.println( "  <TR ALIGN=\"center\" VALIGN=\"top\">" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "    <A HREF=\"" + root
            + "overview-summary.html\"><FONT CLASS=\"NavBarFont1\"><B>Overview</B></FONT></A>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#FFFFFF\" CLASS=\"NavBarCell1Rev\">" );
        pw.println( "     &nbsp;<FONT CLASS=\"NavBarFont1Rev\"><B>Package</B></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "    <FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "    <FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "    <FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "    <FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "    <FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "    <FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  </TR>" );
        pw.println( "</TABLE>" );
        pw.println( "</TD>" );
        pw.println( "<TD ALIGN=\"right\" VALIGN=\"top\" ROWSPAN=\"3\"><EM>" );
        if ( top )
        {
            if ( StringUtils.isNotEmpty( getOptions().getHeader() ) )
            {
                pw.println( getOptions().getHeader() );
            }
        }
        else
        {
            if ( StringUtils.isNotEmpty( getOptions().getFooter() ) )
            {
                pw.println( getOptions().getFooter() );
            }
        }
        pw.println( "</EM>" );
        pw.println( "</TD>" );
        pw.println( "</TR>" );
        pw.println( "" );
        pw.println( "<TR>" );
        pw.println( "<TD BGCOLOR=\"white\" CLASS=\"NavBarCell2\"><FONT SIZE=\"-2\">&nbsp;&nbsp;&nbsp;</FONT></TD>" );
        pw.println( "<TD BGCOLOR=\"white\" CLASS=\"NavBarCell2\"><FONT SIZE=\"-2\">" );
        pw.println( "  <A HREF=\"" + root + "index.html\" TARGET=\"_top\"><B>FRAMES</B></A>  &nbsp;" );
        pw.println( "&nbsp;<A HREF=\"" + current + "\" TARGET=\"_top\"><B>NO FRAMES</B></A>  &nbsp;" );
        pw.println( "</FONT></TD>" );
        pw.println( "</TR>" );
        pw.println( "</TABLE>" );
        if ( top )
        {
            pw.println( "<A NAME=\"skip-navbar_top\"></A>" );
        }
        else
        {
            pw.println( "<A NAME=\"skip-navbar_bottom\"></A>" );
        }
    }

    /**
     * Method createOverviewSummaryFrame
     *
     * @throws IOException if any
     */
    private void createOverviewSummaryFrame()
        throws IOException
    {
        String fileName = getDestDir() + File.separatorChar + "overview-summary.html";
        File file = new File( fileName );

        createDirs( file );

        FileOutputStream fos = new FileOutputStream( file );
        OutputStreamWriter fw;
        if ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) )
        {
            fw = new OutputStreamWriter( fos, getOptions().getDocencoding() );
        }
        else
        {
            fw = new OutputStreamWriter( fos );
        }
        PrintWriter pw = new PrintWriter( fw );

        pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
            + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
        pw.println( "<HTML>" );
        pw.println( "<HEAD>" );
        pw.println( getGeneratedBy() );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions().getDocencoding()
                                                                                   : DEFAULT_DOCENCODING );
        pw.println( "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=" + encoding + "\">" );
        if ( StringUtils.isNotEmpty( getOptions().getWindowtitle() ) )
        {
            pw.println( "<TITLE>Overview (" + getOptions().getWindowtitle() + ")</TITLE>" );
            pw.println( "<META NAME=\"keywords\" CONTENT=\"Overview, " + getOptions().getWindowtitle() + "\">" );
        }
        else
        {
            pw.println( "<TITLE>Overview Summary</TITLE>" );
        }
        pw.println( "<LINK REL=\"stylesheet\" TYPE=\"text/css\" HREF=\"styles.css\" TITLE=\"Style\">" );
        pw.println( "<SCRIPT TYPE=\"text/javascript\">" );
        pw.println( "<!--" );
        pw.println( "function windowTitle()" );
        pw.println( "{" );
        if ( StringUtils.isNotEmpty( getOptions().getWindowtitle() ) )
        {
            pw.println( "    parent.document.title=\"Overview (" + getOptions().getWindowtitle() + ")\";" );
        }
        else
        {
            pw.println( "    parent.document.title=\"Overview Summary\";" );
        }
        pw.println( "}" );
        pw.println( "//-->" );
        pw.println( "</SCRIPT>" );
        pw.println( "</HEAD>" );

        pw.println( "" );

        pw.println( "<BODY BGCOLOR=\"white\" ONLOAD=\"windowTitle();\">" );

        pw.println( "" );
        pw.println( "<!-- ========= START OF TOP NAVBAR ======= -->" );
        if ( StringUtils.isNotEmpty( getOptions().getTop() ) )
        {
            pw.println( getOptions().getTop() );
            pw.println( "<HR>" );
        }
        createOverviewSummaryFrameExtras( pw, true );
        pw.println( "<!-- ========= END OF TOP NAVBAR ========= -->" );

        pw.println( "<HR>" );
        pw.println( "<CENTER>" );
        if ( StringUtils.isNotEmpty( getOptions().getDoctitle() ) )
        {
            pw.println( "<H1>" + getOptions().getDoctitle() + "</H1>" );
        }
        else
        {
            pw.println( "<H1></H1>" );
        }
        pw.println( "</CENTER>" );
        pw.println( "" );
        pw.println( "<TABLE BORDER=\"1\" WIDTH=\"100%\" CELLPADDING=\"3\" CELLSPACING=\"0\" SUMMARY=\"\">" );
        pw.println( "<TR BGCOLOR=\"#CCCCFF\" CLASS=\"TableHeadingColor\">" );
        pw.println( "<TH ALIGN=\"left\" COLSPAN=\"2\"><FONT SIZE=\"+2\">" );
        pw.println( "<B>Packages</B></FONT></TH>" );
        pw.println( "</TR>" );

        Iterator iter = packageNames.iterator();
        while ( iter.hasNext() )
        {
            String packageName = (String) iter.next();
            String packageFileName = packageName.replace( '.', '/' ) + '/' + "package-summary.html";

            pw.println( "<TR BGCOLOR=\"white\" CLASS=\"TableRowColor\">" );
            pw.println( "<TD WIDTH=\"20%\"><B><A HREF=\"" + packageFileName + "\">" + packageName + "</A></B></TD>" );
            pw.println( "<TD>&nbsp;</TD>" );
            pw.println( "</TR>" );
        }
        pw.println( "</TABLE>" );

        pw.println( "" );
        pw.println( "<P>&nbsp;</P><HR>" );

        pw.println( "<!-- ========= START OF BOTTOM NAVBAR ======= -->" );
        createOverviewSummaryFrameExtras( pw, false );
        pw.println( "<!-- ========= END OF BOTTOM NAVBAR ======= -->" );

        if ( StringUtils.isNotEmpty( getOptions().getBottom() ) )
        {
            pw.println( "<HR>" );
            pw.println( getOptions().getBottom() );
        }

        pw.println( "</BODY>" );
        pw.println( "</HTML>" );
        IOUtil.close( pw );
    }

    private void createOverviewSummaryFrameExtras( PrintWriter pw, boolean top )
    {
        if ( top )
        {
            pw.println( "<A NAME=\"navbar_top\"><!-- --></A>" );
            pw.println( "<A HREF=\"#skip-navbar_top\" TITLE=\"Skip navigation links\"></A>" );
        }
        else
        {
            pw.println( "<A NAME=\"navbar_bottom\"><!-- --></A>" );
            pw.println( "<A HREF=\"#skip-navbar_bottom\" TITLE=\"Skip navigation links\"></A>" );
        }
        pw.println( "<TABLE BORDER=\"0\" WIDTH=\"100%\" CELLPADDING=\"1\" CELLSPACING=\"0\" SUMMARY=\"\">" );
        pw.println( "<TR>" );
        pw.println( "<TD COLSPAN=\"2\" BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        if ( top )
        {
            pw.println( "<A NAME=\"navbar_top_firstrow\"><!-- --></A>" );
        }
        else
        {
            pw.println( "<A NAME=\"navbar_bottom_firstrow\"><!-- --></A>" );
        }
        pw.println( "<TABLE BORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"3\" SUMMARY=\"\">" );
        pw.println( "  <TR ALIGN=\"center\" VALIGN=\"top\">" );
        pw.println( "  <TD BGCOLOR=\"#FFFFFF\" CLASS=\"NavBarCell1Rev\">"
            + "<FONT CLASS=\"NavBarFont1Rev\"><B>Overview</B></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "     &nbsp;<FONT CLASS=\"NavBarFont1\">Package</FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "     &nbsp;<FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "     &nbsp;<FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "     &nbsp;<FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "     &nbsp;<FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "     &nbsp;<FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  <TD BGCOLOR=\"#EEEEFF\" CLASS=\"NavBarCell1\">" );
        pw.println( "     &nbsp;<FONT CLASS=\"NavBarFont1\"></FONT>&nbsp;</TD>" );
        pw.println( "  </TR>" );
        pw.println( "</TABLE>" );
        pw.println( "</TD>" );
        pw.println( "<TD ALIGN=\"right\" VALIGN=\"top\" ROWSPAN=\"3\"><EM>" );
        if ( top )
        {
            if ( StringUtils.isNotEmpty( getOptions().getHeader() ) )
            {
                pw.println( getOptions().getHeader() );
            }
        }
        else
        {
            if ( StringUtils.isNotEmpty( getOptions().getFooter() ) )
            {
                pw.println( getOptions().getFooter() );
            }
        }
        pw.println( "</EM>" );
        pw.println( "</TD>" );
        pw.println( "</TR>" );
        pw.println( "" );
        pw.println( "<TR>" );
        pw.println( "<TD BGCOLOR=\"white\" CLASS=\"NavBarCell2\"><FONT SIZE=\"-2\">&nbsp;&nbsp;&nbsp;</FONT></TD>" );
        pw.println( "<TD BGCOLOR=\"white\" CLASS=\"NavBarCell2\"><FONT SIZE=\"-2\">" );
        pw.println( "  <A HREF=\"index.html\" target=\"_top\"><B>FRAMES</B></A>  &nbsp;" );
        pw.println( "&nbsp;<A HREF=\"overview-summary.html\" TARGET=\"_top\"><B>NO FRAMES</B></A>  &nbsp;" );
        pw.println( "</FONT></TD>" );
        pw.println( "</TR>" );
        pw.println( "</TABLE>" );
        if ( top )
        {
            pw.println( "<A NAME=\"skip-navbar_top\"></A>" );
        }
        else
        {
            pw.println( "<A NAME=\"skip-navbar_bottom\"></A>" );
        }
    }
}
