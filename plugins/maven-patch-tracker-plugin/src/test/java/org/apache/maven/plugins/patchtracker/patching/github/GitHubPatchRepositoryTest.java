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

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.patchtracker.patching.PatchRepository;
import org.apache.maven.plugins.patchtracker.patching.PatchRepositoryRequest;
import org.apache.maven.plugins.patchtracker.patching.PatchRepositoryResult;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author Olivier Lamy
 */
public class GitHubPatchRepositoryTest
    extends PlexusTestCase
{

    static class MockLogging
        implements Log
    {

        public boolean isDebugEnabled()
        {
            return true;
        }

        private void log( CharSequence charSequence )
        {
            System.out.println( charSequence );
        }

        public void debug( CharSequence charSequence )
        {
            log( charSequence );
        }

        public void debug( CharSequence charSequence, Throwable throwable )
        {
            log( charSequence );
        }

        public void debug( Throwable throwable )
        {
            throwable.printStackTrace();
        }

        public boolean isInfoEnabled()
        {
            return true;
        }

        public void info( CharSequence charSequence )
        {
            log( charSequence );
        }

        public void info( CharSequence charSequence, Throwable throwable )
        {
            log( charSequence );
        }

        public void info( Throwable throwable )
        {
            throwable.printStackTrace();
        }

        public boolean isWarnEnabled()
        {
            return true;
        }

        public void warn( CharSequence charSequence )
        {
            log( charSequence );
        }

        public void warn( CharSequence charSequence, Throwable throwable )
        {
            log( charSequence );
        }

        public void warn( Throwable throwable )
        {
            throwable.printStackTrace();
        }

        public boolean isErrorEnabled()
        {
            return true;
        }

        public void error( CharSequence charSequence )
        {
            log( charSequence );
        }

        public void error( CharSequence charSequence, Throwable throwable )
        {
            log( charSequence );
        }

        public void error( Throwable throwable )
        {
            throwable.printStackTrace();
        }
    }

    public void testGetGitHubPoolRequest()
        throws Exception
    {
        PatchRepository patchRepository = (PatchRepository) lookup( PatchRepository.class.getName(), "github" );

        // test data for https://github.com/apache/maven-surefire/pull/4
        PatchRepositoryRequest request =
            new PatchRepositoryRequest().setId( "4" ).setOrganization( "apache" ).setRepository(
                "maven-surefire" ).setUrl( "https://api.github.com" );

        PatchRepositoryResult result = patchRepository.getPatch( request, new MockLogging() );

        System.out.println( "result:" + result.toString() );

        assertEquals( "[SUREFIRE-876] avoid using Description.getTestClass()", result.getTitle() );

        assertEquals( "https://github.com/apache/maven-surefire/pull/4", result.getHtmlUrl() );

        assertEquals( "https://github.com/apache/maven-surefire/pull/4.patch", result.getPatchUrl() );

    }
}
