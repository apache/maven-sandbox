package org.apache.maven.doxia.module.mediawiki.parser.testutil;

import org.apache.maven.doxia.sink.Sink;
import org.easymock.MockControl;

public class SinkMockAndControl
    implements MockAndControl
{

    private MockControl control;

    private Sink sink;

    public SinkMockAndControl()
    {
        control = MockControl.createControl( Sink.class );
        sink = (Sink) control.getMock();
    }

    public MockControl getControl()
    {
        return control;
    }

    public Sink getSink()
    {
        return sink;
    }

    public void replay()
    {
        control.replay();
    }

    public void verify()
    {
        control.verify();
    }

    public void anchor_()
    {
        sink.anchor_();
        control.setVoidCallable();
    }

    public void anchor( String name )
    {
        sink.anchor( name );
        control.setVoidCallable();
    }

    public void author_()
    {
        sink.author_();
        control.setVoidCallable();
    }

    public void author()
    {
        sink.author();
        control.setVoidCallable();
    }

    public void body_()
    {
        sink.body_();
        control.setVoidCallable();
    }

    public void body()
    {
        sink.body();
        control.setVoidCallable();
    }

    public void bold_()
    {
        sink.bold_();
        control.setVoidCallable();
    }

    public void bold()
    {
        sink.bold();
        control.setVoidCallable();
    }

    public void close()
    {
        sink.close();
        control.setVoidCallable();
    }

    public void date_()
    {
        sink.date_();
        control.setVoidCallable();
    }

    public void date()
    {
        sink.date();
        control.setVoidCallable();
    }

    public void definedTerm_()
    {
        sink.definedTerm_();
        control.setVoidCallable();
    }

    public void definedTerm()
    {
        sink.definedTerm();
        control.setVoidCallable();
    }

    public void definition_()
    {
        sink.definition_();
        control.setVoidCallable();
    }

    public void definition()
    {
        sink.definition();
        control.setVoidCallable();
    }

    public void definitionList_()
    {
        sink.definitionList_();
        control.setVoidCallable();
    }

    public void definitionList()
    {
        sink.definitionList();
        control.setVoidCallable();
    }

    public void definitionListItem_()
    {
        sink.definitionListItem_();
        control.setVoidCallable();
    }

    public void definitionListItem()
    {
        sink.definitionListItem();
        control.setVoidCallable();
    }

    public void figure_()
    {
        sink.figure_();
        control.setVoidCallable();
    }

    public void figure()
    {
        sink.figure();
        control.setVoidCallable();
    }

    public void figureCaption_()
    {
        sink.figureCaption_();
        control.setVoidCallable();
    }

    public void figureCaption()
    {
        sink.figureCaption();
        control.setVoidCallable();
    }

    public void figureGraphics( String name )
    {
        sink.figureGraphics( name );
        control.setVoidCallable();
    }

    public void flush()
    {
        sink.flush();
        control.setVoidCallable();
    }

    public void head_()
    {
        sink.head_();
        control.setVoidCallable();
    }

    public void head()
    {
        sink.head();
        control.setVoidCallable();
    }

    public void horizontalRule()
    {
        sink.horizontalRule();
        control.setVoidCallable();
    }

    public void italic_()
    {
        sink.italic_();
        control.setVoidCallable();
    }

    public void italic()
    {
        sink.italic();
        control.setVoidCallable();
    }

    public void lineBreak()
    {
        sink.lineBreak();
        control.setVoidCallable();
    }

    public void link_()
    {
        sink.link_();
        control.setVoidCallable();
    }

    public void link( String name )
    {
        sink.link( name );
        control.setVoidCallable();
    }

    public void list_()
    {
        sink.list_();
        control.setVoidCallable();
    }

    public void list()
    {
        sink.list();
        control.setVoidCallable();
    }

    public void listItem_()
    {
        sink.listItem_();
        control.setVoidCallable();
    }

    public void listItem()
    {
        sink.listItem();
        control.setVoidCallable();
    }

    public void monospaced_()
    {
        sink.monospaced_();
        control.setVoidCallable();
    }

    public void monospaced()
    {
        sink.monospaced();
        control.setVoidCallable();
    }

    public void nonBreakingSpace()
    {
        sink.nonBreakingSpace();
        control.setVoidCallable();
    }

    public void numberedList_()
    {
        sink.numberedList_();
        control.setVoidCallable();
    }

    public void numberedList( int numbering )
    {
        sink.numberedList( numbering );
        control.setVoidCallable();
    }

    public void numberedListItem_()
    {
        sink.numberedListItem_();
        control.setVoidCallable();
    }

    public void numberedListItem()
    {
        sink.numberedListItem();
        control.setVoidCallable();
    }

    public void pageBreak()
    {
        sink.pageBreak();
        control.setVoidCallable();
    }

    public void paragraph_()
    {
        sink.paragraph_();
        control.setVoidCallable();
    }

    public void paragraph()
    {
        sink.paragraph();
        control.setVoidCallable();
    }

    public void rawText( String text )
    {
        sink.rawText( text );
        control.setVoidCallable();
    }

    public void section1_()
    {
        sink.section1_();
        control.setVoidCallable();
    }

    public void section1()
    {
        sink.section1();
        control.setVoidCallable();
    }

    public void section2_()
    {
        sink.section2_();
        control.setVoidCallable();
    }

    public void section2()
    {
        sink.section2();
        control.setVoidCallable();
    }

    public void section3_()
    {
        sink.section3_();
        control.setVoidCallable();
    }

    public void section3()
    {
        sink.section3();
        control.setVoidCallable();
    }

    public void section4_()
    {
        sink.section4_();
        control.setVoidCallable();
    }

    public void section4()
    {
        sink.section4();
        control.setVoidCallable();
    }

    public void section5_()
    {
        sink.section5_();
        control.setVoidCallable();
    }

    public void section5()
    {
        sink.section5();
        control.setVoidCallable();
    }

    public void sectionTitle_()
    {
        sink.sectionTitle_();
        control.setVoidCallable();
    }

    public void sectionTitle()
    {
        sink.sectionTitle();
        control.setVoidCallable();
    }

    public void sectionTitle1_()
    {
        sink.sectionTitle1_();
        control.setVoidCallable();
    }

    public void sectionTitle1()
    {
        sink.sectionTitle1();
        control.setVoidCallable();
    }

    public void sectionTitle2_()
    {
        sink.sectionTitle2_();
        control.setVoidCallable();
    }

    public void sectionTitle2()
    {
        sink.sectionTitle2();
        control.setVoidCallable();
    }

    public void sectionTitle3_()
    {
        sink.sectionTitle3_();
        control.setVoidCallable();
    }

    public void sectionTitle3()
    {
        sink.sectionTitle3();
        control.setVoidCallable();
    }

    public void sectionTitle4_()
    {
        sink.sectionTitle4_();
        control.setVoidCallable();
    }

    public void sectionTitle4()
    {
        sink.sectionTitle4();
        control.setVoidCallable();
    }

    public void sectionTitle5_()
    {
        sink.sectionTitle5_();
        control.setVoidCallable();
    }

    public void sectionTitle5()
    {
        sink.sectionTitle5();
        control.setVoidCallable();
    }

    public void table_()
    {
        sink.table_();
        control.setVoidCallable();
    }

    public void table()
    {
        sink.table();
        control.setVoidCallable();
    }

    public void tableCaption_()
    {
        sink.tableCaption_();
        control.setVoidCallable();
    }

    public void tableCaption()
    {
        sink.tableCaption();
        control.setVoidCallable();
    }

    public void tableCell_()
    {
        sink.tableCell_();
        control.setVoidCallable();
    }

    public void tableCell()
    {
        sink.tableCell();
        control.setVoidCallable();
    }

    public void tableCell( String width )
    {
        sink.tableCell( width );
        control.setVoidCallable();
    }

    public void tableHeaderCell_()
    {
        sink.tableHeaderCell_();
        control.setVoidCallable();
    }

    public void tableHeaderCell()
    {
        sink.tableHeaderCell();
        control.setVoidCallable();
    }

    public void tableHeaderCell( String width )
    {
        sink.tableHeaderCell( width );
        control.setVoidCallable();
    }

    public void tableRow_()
    {
        sink.tableRow_();
        control.setVoidCallable();
    }

    public void tableRow()
    {
        sink.tableRow();
        control.setVoidCallable();
    }

    public void tableRows_()
    {
        sink.tableRows_();
        control.setVoidCallable();
    }

    public void tableRows( int[] justification, boolean grid )
    {
        sink.tableRows( justification, grid );
        control.setVoidCallable();
    }

    public void text( String text )
    {
        sink.text( text );
        control.setVoidCallable();
    }

    public void title_()
    {
        sink.title_();
        control.setVoidCallable();
    }

    public void title()
    {
        sink.title();
        control.setVoidCallable();
    }

    public void verbatim_()
    {
        sink.verbatim_();
        control.setVoidCallable();
    }

    public void verbatim( boolean boxed )
    {
        sink.verbatim( boxed );
        control.setVoidCallable();
    }

}
