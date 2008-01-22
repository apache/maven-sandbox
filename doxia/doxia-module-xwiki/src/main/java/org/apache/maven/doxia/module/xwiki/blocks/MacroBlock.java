package org.apache.maven.doxia.module.xwiki.blocks;

import org.apache.maven.doxia.sink.Sink;

import java.util.HashMap;
import java.util.Map;

public class MacroBlock
    implements Block
{
    private String name;

    private Map parameters;

    private String content;

    public MacroBlock( String name, Map parameters, String content )
    {
        this.name = name;
        this.parameters = parameters;
        this.content = content;
    }

    public void traverse( Sink sink )
    {
        // TODO
    }

    public String getName()
    {
        return this.name;
    }

    public Map getParameters()
    {
        Map newParameters = new HashMap( this.parameters );
        return newParameters;
    }

    public String getContent()
    {
        return this.content;
    }
}
