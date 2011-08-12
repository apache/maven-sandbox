/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.mae.project.session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;

public class SessionWorkspaceReader
    implements WorkspaceReader
{

    private static final WorkspaceRepository REPO = new WorkspaceRepository();

    private final ProjectToolsSession session;

    public SessionWorkspaceReader( final ProjectToolsSession session )
    {
        this.session = session;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sonatype.aether.repository.WorkspaceReader#getRepository()
     */
    @Override
    public WorkspaceRepository getRepository()
    {
        return REPO;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sonatype.aether.repository.WorkspaceReader#findArtifact(org.sonatype.aether.artifact.Artifact)
     */
    @Override
    public File findArtifact( final Artifact artifact )
    {
        return session.getReactorPom( artifact );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.sonatype.aether.repository.WorkspaceReader#findVersions(org.sonatype.aether.artifact.Artifact)
     */
    @Override
    public List<String> findVersions( final Artifact artifact )
    {
        final List<String> versions = new ArrayList<String>( 1 );

        final MavenProject project = session.getReactorProject( artifact );
        if ( project != null )
        {
            versions.add( project.getVersion() );
        }

        return versions;
    }

}
