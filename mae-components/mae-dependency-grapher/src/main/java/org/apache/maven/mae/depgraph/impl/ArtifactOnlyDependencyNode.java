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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.version.Version;
import org.sonatype.aether.version.VersionConstraint;

public final class ArtifactOnlyDependencyNode
    implements DependencyNode
{

    private final Dependency dep;

    private String preVersion;

    private String preScope;

    private final Map<Object, Object> data = new LinkedHashMap<Object, Object>();

    private String requestContext = "project";

    public ArtifactOnlyDependencyNode( final Artifact artifact )
    {
        dep = new Dependency( artifact, null );
    }

    @Override
    public List<DependencyNode> getChildren()
    {
        return Collections.emptyList();
    }

    @Override
    public Dependency getDependency()
    {
        return dep;
    }

    @Override
    public synchronized void setArtifact( final Artifact artifact )
    {
        if ( artifact == null )
        {
            return;
        }
        else if ( preVersion == null )
        {
            preVersion = dep.getArtifact().getVersion();
        }

        dep.setArtifact( artifact );
    }

    @Override
    public List<Artifact> getRelocations()
    {
        return Collections.emptyList();
    }

    @Override
    public Collection<Artifact> getAliases()
    {
        return Collections.emptySet();
    }

    @Override
    public VersionConstraint getVersionConstraint()
    {
        return null;
    }

    @Override
    public Version getVersion()
    {
        return null;
    }

    @Override
    public synchronized void setScope( final String scope )
    {
        if ( scope == null )
        {
            return;
        }

        if ( preScope == null )
        {
            preScope = dep.getScope();
        }

        dep.setScope( scope );
    }

    @Override
    public synchronized String getPremanagedVersion()
    {
        return preVersion == null ? dep.getArtifact().getVersion() : preVersion;
    }

    @Override
    public synchronized String getPremanagedScope()
    {
        return preScope == null ? dep.getScope() : preScope;
    }

    @Override
    public List<RemoteRepository> getRepositories()
    {
        return Collections.emptyList();
    }

    @Override
    public String getRequestContext()
    {
        return requestContext;
    }

    @Override
    public void setRequestContext( final String requestContext )
    {
        this.requestContext = requestContext;
    }

    @Override
    public Map<Object, Object> getData()
    {
        return data;
    }

    @Override
    public void setData( final Object key, final Object value )
    {
        data.put( key, value );
    }

    @Override
    public boolean accept( final DependencyVisitor visitor )
    {
        return false;
    }

}
