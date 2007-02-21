package org.apache.maven.shared.artifact.tools.repository;

import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;

public interface ArtifactRepositorySource
{

    ArtifactRepository getLocalRepository()
        throws InvalidConfigurationException;

    List getArtifactRepositories();

}
