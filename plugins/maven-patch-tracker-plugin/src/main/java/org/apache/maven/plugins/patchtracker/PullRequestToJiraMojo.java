package org.apache.maven.plugins.patchtracker;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.patchtracker.github.PullRequest;
import org.apache.maven.plugins.patchtracker.tracking.PatchTracker;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerException;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerRequest;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerResult;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.io.IOException;


/**
 * @author Olivier Lamy
 * @goal to-issue
 * @aggregator
 */
public class PullRequestToJiraMojo
    extends AbstractPatchMojo
{

    /**
     * github user/organization  : github.com/apache use apache
     *
     * @parameter expression="${patch.pullrequest.user}" default-value=""
     */
    protected String user;

    /**
     * github repo  : github.com/apache/maven-3 use maven-3
     *
     * @parameter expression="${patch.pullrequest.repo}" default-value=""
     */
    protected String repo;

    /**
     * pull request id
     *
     * @parameter expression="${patch.pullrequest.id}" default-value=""
     */
    protected String pullRequestId;


    /**
     * github api url
     *
     * @parameter expression="${patch.pullrequest.githubApiUrl}" default-value="https://api.github.com/repos"
     */
    protected String githubApiUrl;


    DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        // format curl -v https://api.github.com/repos/apache/directmemory/pulls/1
        try
        {

            String url = githubApiUrl + "/" + user + "/" + repo + "/pulls/" + pullRequestId;
            getLog().debug( "url" + url );

            HttpGet httpGet = new HttpGet( url );

            HttpResponse httpResponse = defaultHttpClient.execute( httpGet );

            ObjectMapper objectMapper = new ObjectMapper();

            // we don't parse all stuff
            objectMapper.configure( DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false );

            String response = IOUtils.toString( httpResponse.getEntity().getContent() );

            getLog().debug( "response:" + response );

            PullRequest pullRequest = objectMapper.reader( PullRequest.class ).readValue( response );

            getLog().debug( "pullRequest:" + pullRequest.toString() );

            PatchTrackerRequest patchTrackerRequest = buidPatchTrackerRequest( false );

            patchTrackerRequest.setSummary( pullRequest.getTitle() );

            patchTrackerRequest.setDescription( pullRequest.getBody() + ". url: " + pullRequest.getPatch_url() );

            patchTrackerRequest.setPatchContent( getPatchContent( pullRequest ) );

            PatchTracker patchTracker = getPatchTracker();

            PatchTrackerResult result = patchTracker.createPatch( patchTrackerRequest );
            getLog().info( "issue created with id:" + result.getPatchId() + ", url:" + result.getPatchUrl() );
        }
        catch ( ClientProtocolException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( ComponentLookupException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( PatchTrackerException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    protected String getPatchContent( PullRequest pullRequest )
        throws IOException, ClientProtocolException
    {
        HttpGet httpGet = new HttpGet( pullRequest.getPatch_url() );

        HttpResponse httpResponse = defaultHttpClient.execute( httpGet );

        return IOUtils.toString( httpResponse.getEntity().getContent() );
    }
}
