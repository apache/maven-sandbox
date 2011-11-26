package org.apache.maven.plugins.patchtracker.patching;
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

import java.io.Serializable;

/**
 * @author Olivier Lamy
 */
public class PatchRepositoryRequest
    implements Serializable
{
    /**
     * for github user/organization  : github.com/apache use apache
     */
    protected String organization;

    /**
     * for github repo  : github.com/apache/maven-3 use maven-3
     */
    protected String repository;

    /**
     * for github pull request id
     *
     * @parameter expression="${patch.pullrequest.id}" default-value=""
     */
    protected String id;


    /**
     * for github api url https://api.github.com/repos
     */
    protected String url;

    public PatchRepositoryRequest()
    {
        // no op
    }

    public String getOrganization()
    {
        return organization;
    }

    public PatchRepositoryRequest setOrganization( String organization )
    {
        this.organization = organization;
        return this;
    }

    public String getRepository()
    {
        return repository;
    }

    public PatchRepositoryRequest setRepository( String repository )
    {
        this.repository = repository;
        return this;
    }

    public String getId()
    {
        return id;
    }

    public PatchRepositoryRequest setId( String id )
    {
        this.id = id;
        return this;
    }

    public String getUrl()
    {
        return url;
    }

    public PatchRepositoryRequest setUrl( String url )
    {
        this.url = url;
        return this;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( "PatchRepositoryRequest" );
        sb.append( "{organization='" ).append( organization ).append( '\'' );
        sb.append( ", repository='" ).append( repository ).append( '\'' );
        sb.append( ", id='" ).append( id ).append( '\'' );
        sb.append( ", url='" ).append( url ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
