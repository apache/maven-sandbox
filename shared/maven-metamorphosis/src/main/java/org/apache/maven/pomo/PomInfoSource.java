package org.apache.maven.pomo;

/**
 * @author Jason van Zyl
 */
public interface PomInfoSource
{
    String getModelVersion();

    String getGroupId();

    String getArtifactId();

    String getVersion();
}
