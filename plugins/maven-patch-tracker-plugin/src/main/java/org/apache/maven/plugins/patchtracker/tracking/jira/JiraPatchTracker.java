package org.apache.maven.plugins.patchtracker.tracking.jira;
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
import org.apache.maven.plugins.patchtracker.tracking.PatchTracker;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerException;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerRequest;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerResult;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.JiraSoapServiceService;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.JiraSoapServiceServiceLocator;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteAuthenticationException;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteException;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteIssue;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Olivier Lamy
 * @plexus.component role="org.apache.maven.plugins.patchtracker.tracking.PatchTracker" role-hint="jira"
 */
public class JiraPatchTracker
    implements PatchTracker
{
    public PatchTrackerResult createPatch( PatchTrackerRequest patchTrackerRequest )
        throws PatchTrackerException
    {

        JiraSession jiraSession = createSession( patchTrackerRequest );
        try
        {
            RemoteIssue remoteIssue = null;
            // is it an update
            if ( patchTrackerRequest.getPatchId() != null )
            {

            }
            else
            {
                remoteIssue = new RemoteIssue();
                remoteIssue.setProject( extractProjectKey( patchTrackerRequest.getUrl() ) );
                remoteIssue.setSummary( patchTrackerRequest.getSummary() );
                remoteIssue.setDescription( patchTrackerRequest.getDescription() );

                remoteIssue = jiraSession.createIssue( remoteIssue );

                // TODO handle of boolean result
                jiraSession.addBase64EncodedAttachmentsToIssue( remoteIssue.getKey(), remoteIssue.getKey(),
                                                                patchTrackerRequest.getPatchContent() );
            }

            PatchTrackerResult patchTrackerResult = new PatchTrackerResult();
            patchTrackerResult.setPatchId( remoteIssue.getKey() );
            patchTrackerResult.setPatchUrl(
                extractBaseUrl( patchTrackerRequest.getUrl() ) + "/browse/" + remoteIssue.getKey() );
            return patchTrackerResult;
        }
        catch ( RemoteAuthenticationException e )
        {
            throw new PatchTrackerException( e.getMessage(), e );
        }
        catch ( RemoteException e )
        {
            throw new PatchTrackerException( e.getMessage(), e );
        }
        catch ( java.rmi.RemoteException e )
        {
            throw new PatchTrackerException( e.getMessage(), e );
        }
    }


    public JiraSession createSession( PatchTrackerRequest patchTrackerRequest )
        throws PatchTrackerException
    {
        if ( StringUtils.isEmpty( patchTrackerRequest.getUserName() ) || StringUtils.isEmpty(
            patchTrackerRequest.getPassword() ) )
        {
            // remote access not supported
            throw new PatchTrackerException( "username or password for jira access are null or empty" );
        }
        JiraSoapServiceService jiraSoapServiceGetter = new JiraSoapServiceServiceLocator();
        try
        {
            org.apache.maven.plugins.patchtracker.tracking.jira.soap.JiraSoapService service =
                jiraSoapServiceGetter.getJirasoapserviceV2(
                    new URL( extractBaseUrlAsUrl( patchTrackerRequest.getUrl() ), "rpc/soap/jirasoapservice-v2" ) );
            return new JiraSession( service, service.login( patchTrackerRequest.getUserName(),
                                                            patchTrackerRequest.getPassword() ),
                                    extractProjectKey( patchTrackerRequest.getUrl() ) );
        }
        catch ( MalformedURLException e )
        {
            throw new PatchTrackerException( e.getMessage(), e );
        }
        catch ( ServiceException e )
        {
            throw new PatchTrackerException( e.getMessage(), e );
        }
        catch ( RemoteAuthenticationException e )
        {
            throw new PatchTrackerException( e.getMessage(), e );
        }
        catch ( RemoteException e )
        {
            throw new PatchTrackerException( e.getMessage(), e );
        }
        catch ( java.rmi.RemoteException e )
        {
            throw new PatchTrackerException( e.getMessage(), e );
        }
    }

    /**
     * @param url https://jira.codehaus.org/browse/MNG
     * @return the project key MNG
     */
    protected String extractProjectKey( String url )
    {
        return ( StringUtils.endsWith( url, "/" ) )
            ? StringUtils.substringAfterLast( StringUtils.removeEnd( url, "/" ), "/" )
            : StringUtils.substringAfterLast( url, "/" );


    }

    /**
     * @param url https://jira.codehaus.org/browse/MNG
     * @return https://jira.codehaus.org
     */
    protected String extractBaseUrl( String url )
    {
        return StringUtils.substringBefore( url, "/browse" );

    }

    protected URL extractBaseUrlAsUrl( String url )
        throws MalformedURLException
    {
        return new URL( extractBaseUrl( url ) );

    }
}
