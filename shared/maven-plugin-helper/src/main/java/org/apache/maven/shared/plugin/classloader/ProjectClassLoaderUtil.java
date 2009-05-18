package org.apache.maven.shared.plugin.classloader;

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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Helper class to build a ClassLoader from MavenProject artifacts and dependencies.
 * 
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public final class ProjectClassLoaderUtil
{
    private ProjectClassLoaderUtil()
    {
        // utility class
    }

    /**
     * Create a ClassLoader for the project with the specified scope, optionnaly including sources
     * 
     * @throws MalformedURLException
     */
    public static ClassLoader buildProjectClassLoader( MavenProject project, String scope, boolean includeSources )
        throws MojoExecutionException
    {
        List urls = doBuildProjectClasspath( project, scope, includeSources );
        URL[] classpath = (URL[]) urls.toArray( new URL[urls.size()] );
        return new URLClassLoader( classpath );
    }

    /**
     * Create a ClassLoader for the project with the specified scope, optionnaly including sources, and include the
     * specified artifacts (typically from <code>${plugin.artifacts}</code>)
     * 
     * @throws MalformedURLException
     */
    public static ClassLoader buildProjectClassLoaderWithArtifacts( MavenProject project, String scope,
                                                                    boolean includeSources,
                                                                    List additionalClassPathArtifacts )
        throws MojoExecutionException
    {
        List urls = doBuildProjectClasspath( project, scope, includeSources );
        for ( Iterator iterator = additionalClassPathArtifacts.iterator(); iterator.hasNext(); )
        {
            Artifact artifact = (Artifact) iterator.next();
            try
            {
                urls.add( artifact.getFile().toURI().toURL() );
            }
            catch ( MalformedURLException e )
            {
                throw new MojoExecutionException( "Failed to resolve artifact " + artifact + " as URL", e );
            }
        }
        URL[] classpath = (URL[]) urls.toArray( new URL[urls.size()] );
        return new URLClassLoader( classpath );
    }

    /**
     * Create a ClassLoader for the project with the specified scope, optionnaly including sources, and include the
     * specified classpath elements.
     * 
     * @throws MalformedURLException
     */
    public static ClassLoader buildProjectClassLoaderWithElements( MavenProject project, String scope,
                                                                   boolean includeSources,
                                                                   List additionalClassPathElements )
        throws MojoExecutionException
    {
        List urls = doBuildProjectClasspath( project, scope, includeSources );
        for ( Iterator iterator = additionalClassPathElements.iterator(); iterator.hasNext(); )
        {
            String path = (String) iterator.next();
            try
            {
                urls.add( new File( path ).toURI().toURL() );
            }
            catch ( MalformedURLException e )
            {
                throw new MojoExecutionException( "Failed to resolve path " + path + " as URL", e );
            }
        }
        URL[] classpath = (URL[]) urls.toArray( new URL[urls.size()] );
        return new URLClassLoader( classpath );
    }

    private static List doBuildProjectClasspath( MavenProject project, String scope, boolean includeSources )
        throws MojoExecutionException
    {
        List urls;
        try
        {
            urls = buildProjectClasspath( project, scope, includeSources );
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( "Failed to resolve project dependencies as URL", e );
        }
        return urls;
    }

    /**
     * @return Lis<URL> equivalent to the project Classpath
     * @throws MalformedURLException
     */
    private static List buildProjectClasspath( MavenProject project, String scope, boolean includeSources )
        throws MalformedURLException
    {
        List urls = new ArrayList();

        File outputDirectory = new File( project.getBuild().getOutputDirectory() );
        urls.add( outputDirectory.toURI().toURL() );
        if ( includeSources )
        {
            List sourceRoots = project.getCompileSourceRoots();
            for ( Iterator iterator = sourceRoots.iterator(); iterator.hasNext(); )
            {
                String sourceRoot = (String) iterator.next();
                urls.add( new File( sourceRoot ).toURI().toURL() );
            }
        }

        if ( scope.equals( Artifact.SCOPE_TEST ) )
        {
            File testOutputDirectory = new File( project.getBuild().getTestOutputDirectory() );
            urls.add( testOutputDirectory.toURI().toURL() );
            if ( includeSources )
            {
                List testSourceRoots = project.getTestCompileSourceRoots();
                for ( Iterator iterator = testSourceRoots.iterator(); iterator.hasNext(); )
                {
                    String testSourceRoot = (String) iterator.next();
                    urls.add( new File( testSourceRoot ).toURI().toURL() );
                }
            }
        }

        List artifacts = getArtifactsByScope( project, scope );
        for ( Iterator iterator = artifacts.iterator(); iterator.hasNext(); )
        {
            Artifact artifact = (Artifact) iterator.next();
            if ( !artifact.isResolved() )
            {
                throw new IllegalStateException( "Artifact is not resolved. \n"
                    + "Plugin must declare @requiresDependencyResolution " + scope );
            }
            urls.add( artifact.getFile().toURI().toURL() );
        }

        return urls;
    }

    public static List getArtifactsByScope( MavenProject project, String scope )
    {
        List artifacts;
        if ( scope.equals( Artifact.SCOPE_COMPILE ) )
        {
            artifacts = project.getCompileArtifacts();
        }
        else if ( scope.equals( Artifact.SCOPE_RUNTIME ) )
        {
            artifacts = project.getRuntimeArtifacts();
        }
        else if ( scope.equals( Artifact.SCOPE_TEST ) )
        {
            artifacts = project.getTestArtifacts();
        }
        else
        {
            throw new IllegalArgumentException( "Usuported scope" + scope );
        }
        return artifacts;
    }
}
