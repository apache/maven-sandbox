package org.apache.maven.shared.parser.java;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import junit.framework.TestCase;

public class JavaParserTest
    extends TestCase
{
    private File getSampleFile( int idx )
    {
        return new File( "src/test/samples/sample-" + idx + ".java" );
    }

    public void testParseSimple()
        throws RecognitionException, TokenStreamException, FileNotFoundException
    {
        File sample = getSampleFile( 1 );
        JavaParser parser = new JavaParser();
        List parsed = parser.parse( sample );
        assertNotNull( parsed );
        assertEquals( 1, parsed.size() );
        
        // TODO: No real meat here (yet)
    }

    public void testParseSimpleWithImport()
        throws RecognitionException, TokenStreamException, FileNotFoundException
    {
        File sample = getSampleFile( 2 );
        JavaParser parser = new JavaParser();
        List parsed = parser.parse( sample );
        assertNotNull( parsed );
        assertEquals( 1, parsed.size() );
        
        // TODO: No real meat here (yet)
    }

    public void testParseImportAndJavadoc()
        throws RecognitionException, TokenStreamException, FileNotFoundException
    {
        File sample = getSampleFile( 3 );
        JavaParser parser = new JavaParser();
        List parsed = parser.parse( sample );
        assertNotNull( parsed );
        assertEquals( 1, parsed.size() );
        
        // TODO: No real meat here (yet)
    }

    public void testParseMultipleComments()
        throws RecognitionException, TokenStreamException, FileNotFoundException
    {
        File sample = getSampleFile( 5 );
        JavaParser parser = new JavaParser();
        List parsed = parser.parse( sample );
        assertNotNull( parsed );
        assertEquals( 1, parsed.size() );
        
        // TODO: No real meat here (yet)
    }
}
