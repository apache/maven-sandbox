package org.apache.maven.plugin.plugintest.stage;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.GroupRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class PluginStagingManager
{

    private ArtifactFactory artifactFactory;

    private final Log log;

    private ArtifactRepository localRepository;

    private ArtifactInstaller installer;

    public PluginStagingManager( ArtifactRepository localRepository, ArtifactFactory artifactFactory,
                                 ArtifactInstaller installer, Log log )
    {
        this.localRepository = localRepository;
        this.artifactFactory = artifactFactory;
        this.installer = installer;
        this.log = log;
    }

    public Artifact duplicateProjectArtifact( Artifact projectArtifact, String projectPackaging )
    {
        Artifact artifact =
            artifactFactory.createBuildArtifact( projectArtifact.getGroupId(), projectArtifact.getArtifactId(),
                                                 projectArtifact.getVersion(), projectPackaging );
        
        artifact.setFile( projectArtifact.getFile() );

        Collection metadataList = projectArtifact.getMetadataList();

        if ( metadataList != null )
        {
            for ( Iterator iter = metadataList.iterator(); iter.hasNext(); )
            {
                ArtifactMetadata metadata = ( ArtifactMetadata ) iter.next();

                artifact.addMetadata( metadata );
            }
        }

        return artifact;
    }

    public void addPluginMetadata( Artifact artifact, String projectName, String goalPrefixOverride )
    {
        Versioning versioning = new Versioning();
        versioning.setLatest( artifact.getVersion() );
        versioning.updateTimestamp();
        ArtifactRepositoryMetadata metadata = new ArtifactRepositoryMetadata( artifact, versioning );
        artifact.addMetadata( metadata );

        GroupRepositoryMetadata groupMetadata = new GroupRepositoryMetadata( artifact.getGroupId() );
        groupMetadata.addPluginMapping( getGoalPrefix( goalPrefixOverride, artifact ), artifact.getArtifactId(),
                                        projectName );

        artifact.addMetadata( groupMetadata );
    }

    private String getGoalPrefix( String goalPrefixOverride, Artifact artifact )
    {
        if ( goalPrefixOverride == null )
        {
            goalPrefixOverride = PluginDescriptor.getGoalPrefixFromArtifactId( artifact.getArtifactId() );
        }

        return goalPrefixOverride;
    }

    public void installPlugin( Artifact artifact, File pomFile )
        throws PluginStagingException
    {
        try
        {
            ArtifactMetadata metadata = new ProjectArtifactMetadata( artifact, pomFile );
            artifact.addMetadata( metadata );

            File file = artifact.getFile();

            // Here, we have a temporary solution to MINSTALL-3 (isDirectory() is true if it went through compile
            // but not package). We are designing in a proper solution for Maven 2.1
            if ( file != null && !file.isDirectory() )
            {
                installer.install( file, artifact, localRepository );
            }
            else
            {
                throw new PluginStagingException(
                                                  "The packaging for this project did not assign a file to the build artifact" );
            }
        }
        catch ( ArtifactInstallationException e )
        {
            throw new PluginStagingException( e.getMessage(), e );
        }
    }
}
