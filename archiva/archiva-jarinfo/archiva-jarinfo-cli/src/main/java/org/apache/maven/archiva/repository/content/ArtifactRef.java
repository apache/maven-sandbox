package org.apache.maven.archiva.repository.content;

import org.apache.commons.lang.StringUtils;

public class ArtifactRef
{
    private String groupId;

    private String artifactId;

    private String version;

    private String classifier;

    private String type;

    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append( StringUtils.defaultString( groupId ) ).append( ':' );
        buf.append( StringUtils.defaultString( artifactId ) ).append( ':' );
        buf.append( StringUtils.defaultString( version ) ).append( ':' );
        buf.append( StringUtils.defaultString( classifier ) ).append( ':' );
        buf.append( StringUtils.defaultString( type ) );

        return buf.toString();
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

    public String getClassifier()
    {
        return classifier;
    }

    public void setClassifier( String classifier )
    {
        this.classifier = classifier;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }
}
