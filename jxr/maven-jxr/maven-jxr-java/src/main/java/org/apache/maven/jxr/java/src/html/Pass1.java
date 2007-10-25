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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.maven.jxr.java.src.JavaSrcOptions;
import org.apache.maven.jxr.java.src.symtab.HTMLTag;
import org.apache.maven.jxr.java.src.symtab.HTMLTagContainer;
import org.apache.maven.jxr.java.src.symtab.PackageDef;
import org.apache.maven.jxr.java.src.symtab.SymbolTable;
import org.apache.maven.jxr.java.src.util.JSCollections;
import org.apache.maven.jxr.java.src.util.SkipCRInputStream;
import org.apache.maven.jxr.java.src.xref.FileListener;
import org.apache.maven.jxr.java.src.xref.JavaXref;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import antlr.ANTLRException;

/**
 * Class Pass1
 *
 * @version $Id$
 */
public class Pass1
    extends AbstractPass
    implements FileListener
{
    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( Pass1.class );

    int currentColumn;

    int currentChar;

    HashSet inputFiles = new HashSet();

    // ----------------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------------

    /**
     * Constructor Pass1
     *
     * @param conf object
     */
    public Pass1( JavaSrcOptions conf )
    {
        super( conf );
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void notify( String path )
    {
        printAdvancement( path );
        inputFiles.add( path );
    }

    /**
     * @throws IOException if any
     */
    public void run()
        throws IOException
    {
        List javaFiles = FileUtils.getFileNames( new File( getSrcDir() ), "**/*.java", DEFAULT_EXCLUDES, true );

        // create a new symbol table
        SymbolTable symbolTable = SymbolTable.getSymbolTable();

        print( "Output dir: " + getDestDir() );

        symbolTable.setOutDirPath( getDestDir() );

        println( "Parsing" );

        // for each directory/file specified on the command line
        for ( Iterator it = javaFiles.iterator(); it.hasNext(); )
        {
            String file = (String) it.next();
            try
            {
                JavaXref.doFile( new File( file ), symbolTable, getOptions().isRecurse(), this ); // parse it
            }
            catch ( ANTLRException e )
            {
                throw new IOException( "ANTLRException: " + e.getMessage() );
            }
        }

        println( "Resolving types" );

        // resolve the types of all symbols in the symbol table
        symbolTable.resolveTypes();
        symbolTable.resolveRefs();

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

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

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
        String encoding = ( StringUtils.isNotEmpty( getOptions().getDocencoding() ) ? getOptions()
            .getDocencoding() : System.getProperty( "file.encoding" ) );

        String header = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset="
            + encoding
            + "\">\n"
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

        if ( StringUtils.isNotEmpty( getOptions().getTop() ) )
        {
            header += getOptions().getTop() + "<hr>\n";
        }

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
            SkipCRInputStream is = new SkipCRInputStream( new FileInputStream( javaFile ) );
            if ( StringUtils.isNotEmpty( getOptions().getEncoding() ) )
            {
                input = new LineNumberReader( new InputStreamReader( is, getOptions().getEncoding() ) );
            }
            else
            {

                input = new LineNumberReader( new InputStreamReader( is ) );
            }
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
                    SkipCRInputStream is = new SkipCRInputStream( new FileInputStream( javaFile ) );
                    if ( StringUtils.isNotEmpty( getOptions().getEncoding() ) )
                    {
                        input = new LineNumberReader( new InputStreamReader( is, getOptions().getEncoding() ) );
                    }
                    else
                    {

                        input = new LineNumberReader( new InputStreamReader( is ) );
                    }
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

    private void printAdvancement( String description )
    {
        if ( getOptions().isVerbose() )
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

    private static void print( String description )
    {
        System.out.print( description );
    }

    /**
     * Method getBackupPath
     *
     * @param tagList
     * @param element
     * @return
     */
    private static String getBackupPath( Object[] tagList, int element )
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
}
