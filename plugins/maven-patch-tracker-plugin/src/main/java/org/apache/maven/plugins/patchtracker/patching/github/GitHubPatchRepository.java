package org.apache.maven.plugins.patchtracker.patching.github;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.patchtracker.patching.PatchRepository;
import org.apache.maven.plugins.patchtracker.patching.PatchRepositoryException;
import org.apache.maven.plugins.patchtracker.patching.PatchRepositoryRequest;
import org.apache.maven.plugins.patchtracker.patching.PatchRepositoryResult;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * @author Olivier Lamy
 * @plexus.component role="org.apache.maven.plugins.patchtracker.patching.PatchRepository" role-hint="github"
 */
public class GitHubPatchRepository
    implements PatchRepository
{
    DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

    public PatchRepositoryResult getPatch( PatchRepositoryRequest patchRepositoryRequest, Log log )
        throws PatchRepositoryException
    {
        try
        {
            //curl -v https://api.github.com/repos/apache/directmemory/pulls/1
            String baseUrl = patchRepositoryRequest.getUrl();
            if ( StringUtils.isEmpty( baseUrl ) )
            {
                baseUrl = "https://api.github.com";
                log.info( "github api url is empty use default:" + baseUrl );
            }
            String url = patchRepositoryRequest.getUrl() + "/repos/" + patchRepositoryRequest.getOrganization() + "/"
                + patchRepositoryRequest.getRepository() + "/pulls/" + patchRepositoryRequest.getId();
            log.debug( "url" + url );

            HttpGet httpGet = new HttpGet( url );

            HttpResponse httpResponse = defaultHttpClient.execute( httpGet );

            String response = IOUtils.toString( httpResponse.getEntity().getContent() );

            log.debug( "response:" + response );

            PatchRepositoryResult patchRepositoryResult = fromJson( response );

            log.debug( "patchRepositoryResult:" + patchRepositoryResult );

            patchRepositoryResult.setPatchContent( getPatchContent( patchRepositoryResult ) );

            log.debug( "pullRequest:" + patchRepositoryResult.toString() );
            return patchRepositoryResult;
        }
        catch ( IOException e )
        {
            throw new PatchRepositoryException( e.getMessage(), e );
        }
    }

    protected String getPatchContent( PatchRepositoryResult pullRequest )
        throws IOException, ClientProtocolException
    {
        HttpGet httpGet = new HttpGet( pullRequest.getPatchUrl() );

        HttpResponse httpResponse = defaultHttpClient.execute( httpGet );

        return IOUtils.toString( httpResponse.getEntity().getContent() );
    }

    /**
     * parse json from gitbut see http://developer.github.com/v3/pulls/
     *
     * @param jsonContent
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     */
    protected PatchRepositoryResult fromJson( String jsonContent )
        throws IOException, JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();

        // we don't parse all stuff
        // se json format below or documentation
        objectMapper.configure( DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false );

        //objectMapper.

        return objectMapper.reader( GithubPatchRepositoryResult.class ).readValue( jsonContent );
    }

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

}
