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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.patchtracker.tracking.PatchTracker;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerException;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerRequest;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerResult;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.JiraSoapServiceService;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.JiraSoapServiceServiceLocator;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteAuthenticationException;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteComponent;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteException;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteIssue;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Olivier Lamy
 * @plexus.component role="org.apache.maven.plugins.patchtracker.tracking.PatchTracker" role-hint="jira"
 */
public class JiraPatchTracker
    implements PatchTracker
{
    public PatchTrackerResult createPatch( PatchTrackerRequest patchTrackerRequest, Log log )
        throws PatchTrackerException
    {

        JiraSession jiraSession = createSession( patchTrackerRequest, log );
        try
        {
            RemoteIssue remoteIssue = new RemoteIssue();
            remoteIssue.setProject( extractProjectKey( patchTrackerRequest.getUrl() ) );
            remoteIssue.setSummary( patchTrackerRequest.getSummary() );
            remoteIssue.setDescription( patchTrackerRequest.getDescription() );
            remoteIssue.setType( patchTrackerRequest.getPatchType() );
            remoteIssue.setPriority( patchTrackerRequest.getPatchPriority() );

            // do we have a component id ??
            String componentId =
                getComponentId( patchTrackerRequest.getUrl(), extractProjectKey( patchTrackerRequest.getUrl() ) );

            if ( StringUtils.isNotEmpty( componentId ) )
            {
                List<RemoteComponent> remoteComponents = jiraSession.getRemoteComponents();
                for ( RemoteComponent remoteComponent : remoteComponents )
                {
                    if ( StringUtils.equalsIgnoreCase( componentId, remoteComponent.getId() ) )
                    {
                        remoteIssue.setComponents( new RemoteComponent[]{ remoteComponent } );
                        break;
                    }
                }
            }

            remoteIssue = jiraSession.createIssue( remoteIssue );

            // TODO handle of boolean result
            jiraSession.addBase64EncodedAttachmentsToIssue( remoteIssue.getKey(), remoteIssue.getKey(),
                                                            patchTrackerRequest.getPatchContent() );

            // add a comment

            return new PatchTrackerResult().setPatchId( remoteIssue.getKey() ).setPatchUrl(
                extractBaseUrl( patchTrackerRequest.getUrl() ) + "/browse/" + remoteIssue.getKey() );
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

    public PatchTrackerResult updatePatch( PatchTrackerRequest patchTrackerRequest, Log log )
        throws PatchTrackerException
    {

        JiraSession jiraSession = createSession( patchTrackerRequest, log );
        try
        {
            RemoteIssue remoteIssue = jiraSession.findRemoteIssue( patchTrackerRequest.getPatchId() );

            if ( patchTrackerRequest.getPatchId() == null )
            {
                throw new PatchTrackerException( "patch id is mandatory when updating the patch tracker" );
            }

            // TODO handle of boolean result
            jiraSession.addBase64EncodedAttachmentsToIssue( remoteIssue.getKey(), remoteIssue.getKey(),
                                                            patchTrackerRequest.getPatchContent() );

            jiraSession.addCommentToIssue( remoteIssue.getKey(), patchTrackerRequest.getDescription() );

            return new PatchTrackerResult().setPatchId( remoteIssue.getKey() ).setPatchUrl(
                extractBaseUrl( patchTrackerRequest.getUrl() ) + "/browse/" + remoteIssue.getKey() );

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

    public JiraSession createSession( PatchTrackerRequest patchTrackerRequest, Log log )
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
            URL baseUrl = extractBaseUrlAsUrl( patchTrackerRequest.getUrl() );
            log.debug( "baseUrl:" + baseUrl.toExternalForm() );
            org.apache.maven.plugins.patchtracker.tracking.jira.soap.JiraSoapService service =
                jiraSoapServiceGetter.getJirasoapserviceV2( new URL( baseUrl, "/rpc/soap/jirasoapservice-v2" ) );
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
        // case component id in url: https://jira.codehaus.org/browse/MSHARED/component/15255
        if ( StringUtils.contains( url, "/component/" ) )
        {
            url = StringUtils.substringBeforeLast( url, "/component" );
        }

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

    protected String getComponentId( String url, String projectKey )
    {
        //https://jira.codehaus.org/browse/MSHARED/component/15255
        // return 15255
        if ( StringUtils.contains( url, "/" + projectKey + "/component/" ) )
        {
            return StringUtils.substringAfterLast( url, "/" + projectKey + "/component/" );
        }
        return null;
    }
}
