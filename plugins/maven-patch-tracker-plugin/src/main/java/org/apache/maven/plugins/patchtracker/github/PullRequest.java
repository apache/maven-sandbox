package org.apache.maven.plugins.patchtracker.github;
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
public class PullRequest
    implements Serializable
{
    /**
     * json format
     * <p/>
     * curl -v https://api.github.com/repos/apache/directmemory/pulls/1
     * <p/>
     * {
     * "merged_by": null,
     * "merged": false,
     * "title": "test wonderful patch ",
     * "head": {
     * "repo": {
     * "watchers": 1,
     * "forks": 0,
     * "pushed_at": "2011-11-26T15:17:32Z",
     * "svn_url": "https://svn.github.com/olamy/directmemory",
     * "description": "Mirror of Apache DirectMemory",
     * "created_at": "2011-11-26T09:39:35Z",
     * "url": "https://api.github.com/repos/olamy/directmemory",
     * "fork": true,
     * "language": "Java",
     * "git_url": "git://github.com/olamy/directmemory.git",
     * "clone_url": "https://github.com/olamy/directmemory.git",
     * "open_issues": 0,
     * "private": false,
     * "html_url": "https://github.com/olamy/directmemory",
     * "homepage": null,
     * "size": 112,
     * "master_branch": "trunk",
     * "updated_at": "2011-11-26T15:17:32Z",
     * "owner": {
     * "avatar_url": "https://secure.gravatar.com/avatar/bab29f762bea8e578505424443d8cd41?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-140.png",
     * "url": "https://api.github.com/users/olamy",
     * "login": "olamy",
     * "gravatar_id": "bab29f762bea8e578505424443d8cd41",
     * "id": 19728
     * },
     * "name": "directmemory",
     * "id": 2854963,
     * "ssh_url": "git@github.com:olamy/directmemory.git"
     * },
     * "user": {
     * "avatar_url": "https://secure.gravatar.com/avatar/bab29f762bea8e578505424443d8cd41?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-140.png",
     * "url": "https://api.github.com/users/olamy",
     * "login": "olamy",
     * "gravatar_id": "bab29f762bea8e578505424443d8cd41",
     * "id": 19728
     * },
     * "ref": "trunk",
     * "label": "olamy:trunk",
     * "sha": "73d403199b415108a6f2a72c95b9ec684e6f1dc1"
     * },
     * "_links": {
     * "html": {
     * "href": "https://github.com/apache/directmemory/pull/1"
     * },
     * "review_comments": {
     * "href": "https://api.github.com/repos/apache/directmemory/pulls/1/comments"
     * },
     * "self": {
     * "href": "https://api.github.com/repos/apache/directmemory/pulls/1"
     * },
     * "comments": {
     * "href": "https://api.github.com/repos/apache/directmemory/issues/1/comments"
     * }
     * },
     * "merged_at": null,
     * "created_at": "2011-11-26T14:59:52Z",
     * "state": "open",
     * "url": "https://api.github.com/repos/apache/directmemory/pulls/1",
     * "commits": 3,
     * "deletions": 0,
     * "changed_files": 2,
     * "review_comments": 0,
     * "closed_at": null,
     * "user": {
     * "avatar_url": "https://secure.gravatar.com/avatar/bab29f762bea8e578505424443d8cd41?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-140.png",
     * "url": "https://api.github.com/users/olamy",
     * "login": "olamy",
     * "gravatar_id": "bab29f762bea8e578505424443d8cd41",
     * "id": 19728
     * },
     * "base": {
     * "repo": {
     * "watchers": 3,
     * "forks": 2,
     * "pushed_at": "2011-11-03T00:02:25Z",
     * "svn_url": "https://svn.github.com/apache/directmemory",
     * "description": "Mirror of Apache DirectMemory",
     * "created_at": "2011-10-15T07:00:08Z",
     * "url": "https://api.github.com/repos/apache/directmemory",
     * "fork": false,
     * "language": "Java",
     * "git_url": "git://github.com/apache/directmemory.git",
     * "clone_url": "https://github.com/apache/directmemory.git",
     * "open_issues": 0,
     * "private": false,
     * "html_url": "https://github.com/apache/directmemory",
     * "homepage": null,
     * "size": 3076,
     * "master_branch": "trunk",
     * "updated_at": "2011-11-26T09:39:35Z",
     * "owner": {
     * "avatar_url": "https://secure.gravatar.com/avatar/a676c0bf448fcd49f588249ead719b4c?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-140.png",
     * "url": "https://api.github.com/users/apache",
     * "login": "apache",
     * "gravatar_id": "a676c0bf448fcd49f588249ead719b4c",
     * "id": 47359
     * },
     * "name": "directmemory",
     * "id": 2580771,
     * "ssh_url": "git@github.com:apache/directmemory.git"
     * },
     * "user": {
     * "avatar_url": "https://secure.gravatar.com/avatar/a676c0bf448fcd49f588249ead719b4c?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-140.png",
     * "url": "https://api.github.com/users/apache",
     * "login": "apache",
     * "gravatar_id": "a676c0bf448fcd49f588249ead719b4c",
     * "id": 47359
     * },
     * "ref": "trunk",
     * "label": "apache:trunk",
     * "sha": "f9ddc283dbc4f391a0876b5bd36453ac7e563b79"
     * },
     * "html_url": "https://github.com/apache/directmemory/pull/1",
     * "updated_at": "2011-11-26T15:17:33Z",
     * "number": 1,
     * "issue_url": "https://github.com/apache/directmemory/issues/1",
     * "mergeable": true,
     * "additions": 6,
     * "patch_url": "https://github.com/apache/directmemory/pull/1.patch",
     * "id": 530496,
     * "comments": 0,
     * "body": "test email works to directmemory ml .",
     * "diff_url": "https://github.com/apache/directmemory/pull/1.diff"
     * }
     */

    private String title;

    private String html_url;

    private String patch_url;

    private String body;

    public PullRequest()
    {
        // no op
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getHtml_url()
    {
        return html_url;
    }

    public void setHtml_url( String html_url )
    {
        this.html_url = html_url;
    }

    public String getPatch_url()
    {
        return patch_url;
    }

    public void setPatch_url( String patch_url )
    {
        this.patch_url = patch_url;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody( String body )
    {
        this.body = body;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( "PullRequest" );
        sb.append( "{title='" ).append( title ).append( '\'' );
        sb.append( ", html_url='" ).append( html_url ).append( '\'' );
        sb.append( ", patch_url='" ).append( patch_url ).append( '\'' );
        sb.append( ", body='" ).append( body ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
