package org.apache.maven.shared.artifact.tools.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.components.MavenComponentAccess;

public class SimpleArtifactRepositorySource
    implements ArtifactRepositorySource
{

    private final List remoteRepositories;

    private final ArtifactRepository localRepository;

    public SimpleArtifactRepositorySource( String localRepositoryPath, MavenComponentAccess componentAccess )
        throws InvalidConfigurationException
    {
        this( null, localRepositoryPath, componentAccess );
    }

    public SimpleArtifactRepositorySource( List remoteRepositoryUrls, String localRepositoryPath,
                                           MavenComponentAccess componentAccess )
        throws InvalidConfigurationException
    {
        ArtifactRepositoryTool repoTool = new ArtifactRepositoryTool( componentAccess );

        List remoteRepos = null;
        if ( remoteRepositoryUrls != null && !remoteRepositoryUrls.isEmpty() )
        {
            remoteRepos = new ArrayList( remoteRepositoryUrls.size() );

            for ( int i = 0; i < remoteRepositoryUrls.size(); i++ )
            {
                String url = (String) remoteRepositoryUrls.get( i );

                remoteRepos.add( repoTool.buildArtifactRepository( "remote" + i, url, "Remote Repository #" + i,
                                                                   "default", true, true ) );
            }
        }
        else
        {
            remoteRepos = Collections.EMPTY_LIST;
        }

        this.remoteRepositories = remoteRepos;

        this.localRepository = repoTool.buildLocalRepository( localRepositoryPath );
    }

    public List getArtifactRepositories()
    {
        return remoteRepositories;
    }

    public ArtifactRepository getLocalRepository()
        throws InvalidConfigurationException
    {
        return localRepository;
    }

}
