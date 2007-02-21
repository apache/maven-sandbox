package org.apache.maven.plugin.plugintest.backup;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.GroupRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class PluginBackupManager
{

    private final Log log;

    private final ArtifactRepository localRepository;

    public PluginBackupManager( ArtifactRepository localRepository, Log log )
    {
        this.localRepository = localRepository;
        this.log = log;
    }

    public void backupInstalledPluginData( Artifact projectArtifact, File backupDirectory )
        throws IOException
    {
        ArtifactRepositoryLayout layout = localRepository.getLayout();

        File basedir = new File( localRepository.getBasedir() );

        // find the directory where plugin-group metadata is stored...for prefix mappings.
        GroupRepositoryMetadata groupMetadata = new GroupRepositoryMetadata( projectArtifact.getGroupId() );

        String relativePath = layout.pathOfLocalRepositoryMetadata( groupMetadata, localRepository );

        File localPath = new File( basedir, relativePath );

        File groupMetadataDir = localPath.getParentFile();

        backup( groupMetadataDir, "**/maven-metadata*.xml", relativePath, backupDirectory );

        // find the directory where plugin-artifact metadata is stored...for snapshot versioning.
        Versioning versioning = new Versioning();
        versioning.setLatest( projectArtifact.getVersion() );
        versioning.updateTimestamp();

        ArtifactRepositoryMetadata artifactMetadata = new ArtifactRepositoryMetadata( projectArtifact, versioning );

        relativePath = layout.pathOfLocalRepositoryMetadata( artifactMetadata, localRepository );

        localPath = new File( basedir, relativePath );

        File artifactMetadataDir = localPath.getParentFile();

        backup( artifactMetadataDir, "**/maven-metadata*", relativePath, backupDirectory );

        // find the directory where the plugin artifact itself is stored...in case a file gets overwritten.
        relativePath = layout.pathOf( projectArtifact );

        localPath = new File( basedir, relativePath );

        File artifactDir = localPath.getParentFile();

        backup( artifactDir, "**/*", relativePath, backupDirectory );

        // find the directory where the plugin POM is stored...in case the POM version is the same.
        ProjectArtifactMetadata pomMetadata = new ProjectArtifactMetadata( projectArtifact );

        relativePath = layout.pathOfLocalRepositoryMetadata( pomMetadata, localRepository );

        localPath = new File( basedir, relativePath );

        File pomDir = localPath.getParentFile();

        backup( pomDir, "**/*", relativePath, backupDirectory );
    }

    private void backup( File groupMetadataDir, String pattern, String relativePath, File backupDirectory )
        throws IOException
    {
        FileSet fs = new FileSet();

        fs.setDirectory( groupMetadataDir.getAbsolutePath() );
        fs.addInclude( pattern );

        FileSetManager fsm = new FileSetManager( log );

        String[] includedFiles = fsm.getIncludedFiles( fs );

        File destDir = new File( backupDirectory, relativePath );
        destDir.mkdirs();

        for ( int i = 0; i < includedFiles.length; i++ )
        {
            File src = new File( groupMetadataDir, includedFiles[i] );
            File dest = new File( destDir, includedFiles[i] );

            dest.getParentFile().mkdirs();

            FileUtils.copyFile( src, dest );
        }
    }

    public void restorePluginData( File backupDirectory )
        throws IOException
    {
        File localRepoDir = new File( localRepository.getBasedir() );
        
        FileUtils.copyDirectory( backupDirectory, localRepoDir );
    }

}
