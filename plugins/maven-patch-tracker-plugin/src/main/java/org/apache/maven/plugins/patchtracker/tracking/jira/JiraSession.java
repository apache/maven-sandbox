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

import org.apache.axis.encoding.Base64;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.JiraSoapService;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteAuthenticationException;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteComment;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteComponent;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteException;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteIssue;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemotePermissionException;
import org.apache.maven.plugins.patchtracker.tracking.jira.soap.RemoteValidationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Olivier Lamy
 */
public class JiraSession
{
    private final JiraSoapService service;

    /**
     * security token is used by the server to associate SOAP invocations with the user.
     */
    private final String token;

    private final String projectKey;

    public JiraSession( JiraSoapService service, String token, String projectKey )
    {
        this.service = service;
        this.token = token;
        this.projectKey = projectKey;
    }

    public RemoteIssue createIssue( RemoteIssue remoteIssue )
        throws RemotePermissionException, RemoteValidationException, RemoteAuthenticationException, RemoteException,
        java.rmi.RemoteException
    {
        return service.createIssue( token, remoteIssue );
    }

    public boolean addBase64EncodedAttachmentsToIssue( String issueKey, String fileName, String attachmentContent )
        throws RemotePermissionException, RemoteValidationException, RemoteAuthenticationException, RemoteException,
        java.rmi.RemoteException
    {

        return service.addBase64EncodedAttachmentsToIssue( token, issueKey, new String[]{ fileName }, new String[]{
            Base64.encode( attachmentContent.getBytes() ) } );
    }

    public RemoteIssue findRemoteIssue( String issueKey )
        throws RemotePermissionException, RemoteValidationException, RemoteAuthenticationException, RemoteException,
        java.rmi.RemoteException
    {
        return service.getIssue( token, issueKey );
    }

    public void addCommentToIssue( String issueKey, String comment )
        throws RemotePermissionException, RemoteValidationException, RemoteAuthenticationException, RemoteException,
        java.rmi.RemoteException
    {
        RemoteComment remoteComment = new RemoteComment();
        remoteComment.setBody( comment );
        service.addComment( token, issueKey, remoteComment );
    }

    public List<RemoteComponent> getRemoteComponents()
        throws RemotePermissionException, RemoteValidationException, RemoteAuthenticationException, RemoteException,
        java.rmi.RemoteException
    {
        RemoteComponent[] remoteComponents = service.getComponents( token, projectKey );
        return remoteComponents == null ? Collections.<RemoteComponent>emptyList() : Arrays.asList( remoteComponents );
    }
}
