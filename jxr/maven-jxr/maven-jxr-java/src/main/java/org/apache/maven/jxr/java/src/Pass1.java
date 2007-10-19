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

import org.apache.log4j.Logger;
import org.apache.maven.jxr.java.src.symtab.HTMLTag;
import org.apache.maven.jxr.java.src.symtab.HTMLTagContainer;
import org.apache.maven.jxr.java.src.symtab.PackageDef;
import org.apache.maven.jxr.java.src.symtab.SymbolTable;
import org.apache.maven.jxr.java.src.util.JSCollections;
import org.apache.maven.jxr.java.src.util.SkipCRInputStream;
import org.apache.maven.jxr.java.src.xref.FileListener;
import org.apache.maven.jxr.java.src.xref.JavaXref;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class Pass1
 *
 * @version $Id$
 */
public class Pass1
    implements FileListener
{
    /** Field DEFAULT_DIR */
    public static final String DEFAULT_DIR = ".";

    /** Field USAGE */
    public static final String USAGE = "Usage: java [-DdestDir=<doc dir>] [-Dtitle=<title>] [-Dverbose=true] "
        + "[-Drecurse=true] " + Pass1.class.getName() + " <source dir> [<source dir> <source dir> ...]";

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( Pass1.class );

    /** Output dir */
    private String destDir;

    /** Title to be placed in the HTML title tag */
    private String title;

    /** Specify recursive pass */
    private boolean recurse;

    /** Specify verbose information */
    private boolean verbose;

    int currentColumn;

    int currentChar;

    HashSet inputFiles = new HashSet();

    // ----------------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------------

    /**
     * Constructor Pass1
     */
    public Pass1()
    {
        // nop
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * @return the output dir
     */
    public String getDestDir()
    {
        return this.destDir;
    }

    /**
     * @param d a new output dir
     */
    public void setDestDir( String d )
    {
        this.destDir = d;
    }

    /**
     * @return the windows title
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @param t a new windows title
     */
    public void setTitle( String t )
    {
        this.title = t;
    }

    /**
     * @return recursive pass
     */
    public boolean isRecurse()
    {
        return this.recurse;
    }

    /**
     * @param recurse true to do a recursive pass, false otherwise
     */
    public void setRecurse( boolean recurse )
    {
        this.recurse = recurse;
    }

    /**
     * @return verbose information
     */
    public boolean isVerbose()
    {
        return this.verbose;
    }

    /**
     * @param verbose true to verbose information, false otherwise
     */
    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
    }

    /** {@inheritDoc} */
    public void notify( String path )
    {
        printAdvancement( path );
        inputFiles.add( path );
    }

    /**
     * Main method to pass Java source files.
     *
     * @param args not null
     * @see #initializeDefaults()
     * @see #run(String[])
     * @throws Exception if any
     */
    public static void main( String args[] )
        throws Exception
    {
        Pass1 p1 = new Pass1();

        p1.initializeDefaults();
        p1.run( args );
    }

    /**
     * @param args not null
     * @throws IllegalArgumentException if args is null
     */
    public void run( String[] args )
    {
        if ( args == null )
        {
            throw new IllegalArgumentException( "args is required" );
        }

        // Use a try/catch block for parser exceptions
        try
        {
            // create a new symbol table
            SymbolTable symbolTable = SymbolTable.getSymbolTable();

            // if we have at least one command-line argument
            if ( args.length > 0 )
            {
                print( "Output dir: " + getDestDir() );

                symbolTable.setOutDirPath( getDestDir() );

                println( "Parsing" );

                // for each directory/file specified on the command line
                for ( int i = 0; i < args.length; i++ )
                {
                    JavaXref.doFile( new File( args[i] ), symbolTable, isRecurse(), this ); // parse it
                }

                println( "Resolving types" );

                // resolve the types of all symbols in the symbol table
                symbolTable.resolveTypes();
                symbolTable.resolveRefs();
            }
            else
            {
                println( USAGE );
                return;
            }

            // Iterate through each package
            Hashtable packageTable = symbolTable.getPackages();
            Enumeration pEnum = packageTable.elements();

            println( "Persisting definitions" );

            while ( pEnum.hasMoreElements() )
            {
                PackageDef pDef = (PackageDef) pEnum.nextElement();

                printAdvancement( "Processing package " + pDef.getName() );

                // Generate tags for each package.  We cannot do one class
                // at a time because more than one class might be in a
                // single file, and we write out each file only one time.
                HTMLTagContainer tagList = new HTMLTagContainer();

                pDef.generateTags( tagList );

                Hashtable fileTable = tagList.getFileTable();
                Enumeration enumList = fileTable.keys();
                Vector tempFileTags = new Vector();

                while ( enumList.hasMoreElements() )
                {
                    tempFileTags.clear();

                    File f = (File) enumList.nextElement();

                    if ( inputFiles.contains( f.getAbsolutePath() ) )
                    {
                        Vector fileTags = (Vector) fileTable.get( f );

                        tempFileTags.addAll( fileTags );

                        // Generate the HTML tags for all references in this file
                        // I.e. generate HTML mark-up of this .java file
                        SymbolTable.createReferenceTags( f, tempFileTags );
                        SymbolTable.getCommentTags( f, tempFileTags );
                        SymbolTable.getLiteralTags( f, tempFileTags );
                        SymbolTable.getKeywordTags( f, tempFileTags );
                        createClassFiles( tempFileTags );
                    }
                }

                // Create reference files
                // I.e. generate HTML mark-up of all definitions in this package's .java files
                // (no longer -- this happens in Pass2 now)
                // System.out.println("\nWriting definition HTML...");
                // pDef.generateReferenceFiles(getOutDir());
                pDef.persistDefinitions( getDestDir() );
            }

            println( "Persisting references" );

            symbolTable.persistRefs( getDestDir() );
        }
        catch ( Exception e )
        {
            log.error( "Exception: " + e.getMessage(), e );
            //System.exit(1);                                 // make this behavior an option?
        }
    }

    /**
     * Initialize defaults fields
     */
    public void initializeDefaults()
    {
        String outdir = System.getProperty( "destDir" );

        if ( outdir == null )
        {
            outdir = DEFAULT_DIR;
        }

        setDestDir( outdir );

        String t = System.getProperty( "title" );

        if ( t == null )
        {
            t = "Pass1: " + outdir;
        }

        setTitle( t );

        boolean doRecurse = true;
        String recurseStr = System.getProperty( "recurse" );

        if ( recurseStr != null )
        {
            recurseStr = recurseStr.trim();

            if ( recurseStr.equalsIgnoreCase( "off" ) || recurseStr.equalsIgnoreCase( "false" )
                || recurseStr.equalsIgnoreCase( "no" ) || recurseStr.equalsIgnoreCase( "0" ) )
            {
                doRecurse = false;
            }
        }

        setRecurse( doRecurse );

        boolean v = false;
        String verboseStr = System.getProperty( "verbose" );

        if ( verboseStr != null )
        {
            verboseStr = verboseStr.trim();

            if ( verboseStr.equalsIgnoreCase( "on" ) || verboseStr.equalsIgnoreCase( "true" )
                || verboseStr.equalsIgnoreCase( "yes" ) || verboseStr.equalsIgnoreCase( "1" ) )
            {
                v = true;
            }
        }

        setVerbose( v );
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Method createDirs
     *
     * @param f
     */
    private void createDirs( File f )
    {
        String parentDir = f.getParent();
        File directory = new File( parentDir );

        if ( !directory.exists() )
        {
            directory.mkdirs();
        }
    }

    /**
     * Method getBackupPath
     *
     * @param tagList
     * @param element
     * @return
     */
    private String getBackupPath( Object[] tagList, int element )
    {
        HTMLTag t = (HTMLTag) tagList[element];
        String packageName = t.getPackageName();

        if ( packageName.equals( "" ) )
        {
            File tempFile = t.getFile();
            int i = Math.min( element + 1, tagList.length );
            HTMLTag tempTag = (HTMLTag) tagList[i];

            while ( tempTag.getFile().equals( tempFile ) && ( i < tagList.length ) )
            {
                if ( ( tempTag.getPackageName() != null ) && ( tempTag.getPackageName().length() > 0 ) )
                {
                    packageName = tempTag.getPackageName();

                    break;
                }

                i++;

                tempTag = (HTMLTag) tagList[i];
            }
        }

        return getBackupPath( packageName );
    }

    /**
     * Method createClassFile
     *
     * @param tagList
     * @param element
     * @return
     * @throws IOException
     */
    private HTMLOutputWriter createClassFile( Object[] tagList, int element )
        throws IOException
    {
        HTMLTag t = (HTMLTag) tagList[element];
        String packageName = t.getPackageName();

        if ( packageName.equals( "" ) )
        {
            File tempFile = t.getFile();
            int i = Math.min( element + 1, tagList.length );
            HTMLTag tempTag = (HTMLTag) tagList[i];

            while ( tempTag.getFile().equals( tempFile ) && ( i < tagList.length ) )
            {
                if ( ( tempTag.getPackageName() != null ) && ( tempTag.getPackageName().length() > 0 ) )
                {
                    packageName = tempTag.getPackageName();

                    break;
                }

                i++;

                tempTag = (HTMLTag) tagList[i];
            }
        }

        String fileName = t.getFile().toString();

        if ( log.isDebugEnabled() )
        {
            log.debug( "createClassFile(Object[], int) - Package name=" + t.getPackageName() );
        }

        String packagePath = packageName.replace( '.', File.separatorChar );
        //String htmlPackagePath = packageName.replace('.', '/');
        String pathName = getDestDir() + File.separatorChar + packagePath;

        int position = fileName.lastIndexOf( File.separatorChar );

        if ( position == -1 )
        {
            position = 0;
        }

        String baseName = fileName.substring( position, fileName.length() );
        //String className = baseName.substring(
        //        0, baseName.lastIndexOf('.')).replace(File.separatorChar, '.');

        baseName = baseName.replace( '.', '_' );
        baseName = baseName + ".html";

        String newFileName = pathName + File.separatorChar + baseName;
        File f = new File( newFileName );

        createDirs( f );

        HTMLOutputWriter output = new LineOutputWriter( new BufferedOutputStream( new FileOutputStream( f ) ) );
        String backup = getBackupPath( tagList, element );
        String header = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "<title>"
            + packageName
            + "."
            + SymbolTable.getClassList( t.getFile() )
            + "</title>\n"
            + "<LINK rel=\"stylesheet\" type=\"text/css\" href=\""
            + backup
            + "styles.css\">\n"
            + "</head>\n"
            + "<body>\n";

        output.write( header, 0, header.length() );

        // "'<A HREF=\"./"+htmlPackagePath+"/classList.html\" TARGET=\"packageFrame\">" + packageName + "</A>: " + SymbolTable.getClassList(t.getFile()) + "');\n"+
        packagePath = packageName.replace( '.', '/' );

        output.write( "<pre>\n", 0, 6 );

        return ( output );
    }

    /**
     * Method finishFile
     *
     * @param input
     * @param output
     * @throws IOException
     */
    private void finishFile( LineNumberReader input, HTMLOutputWriter output )
        throws IOException
    {
        while ( currentChar != -1 )
        {
            output.writeHTML( currentChar );

            currentChar = input.read();
        }

        input.close();
        output.write( "</pre>\n", 0, 7 );
        output.write( "</body></html>" );
        output.flush();
        output.close();
    }

    /**
     * Method writeUntilNextTag
     *
     * @param t
     * @param input
     * @param output
     * @throws IOException
     */
    private void writeUntilNextTag( HTMLTag t, LineNumberReader input, HTMLOutputWriter output )
        throws IOException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "writeUntilNextTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Looking for next tag line:|" );
        }

        while ( ( currentChar != -1 ) && ( input.getLineNumber() + 1 ) != t.getLine() )
        {
            output.writeHTML( currentChar );

            if ( log.isDebugEnabled() )
            {
                log.debug( "writeUntilNextTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - _currentChar"
                    + currentChar );
            }

            currentChar = input.read();
        }

        // Write out last carriage return
        output.writeHTML( currentChar );

        if ( log.isDebugEnabled() )
        {
            log.debug( "writeUntilNextTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - _currentChar" + currentChar );
        }

        currentChar = input.read();

        if ( log.isDebugEnabled() )
        {
            log.debug( "writeUntilNextTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - |" );
        }
    }

    /**
     * Method writeComment
     *
     * @param t
     * @param input
     * @param output
     * @throws IOException
     */
    private void writeComment( HTMLTag t, LineNumberReader input, HTMLOutputWriter output )
        throws IOException
    {
        int length = t.getLength();
        int i = 0;

        output.write( "<span class=\"comment\">" );

        while ( i < length )
        {
            if ( currentChar == '\n' )
            {
                output.write( "</span>" );
            }
            output.writeHTML( (char) currentChar );

            if ( currentChar == '\n' )
            {
                output.write( "<span class=\"comment\">" );
                currentColumn = 0;
            }

            currentChar = input.read();

            currentColumn++;
            i++;
        }

        output.write( "</span>" );

        if ( currentChar == '\n' )
        {
            currentColumn = 0;
        }
    }

    /**
     * Method writeHTMLTag
     *
     * @param t
     * @param input
     * @param output
     * @throws IOException
     */
    private void writeHTMLTag( HTMLTag t, LineNumberReader input, HTMLOutputWriter output )
        throws IOException
    {
        // Write out line from current column to tag start column
        if ( log.isDebugEnabled() )
        {
            log.debug( "writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Current column=" + currentColumn );
            log.debug( "writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Writing up to tag start:|" );
        }

        while ( currentColumn < t.getStartColumn() )
        {
            output.writeHTML( currentChar );

            if ( currentChar == '\n' )
            {
                currentColumn = 0;
            }

            if ( log.isDebugEnabled() )
            {
                log.debug( "writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - _currentChar=" + currentChar );
            }

            currentChar = input.read();

            currentColumn++;
        }

        if ( log.isDebugEnabled() )
        {
            log.debug( "writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - |" );
        }

        // Check for comment
        if ( t.isComment() )
        {
            writeComment( t, input, output );
        }
        else if ( t.isLiteral() )
        {
            writeLiteral( t, input, output );
        }
        else if ( t.isKeyword() )
        {
            writeKeyword( t, input, output );
        }
        else
        {
            // Write HTML tag
            output.write( t.getText() );

            if ( log.isDebugEnabled() )
            {
                log.debug( "writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Wrote tag:" + t.getText() );
            }

            // Read past original token
            int length = t.getOrigLength();

            length = length - t.getNumBreaks();

            if ( log.isDebugEnabled() )
            {
                log.debug( "writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Skipping:\"" );
            }

            for ( int j = 0; j < length; j++ )
            {
                if ( log.isDebugEnabled() )
                {
                    log.debug( "writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - _currentChar="
                        + currentChar );
                }

                if ( currentChar == '\n' )
                {
                    currentColumn = 0;
                }

                currentChar = input.read();

                currentColumn++;
            }

            if ( currentChar == '\n' )
            {
                currentColumn = 0;
            }
        }
    }

    private void writeLiteral( HTMLTag t, LineNumberReader input, HTMLOutputWriter output )
        throws IOException
    {
        int length = t.getLength();
        int i = 0;

        output.write( "<span class=\"string\">" );

        while ( i < length )
        {
            output.writeHTML( (char) currentChar );

            if ( currentChar == '\n' )
            {
                currentColumn = 0;
            }

            currentChar = input.read();

            currentColumn++;
            i++;
        }

        output.write( "</span>" );

        if ( currentChar == '\n' )
        {
            currentColumn = 0;
        }
    }

    private void writeKeyword( HTMLTag t, LineNumberReader input, HTMLOutputWriter output )
        throws IOException
    {
        int length = t.getLength();
        int i = 0;

        output.write( "<strong>" );

        while ( i < length )
        {
            output.writeHTML( (char) currentChar );

            if ( currentChar == '\n' )
            {
                currentColumn = 0;
            }

            currentChar = input.read();

            currentColumn++;
            i++;
        }

        output.write( "</strong>" );

        if ( currentChar == '\n' )
        {
            currentColumn = 0;
        }
    }

    /**
     * Method createClassFiles
     *
     * @param tagList
     */
    private void createClassFiles( Vector tagList )
    {
        HTMLTag t;
        File javaFile;
        LineNumberReader input;
        HTMLOutputWriter output;
        Object[] sortedList;

        sortedList = JSCollections.sortVector( tagList );

        // Collections.sort(tagList);
        t = (HTMLTag) sortedList[0];
        javaFile = t.getFile();

        printAdvancement( "Writing tags for file " + javaFile.toString() );

        // Create first file
        try
        {
            output = createClassFile( sortedList, 0 );
            input = new LineNumberReader(
                                          new InputStreamReader(
                                                                 new SkipCRInputStream( new FileInputStream( javaFile ) ) ) );
            currentChar = input.read();
            currentColumn = 1;
        }
        catch ( Exception e )
        {
            log.error( "1: Could not open file:" + javaFile.getAbsolutePath() + " or html file.", e );
            return;
        }

        for ( int i = 0; i < sortedList.length; i++ )
        {
            t = (HTMLTag) sortedList[i];

            if ( log.isDebugEnabled() )
            {
                log.debug( "createClassFiles(Vector) - nTag Text=\"" + t.getText() + "\"" );
                log.debug( "createClassFiles(Vector) - Length=" + t.getOrigLength() );
                log.debug( "createClassFiles(Vector) - Line,col=" + t.getLine() + "," + t.getStartColumn() );
            }

            // Check for new java file encountered.
            // Close previous files and open new ones.
            String currentFile = javaFile.toString();
            String newFile = t.getFile().toString();

            if ( log.isDebugEnabled() )
            {
                log.debug( "createClassFiles(Vector) - cur file=|" + currentFile + "|" );
                log.debug( "createClassFiles(Vector) - new file=|" + newFile + "|" );
            }

            if ( !newFile.equals( currentFile ) )
            {
                try
                {

                    // Write out rest of previous file
                    finishFile( input, output );

                    // Open new file
                    javaFile = t.getFile();
                    input = new LineNumberReader(
                                                  new InputStreamReader(
                                                                         new SkipCRInputStream(
                                                                                                new FileInputStream(
                                                                                                                     javaFile ) ) ) );
                    output = createClassFile( sortedList, i );
                    currentColumn = 1;
                    currentChar = input.read();
                }
                catch ( Exception e )
                {
                    log.error( "2: Error handling tag:" + t, e );

                    continue;
                }
            }

            // Check for new line encountered
            if ( t.getLine() != ( input.getLineNumber() + 1 ) )
            {
                currentColumn = 1;

                // Write out characters until we reach the line
                try
                {
                    writeUntilNextTag( t, input, output );
                }
                catch ( Exception e )
                {
                    log.error( "3: Error handling tag:" + t, e );

                    continue;
                }
            }

            try
            {
                writeHTMLTag( t, input, output );
            }
            catch ( Exception e )
            {
                log.error( "4: Error handling tag:" + t, e );

                continue;
            }
        } // end for

        // Finish writing out the file
        try
        {
            finishFile( input, output );
        }
        catch ( IOException e )
        {
            log.error( "IOException: " + e.getMessage(), e );
        }
    }

    private void print( String description )
    {
        System.out.print( description );
    }

    private void println( String description )
    {
        System.out.print( "\n" );
        System.out.print( description );
    }

    private void printAdvancement( String description )
    {
        if ( isVerbose() )
        {
            System.out.println( description );
        }
        else
        {
            System.out.print( "." );
        }
    }

    // ----------------------------------------------------------------------
    // Static methods
    // ----------------------------------------------------------------------

    /**
     * Returns the path to the top level of the source hierarchy from the files
     * og\f a given class.
     *
     * @param packageName the package to get the backup path for
     * @return
     * @returns the path from the package to the top level, as a string
     */
    private static String getBackupPath( String packageName )
    {
        StringTokenizer st = new StringTokenizer( packageName, "." );
        String backup = "";
        int dirs = 0;
        String newPath = "";

        if ( log.isDebugEnabled() )
        {
            log.debug( "getBackupPath(String) - Package Name for BackupPath=" + packageName );
        }

        dirs = st.countTokens();

        for ( int j = 0; j < dirs; j++ )
        {
            backup = backup + "../";
        }

        newPath = backup;

        if ( log.isDebugEnabled() )
        {
            log.debug( "getBackupPath(String) - Package Name for newPath=" + newPath );
        }

        return ( newPath );
    }
}
