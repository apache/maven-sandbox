package org.apache.maven.project.builder;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PomArtifactResolver {

    private ArtifactRepository localRepository;

    private List<ArtifactRepository> remoteRepositories;

    private ArtifactResolver resolver;

    public PomArtifactResolver(ArtifactRepository localRepository, List<ArtifactRepository> remoteRepositories, ArtifactResolver resolver) {
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
        this.resolver = resolver;
    }

    /**
     * State change: sets file.
     *
     * @param artifact
     */
    public void resolve(Artifact artifact) throws IOException {
        File artifactFile = new File(localRepository.getBasedir(), localRepository.pathOf(artifact));
        artifact.setFile(artifactFile);

        try {
            resolver.resolve( artifact, remoteRepositories, localRepository );
        } catch (ArtifactResolutionException e) {
            throw new IOException(e.getMessage());
        } catch (ArtifactNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }
}
