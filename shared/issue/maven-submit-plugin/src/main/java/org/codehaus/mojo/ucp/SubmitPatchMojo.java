package org.codehaus.mojo.ucp;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
public class SubmitPatchMojo
    extends AbstractSubmitMojo
{
    /**
     * @component
     */
    private InputHandler inputHandler;

    private JiraSoapClient client;

    /**
     * @parameter expression="${patchServerUrl}"
     * default-value="http://localhost:8030/RPC2"
     */
    private String patchServerUrl;

    /**
     * @parameter expression="${patchFile}"
     * default-value="${project.artifactId}.diff"
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

    protected  void doSubmit()
        throws MojoExecutionException
    {

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

            client.addComment( issue.getKey(), patchServerUrl + "/" + patchFile );            
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error creating issue.", e );
        }

    }

    private void submitPatch( String patchFile, String patchText )
        throws Exception
    {
        // This will entail saying something in the POM about processing patches and where they should
        // go. The patch server.

        XmlRpcClient client = new XmlRpcClient( new URL( patchServerUrl ) );

        Vector args = new Vector();

        args.add( patchFile );

        args.add( Base64.encode( patchText.getBytes() ) );

        client.execute( "patch.accept", args );
    }
}
