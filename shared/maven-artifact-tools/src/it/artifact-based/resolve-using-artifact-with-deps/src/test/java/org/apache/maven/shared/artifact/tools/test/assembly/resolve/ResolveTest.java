package org.apache.maven.shared.artifact.tools.test.assembly.resolve;

import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.components.ComponentAccessException;
import org.apache.maven.shared.artifact.tools.resolve.InvalidArtifactSpecificationException;

import junit.framework.TestCase;

public class ResolveTest
    extends TestCase
{

    public void testResolveArtifact()
        throws ArtifactResolutionException, ArtifactNotFoundException, ComponentAccessException,
        InvalidConfigurationException, InvalidArtifactSpecificationException, ProjectBuildingException
    {
        new Resolve().resolveJunitArtifact();
    }

    public void testResolvePom()
        throws ArtifactResolutionException, ArtifactNotFoundException, ComponentAccessException,
        InvalidConfigurationException, InvalidArtifactSpecificationException, ProjectBuildingException
    {
        new Resolve().resolveJunitPom();
    }

}
