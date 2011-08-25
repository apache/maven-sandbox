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

package org.apache.maven.mae.depgraph.impl;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.depgraph.DepGraphLoader;
import org.apache.maven.mae.depgraph.DependencyGraph;
import org.apache.maven.mae.project.ProjectLoader;
import org.apache.maven.mae.project.session.ProjectToolsSession;
import org.apache.maven.mae.project.session.SessionInitializer;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositorySystemSession;

@Component( role = DepGraphLoader.class )
public class DefaultDepGraphLoader
    implements DepGraphLoader
{

    @SuppressWarnings( "unused" )
    private static final Logger LOGGER = Logger.getLogger( DefaultDepGraphLoader.class );

    @Requirement
    private DependencyGraphResolver dependencyGraphResolver;

    @Requirement
    private SessionInitializer sessionInitializer;

    @Requirement
    private ProjectLoader projectLoader;

    @Override
    public DependencyGraph loadProjectDependencyGraph( final File rootPom,
                                                       final ProjectToolsSession session,
                                                       final boolean includeModuleProjects )
        throws MAEException
    {
        sessionInitializer.initializeSessionComponents( session );

        List<MavenProject> projects;
        if ( includeModuleProjects )
        {
            projects =
                projectLoader.buildReactorProjectInstances( session, includeModuleProjects, rootPom );
        }
        else
        {
            projects =
                Collections.singletonList( projectLoader.buildProjectInstance( rootPom, session ) );
        }

        final DependencyGraph depGraph =
            dependencyGraphResolver.accumulateGraph( projects,
                                                     session.getRepositorySystemSession(), session );
        session.setState( depGraph );

        return depGraph;
    }

    @Override
    public DependencyGraph resolveProjectDependencies( final File rootPom,
                                                       final ProjectToolsSession session,
                                                       final boolean includeModuleProjects )
        throws MAEException
    {
        sessionInitializer.initializeSessionComponents( session );

        List<MavenProject> projects;
        if ( includeModuleProjects )
        {
            projects =
                projectLoader.buildReactorProjectInstances( session, includeModuleProjects, rootPom );
        }
        else
        {
            projects =
                Collections.singletonList( projectLoader.buildProjectInstance( rootPom, session ) );
        }

        final RepositorySystemSession rss = session.getRepositorySystemSession();
        final DependencyGraph depGraph =
            dependencyGraphResolver.accumulateGraph( projects, rss, session );
        dependencyGraphResolver.resolveGraph( depGraph, projects, rss, session );

        session.setState( depGraph );

        return depGraph;
    }

}
