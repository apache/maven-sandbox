package org.apache.maven.archiva.jarinfo.model;

import org.apache.maven.archiva.jarinfo.utils.EmptyUtils;

public class AssignedId
{
    private String groupId;

    private String artifactId;

    private String version;

    private String name;

    private String vendor;

    public boolean valid()
    {
        return !EmptyUtils.isEmpty( groupId ) && !EmptyUtils.isEmpty( artifactId ) && !EmptyUtils.isEmpty( version );
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor( String vendor )
    {
        this.vendor = vendor;
    }

}
