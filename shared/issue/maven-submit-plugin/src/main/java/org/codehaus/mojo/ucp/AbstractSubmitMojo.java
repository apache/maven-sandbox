package org.codehaus.mojo.ucp;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.Base64;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.BinaryEncoder;
import org.codehaus.plexus.components.interactivity.InputHandler;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.mojo.ucp.jira.RemoteIssue;

import java.net.URL;
import java.util.Vector;
import java.rmi.Remote;

/**
 * Goal which touches a timestamp file.
 *
 * @goal submit
 * @todo need to be able to revoke a patch so it can be replaced
 */
public abstract class AbstractSubmitMojo
    extends AbstractMojo
{
    /**
     * @component
     */
    protected InputHandler inputHandler;

    protected JiraSoapClient client;

    /**
     * @parameter expression="${issueManagementRemotingUrl}"
     * default-value="http://jira.codehaus.org/rpc/soap/jirasoapservice-v2"
     */
    protected String issueManagementRemotingUrl;

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

            doSubmit();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error making submission.", e );
        }
    }

    private void setupJiraSoapClient()
        throws Exception
    {
        client = new JiraSoapClient();

        client.setEndpoint( new URL( issueManagementRemotingUrl ) );

        // TODO: some freeform settings? Where to get these

        client.setLogin( "jason" );

        client.setPassword( "jira913" );

        client.initialize();
    }

    //TODO: now to get most of these reliably from the POM

    protected RemoteIssue createIssue( String projectKey,
                                       String summary,
                                       String description,
                                       String issueTypeId,
                                       String priorityId,
                                       String componentId,
                                       String versionId,
                                       String assigneeId )
        throws Exception
    {
        return client.createIssue( projectKey, summary, description, issueTypeId, priorityId, componentId, versionId, assigneeId );
    }

    protected abstract void doSubmit()
        throws Exception;
}
