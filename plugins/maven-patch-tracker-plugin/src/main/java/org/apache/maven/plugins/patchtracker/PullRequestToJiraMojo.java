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

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.patchtracker.patching.PatchRepository;
import org.apache.maven.plugins.patchtracker.patching.PatchRepositoryException;
import org.apache.maven.plugins.patchtracker.patching.PatchRepositoryRequest;
import org.apache.maven.plugins.patchtracker.patching.PatchRepositoryResult;
import org.apache.maven.plugins.patchtracker.tracking.PatchTracker;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerException;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerRequest;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerResult;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;


/**
 * @author Olivier Lamy
 * @goal to-issue
 * @aggregator
 */
public class PullRequestToJiraMojo
    extends AbstractPatchMojo
{

    /**
     * for github user/organization  : github.com/apache use apache
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
            PatchRepositoryRequest patchRepositoryRequest =
                new PatchRepositoryRequest().setUrl( githubApiUrl ).setRepository( repo ).setId(
                    pullRequestId ).setOrganization( user );

            PatchRepository patchRepository = getPatchRepository();

            PatchRepositoryResult result = patchRepository.getPatch( patchRepositoryRequest, getLog() );

            PatchTrackerRequest patchTrackerRequest = buidPatchTrackerRequest( false );

            patchTrackerRequest.setSummary( result.getTitle() );

            patchTrackerRequest.setDescription( result.getDescription() + ". url: " + result.getPatchUrl() );

            patchTrackerRequest.setPatchContent( result.getPatchContent() );

            getLog().debug( "patchTrackerRequest:" + patchTrackerRequest.toString() );

            PatchTracker patchTracker = getPatchTracker();

            PatchTrackerResult patchTrackerResult = patchTracker.createPatch( patchTrackerRequest );
            getLog().info( "issue created with id:" + patchTrackerResult.getPatchId() + ", url:"
                               + patchTrackerResult.getPatchUrl() );

        }
        catch ( ComponentLookupException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( PatchRepositoryException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( PatchTrackerException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

}
