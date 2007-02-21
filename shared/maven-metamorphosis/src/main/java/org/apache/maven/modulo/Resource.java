package org.apache.maven.modulo;

/**
 * @author Jason van Zyl
 */
public class Resource
{
    private String directory;

    private String includes;

    private String excludse;

    public String getDirectory()
    {
        return directory;
    }

    public void setDirectory( String directory )
    {
        this.directory = directory;
    }

    public String getIncludes()
    {
        return includes;
    }

    public void setIncludes( String includes )
    {
        this.includes = includes;
    }

    public String getExcludse()
    {
        return excludse;
    }

    public void setExcludse( String excludse )
    {
        this.excludse = excludse;
    }
}
