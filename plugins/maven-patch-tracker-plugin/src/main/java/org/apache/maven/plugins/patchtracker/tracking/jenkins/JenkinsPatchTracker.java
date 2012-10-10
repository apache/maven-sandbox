package org.apache.maven.plugins.patchtracker.tracking.jenkins;
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

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.patchtracker.tracking.PatchTracker;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerException;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerRequest;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerResult;

import java.io.File;
import java.io.IOException;

/**
 * @author Olivier Lamy
 * @plexus.component role="org.apache.maven.plugins.patchtracker.tracking.PatchTracker" role-hint="jenkins"
 */
public class JenkinsPatchTracker
    implements PatchTracker
{
    DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

    public PatchTrackerResult createPatch( PatchTrackerRequest patchTrackerRequest, Log log )
        throws PatchTrackerException
    {
        File tmpPathFile = null;
        try
        {
            tmpPathFile = File.createTempFile( "jenkins", "patch" );
            FileUtils.write( tmpPathFile, patchTrackerRequest.getPatchContent() );

            HttpPost post = new HttpPost( patchTrackerRequest.getUrl() + "/buildWithParameters?delay=0sec" );

            MultipartEntity entity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );

            FileBody fileBody = new FileBody( tmpPathFile );
            entity.addPart( "patch.diff", fileBody );

            post.setEntity( entity );

            HttpResponse r = defaultHttpClient.execute( post );

            log.info( "r:" + r.getStatusLine().getStatusCode() + ", status: " + r.getStatusLine().getReasonPhrase()+"," + EntityUtils.toString(  r.getEntity()) );

            // FIXME verify response code !


            return new PatchTrackerResult();
        }
        catch ( IOException e )
        {
            throw new PatchTrackerException( e.getMessage(), e );
        }
        finally
        {
            FileUtils.deleteQuietly( tmpPathFile );
        }
    }

    public PatchTrackerResult updatePatch( PatchTrackerRequest patchTrackerRequest, Log log )
        throws PatchTrackerException
    {
        throw new RuntimeException( "updatePatch is not implemented with Jenkins" );
    }
}
