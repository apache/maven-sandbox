package org.apache.maven.shared.artifact.tools.testutils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.artifact.tools.components.MavenComponentAccess;
import org.apache.maven.shared.tools.easymock.MockManager;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;

public class MockedMavenComponentAccess
    extends MavenComponentAccess
{

    public MockControl settingsBuilderCtl;

    public Map layoutCtls;

    public MockControl repoFactoryCtl;

    private MockControl resolverCtl;

    private MockControl handlerManagerCtl;

    private MockControl projectBuilderCtl;

    private MockControl repositoryMetadataMgrCtl;

    public MockedMavenComponentAccess()
    {
    }

    public void setArtifactHandlerManager( ArtifactHandlerManager artifactHandlerManager )
    {
        super.setArtifactHandlerManager( artifactHandlerManager );
    }

    public void setArtifactRepositoryFactory( ArtifactRepositoryFactory artifactRepositoryFactory )
    {
        super.setArtifactRepositoryFactory( artifactRepositoryFactory );
    }

    public void setArtifactRepositoryLayoutsById( Map artifactRepositoryLayoutsById )
    {
        super.setArtifactRepositoryLayoutsById( artifactRepositoryLayoutsById );
    }

    public void setArtifactResolver( ArtifactResolver artifactResolver )
    {
        super.setArtifactResolver( artifactResolver );
    }

    public void setContainer( PlexusContainer container )
    {
        super.setContainer( container );
    }

    public void setMavenProjectBuilder( MavenProjectBuilder mavenProjectBuilder )
    {
        super.setMavenProjectBuilder( mavenProjectBuilder );
    }

    public void setSettingsBuilder( MavenSettingsBuilder settingsBuilder )
    {
        super.setSettingsBuilder( settingsBuilder );
    }

    public void expectGetSettingsBuiler( MockManager mockManager )
    {
        settingsBuilderCtl = MockControl.createControl( MavenSettingsBuilder.class );

        mockManager.add( settingsBuilderCtl );

        setSettingsBuilder( (MavenSettingsBuilder) settingsBuilderCtl.getMock() );
    }

    public void expectBuildSettings( File file, Settings settings )
    {
        MavenSettingsBuilder builder = getSettingsBuilder();

        try
        {
            builder.buildSettings( file );
        }
        catch ( IOException e )
        {
            throw new IllegalStateException( "Should never happen." );
        }
        catch ( XmlPullParserException e )
        {
            throw new IllegalStateException( "Should never happen." );
        }

        settingsBuilderCtl.setReturnValue( settings, MockControl.ZERO_OR_MORE );
    }

    public void expectGetArtifactRepositoryLayout( MockManager mockManager, String layoutId )
    {
        Map layouts = getArtifactRepositoryLayoutsById();

        if ( layouts == null )
        {
            layouts = new HashMap();
            setArtifactRepositoryLayoutsById( layouts );

            layoutCtls = new HashMap();
        }

        MockControl layoutCtl = MockControl.createControl( ArtifactRepositoryLayout.class );

        layoutCtls.put( layoutId, layoutCtl );

        mockManager.add( layoutCtl );

        layouts.put( layoutId, layoutCtl.getMock() );

    }

    public void expectGetArtifactRepositoryFactory( MockManager mm )
    {
        repoFactoryCtl = MockControl.createControl( ArtifactRepositoryFactory.class );

        mm.add( repoFactoryCtl );

        setArtifactRepositoryFactory( (ArtifactRepositoryFactory) repoFactoryCtl.getMock() );
    }

    public void expectCreateArtifactRepositoryWithBasedir( MockManager mm, String basedir )
    {
        ArtifactRepositoryFactory repoFactory = getArtifactRepositoryFactory();

        MockControl repoCtl = MockControl.createControl( ArtifactRepository.class );

        mm.add( repoCtl );

        ArtifactRepository repo = (ArtifactRepository) repoCtl.getMock();

        repo.getBasedir();
        repoCtl.setReturnValue( basedir, MockControl.ZERO_OR_MORE );

        repoFactory.createArtifactRepository( null, null, null, null, null );
        repoFactoryCtl.setMatcher( MockControl.ALWAYS_MATCHER );
        repoFactoryCtl.setReturnValue( repo, MockControl.ZERO_OR_MORE );
    }

    public void expectCreateArtifactRepositoryWithUrl( MockManager mm, String url )
    {
        ArtifactRepositoryFactory repoFactory = getArtifactRepositoryFactory();

        MockControl repoCtl = MockControl.createControl( ArtifactRepository.class );

        ArtifactRepository repo = (ArtifactRepository) repoCtl.getMock();

        repo.getUrl();
        repoCtl.setReturnValue( url, MockControl.ZERO_OR_MORE );

        mm.add( repoCtl );

        repoFactory.createArtifactRepository( null, null, null, null, null );
        repoFactoryCtl.setMatcher( MockControl.ALWAYS_MATCHER );
        repoFactoryCtl.setReturnValue( repoCtl.getMock(), MockControl.ZERO_OR_MORE );
    }

    public void expectCreateDefaultArtifactRepositoryWithIdNameLocationAndLayout( String id, String name,
                                                                                  String location,
                                                                                  ArtifactRepositoryLayout layout )
    {
        DefaultArtifactRepository repo = new DefaultArtifactRepository( id, location, layout );

        repo.setName( name );

        ArtifactRepositoryFactory repoFactory = getArtifactRepositoryFactory();
        repoFactory.createArtifactRepository( null, null, null, null, null );

        repoFactoryCtl.setMatcher( MockControl.ALWAYS_MATCHER );
        repoFactoryCtl.setReturnValue( repo, MockControl.ZERO_OR_MORE );
    }

    public void expectCreateArtifactRepositoryWithIdUrlLayoutAndPolicies( MockManager mm, final String id,
                                                                          final String url, final String layoutId,
                                                                          final boolean snapshots,
                                                                          final boolean releases )
        throws InvalidConfigurationException
    {
        ArtifactRepositoryFactory repoFactory = getArtifactRepositoryFactory();
        final ArtifactRepositoryLayout layout = getArtifactRepositoryLayout( layoutId );

        repoFactory.createArtifactRepository( id, url, layout, null, null );

        repoFactoryCtl.setMatcher( new ArgumentsMatcher()
        {

            public boolean matches( Object[] expected, Object[] actual )
            {
                return id.equals( actual[0] ) && url.equals( actual[1] ) && layout.equals( actual[2] )
                    && ( (ArtifactRepositoryPolicy) actual[3] ).isEnabled() == snapshots
                    && ( (ArtifactRepositoryPolicy) actual[4] ).isEnabled() == releases;
            }

            public String toString( Object[] arguments )
            {
                return "Matcher: {" + id + " == " + arguments[0] + "; " + url + " == " + arguments[1] + "; " + layout
                    + " == " + arguments[2] + "; " + snapshots + " == " + arguments[3] + "; " + releases + " == "
                    + arguments[4] + "}";
            }

        } );

        MockControl repoCtl = MockControl.createControl( ArtifactRepository.class );
        ArtifactRepository repo = (ArtifactRepository) repoCtl.getMock();

        mm.add( repoCtl );

        repoFactoryCtl.setReturnValue( repo, MockControl.ZERO_OR_MORE );
    }

    public void expectGetArtifactResolver( MockManager mm )
    {
        resolverCtl = MockControl.createControl( ArtifactResolver.class );

        mm.add( resolverCtl );

        setArtifactResolver( (ArtifactResolver) resolverCtl.getMock() );
    }

    public void expectGetArtifactHandlerManagerWithHandlerForTypeWithClassifier( MockManager mm, String type,
                                                                                 String classifier )
    {
        handlerManagerCtl = MockControl.createControl( ArtifactHandlerManager.class );

        mm.add( handlerManagerCtl );

        ArtifactHandlerManager mgr = (ArtifactHandlerManager) handlerManagerCtl.getMock();

        setArtifactHandlerManager( mgr );

        MockControl handlerCtl = MockControl.createControl( ArtifactHandler.class );

        mm.add( handlerCtl );

        ArtifactHandler handler = (ArtifactHandler) handlerCtl.getMock();

        handler.getClassifier();
        handlerCtl.setReturnValue( classifier, MockControl.ZERO_OR_MORE );

        mgr.getArtifactHandler( type );
        handlerManagerCtl.setReturnValue( handler, MockControl.ZERO_OR_MORE );
    }

    public void expectGetProjectBuilder( MockManager mm )
    {
        projectBuilderCtl = MockControl.createControl( MavenProjectBuilder.class );

        mm.add( projectBuilderCtl );

        setMavenProjectBuilder( (MavenProjectBuilder) projectBuilderCtl.getMock() );
    }

    public void expectResolve( Artifact artifact, List remoteRepos, ArtifactRepository localRepo )
    {
        ArtifactResolver resolver = getArtifactResolver();

        try
        {
            resolver.resolve( artifact, remoteRepos, localRepo );
            resolverCtl.setMatcher( new ArgumentsMatcher()
            {

                public boolean matches( Object[] expected, Object[] actual )
                {
                    Artifact expectedArtifact = (Artifact) expected[0];
                    List expectedRemoteRepos = (List) expected[1];
                    ArtifactRepository expectedLocalRepo = (ArtifactRepository) expected[2];

                    Artifact actualArtifact = (Artifact) actual[0];
                    List actualRemoteRepos = (List) actual[1];
                    ArtifactRepository actualLocalRepo = (ArtifactRepository) actual[2];

                    boolean artifactMatch = expectedArtifact.getId().equals( actualArtifact.getId() );

                    boolean remoteRepoMatch = true;

                    if ( expectedRemoteRepos == actualRemoteRepos )
                    {
                        // matched.
                    }
                    else if ( expectedRemoteRepos.size() != actualRemoteRepos.size() )
                    {
                        remoteRepoMatch = false;
                    }
                    else
                    {
                        for ( int i = 0; i < expectedRemoteRepos.size(); i++ )
                        {
                            ArtifactRepository expectedRepo = (ArtifactRepository) expectedRemoteRepos.get( i );
                            ArtifactRepository actualRepo = (ArtifactRepository) actualRemoteRepos.get( i );

                            if ( expectedRepo.getUrl().equals( actualRepo.getUrl() ) )
                            {
                                remoteRepoMatch = false;
                                break;
                            }
                        }
                    }

                    boolean localRepoMatch = expectedLocalRepo.getBasedir().equals( actualLocalRepo.getBasedir() );

                    return artifactMatch && remoteRepoMatch && localRepoMatch;
                }

                public String toString( Object[] arguments )
                {
                    return "resolve matcher: {" + ( (Artifact) arguments[0] ).getId() + ", remoteRepos: "
                        + arguments[1] + ", localRepo: " + arguments[2];
                }

            } );
        }
        catch ( ArtifactResolutionException e )
        {
            Assert.fail( "should not happen." );
        }
        catch ( ArtifactNotFoundException e )
        {
            Assert.fail( "should not happen." );
        }
    }

    public void expectBuildFromRepository( Artifact artifact, List remoteRepos, ArtifactRepository localRepo,
                                           MavenProject project )
    {
        MavenProjectBuilder builder = getMavenProjectBuilder();

        try
        {
            builder.buildFromRepository( artifact, remoteRepos, localRepo );
            projectBuilderCtl.setMatcher( new ArgumentsMatcher()
            {

                public boolean matches( Object[] expected, Object[] actual )
                {
                    Artifact expectedArtifact = (Artifact) expected[0];
                    ArtifactRepository expectedLocalRepo = (ArtifactRepository) expected[2];

                    Artifact actualArtifact = (Artifact) actual[0];
                    ArtifactRepository actualLocalRepo = (ArtifactRepository) actual[2];

                    boolean artifactMatch = expectedArtifact.getId().equals( actualArtifact.getId() );

                    boolean localRepoMatch = expectedLocalRepo.getBasedir().equals( actualLocalRepo.getBasedir() );

                    return artifactMatch && localRepoMatch;
                }

                public String toString( Object[] arguments )
                {
                    return "build matcher: {" + ( (Artifact) arguments[0] ).getId() + ", localRepo: " + arguments[2];
                }

            } );
            
            projectBuilderCtl.setReturnValue( project, MockControl.ZERO_OR_MORE );
        }
        catch ( ProjectBuildingException e )
        {
            Assert.fail( "should not happen." );
        }
    }

    public void expectGetRepositoryMetadataManager( MockManager mm )
    {
        repositoryMetadataMgrCtl = MockControl.createControl( RepositoryMetadataManager.class );
        
        mm.add( repositoryMetadataMgrCtl );
        
        RepositoryMetadataManager rmm = (RepositoryMetadataManager) repositoryMetadataMgrCtl.getMock();
        
        setRepositoryMetadataManager( rmm );
    }

}
