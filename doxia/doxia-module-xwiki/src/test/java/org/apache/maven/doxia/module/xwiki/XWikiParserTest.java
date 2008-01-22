package org.apache.maven.doxia.module.xwiki;

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

import junit.framework.TestCase;
import org.apache.maven.doxia.module.xwiki.blocks.FigureBlock;
import org.apache.maven.doxia.module.xwiki.blocks.ParagraphBlock;
import org.apache.maven.doxia.module.xwiki.blocks.TextBlock;
import org.apache.maven.doxia.module.xwiki.blocks.SectionBlock;
import org.apache.maven.doxia.module.xwiki.blocks.BoldBlock;
import org.apache.maven.doxia.module.xwiki.blocks.ItalicBlock;
import org.apache.maven.doxia.module.xwiki.blocks.LinkBlock;

import java.io.StringReader;
import java.util.List;

/**
 * Test class for XWikiParser.
 */
public class XWikiParserTest
    extends TestCase
{
    private XWikiParser parser;

    protected void setUp()
    {
        this.parser = new XWikiParser();
    }

    public void testSimpleImageOnALine()
        throws Exception
    {
        List blocks = parser.parse( new StringReader( "{image:photo.jpg}" ) );

        assertEquals( 1, blocks.size() );
        assertTrue( blocks.get( 0 ) instanceof FigureBlock );
        FigureBlock figureBlock = (FigureBlock) blocks.get( 0 );
        assertEquals( "photo.jpg", figureBlock.getLocation() );
        assertNull( figureBlock.getCaption() );
    }

    public void testImageInsideAParagraph()
        throws Exception
    {
        List blocks = parser.parse( new StringReader( "Image inside a {image:photo.jpg} paragraph." ) );
        assertEquals( 1, blocks.size() );
        assertTrue( blocks.get( 0 ) instanceof ParagraphBlock );
        ParagraphBlock paraBlock = (ParagraphBlock) blocks.get( 0 );
        assertEquals( 3, paraBlock.getBlocks().size() );
        assertTrue( paraBlock.getBlocks().get( 0 ) instanceof TextBlock );
        TextBlock textBlock1 = (TextBlock) paraBlock.getBlocks().get( 0 );
        assertEquals( "Image inside a ", textBlock1.getText() );
        FigureBlock figureBlock = (FigureBlock) paraBlock.getBlocks().get( 1 );
        assertEquals( "photo.jpg", figureBlock.getLocation() );
        assertNull( figureBlock.getCaption() );
        TextBlock textBlock2 = (TextBlock) paraBlock.getBlocks().get( 2 );
        assertEquals( " paragraph.", textBlock2.getText() );
    }

    public void testSections() throws Exception
    {
        String content = "1 Section1\n"
            + "1.1 Section2\n"
            + "1.1.1 Section3\n"
            + "1.1.1.1 Section4\n"
            + "1.1.1.1.1 Section5\n"
            + "1  TitleWithLeadingSpace\n"
            + "   1 TitleWithSpacesBefore";
        List blocks = parser.parse( new StringReader( content ) );
        assertEquals( 7, blocks.size() );
        assertEquals( "Section1", ((SectionBlock) blocks.get( 0)).getTitle());
        assertEquals( 1, ((SectionBlock) blocks.get( 0)).getLevel());
        assertEquals( "Section2", ((SectionBlock) blocks.get( 1)).getTitle());
        assertEquals( 2, ((SectionBlock) blocks.get( 1)).getLevel());
        assertEquals( "Section3", ((SectionBlock) blocks.get( 2)).getTitle());
        assertEquals( 3, ((SectionBlock) blocks.get( 2)).getLevel());
        assertEquals( "Section4", ((SectionBlock) blocks.get( 3)).getTitle());
        assertEquals( 4, ((SectionBlock) blocks.get( 3)).getLevel());
        assertEquals( "Section5", ((SectionBlock) blocks.get( 4)).getTitle());
        assertEquals( 5, ((SectionBlock) blocks.get( 4)).getLevel());
        assertEquals( "TitleWithLeadingSpace", ((SectionBlock) blocks.get( 5)).getTitle());
        assertEquals( 1, ((SectionBlock) blocks.get( 5)).getLevel());
        assertEquals( "TitleWithSpacesBefore", ((SectionBlock) blocks.get( 6)).getTitle());
        assertEquals( 1, ((SectionBlock) blocks.get( 6)).getLevel());
    }

    public void testParagraphWithBoldAndItalic() throws Exception
    {
        List blocks = parser.parse( new StringReader( "Simple paragraph with *bold* and ~~italic~~ text." ) );
        assertEquals( 1, blocks.size() );
        ParagraphBlock paraBlock = (ParagraphBlock) blocks.get( 0 );
        assertEquals( 5, paraBlock.getBlocks().size() );
        assertEquals("Simple paragraph with ", ((TextBlock) paraBlock.getBlocks().get(0)).getText());        
        BoldBlock boldBlock = (BoldBlock) paraBlock.getBlocks().get(1);
        assertEquals(1, boldBlock.getBlocks().size());
        assertEquals("bold", ((TextBlock) boldBlock.getBlocks().get(0)).getText());      
        assertEquals(" and ", ((TextBlock) paraBlock.getBlocks().get(2)).getText());        
        ItalicBlock italicBlock = (ItalicBlock) paraBlock.getBlocks().get(3);
        assertEquals(1, italicBlock.getBlocks().size());
        assertEquals("italic", ((TextBlock) italicBlock.getBlocks().get(0)).getText());
        assertEquals(" text.", ((TextBlock) paraBlock.getBlocks().get(4)).getText());
    }

    /**
     * Note: The Link parser is tested in the link parser test class. Here we're just testing that the XWiki parser
     * understands a link inside a paragraph.
     */
    public void testParagraphWithLink() throws Exception
    {
        List blocks = parser.parse( new StringReader( "[JIRA|http://jira.codehaus.org]" ) );
        assertEquals( 1, blocks.size() );
        ParagraphBlock paraBlock = (ParagraphBlock) blocks.get( 0 );
        assertEquals( 1, paraBlock.getBlocks().size() );
        assertEquals("JIRA", ((LinkBlock) paraBlock.getBlocks().get(0)).getText());
        assertEquals("http://jira.codehaus.org", ((LinkBlock) paraBlock.getBlocks().get(0)).getReference());        
    }
}
