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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.patchtracker.tracking.PatchTracker;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerException;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerRequest;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerResult;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * Goal which create a diff/patch file from the current project and create an issue in the project
 * with attaching the created patch file
 */
@Mojo ( name = "update", aggregator = true )
public class UpdatePatchMojo
    extends AbstractPatchMojo
{

    @Parameter ( defaultValue = "", property = "patch.patchId" )
    protected String patchId;


    public void execute()
        throws MojoExecutionException
    {

        try
        {
            // TODO do a status before and complains if some files in to be added status ?

            String patchContent = getPatchContent();

            PatchTrackerRequest patchTrackerRequest = buidPatchTrackerRequest( false );

            patchTrackerRequest.setPatchId( getPatchId() ).setPatchContent( patchContent ).setDescription(
                getPatchTrackerDescription() );

            getLog().debug( patchTrackerRequest.toString() );
            PatchTracker patchTracker = getPatchTracker();
            PatchTrackerResult result = patchTracker.updatePatch( patchTrackerRequest, getLog() );
            getLog().info( "issue updated with id:" + result.getPatchId() + ", url:" + result.getPatchUrl() );
        }
        catch ( ComponentLookupException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( PatchTrackerException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( PrompterException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }


    }

    protected String getPatchId()
        throws PrompterException, MojoExecutionException
    {
        String value = null;

        // cli must win !
        if ( StringUtils.isNotEmpty( patchId ) )
        {
            value = patchId;
        }

        return getValue( value, "patch id to update ?", null, true,
                         "you must configure a patch id when updating an issue or at least use interactive mode", value,
                         false );
    }


}
