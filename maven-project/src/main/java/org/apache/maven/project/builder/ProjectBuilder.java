package org.apache.maven.project.builder;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.InterpolatorProperty;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.Collection;

public interface ProjectBuilder {

    String ROLE = ProjectBuilder.class.getName();

    MavenProject buildFromArtifact(Artifact artifact, Collection<InterpolatorProperty> interpolatorProperties,
                                   PomArtifactResolver resolver)
            throws IOException;

    MavenProject buildFromStream(InputStream pom, Collection<InterpolatorProperty> interpolatorProperties,
                                 PomArtifactResolver resolver)
            throws IOException;
}
