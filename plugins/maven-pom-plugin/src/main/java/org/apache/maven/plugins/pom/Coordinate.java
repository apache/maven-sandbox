package org.apache.maven.plugins.pom;

/**
 * A base for groupId, artifactId, version
 * @author eredmond
 */
public class Coordinate
{
    private String groupId;
    
    private String artifactId;
    
    private String version;

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }
}
