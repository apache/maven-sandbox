package org.apache.maven.modulo;

import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * @author Jason van Zyl
 */
public class DefaultMappingModel
    implements MappingModel
{
    private String groupId;

    private String artifactId;

    private String version;

    private String packaging;

    private Set jarDirectories;

    private String sourceDirectory;

    private String testSourceDirectory;

    private String integrationTestSourceDirectory;

    private Set resourceDirectories;

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

    public String getPackaging()
    {
        return packaging;
    }

    public void setPackaging( String packaging )
    {
        this.packaging = packaging;
    }

    public Set getJarDirectories()
    {
        return jarDirectories;
    }

    public void addJarDirectory( Resource jarDirectory )
    {
        if ( jarDirectories == null )
        {
            jarDirectories = new LinkedHashSet();
        }

        jarDirectories.add( jarDirectory );
    }

    public String getSourceDirectory()
    {
        return sourceDirectory;
    }

    public void setSourceDirectory( String sourceDirectory )
    {
        this.sourceDirectory = sourceDirectory;
    }

    public String getTestSourceDirectory()
    {
        return testSourceDirectory;
    }

    public void setTestSourceDirectory( String testSourceDirectory )
    {
        this.testSourceDirectory = testSourceDirectory;
    }

    public String getIntegrationTestSourceDirectory()
    {
        return integrationTestSourceDirectory;
    }

    public void setIntegrationTestSourceDirectory( String integrationTestSourceDirectory )
    {
        this.integrationTestSourceDirectory = integrationTestSourceDirectory;
    }

    public Set getResourceDirectories()
    {
        return resourceDirectories;
    }

    public void addResourceDirectory( Resource resourceDirectory )
    {
        if ( resourceDirectories == null )
        {
            resourceDirectories = new LinkedHashSet();
        }

        resourceDirectories.add( resourceDirectory );
    }
}
