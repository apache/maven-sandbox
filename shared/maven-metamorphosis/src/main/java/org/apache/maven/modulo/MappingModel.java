package org.apache.maven.modulo;

import java.util.Set;

/**
 * Information to map a non-Maven project into a Maven compliant project. This would includes:
 *
 * JARs that you want resolved to Maven artifacts
 * sources you want translated to be Maven compliant
 * resources you want translated to be Maven compliant
 *
 * @author Jason van Zyl
 */
public interface MappingModel
{
    String getGroupId();

    String getArtifactId();

    String getVersion();

    String getPackaging();

    Set getJarDirectories();

    String getSourceDirectory();

    String getTestSourceDirectory();

    String getIntegrationTestSourceDirectory();

    Set getResourceDirectories();
}
