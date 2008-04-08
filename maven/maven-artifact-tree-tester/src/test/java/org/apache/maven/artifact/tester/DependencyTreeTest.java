package org.apache.maven.artifact.tester;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.DefaultMavenProjectBuilder;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.filter.AncestorOrSelfDependencyNodeFilter;
import org.apache.maven.shared.dependency.tree.filter.DependencyNodeFilter;
import org.apache.maven.shared.dependency.tree.filter.StateDependencyNodeFilter;
import org.apache.maven.shared.dependency.tree.traversal.BuildingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.tree.traversal.CollectingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;
import org.apache.maven.shared.dependency.tree.traversal.FilteringDependencyNodeVisitor;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DependencyTreeTest
    extends PlexusTestCase
{
    private ArtifactRepositoryFactory artifactRepositoryFactory;

    private File repositoryLocation;

    private File generatedRepositoryLocation;

    private File localRepository;

    private DependencyTreeBuilder dependencyTreeBuilder;

    private ArtifactFactory artifactFactory;

    private ArtifactMetadataSource artifactMetadataSource;

    private ArtifactCollector artifactCollector;

    private MavenProjectBuilder projectBuilder;

    private Field rawProjectCacheField;

    private Field processedProjectCacheField;

    private ArtifactRepository localArtifactRepository;

    private DefaultRepositoryLayout layout;

    public void setUp()
        throws Exception
    {
        super.setUp();

        artifactRepositoryFactory = (ArtifactRepositoryFactory) lookup( ArtifactRepositoryFactory.class );

        dependencyTreeBuilder = (DependencyTreeBuilder) lookup( DependencyTreeBuilder.class );

        artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.class );

        artifactMetadataSource = (ArtifactMetadataSource) lookup( ArtifactMetadataSource.class, "maven" );

        artifactCollector = (ArtifactCollector) lookup( ArtifactCollector.class );

        projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.class );

        localRepository = new File( System.getProperty( "user.home" ), ".m2/repository" );

        String repositoryLocation = System.getProperty( "repositoryLocation" );
        if ( repositoryLocation == null )
        {
            throw new Exception( "Must specify the system property 'repositoryLocation'" );
        }

        this.repositoryLocation = new File( repositoryLocation );

        WagonManager wagonManager = (WagonManager) lookup( WagonManager.class.getName() );
        wagonManager.addMirror( "internal", "*", this.repositoryLocation.toURL().toExternalForm() );

        String generatedRepositoryLocation = System.getProperty( "generatedRepositoryLocation" );
        if ( generatedRepositoryLocation == null )
        {
            generatedRepositoryLocation = repositoryLocation;
        }

        this.generatedRepositoryLocation = new File( generatedRepositoryLocation );

        layout = new DefaultRepositoryLayout();

        localArtifactRepository =
            artifactRepositoryFactory.createArtifactRepository( "local", this.localRepository.toURL().toExternalForm(),
                                                                layout, null, null );
    }

    @SuppressWarnings( "unchecked" )
    public void testMatches()
        throws Exception
    {
        String includes = System.getProperty( "includes", "**/*.pom" );

        List<File> files = FileUtils.getFiles( repositoryLocation, includes, null, false );

        System.err.println( "Checking " + files.size() + " files" );

        for ( File f : files )
        {
            checkFile( f.getPath() );
        }
    }

    private void checkFile( String path )
        throws Exception
    {
        File expectedFile = new File( generatedRepositoryLocation, path + ".xml" );
        if ( !expectedFile.exists() )
        {
            return;
        }

        Dependency expected = readTree( expectedFile );

        MavenProject project =
            projectBuilder.build( new File( repositoryLocation, path ), localArtifactRepository, null, false );

        // manually flush out the cache for memory concerns and more accurate building
        flushProjectCache( projectBuilder );

        // TODO: do this for different values of new ScopeArtifactFilter( scope )
        ArtifactFilter artifactFilter = null;
        DependencyNode rootNode =
            dependencyTreeBuilder.buildDependencyTree( project, localArtifactRepository, artifactFactory,
                                                       artifactMetadataSource, artifactFilter, artifactCollector );

        Dependency actual = new Dependency();

        DependencyNodeVisitor visitor = new BuildingDependencyNodeVisitor( new DependencyCheckVisitor( actual ) );

        CollectingDependencyNodeVisitor collectingVisitor = new CollectingDependencyNodeVisitor();
        DependencyNodeVisitor firstPassVisitor =
            new FilteringDependencyNodeVisitor( collectingVisitor, StateDependencyNodeFilter.INCLUDED );
        rootNode.accept( firstPassVisitor );

        DependencyNodeFilter secondPassFilter = new AncestorOrSelfDependencyNodeFilter( collectingVisitor.getNodes() );
        visitor = new FilteringDependencyNodeVisitor( visitor, secondPassFilter );

        rootNode.accept( visitor );

        if ( !expected.equals( actual ) )
        {
            assertEquals( "Different set for: " + path, expected.getDependencySet(), actual.getDependencySet() );

            System.err.println( "Warning: tree differs but result is same for: " + path );
        }
    }

    private void flushProjectCache( MavenProjectBuilder projectBuilder )
    {
        try
        {
            if ( rawProjectCacheField == null )
            {
                rawProjectCacheField = DefaultMavenProjectBuilder.class.getDeclaredField( "rawProjectCache" );
                rawProjectCacheField.setAccessible( true );
            }

            if ( processedProjectCacheField == null )
            {
                processedProjectCacheField =
                    DefaultMavenProjectBuilder.class.getDeclaredField( "processedProjectCache" );
                processedProjectCacheField.setAccessible( true );
            }

            rawProjectCacheField.set( projectBuilder, new HashMap() );

            processedProjectCacheField.set( projectBuilder, new HashMap() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private static class DependencyCheckVisitor
        implements DependencyNodeVisitor
    {
        private Stack<Dependency> stack = new Stack<Dependency>();

        private Dependency actual;

        public DependencyCheckVisitor( Dependency actual )
        {
            this.actual = actual;
        }

        public boolean visit( DependencyNode node )
        {
            Artifact artifact = node.getArtifact();

            if ( stack.isEmpty() )
            {
                actual.groupId = artifact.getGroupId();
                actual.artifactId = artifact.getArtifactId();
                actual.version = artifact.getVersion();
                actual.scope = artifact.getScope();
                actual.type = artifact.getType();

                stack.push( actual );
            }
            else
            {
                Dependency d = new Dependency();
                d.groupId = artifact.getGroupId();
                d.artifactId = artifact.getArtifactId();
                d.version = artifact.getVersion();
                d.type = artifact.getType();
                d.scope = artifact.getScope();
                actual.dependencies.add( d );

                stack.push( actual );

                actual = d;
            }

            return true;
        }

        public boolean endVisit( DependencyNode arg0 )
        {
            actual = stack.pop();

            return true;
        }
    }

    private static Dependency readTree( File file )
        throws DocumentException
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read( file );

        Element tree = document.getRootElement();
        assertEquals( 1, tree.elements().size() );

        return readDependency( tree.element( "dependency" ) );
    }

    @SuppressWarnings( "unchecked" )
    private static Dependency readDependency( Element element )
    {
        Dependency dependency = new Dependency();
        dependency.groupId = element.element( "groupId" ).getTextTrim();
        dependency.artifactId = element.element( "artifactId" ).getTextTrim();
        dependency.version = element.element( "version" ).getTextTrim();
        dependency.type = element.element( "type" ).getTextTrim();

        Element e = element.element( "scope" );
        if ( e != null )
        {
            dependency.scope = e.getTextTrim();
        }

        e = element.element( "dependencies" );
        if ( e != null )
        {
            for ( Element d : (List<Element>) e.elements( "dependency" ) )
            {
                dependency.dependencies.add( readDependency( d ) );
            }
        }
        return dependency;
    }

    private static class Dependency
    {
        String groupId;

        String artifactId;

        String version;

        String type;

        String scope;

        List<Dependency> dependencies = new LinkedList<Dependency>();

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( artifactId == null ) ? 0 : artifactId.hashCode() );
            result = prime * result + ( ( dependencies == null ) ? 0 : dependencies.hashCode() );
            result = prime * result + ( ( groupId == null ) ? 0 : groupId.hashCode() );
            result = prime * result + ( ( scope == null ) ? 0 : scope.hashCode() );
            result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
            result = prime * result + ( ( version == null ) ? 0 : version.hashCode() );
            return result;
        }

        public Set<Dependency> getDependencySet()
        {
            Set<Dependency> set = new HashSet<Dependency>();
            addDependencies( set );
            return set;
        }

        public void addDependencies( Set<Dependency> set )
        {
            Dependency clone = new Dependency();
            clone.artifactId = artifactId;
            clone.groupId = groupId;
            clone.scope = scope;
            clone.type = type;
            clone.version = version;

            set.add( clone );

            for ( Dependency d : dependencies )
            {
                d.addDependencies( set );
            }
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            final Dependency other = (Dependency) obj;
            if ( artifactId == null )
            {
                if ( other.artifactId != null )
                    return false;
            }
            else if ( !artifactId.equals( other.artifactId ) )
                return false;
            if ( dependencies == null )
            {
                if ( other.dependencies != null )
                    return false;
            }
            else if ( !dependencies.equals( other.dependencies ) )
                return false;
            if ( groupId == null )
            {
                if ( other.groupId != null )
                    return false;
            }
            else if ( !groupId.equals( other.groupId ) )
                return false;
            if ( scope == null )
            {
                if ( other.scope != null )
                    return false;
            }
            else if ( !scope.equals( other.scope ) )
                return false;
            if ( type == null )
            {
                if ( other.type != null )
                    return false;
            }
            else if ( !type.equals( other.type ) )
                return false;
            if ( version == null )
            {
                if ( other.version != null )
                    return false;
            }
            else if ( !version.equals( other.version ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return toString( "  " );
        }

        private String toString( String indent )
        {
            String s = groupId + ":" + artifactId + ":" + version + ":" + type;
            if ( scope != null )
            {
                s += ":" + scope;
            }
            for ( Dependency d : dependencies )
            {
                s += "\n" + indent + d.toString( indent + "  " );
            }
            return s;
        }

    }
}
