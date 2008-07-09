package org.apache.maven.project.builder;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.ArtifactStatus;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;

import java.util.List;
import java.util.Date;
import java.util.HashSet;
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

        for(ArtifactRepository ar: remoteRepositories) {
            System.out.println("repository: " + ar.getUrl());
        }

        try {
            resolver.resolve( artifact, remoteRepositories, localRepository );
        } catch (ArtifactResolutionException e) {
            throw new IOException(e.getMessage());
        } catch (ArtifactNotFoundException e) {
            throw new IOException(e.getMessage());
        }
        /*
        ArtifactResolutionRequest request = new ArtifactResolutionRequest()
                .setArtifact(artifact)
                .setLocalRepository(localRepository)
                .setArtifactDependencies(new HashSet<Artifact>())
                .setRemoteRepostories(remoteRepositories);
        resolver.resolve(request);
        */

        /*
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
        */
    }
}
