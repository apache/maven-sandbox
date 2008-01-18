package org.apache.maven.doxia.module.xwiki.parser;

import junit.framework.TestCase;

public class MacroParserTest
    extends TestCase
{
    private MacroParser parser;

    protected void setUp()
    {
        parser = new MacroParser();
    }

    public void testParseSimplestMacroWithCondensedClosingStyle()
        throws Exception
    {
        String macro = "{macro/}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( macro.length(), result.position );
        assertNotNull( result.block );
        assertEquals( "macro", result.block.getName() );
        assertEquals( "", result.block.getContent() );
        assertTrue( result.block.getParameters().isEmpty() );
    }

    public void testParseSimplestMacroWithExpandedClosingStyle()
        throws Exception
    {
        String macro = "{macro}{/macro}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( macro.length(), result.position );
        assertNotNull( result.block );
        assertEquals( "macro", result.block.getName() );
        assertEquals( "", result.block.getContent() );
        assertTrue( result.block.getParameters().isEmpty() );
    }

    public void testParseMacroWithOneParameter()
        throws Exception
    {
        String macro = "{macro:param1=value1/}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( macro.length(), result.position );
        assertNotNull( result.block );
        assertEquals( "macro", result.block.getName() );
        assertEquals( "", result.block.getContent() );
        assertEquals( 1, result.block.getParameters().size() );
        assertEquals( "value1", result.block.getParameters().get( "param1" ) );
    }

    public void testParseMacroWithSeveralParameters()
        throws Exception
    {
        String macro = "{macro:param1=value1|param2=value2/}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( macro.length(), result.position );
        assertNotNull( result.block );
        assertEquals( "macro", result.block.getName() );
        assertEquals( "", result.block.getContent() );
        assertEquals( 2, result.block.getParameters().size() );
        assertEquals( "value1", result.block.getParameters().get( "param1" ) );
        assertEquals( "value2", result.block.getParameters().get( "param2" ) );
    }

    public void testParseMacroWithContent()
        throws Exception
    {
        String macro = "{macro}Some /=|content{/macro}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( macro.length(), result.position );
        assertNotNull( result.block );
        assertEquals( "macro", result.block.getName() );
        assertEquals( "Some /=|content", result.block.getContent() );
        assertEquals( 0, result.block.getParameters().size() );
    }

    public void testParseMacroWithInvalidMacroName()
        throws Exception
    {
        // This is not a macro. It should be ignored and no macro block should be created
        String macro = "{[link]/}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( 1, result.position );
        assertNull( result.block );
    }

    public void testParseOldStyleMacroInCompatibilityModeWhenMultilineMacro()
        throws Exception
    {
        parser.setCompatibilityMode( true );
        String macro = "{code}Some content here{code}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( macro.length(), result.position );
        assertNotNull( result.block );
        assertEquals( "code", result.block.getName() );
        assertEquals( "Some content here", result.block.getContent() );
        assertEquals( 0, result.block.getParameters().size() );
    }

    public void testParseOldStyleMacroInCompatibilityModeWhenSinglelineMacro()
        throws Exception
    {
        parser.setCompatibilityMode( true );
        String macro = "{somesinglelinemacro}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( macro.length(), result.position );
        assertNotNull( result.block );
        assertEquals( "somesinglelinemacro", result.block.getName() );
        assertEquals( "", result.block.getContent() );
        assertEquals( 0, result.block.getParameters().size() );
    }

    public void testParseOldStyleMacroWithDefaultParameterWithNoValue()
        throws Exception
    {
        parser.setCompatibilityMode( true );
        String macro = "{macro:value/}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( macro.length(), result.position );
        assertNotNull( result.block );
        assertEquals( "macro", result.block.getName() );
        assertEquals( "", result.block.getContent() );
        assertEquals( 1, result.block.getParameters().size() );
        assertEquals( "value", result.block.getParameters().get( "default" ) );
    }

    public void testParseOldStyleMacroWithDefaultParameterWithNoValueAndOldClosingStyle()
        throws Exception
    {
        parser.setCompatibilityMode( true );
        String macro = "{macro:value}";
        MacroParser.MacroParserResult result = parser.parse( macro + " ...", 1 );

        assertEquals( macro.length(), result.position );
        assertNotNull( result.block );
        assertEquals( "macro", result.block.getName() );
        assertEquals( "", result.block.getContent() );
        assertEquals( 1, result.block.getParameters().size() );
        assertEquals( "value", result.block.getParameters().get( "default" ) );
    }
}
