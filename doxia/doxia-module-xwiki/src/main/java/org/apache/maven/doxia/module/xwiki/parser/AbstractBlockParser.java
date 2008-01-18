package org.apache.maven.doxia.module.xwiki.parser;

public abstract class AbstractBlockParser
    implements BlockParser
{
    private boolean isInCompatibilityMode;

    public void setCompatibilityMode( boolean isInCompatibilityMode )
    {
        this.isInCompatibilityMode = isInCompatibilityMode;
    }

    public boolean isInCompatibilityMode()
    {
        return this.isInCompatibilityMode;
    }
}
