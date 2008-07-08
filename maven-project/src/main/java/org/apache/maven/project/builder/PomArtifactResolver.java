package org.apache.maven.project.builder;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.ArtifactStatus;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;

import java.util.List;
import java.util.Date;
import java.io.IOException;
import java.io.File;

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

        if (!artifact.isSnapshot() && (ArtifactStatus.NONE.compareTo(ArtifactStatus.DEPLOYED) < 0)) {
            ArtifactRepositoryPolicy policy = new ArtifactRepositoryPolicy();
            policy.setUpdatePolicy(ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER);
            try {
                if (policy.checkOutOfDate(new Date(artifact.getFile().lastModified()))) {
                    artifact.setResolved(false);
                    resolver.resolveAlways(artifact, remoteRepositories, localRepository);
                }
            } catch (ArtifactNotFoundException e) {
                e.printStackTrace();
                throw new IOException("Parent pom not found: File = " + artifactFile.getAbsolutePath());
            } catch (ArtifactResolutionException e) {
                e.printStackTrace();
                throw new IOException("Parent pom not found: File = " + artifactFile.getAbsolutePath());
            }
        }
    }
}
