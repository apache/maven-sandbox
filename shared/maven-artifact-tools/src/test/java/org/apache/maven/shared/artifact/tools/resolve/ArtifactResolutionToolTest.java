package org.apache.maven.shared.artifact.tools.resolve;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.components.MavenComponentAccess;
import org.apache.maven.shared.artifact.tools.repository.ArtifactRepositorySource;
import org.apache.maven.shared.artifact.tools.resolve.testutils.TestRepositorySource;
import org.apache.maven.shared.artifact.tools.testutils.MockedMavenComponentAccess;
import org.apache.maven.shared.tools.easymock.MockManager;
import org.apache.maven.shared.tools.easymock.TestFileManager;
import org.codehaus.plexus.util.IOUtil;
import org.easymock.MockControl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public class ArtifactResolutionToolTest
    extends TestCase
{

    private TestFileManager fileManager = new TestFileManager( "ArtifactResolutionToolTest.", "" );

    public void tearDown()
        throws IOException
    {
        fileManager.cleanUp();
    }

    public void testGetAvailableVersions()
        throws Exception
    {
        String groupId = "org.apache.maven";
        String artifactId = "maven-artifact";

        MockManager mm = new MockManager();

        MavenComponentAccess componentAccess = buildComponentAccess( "jar", mm );

        File dir = fileManager.createTempDir();

        ArtifactRepositoryLayout layout = new DefaultRepositoryLayout();

        ArtifactRepository repo = new DefaultArtifactRepository( "local", dir.toURL().toExternalForm(), layout, null,
                                                                 null );

        TestRepositorySource rs = new TestRepositorySource( repo, Collections.EMPTY_LIST );
        
        Metadata md = new Metadata();
        md.setArtifactId( artifactId );
        md.setGroupId( groupId );
        
        Versioning versioning = new Versioning();
        versioning.addVersion( "2.0.4" );
        
        md.setVersioning( versioning );
        
        mm.replayAll();
        
        File mdFile = new File( dir, "org/apache/maven/maven-artifact/maven-metadata-local.xml" );
        
        mdFile.getParentFile().mkdirs();
        
        FileWriter writer = null;
        
        try
        {
            writer = new FileWriter( mdFile );
            new MetadataXpp3Writer().write( writer, md );
        }
        finally
        {
            IOUtil.close( writer );
        }

        List versions = new ArtifactResolutionTool( componentAccess ).getAvailableVersions( groupId, artifactId, rs );

        assertEquals( 1, versions.size() );
        assertTrue( versions.contains( "2.0.4" ) );
        
        mm.verifyAll();
    }

    public void testResolveArtifact()
        throws ResolutionException, InvalidConfigurationException, InvalidArtifactSpecificationException
    {
        resolve( false );
    }

    public void testResolveProjectMetadata()
        throws ResolutionException, InvalidConfigurationException, InvalidArtifactSpecificationException
    {
        resolve( true );
    }

    private void resolve( boolean resolveProjectOnly )
        throws ResolutionException, InvalidConfigurationException, InvalidArtifactSpecificationException
    {
        String type = resolveProjectOnly ? "pom" : "jar";

        ArtifactQuery query = new ArtifactQuery( "group", "artifact" );
        query.setVersion( "1.0" );


        MockManager mm = new MockManager();

        MockedMavenComponentAccess componentAccess = buildComponentAccess( type, mm );

        List remoteRepos = Collections.EMPTY_LIST;

        MockControl localRepoCtl = MockControl.createControl( ArtifactRepository.class );

        mm.add( localRepoCtl );

        ArtifactRepository localRepo = (ArtifactRepository) localRepoCtl.getMock();

        localRepo.getBasedir();
        localRepoCtl.setReturnValue( "/tmp", MockControl.ONE_OR_MORE );

        MockControl artifactCtl = MockControl.createControl( Artifact.class );

        mm.add( artifactCtl );

        Artifact artifact = (Artifact) artifactCtl.getMock();

        artifact.getId();
        artifactCtl.setReturnValue( "group:artifact:" + type + ":1.0", MockControl.ONE_OR_MORE );

        componentAccess.expectResolve( artifact, remoteRepos, localRepo );

        Model model = new Model();

        MavenProject project = new MavenProject( model );
        project.setOriginalModel( model );

        componentAccess.expectBuildFromRepository( artifact, Collections.EMPTY_LIST, localRepo, project );

        MockControl rsCtl = MockControl.createControl( ArtifactRepositorySource.class );

        mm.add( rsCtl );

        ArtifactRepositorySource rs = (ArtifactRepositorySource) rsCtl.getMock();

        try
        {
            rs.getLocalRepository();
            rsCtl.setReturnValue( localRepo );
        }
        catch ( InvalidConfigurationException e )
        {
            fail( "should never happen" );
        }

        rs.getArtifactRepositories();
        rsCtl.setReturnValue( remoteRepos );

        mm.replayAll();

        ArtifactResolutionResult result = null;

        if ( resolveProjectOnly )
        {
            result = new ArtifactResolutionTool( componentAccess ).resolveProjectMetadata( query, rs );
        }
        else
        {
            result = new ArtifactResolutionTool( componentAccess ).resolve( query, rs );
        }

        assertSame( model, result.getModel() );
        assertSame( project, result.getProject() );

        mm.verifyAll();
    }

    private MockedMavenComponentAccess buildComponentAccess( String type, MockManager mm )
    {
        MockedMavenComponentAccess componentAccess = new MockedMavenComponentAccess();

        componentAccess.expectGetArtifactResolver( mm );
        componentAccess.expectGetArtifactHandlerManagerWithHandlerForTypeWithClassifier( mm, type, null );
        componentAccess.expectGetProjectBuilder( mm );
        componentAccess.expectGetRepositoryMetadataManager( mm );
        
        return componentAccess;
    }

}
