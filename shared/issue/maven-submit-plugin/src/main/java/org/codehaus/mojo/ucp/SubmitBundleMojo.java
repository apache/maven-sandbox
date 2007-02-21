package org.codehaus.mojo.ucp;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.xmlrpc.Base64;
import org.apache.xmlrpc.XmlRpcClient;
import org.codehaus.mojo.ucp.jira.RemoteIssue;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.net.URL;
import java.util.Vector;

/**
 * Goal which touches a timestamp file.
 *
 * @goal submit
 * @todo need to be able to revoke a patch so it can be replaced
 */
public class SubmitBundleMojo
    extends AbstractSubmitMojo
{
    /**
     * @parameter expression="${bundleServerUrl}"
     * default-value="http://localhost:8030/RPC2"
     */
    private String bundleServerUrl;

    /**
     * @parameter expression="${bundleFile}"
     * default-value="${project.artifactId}.diff"
     */
    private String bundleFile;

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

    protected void doSubmit()
        throws Exception
    {
        try
        {
            submitBundle( bundleFile, FileUtils.fileRead( bundleFile ) );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error submitting the patch.", e );
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

            RemoteIssue issue = createIssue( "MNGTEST", summary, description, "1", "1", "11909", "12092", "jason" );

            client.addComment( issue.getKey(), bundleServerUrl + "/" + bundleFile );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error creating issue.", e );
        }

    }

    private void submitBundle( String patchFile, String patchText )
        throws Exception
    {
        // This will entail saying something in the POM about processing patches and where they should
        // go. The patch server.

        XmlRpcClient client = new XmlRpcClient( new URL( bundleServerUrl ) );

        Vector args = new Vector();

        args.add( patchFile );

        args.add( Base64.encode( FileUtils.fileRead( bundleFile ).getBytes() ) );

        client.execute( "bundle.accept", args );
    }
}
