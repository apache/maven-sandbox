package org.apache.maven.shared.artifact.tools.repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.DefaultMavenSettingsBuilder;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Writer;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.testutils.MockedMavenComponentAccess;
import org.apache.maven.shared.tools.easymock.MockManager;
import org.apache.maven.shared.tools.easymock.TestFileManager;
import org.apache.maven.shared.tools.test.ReflectiveSetter;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.IOUtil;

public class MavenArtifactRepositorySourceTest
    extends TestCase
{

    private TestFileManager fileManager = new TestFileManager( "MavenArtifactRepositorySource.test.", ".xml" );

    public void tearDown()
        throws IOException
    {
        fileManager.cleanUp();
    }

    public void testShouldReadSettingsFromDefaultLocationIfAvailable()
        throws Throwable
    {
        String userHome = System.getProperty( "user.home" );
        String relativePath = ".m2/settings.xml";

        File defaultSettingsXml = new File( userHome, relativePath );

        if ( !defaultSettingsXml.exists() )
        {
            System.out.println( "Cannot run test using default location of Maven settings.xml; " + defaultSettingsXml
                + " does not exist." );
            return;
        }

        MavenProject project = new MavenProject( new Model() );

        MockedMavenComponentAccess mca = new MockedMavenComponentAccess();

        MockManager mm = new MockManager();
        
        mca.expectGetSettingsBuiler( mm );
        mca.expectGetArtifactRepositoryFactory( mm );
        
        DefaultMavenSettingsBuilder builder = new DefaultMavenSettingsBuilder();
        
        builder.enableLogging( new ConsoleLogger( Logger.LEVEL_DEBUG, "test" ) );
        
        ReflectiveSetter rs = new ReflectiveSetter( DefaultMavenSettingsBuilder.class );
        
//        <globalSettingsPath>${maven.home}/conf/settings.xml</globalSettingsPath>
//        <userSettingsPath>${user.home}/.m2/settings.xml</userSettingsPath>
        if ( System.getProperty( "maven.home" ) == null )
        {
            System.setProperty( "maven.home", System.getProperty( "user.home" ) );
            rs.setProperty( "globalSettingsPath", "${maven.home}/.m2/settings.xml", builder );
        }
        else
        {
            rs.setProperty( "globalSettingsPath", "${maven.home}/conf/settings.xml", builder );
        }
        
        rs.setProperty( "userSettingsPath", "${user.home}/.m2/settings.xml", builder );
        
        builder.initialize();
        
        mca.setSettingsBuilder( builder );
        
        mca.expectGetArtifactRepositoryLayout( mm, "default" );

        
        new MavenArtifactRepositorySource( project, mca, false );
    }

    public void testShouldReadSettingsFromCustomLocation()
        throws InvalidConfigurationException, IOException
    {
        File settingsFile = fileManager.createTempFile();
        
        Settings settings = new Settings();
        
        String localRepo = "/path/to/local/repo";
        
        settings.setLocalRepository( localRepo );
        
        FileWriter writer = null;
        try
        {
            writer = new FileWriter( settingsFile );
            
            new SettingsXpp3Writer().write( writer, settings );
        }
        finally
        {
            IOUtil.close( writer );
        }

        MavenProject project = new MavenProject( new Model() );

        MockedMavenComponentAccess mca = new MockedMavenComponentAccess();

        MockManager mm = new MockManager();
        
        mca.expectGetSettingsBuiler( mm );
        mca.expectGetArtifactRepositoryFactory( mm );
        mca.expectBuildSettings( settingsFile, settings );
        mca.expectGetArtifactRepositoryLayout( mm, "default" );
        
        mca.expectCreateArtifactRepositoryWithBasedir( mm, localRepo );

        mm.replayAll();
        
        ArtifactRepositorySource source = new MavenArtifactRepositorySource( project, settingsFile, mca, false );

        ArtifactRepository repo = source.getLocalRepository();
        
        assertNotNull( repo );
        assertEquals( localRepo, repo.getBasedir() );
        
        mm.verifyAll();
    }
    
    public void testShouldRetrieveRepositoryListWithMirrorsSubstitutedAppropriately()
        throws IOException, InvalidConfigurationException
    {
        MavenProject project = new MavenProject( new Model() );
        
        ArtifactRepository ar = new DefaultArtifactRepository( "test", "file:///tmp", null );
        
        project.setRemoteArtifactRepositories( Collections.singletonList( ar ) );
        
        Settings settings = new Settings();
        
        Mirror mirror = new Mirror();
        
        mirror.setId( "test.mirror" );
        mirror.setMirrorOf( "test" );
        mirror.setUrl( "file:///other/tmp" );
        
        settings.addMirror( mirror );
        
        File settingsFile = fileManager.createTempFile();
        
        FileWriter writer = null;
        try
        {
            writer = new FileWriter( settingsFile );
            
            new SettingsXpp3Writer().write( writer, settings );
        }
        finally
        {
            IOUtil.close( writer );
        }

        MockedMavenComponentAccess mca = new MockedMavenComponentAccess();

        MockManager mm = new MockManager();
        
        mca.expectGetSettingsBuiler( mm );
        mca.expectGetArtifactRepositoryFactory( mm );
        mca.expectBuildSettings( settingsFile, settings );
        mca.expectGetArtifactRepositoryLayout( mm, "default" );

        mca.expectCreateArtifactRepositoryWithUrl( mm, "file:///other/tmp" );
        
        mm.replayAll();
        
        ArtifactRepositorySource source = new MavenArtifactRepositorySource( project, settingsFile, mca, false );

        List repositories = source.getArtifactRepositories();
        
        assertEquals( 1, repositories.size() );
        
        assertEquals( "file:///other/tmp", ((ArtifactRepository) repositories.get( 0 ) ).getUrl() );
        
        mm.verifyAll();
    }
}
