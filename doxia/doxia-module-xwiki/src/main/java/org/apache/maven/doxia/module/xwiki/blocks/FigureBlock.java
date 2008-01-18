package org.apache.maven.doxia.module.xwiki.blocks;

import org.apache.maven.doxia.sink.Sink;

public class FigureBlock
    implements Block
{
    private String location;

    private String caption;

    public FigureBlock( String location )
    {
        this.location = location;
    }

    public FigureBlock( String location, String caption )
    {
        this.location = location;
        this.caption = caption;
    }

    public void traverse( Sink sink )
    {
        sink.figure();
        sink.figureGraphics( getLocation() );

        if ( caption != null && caption.length() > 0 )
        {
            sink.figureCaption();
            new TextBlock( caption ).traverse( sink );
            sink.figureCaption_();
        }

        sink.figure_();
    }

    public String getLocation()
    {
        return this.location;
    }

    public String getCaption()
    {
        return this.caption;
    }

}
