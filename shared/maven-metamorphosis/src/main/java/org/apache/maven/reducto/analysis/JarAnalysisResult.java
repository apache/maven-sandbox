package org.apache.maven.reducto.analysis;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public interface JarAnalysisResult
{
    String getGroupId();

    void setGroupId( String groupId );

    String getArtifactId();

    void setArtifactId( String artifactId );

    String getVersion();

    void setVersion( String version );

    String getMd5Checksum();

    void setMd5Checksum( String md5Checksum );

    boolean isResolved();

    File getJar();
}
