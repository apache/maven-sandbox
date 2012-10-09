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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
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
 */
@Mojo ( name = "to-issue", aggregator = true )
public class PatchToIssueMojo
    extends AbstractPatchMojo
{

    /**
     * for github user/organization  : github.com/apache use apache
     */
    @Parameter ( property = "patch.request.organisation", defaultValue = "" )
    protected String organisation;

    /**
     * github repo  : github.com/apache/maven-3 use maven-3
     */
    @Parameter ( property = "patch.request.repository", defaultValue = "" )
    protected String repository;

    /**
     * for github: pull request id
     */
    @Parameter ( property = "patch.request.id", defaultValue = "" )
    protected String id;


    /**
     * for github api url https://api.github.com
     */
    @Parameter ( property = "patch.patchSystem.url", defaultValue = "${project.patchManagement.url}" )
    protected String patchSystemUrl;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        try
        {
            PatchRepositoryRequest patchRepositoryRequest =
                new PatchRepositoryRequest().setUrl( patchSystemUrl ).setRepository( repository ).setId(
                    id ).setOrganization( organisation );

            PatchRepository patchRepository = getPatchRepository();

            PatchRepositoryResult result = patchRepository.getPatch( patchRepositoryRequest, getLog() );

            PatchTrackerRequest patchTrackerRequest = buidPatchTrackerRequest( false );

            patchTrackerRequest.setSummary( result.getTitle() );

            patchTrackerRequest.setDescription( result.getDescription() + ". url: " + result.getPatchUrl() );

            patchTrackerRequest.setPatchContent( result.getPatchContent() );

            getLog().debug( "patchTrackerRequest:" + patchTrackerRequest.toString() );

            PatchTracker patchTracker = getPatchTracker();

            PatchTrackerResult patchTrackerResult = patchTracker.createPatch( patchTrackerRequest, getLog() );
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
