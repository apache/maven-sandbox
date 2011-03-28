package org.apache.maven.examples.bundledrepository;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;

@Component( role = AbstractMavenLifecycleParticipant.class, hint = "bundledrepository" )
public class BundledRepositoryMavenExtension
    extends AbstractMavenLifecycleParticipant
{
    @Requirement
    private Logger logger;

    @Requirement
    RuntimeInformation runtime;

    @Requirement
    private ArtifactRepositoryFactory factory;

    public void afterProjectsRead( MavenSession session ) {
        // Currently a dumb implementation - some searching & configuration via execution properties might help
        //  or better adding in to the resolution model with a new repository type

        // Search for projects that have a repository
        boolean found = false;
        List<ArtifactRepository> artifactRepositories = new ArrayList<ArtifactRepository>();
        for ( MavenProject project : session.getProjects() ) {
            File repositoryDir = new File( project.getBasedir(), "repository" );
            if ( repositoryDir.exists() && repositoryDir.isDirectory() ) {
                try {
                    logger.debug( "Found repository in project " + project.getId() + " under " + repositoryDir );
                    artifactRepositories.add( factory.createArtifactRepository( "bundledrepository", repositoryDir.toURL().toExternalForm(), new DefaultRepositoryLayout(), null, null ) );
                }
                catch ( MalformedURLException e ) {
                    logger.error( "Ignoring bad URL: " + e.getMessage() );
                }
            }
        }

        // Add the repository to all projects
        for ( MavenProject project : session.getProjects() ) {
            List<ArtifactRepository> repos = new ArrayList<ArtifactRepository>( project.getRemoteArtifactRepositories() );
            repos.addAll( artifactRepositories );
            project.setRemoteArtifactRepositories( repos );
            logger.debug( "Project " + project.getId() + " now using repositories: " + repos );
        }
    }

}
