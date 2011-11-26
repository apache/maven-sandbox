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
public class PatchRepositoryResult
    implements Serializable
{
    private String title;

    private String htmlUrl;

    private String patchUrl;

    private String description;

    private String patchContent;

    public PatchRepositoryResult()
    {
        // no op
    }

    public String getTitle()
    {
        return title;
    }

    public PatchRepositoryResult setTitle( String title )
    {
        this.title = title;
        return this;
    }

    public String getHtmlUrl()
    {
        return htmlUrl;
    }

    public PatchRepositoryResult setHtmlUrl( String htmlUrl )
    {
        this.htmlUrl = htmlUrl;
        return this;
    }

    public String getPatchUrl()
    {
        return patchUrl;
    }

    public PatchRepositoryResult setPatchUrl( String patchUrl )
    {
        this.patchUrl = patchUrl;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public PatchRepositoryResult setDescription( String description )
    {
        this.description = description;
        return this;
    }

    public String getPatchContent()
    {
        return patchContent;
    }

    public PatchRepositoryResult setPatchContent( String patchContent )
    {
        this.patchContent = patchContent;
        return this;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( "PatchRepositoryResult" );
        sb.append( "{title='" ).append( title ).append( '\'' );
        sb.append( ", htmlUrl='" ).append( htmlUrl ).append( '\'' );
        sb.append( ", patchUrl='" ).append( patchUrl ).append( '\'' );
        sb.append( ", description='" ).append( description ).append( '\'' );
        sb.append( ", patchContent='" ).append( patchContent ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
