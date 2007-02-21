package org.apache.maven.shared.artifact.tools.components;

import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.shared.artifact.tools.InvalidConfigurationException;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.util.Map;

/**
 * @plexus.component role="org.apache.maven.shared.artifact.tools.components.MavenComponentAccess" role-hint="default"
 * 
 * @author jdcasey
 */
public class MavenComponentAccess
    implements Contextualizable
{

    public static final String ROLE = MavenComponentAccess.class.getName();

    /**
     * @plexus.requirement
     */
    private MavenSettingsBuilder settingsBuilder;

    /**
     * @plexus.requirement
     */
    private ArtifactResolver artifactResolver;

    /**
     * @plexus.requirement
     */
    private ArtifactHandlerManager artifactHandlerManager;

    /**
     * @plexus.requirement
     */
    private ArtifactRepositoryFactory artifactRepositoryFactory;

    /**
     * @plexus.requirement
     */
    private MavenProjectBuilder mavenProjectBuilder;
    
    /**
     * @plexus.requirement
     */
    private RepositoryMetadataManager repositoryMetadataManager;

    private PlexusContainer container;

    private Map artifactRepositoryLayoutsById;

    private static MavenComponentAccess selfContainedInstance;

    // used with self-contained instance.
    private static Embedder embedder;

    public MavenComponentAccess()
    {
        //used for Plexus-based initialization.
        this.artifactRepositoryLayoutsById = null;
    }

    public MavenComponentAccess( MavenSettingsBuilder settingsBuilder, ArtifactResolver artifactResolver,
                                 ArtifactHandlerManager artifactHandlerManager,
                                 ArtifactRepositoryFactory artifactRepositoryFactory,
                                 MavenProjectBuilder mavenProjectBuilder, 
                                 RepositoryMetadataManager repositoryMetadataManager, 
                                 Map artifactRepositoryLayoutsById )
    {
        this.settingsBuilder = settingsBuilder;
        this.artifactResolver = artifactResolver;
        this.artifactHandlerManager = artifactHandlerManager;
        this.artifactRepositoryFactory = artifactRepositoryFactory;
        this.mavenProjectBuilder = mavenProjectBuilder;
        this.artifactRepositoryLayoutsById = artifactRepositoryLayoutsById;
        this.repositoryMetadataManager = repositoryMetadataManager;
    }
    
    public static void shutdown()
    {
        if ( selfContainedInstance != null )
        {
            PlexusContainer container = embedder.getContainer();
            ClassRealm containerRealm = container.getContainerRealm();
            
            embedder.stop();
            container.dispose();
            try
            {
                containerRealm.getWorld().disposeRealm( containerRealm.getId() );
            }
            catch ( NoSuchRealmException e )
            {
                // we're killing it anyway, so ignore this.
            }
        }
    }

    public static MavenComponentAccess createSelfContainedInstance()
        throws ComponentAccessException
    {
        if ( selfContainedInstance == null )
        {
            embedder = new Embedder();

            try
            {
                embedder.start();
            }
            catch ( PlexusContainerException e )
            {
                throw new ComponentAccessException( "Failed to start plexus container for maven artifact tooling.", e );
            }
//            catch ( PlexusConfigurationResourceException e )
//            {
//                throw new ComponentAccessException( "Failed to start plexus container for maven artifact tooling.", e );
//            }

            try
            {
                selfContainedInstance = (MavenComponentAccess) embedder.lookup( ROLE );
            }
            catch ( ComponentLookupException e )
            {
                throw new ComponentAccessException(
                                                    "Failed to lookup components necessary for maven artifact tooling.",
                                                    e );
            }
        }

        return selfContainedInstance;
    }

    public ArtifactRepositoryLayout getArtifactRepositoryLayout( String layoutId )
        throws InvalidConfigurationException
    {
        ArtifactRepositoryLayout layout = null;
        
        if ( artifactRepositoryLayoutsById != null )
        {

            layout = (ArtifactRepositoryLayout) artifactRepositoryLayoutsById.get( layoutId );
        }
        else if ( container != null )
        {
            try
            {
                layout = (ArtifactRepositoryLayout) container.lookup( ArtifactRepositoryLayout.ROLE, layoutId );
            }
            catch ( ComponentLookupException e )
            {
                throw new InvalidConfigurationException( "Invalid Maven repository layout: \'" + layoutId + "\'.", e );
            }
        }
        else
        {
            throw new InvalidConfigurationException( "Repository layouts are not available. Either "
                + "use self-contained instance, or construct with a mapping of layouts by id." );
        }
        
        return layout;
    }

    public ArtifactHandlerManager getArtifactHandlerManager()
    {
        return artifactHandlerManager;
    }

    public ArtifactResolver getArtifactResolver()
    {
        return artifactResolver;
    }

    public MavenSettingsBuilder getSettingsBuilder()
    {
        return settingsBuilder;
    }

    public ArtifactRepositoryFactory getArtifactRepositoryFactory()
    {
        return artifactRepositoryFactory;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        this.container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public MavenProjectBuilder getMavenProjectBuilder()
    {
        return mavenProjectBuilder;
    }

    protected Map getArtifactRepositoryLayoutsById()
    {
        return artifactRepositoryLayoutsById;
    }

    protected void setArtifactRepositoryLayoutsById( Map artifactRepositoryLayoutsById )
    {
        this.artifactRepositoryLayoutsById = artifactRepositoryLayoutsById;
    }

    protected PlexusContainer getContainer()
    {
        return container;
    }

    protected void setContainer( PlexusContainer container )
    {
        this.container = container;
    }

    protected void setArtifactHandlerManager( ArtifactHandlerManager artifactHandlerManager )
    {
        this.artifactHandlerManager = artifactHandlerManager;
    }

    protected void setArtifactRepositoryFactory( ArtifactRepositoryFactory artifactRepositoryFactory )
    {
        this.artifactRepositoryFactory = artifactRepositoryFactory;
    }

    protected void setArtifactResolver( ArtifactResolver artifactResolver )
    {
        this.artifactResolver = artifactResolver;
    }

    protected void setMavenProjectBuilder( MavenProjectBuilder mavenProjectBuilder )
    {
        this.mavenProjectBuilder = mavenProjectBuilder;
    }

    protected void setSettingsBuilder( MavenSettingsBuilder settingsBuilder )
    {
        this.settingsBuilder = settingsBuilder;
    }

    public RepositoryMetadataManager getRepositoryMetadataManager()
    {
        return repositoryMetadataManager;
    }

    public void setRepositoryMetadataManager( RepositoryMetadataManager repositoryMetadataManager )
    {
        this.repositoryMetadataManager = repositoryMetadataManager;
    }

}
