package org.apache.maven.pomo;

/**
 * @author Jason van Zyl
 */
public class SimplePomInfoSource
    implements PomInfoSource
{
    private String modelVersion;

    private String groupId;

    private String artifactId;

    private String version;

    public SimplePomInfoSource( String modelVersion,
                                String groupId,
                                String artifactId,
                                String version )
    {
        this.modelVersion = modelVersion;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getModelVersion()
    {
        return modelVersion;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }
}
