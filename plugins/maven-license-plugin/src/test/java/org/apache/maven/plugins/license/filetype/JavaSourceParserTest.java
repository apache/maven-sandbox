package org.apache.maven.plugins.license.filetype;

import org.apache.maven.plugins.license.ParsingException;
import org.apache.maven.plugins.license.util.SourceLocation;

import java.io.File;

import junit.framework.TestCase;

public class JavaSourceParserTest
    extends TestCase
{
    public void testIdentifyCommentInsertionLocation()
        throws ParsingException
    {
        File testFile = new File( "src/test/filetypes/java/inject-1.java" );
        JavaSourceParser parser = new JavaSourceParser();
        SourceLocation location = parser.identifyCommentInsertionLocation( testFile );
        assertNotNull( location );
    }
}
