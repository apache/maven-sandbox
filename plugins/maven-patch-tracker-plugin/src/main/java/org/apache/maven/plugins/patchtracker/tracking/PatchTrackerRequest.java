package org.apache.maven.plugins.patchtracker.tracking;
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

/**
 * @author Olivier Lamy
 */
public class PatchTrackerRequest
{
    private String url;

    private String userName;

    private String password;

    private String patchContent;

    private String summary;

    private String description;

    /**
     * used for updating a patch in the patch tracker, (an issue id for jira: MNG-1234)
     */
    private String patchId;

    /**
     * mandatory for jira: 1 for bug
     */
    private String patchType = "1";

    public PatchTrackerRequest()
    {
        // no op
    }

    public String getUrl()
    {
        return url;
    }

    public PatchTrackerRequest setUrl( String url )
    {
        this.url = url;
        return this;
    }

    public String getUserName()
    {
        return userName;
    }

    public PatchTrackerRequest setUserName( String userName )
    {
        this.userName = userName;
        return this;
    }

    public String getPassword()
    {
        return password;
    }

    public PatchTrackerRequest setPassword( String password )
    {
        this.password = password;
        return this;
    }

    public String getPatchContent()
    {
        return patchContent;
    }

    public PatchTrackerRequest setPatchContent( String patchContent )
    {
        this.patchContent = patchContent;
        return this;
    }

    public String getSummary()
    {
        return summary;
    }

    public PatchTrackerRequest setSummary( String summary )
    {
        this.summary = summary;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public PatchTrackerRequest setDescription( String description )
    {
        this.description = description;
        return this;
    }

    public String getPatchId()
    {
        return patchId;
    }

    public PatchTrackerRequest setPatchId( String patchId )
    {
        this.patchId = patchId;
        return this;
    }

    public String getPatchType()
    {
        return patchType;
    }

    public PatchTrackerRequest setPatchType( String patchType )
    {
        this.patchType = patchType;
        return this;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( "PatchTrackerRequest" );
        sb.append( "{url='" ).append( url ).append( '\'' );
        sb.append( ", userName='" ).append( userName ).append( '\'' );
        sb.append( ", password='" ).append( password ).append( '\'' );
        sb.append( ", patchContent='" ).append( patchContent ).append( '\'' );
        sb.append( ", summary='" ).append( summary ).append( '\'' );
        sb.append( ", description='" ).append( description ).append( '\'' );
        sb.append( ", patchId='" ).append( patchId ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
