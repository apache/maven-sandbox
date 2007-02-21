package org.apache.maven.shared.artifact.tools.repository;

import junit.framework.TestCase;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.settings.Mirror;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.testutils.MockedMavenComponentAccess;
import org.apache.maven.shared.tools.easymock.MockManager;
import org.easymock.MockControl;

public class ArtifactRepositoryToolTest
    extends TestCase
{

    public void testBuildLocalRepository()
        throws InvalidConfigurationException
    {
        MockManager mm = new MockManager();

        MockedMavenComponentAccess mca = new MockedMavenComponentAccess();

        mca.expectGetArtifactRepositoryFactory( mm );
        mca.expectGetArtifactRepositoryLayout( mm, "default" );

        String location = "file:///tmp";

        mca.expectCreateArtifactRepositoryWithBasedir( mm, location );

        mm.replayAll();

        ArtifactRepository repository = new ArtifactRepositoryTool( mca ).buildLocalRepository( location );

        assertEquals( location, repository.getBasedir() );

        mm.verifyAll();
    }

    public void testBuildMirrorRepository()
        throws InvalidConfigurationException
    {
        MockManager mm = new MockManager();

        MockedMavenComponentAccess mca = new MockedMavenComponentAccess();

        mca.expectGetArtifactRepositoryFactory( mm );
        mca.expectGetArtifactRepositoryLayout( mm, "default" );

        String id = "id";
        
        MockControl repoCtl = MockControl.createControl( ArtifactRepository.class );
        ArtifactRepository repo = (ArtifactRepository) repoCtl.getMock();
        
        mm.add( repoCtl );
        
        repo.getId();
        repoCtl.setReturnValue( id );
        
        MockControl layoutCtl = MockControl.createControl( ArtifactRepositoryLayout.class );
        ArtifactRepositoryLayout layout = (ArtifactRepositoryLayout) layoutCtl.getMock();
        
        mm.add( layoutCtl );
        
        repo.getLayout();
        repoCtl.setReturnValue( layout );
        
        repo.getSnapshots();
        repoCtl.setReturnValue( null );
        
        repo.getReleases();
        repoCtl.setReturnValue( null );
        
        String location = "file:///tmp";
        String name = "mirror";
        
        Mirror mirror = new Mirror();
        mirror.setId( "mirror" );
        mirror.setName( name );
        mirror.setUrl( location );
        mirror.setMirrorOf( id );

        mca.expectCreateDefaultArtifactRepositoryWithIdNameLocationAndLayout( id, name, location, layout );

        mm.replayAll();

        ArtifactRepository repository = new ArtifactRepositoryTool( mca ).buildMirrorRepository( repo, mirror );

        assertEquals( location, repository.getUrl() );
        assertEquals( id, repository.getId() );
        
        if ( repository instanceof DefaultArtifactRepository )
        {
            assertEquals( name, ((DefaultArtifactRepository) repository).getName() );
        }

        mm.verifyAll();
    }
    
    public void testBuildArtifactRepository() throws InvalidConfigurationException
    {
        String layoutId = "default";
        String name = "mirror";
        String url = "url";
        String id = "id";
        boolean releases = true;
        boolean snapshots = true;
        
        MockManager mm = new MockManager();

        MockedMavenComponentAccess mca = new MockedMavenComponentAccess();

        mca.expectGetArtifactRepositoryFactory( mm );
        mca.expectGetArtifactRepositoryLayout( mm, "default" );
        mca.expectCreateArtifactRepositoryWithIdUrlLayoutAndPolicies( mm, id, url, "default", snapshots, releases );

        mm.replayAll();
        
        new ArtifactRepositoryTool( mca ).buildArtifactRepository( id, url, name, layoutId, snapshots, releases );
        
        mm.verifyAll();
    }

}
