package org.apache.maven.shared.artifact.tools.resolve;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.components.MavenComponentAccess;
import org.apache.maven.shared.artifact.tools.repository.ArtifactRepositorySource;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Convenience API for resolving Artifacts and their associated POM metadata.
 */
public class ArtifactResolutionTool
{
    private final ArtifactResolver artifactResolver;

    private final ArtifactHandlerManager artifactHandlerManager;

    private final MavenProjectBuilder mavenProjectBuilder;

    private final RepositoryMetadataManager repositoryMetadataManager;

    public ArtifactResolutionTool( MavenComponentAccess componentAccess )
    {
        this.artifactResolver = componentAccess.getArtifactResolver();
        this.artifactHandlerManager = componentAccess.getArtifactHandlerManager();
        this.mavenProjectBuilder = componentAccess.getMavenProjectBuilder();
        this.repositoryMetadataManager = componentAccess.getRepositoryMetadataManager();
    }

    public ArtifactResolutionResult resolve( ArtifactQuery query, ArtifactRepositorySource repositorySource )
        throws InvalidArtifactSpecificationException, InvalidConfigurationException, ResolutionException
    {
        Artifact artifact = query.createArtifact( artifactHandlerManager );

        ArtifactRepository localRepository = repositorySource.getLocalRepository();

        List repositories = repositorySource.getArtifactRepositories();

        try
        {
            artifactResolver.resolve( artifact, repositories, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new ResolutionException( artifact, "Failed to resolve: " + artifact.getId(), e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new ResolutionException( artifact, "Artifact not found: " + artifact.getId(), e );
        }

        MavenProject project;
        try
        {
            project = mavenProjectBuilder.buildFromRepository( artifact, Collections.EMPTY_LIST, localRepository );
        }
        catch ( ProjectBuildingException e )
        {
            throw new ResolutionException( artifact, "Failed to build POM metadata for: " + artifact.getId(), e );
        }

        ArtifactResolutionResult result = new ArtifactResolutionResult( artifact, project );

        return result;
    }

    public ArtifactResolutionResult resolveProjectMetadata( ArtifactQuery query,
                                                            ArtifactRepositorySource repositorySource )
        throws InvalidArtifactSpecificationException, ResolutionException, InvalidConfigurationException
    {
        ArtifactQuery pomQuery = query.copy();
        pomQuery.setType( "pom" );

        return resolve( pomQuery, repositorySource );
    }

    public List getAvailableVersions( String groupId, String artifactId, ArtifactRepositorySource repositorySource )
        throws InvalidArtifactSpecificationException, InvalidConfigurationException, ResolutionException
    {
        ArtifactQuery query = new ArtifactQuery( groupId, artifactId );
        query.setVersion( "not-used" );

        Artifact artifact = query.createArtifact( artifactHandlerManager );

        List remoteRepositories = repositorySource.getArtifactRepositories();
        ArtifactRepository localRepository = repositorySource.getLocalRepository();

        LinkedHashSet versions = new LinkedHashSet();
        Map errors = new LinkedHashMap();
        
        readVersionsFromLocalRepository( artifact, localRepository, versions, errors );

        readVersionsFromRemoteRepositories( artifact, localRepository, remoteRepositories, versions, errors );

        if ( !errors.isEmpty() )
        {
            throw new ResolutionException( new ArtifactRepositoryMetadata( artifact ), errors );
        }

        List versionList = new ArrayList( versions );

        Collections.sort( versionList );

        return versionList;
    }

    private void readVersionsFromRemoteRepositories( Artifact artifact, ArtifactRepository localRepository, List remoteRepositories, LinkedHashSet versions, Map errors )
    {
        for ( Iterator it = remoteRepositories.iterator(); it.hasNext(); )
        {
            ArtifactRepository remoteRepository = (ArtifactRepository) it.next();

            ArtifactRepositoryMetadata metadata = new ArtifactRepositoryMetadata( artifact );

            try
            {
                repositoryMetadataManager.resolveAlways( metadata, localRepository, remoteRepository );
            }
            catch ( RepositoryMetadataResolutionException e )
            {
                errors.put( remoteRepository, e );
                continue;
            }

            Metadata md = metadata.getMetadata();
            Versioning versioning = md.getVersioning();

            List availableVersions = versioning.getVersions();

            versions.addAll( availableVersions );
        }
    }

    private void readVersionsFromLocalRepository( Artifact artifact, ArtifactRepository localRepository, LinkedHashSet versions, Map errors )
    {
        FileReader reader = null;
        
        try
        {
            ArtifactRepositoryMetadata metadata = new ArtifactRepositoryMetadata( artifact );

            File localOnly = new File( localRepository.getBasedir(), localRepository
                .pathOfLocalRepositoryMetadata( metadata, localRepository ) );
            
            if ( localOnly.exists() && !localOnly.isDirectory() )
            {
                reader = new FileReader( localOnly );
                Metadata md = new MetadataXpp3Reader().read( reader );
                
                if ( md != null )
                {
                    Versioning versioning = md.getVersioning();
                    versions.addAll( versioning.getVersions() );
                }
            }
        }
        catch ( IOException e )
        {
            errors.put( localRepository, e );
        }
        catch ( XmlPullParserException e )
        {
            errors.put( localRepository, e );
        }
        finally
        {
            IOUtil.close( reader );
        }
    }
}
