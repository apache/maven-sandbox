package org.apache.maven.shared.artifact.tools.repository;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.components.MavenComponentAccess;
import org.apache.maven.shared.artifact.tools.resolve.ArtifactQuery;
import org.apache.maven.shared.artifact.tools.resolve.InvalidArtifactSpecificationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;

public class ArtifactRepositoryTool
{

    private final MavenComponentAccess componentAccess;

    public ArtifactRepositoryTool( final MavenComponentAccess componentAccess )
    {
        this.componentAccess = componentAccess;
    }

    public ArtifactRepository buildArtifactRepository( final String id, final String url, final String name,
                                                       final String layoutId, final boolean releasesEnabled,
                                                       final boolean snapshotsEnabled )
        throws InvalidConfigurationException
    {
        final ArtifactRepositoryFactory factory = componentAccess.getArtifactRepositoryFactory();

        final ArtifactRepositoryLayout layout = componentAccess.getArtifactRepositoryLayout( layoutId );

        ArtifactRepositoryPolicy releasePolicy = new ArtifactRepositoryPolicy();
        releasePolicy.setEnabled( releasesEnabled );

        ArtifactRepositoryPolicy snapshotPolicy = new ArtifactRepositoryPolicy();
        snapshotPolicy.setEnabled( snapshotsEnabled );

        return factory.createArtifactRepository( id, url, layout, snapshotPolicy, releasePolicy );
    }

    public ArtifactRepository buildMirrorRepository( final ArtifactRepository baseRepository, final Mirror mirror )
    {
        final ArtifactRepositoryFactory repoFactory = componentAccess.getArtifactRepositoryFactory();

        ArtifactRepository mirrorRepository = repoFactory.createArtifactRepository( baseRepository.getId(), mirror
            .getUrl(), baseRepository.getLayout(), baseRepository.getSnapshots(), baseRepository.getReleases() );

        if ( mirrorRepository instanceof DefaultArtifactRepository )
        {
            ( (DefaultArtifactRepository) mirrorRepository ).setName( mirror.getName() );
        }

        return mirrorRepository;
    }

    public File findLocalRepositoryPath( ArtifactQuery query )
        throws InvalidArtifactSpecificationException, InvalidConfigurationException
    {
        ArtifactRepository localRepository = findLocalRepository();

        return findLocalRepositoryPath( query, localRepository );
    }
    
    public File findLocalRepositoryPath( ArtifactQuery query, ArtifactRepositorySource repositorySource )
        throws InvalidArtifactSpecificationException, InvalidConfigurationException
    {
        return findLocalRepositoryPath( query, repositorySource.getLocalRepository() );
    }

    public File findLocalRepositoryPath( ArtifactQuery query, ArtifactRepository localRepository )
        throws InvalidArtifactSpecificationException
    {
        Artifact artifact = query.createArtifact( componentAccess.getArtifactHandlerManager() );

        String relativePath = localRepository.pathOf( artifact );

        String basedir = localRepository.getBasedir();

        return new File( basedir, relativePath );
    }

    public ArtifactRepository findLocalRepository()
        throws InvalidConfigurationException
    {
        MavenSettingsBuilder settingsBuilder = componentAccess.getSettingsBuilder();

        Settings settings;

        try
        {
            settings = settingsBuilder.buildSettings();
        }
        catch ( IOException e )
        {
            throw new InvalidConfigurationException( "Failed to read Maven settings", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new InvalidConfigurationException( "Failed to read Maven settings", e );
        }

        return buildLocalRepository( settings.getLocalRepository() );
    }

    public ArtifactRepository buildLocalRepository( final String localRepositoryLocation )
        throws InvalidConfigurationException
    {
        final ArtifactRepositoryFactory repoFactory = componentAccess.getArtifactRepositoryFactory();
        final ArtifactRepositoryLayout repoLayout = componentAccess.getArtifactRepositoryLayout( "default" );

        return repoFactory.createArtifactRepository( "local", localRepositoryLocation, repoLayout, null, null );
    }

}
