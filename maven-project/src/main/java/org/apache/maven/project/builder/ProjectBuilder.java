package org.apache.maven.project.builder;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.InterpolatorProperty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface ProjectBuilder {

    String ROLE = ProjectBuilder.class.getName();

    MavenProject buildFromLocalPath(InputStream pom, Collection<InterpolatorProperty> interpolatorProperties,
                                 PomArtifactResolver resolver, File baseDirectory)
            throws IOException;

    MavenProject buildFromRepository(InputStream pom, Collection<InterpolatorProperty> interpolatorProperties,
                                     PomArtifactResolver resolver)
            throws IOException;

}
