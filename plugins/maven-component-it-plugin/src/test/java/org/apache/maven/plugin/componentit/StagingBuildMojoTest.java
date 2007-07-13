package org.apache.maven.plugin.componentit;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.componentit.StagingBuildMojo;
import org.apache.maven.shared.test.plugin.ComponentTestTool;
import org.apache.maven.shared.tools.easymock.TestFileManager;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.io.IOException;

public class StagingBuildMojoTest
    extends PlexusTestCase
{
    
    private ComponentTestTool componentTestTool;
    
    private TestFileManager fileManager = new TestFileManager( "StagingBuildMojo.test", "" );
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        this.componentTestTool = (ComponentTestTool) lookup( ComponentTestTool.ROLE, "default" );
    }
    
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        fileManager.cleanUp();
    }
    
    public void testShouldInstallThisPluginToSpecifiedTestLocalRepoLocation()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        File localRepoParent = fileManager.createTempDir();
        File localRepo = new File( localRepoParent, "local-repository" );
        
        assertFalse( localRepo.exists() );
        
        // we must ALWAYS skip unit tests for this unit test...
        new StagingBuildMojo( new File( "pom.xml" ).getCanonicalFile(), true, "testing", localRepo, componentTestTool ).execute();
        
        assertTrue( localRepo.exists() );
    }

}
