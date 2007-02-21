package org.apache.maven.shared.artifact.tools.test.assembly.resolve;

import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.components.ComponentAccessException;
import org.apache.maven.shared.artifact.tools.components.MavenComponentAccess;
import org.apache.maven.shared.artifact.tools.repository.ArtifactRepositorySource;
import org.apache.maven.shared.artifact.tools.repository.SimpleArtifactRepositorySource;
import org.apache.maven.shared.artifact.tools.resolve.ArtifactQuery;
import org.apache.maven.shared.artifact.tools.resolve.ArtifactResolutionResult;
import org.apache.maven.shared.artifact.tools.resolve.ArtifactResolutionTool;
import org.apache.maven.shared.artifact.tools.resolve.InvalidArtifactSpecificationException;

import java.util.Collections;

public class Resolve
{

    public ArtifactResolutionResult resolveJunitArtifact()
        throws ComponentAccessException, InvalidConfigurationException, ArtifactResolutionException,
        ArtifactNotFoundException, InvalidArtifactSpecificationException, ProjectBuildingException
    {
        MavenComponentAccess mca = MavenComponentAccess.createSelfContainedInstance();

        ArtifactQuery query = new ArtifactQuery( "junit", "junit" );
        query.setVersion( "3.8.1" );

        ArtifactRepositorySource rs = new SimpleArtifactRepositorySource( Collections
            .singletonList( "http://repo1.maven.org/maven2" ), "file://" + System.getProperty( "java.io.tmpdir" ), mca );

        ArtifactResolutionResult result = new ArtifactResolutionTool( mca ).resolve( query, rs );

        return result;
    }

    public ArtifactResolutionResult resolveJunitPom()
        throws ComponentAccessException, InvalidConfigurationException, ArtifactResolutionException,
        ArtifactNotFoundException, InvalidArtifactSpecificationException, ProjectBuildingException
    {
        MavenComponentAccess mca = MavenComponentAccess.createSelfContainedInstance();

        ArtifactQuery query = new ArtifactQuery( "junit", "junit" );
        query.setVersion( "3.8.1" );

        ArtifactRepositorySource rs = new SimpleArtifactRepositorySource( Collections
            .singletonList( "http://repo1.maven.org/maven2" ), "file://" + System.getProperty( "java.io.tmpdir" ), mca );

        ArtifactResolutionResult result = new ArtifactResolutionTool( mca ).resolveProjectMetadata( query, rs );

        return result;
    }

}
