package org.apache.maven.plugin.plugit;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.test.plugin.PluginTestTool;
import org.apache.maven.shared.test.plugin.TestToolsException;

import java.io.File;

/**
 * Stage a plugin jar out to a testing local repository location, and rewrite the plugin's version
 * (for the tests only) to a specified value (ideally this value won't correspond to a deployed 
 * version, so the plugin is never resolved remotely by accident).
 * 
 * @goal stage
 * @phase pre-integration-test
 * 
 * @author jdcasey
 */
public class StagePluginMojo
    extends AbstractMojo
{

    /**
     * Whether to allow the plugin's unit tests to run when the plugin jar is generated for 
     * installation in the test local repository. This should be false if your plugin's unit tests
     * run maven builds that need to have the plugin staged (via this plugin). In most cases, this
     * plugin will not execute unless unit tests are run, so it should still be OK to skip unit
     * tests here for improved performance.
     * 
     * @parameter default-value="true"
     */
    private boolean skipUnitTests;

    /**
     * Version to use when installing the plugin into the testing-only local repository.
     * 
     * @parameter default-value="testing"
     * @required
     */
    private String pluginVersion;

    /**
     * The location of the testing-only local repository.
     * 
     * @parameter default-value="${project.build.directory}/local-repository"
     * @required
     */
    private File repositoryDirectory;

    /**
     * Component that orchestrates the plugin staging. This plugin is really just a wrapper around
     * the PluginTestTool.
     * 
     * @component
     */
    private PluginTestTool pluginTestTool;

    public StagePluginMojo()
    {
        // used by Maven
    }

    StagePluginMojo( boolean skipUnitTests, String pluginVersion, File repositoryDirectory,
                     PluginTestTool pluginTestTool )
    {
        this.skipUnitTests = skipUnitTests;
        this.pluginVersion = pluginVersion;
        this.repositoryDirectory = repositoryDirectory;
        this.pluginTestTool = pluginTestTool;
    }

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            if ( skipUnitTests )
            {
                pluginTestTool.preparePluginForUnitTestingWithMavenBuilds( pluginVersion, repositoryDirectory );
            }
            else
            {
                pluginTestTool.preparePluginForIntegrationTesting( pluginVersion, repositoryDirectory );
            }
        }
        catch ( TestToolsException e )
        {
            throw new MojoExecutionException( "Failed to stage plugin with version: " + pluginVersion
                + " to test local repository: " + repositoryDirectory, e );
        }
    }

}
