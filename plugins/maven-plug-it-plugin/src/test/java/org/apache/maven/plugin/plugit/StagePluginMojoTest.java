package org.apache.maven.plugin.plugit;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.test.plugin.PluginTestTool;
import org.apache.maven.shared.tools.easymock.TestFileManager;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;

public class StagePluginMojoTest
    extends PlexusTestCase
{
    
    private PluginTestTool pluginTestTool;
    
    private TestFileManager fileManager = new TestFileManager( "StagePluginMojo.test", "" );
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        this.pluginTestTool = (PluginTestTool) lookup( PluginTestTool.ROLE, "default" );
    }
    
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        fileManager.cleanUp();
    }
    
    public void testShouldInstallThisPluginToSpecifiedTestLocalRepoLocation()
        throws MojoExecutionException, MojoFailureException
    {
        File localRepoParent = fileManager.createTempDir();
        File localRepo = new File( localRepoParent, "local-repository" );
        
        assertFalse( localRepo.exists() );
        
        // we must ALWAYS skip unit tests for this unit test...
        new StagePluginMojo( true, "testing", localRepo, pluginTestTool ).execute();
        
        assertTrue( localRepo.exists() );
    }

}
