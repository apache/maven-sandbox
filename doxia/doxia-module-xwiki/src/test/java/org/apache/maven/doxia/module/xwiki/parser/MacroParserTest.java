package org.apache.maven.doxia.module.xwiki.parser;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MacroParserTest extends TestCase
{
    private MacroParser parser;

    protected void setUp()
    {
        parser = new MacroParser();
    }

    public void testParseSimplestMacroWithCondensedClosingStyle() throws Exception
    {
        List blocks = new ArrayList();
        String macro = "{macro/}";
        int pos = parser.parse( macro + " ...", 1, blocks);

        assertEquals(macro.length(), pos);
        assertEquals(1, blocks.size());
        MacroBlock macroBlock = (MacroBlock) blocks.get(0);
        assertEquals("macro", macroBlock.getName());
        assertEquals("", macroBlock.getContent());
        assertTrue(macroBlock.getParameters().isEmpty());
    }

    public void testParseSimplestMacroWithExpandedClosingStyle() throws Exception
    {
        List blocks = new ArrayList();
        String macro = "{macro}{/macro}";
        int pos = parser.parse( macro + " ...", 1, blocks);

        assertEquals(macro.length(), pos);
        assertEquals(1, blocks.size());
        MacroBlock macroBlock = (MacroBlock) blocks.get(0);
        assertEquals("macro", macroBlock.getName());
        assertEquals("", macroBlock.getContent());
        assertTrue(macroBlock.getParameters().isEmpty());
    }

    public void testParseMacroWithOneParameter() throws Exception
    {
        List blocks = new ArrayList();
        String macro = "{macro:param1=value1/}";
        int pos = parser.parse( macro + " ...", 1, blocks);

        assertEquals(macro.length(), pos);
        assertEquals(1, blocks.size());
        MacroBlock macroBlock = (MacroBlock) blocks.get(0);
        assertEquals("macro", macroBlock.getName());
        assertEquals("", macroBlock.getContent());
        assertEquals(1, macroBlock.getParameters().size());
        assertEquals("value1", macroBlock.getParameters().get("param1"));
    }

    public void testParseMacroWithSeveralParameters() throws Exception
    {
        List blocks = new ArrayList();
        String macro = "{macro:param1=value1|param2=value2/}";
        int pos = parser.parse( macro + " ...", 1, blocks);

        assertEquals(macro.length(), pos);
        assertEquals(1, blocks.size());
        MacroBlock macroBlock = (MacroBlock) blocks.get(0);
        assertEquals("macro", macroBlock.getName());
        assertEquals("", macroBlock.getContent());
        assertEquals(2, macroBlock.getParameters().size());
        assertEquals("value1", macroBlock.getParameters().get("param1"));
        assertEquals("value2", macroBlock.getParameters().get("param2"));
    }

    public void testParseMacroWithContent() throws Exception
    {
        List blocks = new ArrayList();
        String macro = "{macro}Some /=|content{/macro}";
        int pos = parser.parse( macro + " ...", 1, blocks);

        assertEquals(macro.length(), pos);
        assertEquals(1, blocks.size());
        MacroBlock macroBlock = (MacroBlock) blocks.get(0);
        assertEquals("macro", macroBlock.getName());
        assertEquals("Some /=|content", macroBlock.getContent());
        assertEquals(0, macroBlock.getParameters().size());
    }
}
