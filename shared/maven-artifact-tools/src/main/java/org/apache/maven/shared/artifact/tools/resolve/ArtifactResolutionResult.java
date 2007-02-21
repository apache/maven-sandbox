package org.apache.maven.shared.artifact.tools.resolve;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

public class ArtifactResolutionResult
{
    
    private final Artifact artifact;
    
    private final MavenProject project;

    public ArtifactResolutionResult( Artifact artifact, MavenProject project )
    {
        this.artifact = artifact;
        this.project = project;
    }

    public Artifact getArtifact()
    {
        return artifact;
    }

    public Model getModel()
    {
        return project != null ? project.getOriginalModel() : null;
    }

    public MavenProject getProject()
    {
        return project;
    }

    public ArtifactRepository getSourceRepository()
    {
        return artifact != null ? artifact.getRepository() : null;
    }

}
