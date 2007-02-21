package org.apache.maven.shared.artifact.tools.components;

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.apache.maven.shared.tools.easymock.MockManager;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.embed.PlexusEmbedder;
import org.easymock.MockControl;

public class MavenComponentAccessTest
    extends TestCase
{

    public void testShouldConstructWithAllRequiredComponentInstancesAndLayoutMap()
        throws InvalidConfigurationException
    {
        //        public MavenComponentAccess( MavenSettingsBuilder settingsBuilder, ArtifactResolver artifactResolver,
        //                                     ArtifactHandlerManager artifactHandlerManager,
        //                                     ArtifactRepositoryFactory artifactRepositoryFactory,
        //                                     MavenProjectBuilder mavenProjectBuilder, Map artifactRepositoryLayoutsById )

        MockManager mm = new MockManager();

        MockControl msbControl = MockControl.createControl( MavenSettingsBuilder.class );
        mm.add( msbControl );

        MavenSettingsBuilder msb = (MavenSettingsBuilder) msbControl.getMock();

        MockControl arControl = MockControl.createControl( ArtifactResolver.class );
        mm.add( arControl );

        ArtifactResolver ar = (ArtifactResolver) arControl.getMock();

        MockControl ahmControl = MockControl.createControl( ArtifactHandlerManager.class );
        mm.add( ahmControl );

        ArtifactHandlerManager ahm = (ArtifactHandlerManager) ahmControl.getMock();

        MockControl arfControl = MockControl.createControl( ArtifactRepositoryFactory.class );
        mm.add( arfControl );

        ArtifactRepositoryFactory arf = (ArtifactRepositoryFactory) arfControl.getMock();

        MockControl mpbControl = MockControl.createControl( MavenProjectBuilder.class );
        mm.add( mpbControl );

        MavenProjectBuilder mpb = (MavenProjectBuilder) mpbControl.getMock();

        MockControl arlControl = MockControl.createControl( ArtifactRepositoryLayout.class );
        mm.add( arlControl );

        ArtifactRepositoryLayout arl = (ArtifactRepositoryLayout) arlControl.getMock();

        Map layouts = Collections.singletonMap( "test", arl );

        MavenComponentAccess mca = new MavenComponentAccess( msb, ar, ahm, arf, mpb, null, layouts );

        assertSame( arl, mca.getArtifactRepositoryLayout( "test" ) );
    }

    public void testShouldCreateSelfContainedAndLookupDefaultLayout()
        throws ComponentAccessException, InvalidConfigurationException
    {
        MavenComponentAccess mca = MavenComponentAccess.createSelfContainedInstance();

        assertNotNull( mca.getArtifactRepositoryLayout( "default" ) );
    }

    public void testShouldLookupInstanceFromExistingContainerAndFindDefaultLayout()
        throws PlexusContainerException, PlexusConfigurationResourceException, ComponentLookupException,
        InvalidConfigurationException
    {
        PlexusEmbedder embedder = new Embedder();
        embedder.start();

        MavenComponentAccess mca = (MavenComponentAccess) embedder.lookup( MavenComponentAccess.ROLE );

        assertNotNull( mca.getArtifactRepositoryLayout( "default" ) );
    }

}
