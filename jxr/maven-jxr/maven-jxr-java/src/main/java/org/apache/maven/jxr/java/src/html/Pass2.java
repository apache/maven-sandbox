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

import java.io.BufferedOutputStream;
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
import org.codehaus.plexus.util.StringUtils;

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
        InputStreamReader isr = new InputStreamReader( fis );
        BufferedReader br = new BufferedReader( isr );
        Vector v = new Vector();

        while ( ( line = br.readLine() ) != null )
        {
            v.addElement( line );
        }

        br.close();
        isr.close();
        fis.close();

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
        bw.write( "</body></html>" );
        bw.flush();
        bw.close();

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
        if ( StringUtils.isNotEmpty( getOptions().getEncoding() ) )
        {
            fw = new OutputStreamWriter( fos, getOptions().getEncoding() );
        }
        else
        {
            fw = new OutputStreamWriter( fos );
        }
        BufferedWriter result = new BufferedWriter( fw );

        result.write( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
            + "\"http://www.w3.org/TR/html4/loose.dtd\">\n" );
        result.write( "<html>" );
        result.write( "<head>\n" );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions()
            .getDocencoding() : System.getProperty( "file.encoding" ) );
        result.write( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\">\n" );
        result.write( "<title>" + packageName + "." + ref.getReferentFileClass() + " References</title>\n" );
        result.write( "<link rel=\"stylesheet\" type=\"text/css\" " + "href=\"" + getBackupPath( packageName )
            + "styles.css\">\n" );
        result.write( "</head>\n" );
        result.write( "<body>\n" );

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
            bw.write( "<p class=\"classReflist\">" );

            String nameString = "<p class=\"classReflistHeader\">Class: <a name=\"" + ref.getReferentTag()
                + "\" href=\"" + ref.getReferentFileClass() + "_java.html#" + ref.getReferentTag() + "\">"
                + ref.getReferentClass() + "</a></p>";

            bw.write( nameString );
        }
        else if ( ref.getReferentType().equals( ReferenceTypes.METHOD_REF ) )
        {
            bw.write( "<p class=\"methodReflist\">" );
            bw.write( "<!-- hello -->" );

            String nameString = "<p class=\"methodReflistHeader\">Method: <a name=\"" + ref.getReferentTag()
                + "\" href=\"" + ref.getReferentFileClass() + "_java.html#" + ref.getReferentTag() + "\">"
                + ref.getReferentTag() + "</a></p>";

            bw.write( nameString );
        }
        else if ( ref.getReferentType().equals( ReferenceTypes.VARIABLE_REF ) )
        {
            bw.write( "<p class=\"variableReflist\">" );

            String nameString = "<p class=\"variableReflistHeader\">Variable: <a name=\"" + ref.getReferentTag()
                + "\" href=\"" + ref.getReferentFileClass() + "_java.html#" + ref.getReferentTag() + "\">"
                + ref.getReferentTag() + "</a></p>";

            bw.write( nameString );
        }
        else
        {
            bw.write( "<p>open section " + ref.getReferentType() + "</p>" );
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
            String linkString = "<p class=\"classRefItem\"><a href=\"" + linkFilename + "#"
                + ref.getReferringLineNumber() + "\">" + ref.getReferringPackage() + "." + ref.getReferringClass()
                + "." + ref.getReferringMethod() + " (" + ref.getReferringFile() + ":" + ref.getReferringLineNumber()
                + ")</a></p>\n";

            bw.write( linkString );
        }
        else if ( ref.getReferentType().equals( ReferenceTypes.METHOD_REF ) )
        {
            String linkString = "<p class=\"methodRefItem\"><a href=\"" + linkFilename + "#"
                + ref.getReferringLineNumber() + "\">" + ref.getReferringPackage() + "." + ref.getReferringClass()
                + "." + ref.getReferringMethod() + " (" + ref.getReferringFile() + ":" + ref.getReferringLineNumber()
                + ")</a></p>\n";

            bw.write( linkString );
        }
        else if ( ref.getReferentType().equals( ReferenceTypes.VARIABLE_REF ) )
        {
            String linkString = "<p class=\"variableRefItem\"><a href=\"" + linkFilename + "#"
                + ref.getReferringLineNumber() + "\">" + ref.getReferringPackage() + "." + ref.getReferringClass()
                + "." + ref.getReferringMethod() + " (" + ref.getReferringFile() + ":" + ref.getReferringLineNumber()
                + ")</a></p>\n";

            bw.write( linkString );
        }
        else
        {
            bw.write( "<p>link for a " + ref.getReferentType() + "</p>" );
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
     */
    private void createPackageFiles()
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

            try
            {
                pw = new PrintWriter( new BufferedOutputStream( new FileOutputStream( file ) ) );

                pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
                    + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
                pw.println( "<html>" );
                pw.println( "<head>" );
                String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions()
                    .getDocencoding() : System.getProperty( "file.encoding" ) );
                pw.println( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\">" );
                pw.println( "<title>" + packageName + "</title>" );
                pw.println( "<LINK rel=\"stylesheet\" type=\"text/css\" " + "href=\"" + getBackupPath( packageName )
                    + "styles.css\">" );
                pw.println( "</head>" );
                pw.println( "<body>" );

                pw.println( "<h3>" );
                pw.println( "<a href=\"package-summary.html\" target=\"classFrame\">" + packageName + "</a>" );
                pw.println( "</h3>" );
                pw.println( "<p class=packagename>" + packageName + "</p>" );

                pw.println( "<h3>Classes</h3>" );
                Iterator iter = classes.iterator();

                while ( iter.hasNext() )
                {
                    ClassFileEntry cf = (ClassFileEntry) iter.next();
                    String className = cf.getClassName();
                    String fileClassName = cf.getFileName();
                    //int j = className.indexOf('.');
                    String anchor;

                    // if (j == -1)
                    // {
                    anchor = className;

                    // }
                    // else
                    // {
                    // anchor = className.substring(j+1);
                    // }
                    String tag = "<p class=\"classListItem\"><a href=\"" + fileClassName + "_java.html#" + anchor
                        + "\" TARGET=\"classFrame\">" + className + "</a></p>";

                    pw.println( tag );
                }

                pw.println( "</body></html>" );
                pw.close();
            }
            catch ( Exception ex )
            {
                log.error( "Error writing file:" + fileName, ex );
            }
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

        PrintWriter pw = new PrintWriter( new BufferedOutputStream( new FileOutputStream( file ) ) );

        pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" "
            + "\"http://www.w3.org/TR/html4/frameset.dtd\">" );
        pw.println( "<head>" );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions()
            .getDocencoding() : System.getProperty( "file.encoding" ) );
        pw.println( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\">" );
        if ( StringUtils.isNotEmpty( getOptions().getWindowtitle() ) )
        {
            pw.println( "<title>" + getOptions().getWindowtitle() + "</title>" );
        }
        else
        {
            pw.println( "<title>JavaSrc</title>" );
        }
        pw.println( "</head>" );
        pw.println( "<frameset cols=\"20%,80%\">" );
        pw.println( "  <frameset rows=\"30%,70%\">" );
        pw.println( "    <frame src=\"overview-frame.html\" name=\"packageListFrame\">" );
        pw.println( "    <frame src=\"allclasses-frame.html\" name=\"packageFrame\">" );
        pw.println( "  </frameset>" );
        pw.println( "  " );
        pw.println( "  <frameset rows=\"*\">" );
        pw.println( "    <frame src=\"overview-summary.html\" name=\"classFrame\">" );
        pw.println( "  </frameset>" );
        pw.println( "</frameset>" );
        pw.close();
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

        PrintWriter pw = new PrintWriter( new BufferedOutputStream( new FileOutputStream( file ) ) );
        Iterator iter = packageNames.iterator();

        pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
            + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
        pw.println( "<html>" );
        pw.println( "<head>" );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions()
            .getDocencoding() : System.getProperty( "file.encoding" ) );
        pw.println( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\">" );
        pw.println( "<title>Overview</title>" );
        pw.println( "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">" );
        pw.println( "</head>" );
        pw.println( "<body>" );
        if ( StringUtils.isNotEmpty( getOptions().getPackagesheader() ) )
        {
            pw.println( "<b>" + getOptions().getPackagesheader() + "</b>" );
        }
        pw.println( "<h3><a href=\"allclasses-frame.html\" target=\"packageFrame\">All Classes</a></h3>" );
        pw.println( "<h3>Packages</h3>" );
        while ( iter.hasNext() )
        {
            packageName = (String) iter.next();
            packageFileName = packageName.replace( '.', '/' ) + '/' + "classList.html";

            pw.println( "<p class=\"packageListItem\"><A HREF=\"" + packageFileName + "\" TARGET=\"packageFrame\">"
                + packageName + "</A></p>" );
        }

        pw.println( "</body></html>" );
        pw.close();
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

        PrintWriter pw = new PrintWriter( new FileOutputStream( file ) );

        pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
            + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
        pw.println( "<html>" );
        pw.println( "<head>" );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions()
            .getDocencoding() : System.getProperty( "file.encoding" ) );
        pw.println( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\">" );
        pw.println( "<title>All classes</title>" );
        pw.println( "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">" );
        pw.println( "</head>" );
        pw.println( "<body>" );

        pw.println( "<h3>All Classes</h3>" );
        Iterator iter = orderedAllClasses().iterator();

        while ( iter.hasNext() )
        {
            ClassFileEntry cf = (ClassFileEntry) iter.next();
            String className = cf.getClassName();
            String fileClassName = cf.getFileName();
            String anchor = className;
            String tag = "<p class=\"classListItem\"><a href=\"" + fileClassName + "_java.html#" + anchor
                + "\" TARGET=\"classFrame\">" + className + "</a></p>";

            pw.println( tag );
        }

        pw.println( "</body></html>" );
        pw.close();
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

            if ( log.isDebugEnabled() )
            {
                log.debug( "createPackageSummaryFiles() - " + packageName + " has " + classes.size() + " classes" );
            }

            totalClassCount += classes.size();
            fileName = getDestDir() + File.separatorChar + packageName.replace( '.', File.separatorChar )
                + File.separatorChar + "package-summary.html";
            file = new File( fileName );

            createDirs( file );

            pw = new PrintWriter( new BufferedOutputStream( new FileOutputStream( file ) ) );

            pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
                + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
            pw.println( "<html>" );
            pw.println( "<head>" );
            String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions()
                .getDocencoding() : System.getProperty( "file.encoding" ) );
            pw.println( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\">" );
            pw.println( "<title>" + packageName + " Summary</title>" );
            pw.println( "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getBackupPath( packageName )
                + "styles.css\">" );
            pw.println( "</head>" );
            pw.println( "<body>" );

            if ( StringUtils.isNotEmpty( getOptions().getTop() ) )
            {
                pw.println( getOptions().getTop() );
                pw.println( "<hr>" );
            }

            createPackageSummaryFilesExtras( pw, getBackupPath( packageName ), "package-summary.html" );

            pw.println( "<h2>" + packageName + "</h2>" );

            pw.println( "<table class=\"summary\">" );
            pw.println( "<thead>" );
            pw.println( "<tr>" );
            pw.println( "<th>Class Summary</th>" );
            pw.println( "</tr>" );
            pw.println( "</thead>" );
            pw.println( "<tbody>" );

            Iterator iter = classes.iterator();

            while ( iter.hasNext() )
            {
                ClassFileEntry cf = (ClassFileEntry) iter.next();
                String className = cf.getClassName();
                String fileClassName = cf.getFileName();
                String anchor;

                anchor = className;
                pw.println( "<tr>" );
                pw.println( "<td>" );
                pw.println( "<a href=\"" + fileClassName + "_java.html#" + anchor + "\" TARGET=\"classFrame\">"
                    + className + "</a>" );
                pw.println( "</td>" );
                pw.println( "</tr>" );
            }
            pw.println( "</tbody>" );
            pw.println( "</table>" );

            createPackageSummaryFilesExtras( pw, getBackupPath( packageName ), "package-summary.html" );

            pw.println( "<hr>" );
            pw.println( "<div class=\"bottom\">Copyright &copy; 2001-2003 Apache Software Foundation. "
                + "All Rights Reserved.</div>" );
            pw.println( "</body></html>" );
            pw.close();
        }
    }

    private void createPackageSummaryFilesExtras( PrintWriter pw, String root, String current )
    {
        pw.println( "<div class=\"overview\">" );
        pw.println( "<ul>" );
        pw.println( "<li><a href=\"" + root + "overview-summary.html\">Overview</a></li>" );
        pw.println( "<li class=\"selected\">Package</li>" );
        pw.println( "</ul>" );
        pw.println( "</div>" );
        pw.println( "<div class=\"framenoframe\">" );
        pw.println( "<ul>" );
        pw.println( "<li>" );
        pw.println( "<a href=\"" + root + "index.html\" target=\"_top\">FRAMES</a>" );
        pw.println( "</li>" );
        pw.println( "<li>" );
        pw.println( "<a href=\"" + current + "\" target=\"_top\">NO FRAMES</a>" );
        pw.println( "</li>" );
        pw.println( "</ul>" );
        pw.println( "</div>" );
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

        PrintWriter pw = new PrintWriter( new FileOutputStream( file ) );

        pw.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
            + "\"http://www.w3.org/TR/html4/loose.dtd\">" );
        pw.println( "<html>" );
        pw.println( "<head>" );
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions()
            .getDocencoding() : System.getProperty( "file.encoding" ) );
        pw.println( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\">" );
        pw.println( "<title>Overview</title>" );
        pw.println( "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">" );
        pw.println( "</head>" );
        pw.println( "<body>" );

        if ( StringUtils.isNotEmpty( getOptions().getTop() ) )
        {
            pw.println( getOptions().getTop() );
            pw.println( "<hr>" );
        }

        createOverviewSummaryFrameExtras( pw );

        pw.println( "<h2>" + getOptions().getDoctitle() + "</h2>" );

        pw.println( "<table class=\"summary\">" );
        pw.println( "<thead>" );
        pw.println( "<tr>" );
        pw.println( "<th>Packages</th>" );
        pw.println( "</tr>" );
        pw.println( "</thead>" );
        pw.println( "<tbody>" );

        Iterator iter = packageNames.iterator();
        while ( iter.hasNext() )
        {
            String packageName = (String) iter.next();
            String packageFileName = packageName.replace( '.', '/' ) + '/' + "package-summary.html";

            pw.println( "<tr>" );
            pw.println( "<td>" );
            pw.println( "<a href=\"" + packageFileName + "\">" + packageName + "</a>" );
            pw.println( "</td>" );
            pw.println( "</tr>" );
        }

        pw.println( "</tbody>" );
        pw.println( "</table>" );

        createOverviewSummaryFrameExtras( pw );
        pw.println( "<hr>" );
        if ( StringUtils.isNotEmpty( getOptions().getBottom() ) )
        {
            pw.println( "<div class=\"bottom\">" );
            pw.println( getOptions().getBottom() );
            pw.println( "</div>" );
        }
        pw.println( "</body></html>" );
        pw.close();
    }

    private void createOverviewSummaryFrameExtras( PrintWriter pw )
    {
        pw.println( "<div class=\"overview\">" );
        pw.println( "<ul>" );
        pw.println( "<li class=\"selected\">Overview</li>" );
        pw.println( "<li>Package</li>" );
        pw.println( "</ul>" );
        pw.println( "</div>" );
        pw.println( "<div class=\"framenoframe\">" );
        pw.println( "<ul>" );
        pw.println( "<li>" );
        pw.println( "<a href=\"index.html\" target=\"_top\">FRAMES</a>" );
        pw.println( "</li>" );
        pw.println( "<li>" );
        pw.println( "<a href=\"overview-summary.html\" target=\"_top\">NO FRAMES</a>" );
        pw.println( "</li>" );
        pw.println( "</ul>" );
        pw.println( "</div>" );
    }
}
