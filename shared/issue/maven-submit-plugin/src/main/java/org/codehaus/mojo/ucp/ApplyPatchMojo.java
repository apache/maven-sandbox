package org.codehaus.mojo.ucp;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.xmlrpc.XmlRpcClient;
import org.codehaus.plexus.components.interactivity.InputHandler;
import org.codehaus.plexus.util.FileUtils;

import java.net.URL;
import java.util.Vector;

/**
 * Goal which touches a timestamp file.
 *
 * @goal apply
 *
 * @todo need to be able to revoke a patch so it can be replaced
 */
public class ApplyPatchMojo
    extends AbstractMojo
{
    /**
     * @component
     */
    private InputHandler inputHandler;

    private JiraSoapClient client;

    /**
     * @parameter
     *   expression="${patchServerUrl}"
     *   default-value="http://localhost:8030/RPC2"
     */
    private String patchServerUrl;

    /**
     * @parameter
     *   expression="${patchFile}"
     *   default-value="${project.artifactId}.diff"
     */
    private String patchFile;

    // ----------------------------------------------------------------------
    // Process for submitting a patch
    //
    // -> Ask user for a description of the problem
    //    -> Could have some settings for contributors so that we get their name for attribution
    // -> create a diff in the toplevel of the project
    // -> Create an issue with an attachment if no issue exists or
    //    Update an existing issue with the attachment.
    // -> Send it to the issue system
    //
    // We'll start with JIRA
    // ----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException
    {
        try
        {
            setupJiraSoapClient();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error setting up JIRA client.", e );
        }

        try
        {
            getLog().info( "Summary: " );

            String summary = inputHandler.readLine();

            //TODO: make sure there is a summary

            getLog().info( "Description:" );

            //TODO:  make sure there is a description

            String description = inputHandler.readLine();

            //TODO: issueTypeId, priorityId, componentId, versionId, assigneeId and each of these needs
            //      to be validated along the way to make submission easy.

            createIssue( "MNGTEST", summary, description, "1", "1", "11909", "12092", "jason" );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error creating issue.", e );
        }

        // - make sure the patch file exists
        // - make sure it's not zero length
        // - unique patch name or directory

        try
        {
            submitPatch( patchFile, FileUtils.fileRead( patchFile ) );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error submitting the patch.", e );
        }
    }

    private void setupJiraSoapClient()
        throws Exception
    {
        client = new JiraSoapClient();

        client.setEndpoint( new URL( "http://jira.codehaus.org/rpc/soap/jirasoapservice-v2" ) );

        // TODO: some freeform settings? Where to get these

        client.setLogin( "jason" );

        client.setPassword( "jira913" );

        client.initialize();
    }

    //TODO: now to get most of these reliably from the POM

    private void createIssue( String projectKey,
                              String summary,
                              String description,
                              String issueTypeId,
                              String priorityId,
                              String componentId,
                              String versionId,
                              String assigneeId )
        throws Exception
    {
        client.createIssue( projectKey, summary, description, issueTypeId, priorityId, componentId, versionId, assigneeId );
    }

    private void submitPatch( String patchFile, String patchText )
        throws Exception
    {
        // This will entail saying something in the POM about processing patches and where they should
        // go. The patch server.

        XmlRpcClient client = new XmlRpcClient( new URL( patchServerUrl ) );

        Vector args = new Vector();

        System.out.println( "patchFile = " + patchFile );

        args.add( patchFile );

        System.out.println( "patchText = " + patchText );

        args.add( patchText );

        client.execute( "patch.accept", args );
    }
}
