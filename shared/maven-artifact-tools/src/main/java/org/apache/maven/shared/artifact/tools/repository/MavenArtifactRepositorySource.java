package org.apache.maven.shared.artifact.tools.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.components.MavenComponentAccess;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenArtifactRepositorySource
    implements ArtifactRepositorySource
{

    private final MavenProject project;

    private final Settings settings;

    private final boolean usePluginRepositories;

    private final ArtifactRepositoryTool repositoryTool;

    // cache.
    private ArtifactRepository localRepository;

    public MavenArtifactRepositorySource( MavenProject project, MavenComponentAccess componentAccess,
                                          boolean usePluginRepositories )
        throws InvalidConfigurationException
    {
        this( project, null, componentAccess, usePluginRepositories );
    }

    public MavenArtifactRepositorySource( MavenProject project, File userSettingsLocation,
                                          MavenComponentAccess componentAccess, boolean usePluginRepositories )
        throws InvalidConfigurationException
    {
        this.project = project;
        this.usePluginRepositories = usePluginRepositories;
        this.repositoryTool = new ArtifactRepositoryTool( componentAccess );

        try
        {
            if ( userSettingsLocation == null )
            {
                this.settings = componentAccess.getSettingsBuilder().buildSettings();
            }
            else
            {
                this.settings = componentAccess.getSettingsBuilder().buildSettings( userSettingsLocation );
            }
        }
        catch ( IOException e )
        {
            throw new InvalidConfigurationException( "Error reading Maven settings.", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new InvalidConfigurationException( "Error reading Maven settings.", e );
        }
    }

    public List getArtifactRepositories()
    {
        List repos = new ArrayList();
        List repoUrls = new ArrayList();

        if ( usePluginRepositories )
        {
            List pluginRepos = project.getPluginArtifactRepositories();

            for ( Iterator it = pluginRepos.iterator(); it.hasNext(); )
            {
                ArtifactRepository repo = (ArtifactRepository) it.next();

                if ( !repoUrls.contains( repo.getUrl() ) )
                {
                    repos.add( repo );
                }

                repoUrls.add( repo.getUrl() );
            }

        }

        List normalRepos = project.getRemoteArtifactRepositories();

        for ( Iterator it = normalRepos.iterator(); it.hasNext(); )
        {
            ArtifactRepository repo = (ArtifactRepository) it.next();

            if ( !repoUrls.contains( repo.getUrl() ) )
            {
                repos.add( repo );
            }

            repoUrls.add( repo.getUrl() );
        }

        repos = constructMirroredRepositoryList( repos );

        return repos;
    }

    public ArtifactRepository getLocalRepository()
        throws InvalidConfigurationException
    {
        if ( localRepository == null )
        {
            String localRepoLocation = settings.getLocalRepository();

            if ( localRepoLocation != null )
            {
                localRepository = repositoryTool.buildLocalRepository( localRepoLocation );
            }
        }

        return localRepository;
    }

    private List constructMirroredRepositoryList( List repositories )
    {
        List mirrorEnabled = new ArrayList( repositories.size() );

        for ( Iterator it = repositories.iterator(); it.hasNext(); )
        {
            ArtifactRepository repository = (ArtifactRepository) it.next();

            ArtifactRepository mirrorRepo = getMirrorRepository( repository );

            if ( mirrorRepo == null )
            {
                mirrorEnabled.add( repository );
            }
            else
            {
                mirrorEnabled.add( mirrorRepo );
            }
        }

        return mirrorEnabled;
    }

    private ArtifactRepository getMirrorRepository( ArtifactRepository baseRepository )
    {
        Mirror mirror = settings.getMirrorOf( baseRepository.getId() );

        ArtifactRepository mirrored = null;

        if ( mirror != null )
        {
            mirrored = repositoryTool.buildMirrorRepository( baseRepository, mirror );
        }

        return mirrored;
    }
}
