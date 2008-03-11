package org.apache.maven.archiva.jarinfo.model;

import java.util.Calendar;

import org.apache.maven.archiva.jarinfo.utils.EmptyUtils;

/**
 * The generator that created this jarinfo.
 */
public class Generator
{
    /**
     * The name of the tool or person that created this jarinfo.
     * Required
     */
    private String name;

    /**
     * If a tool was used to create this jarinfo, then contains the version of the tool.
     */
    private String version;

    /**
     * When this jarinfo was created (in UTC format)
     */
    private Calendar timestamp;

    public boolean exists()
    {
        return !EmptyUtils.isEmpty( name );
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public Calendar getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Calendar timestamp )
    {
        this.timestamp = timestamp;
    }
}
