package org.apache.maven.shared.artifact.tools.resolve.testutils;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.repository.ArtifactRepositorySource;

import java.util.List;

public class TestRepositorySource
    implements ArtifactRepositorySource
{
    
    private final ArtifactRepository localRepo;
    private final List remoteRepos;

    public TestRepositorySource( ArtifactRepository localRepo, List remoteRepos )
    {
        this.localRepo = localRepo;
        this.remoteRepos = remoteRepos;
    }

    public List getArtifactRepositories()
    {
        return remoteRepos;
    }

    public ArtifactRepository getLocalRepository()
        throws InvalidConfigurationException
    {
        return localRepo;
    }

}
