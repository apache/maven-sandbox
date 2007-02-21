package org.apache.maven.shared.artifact.tools.resolve;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;

import java.io.File;

public class ArtifactQuery
{

    private final String artifactId;

    private final String groupId;

    private String version;

    private String classifier;

    private String type = "jar";

    private String versionRangeSpec;

    private String scope = Artifact.SCOPE_COMPILE;

    private boolean isOptional = false;
    
    private File file;
    
    public ArtifactQuery( String groupId, String artifactId )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }
    
    public ArtifactQuery( Dependency dependency )
    {
        this.groupId = dependency.getGroupId();
        this.artifactId = dependency.getArtifactId();
        this.scope = dependency.getScope();
        this.classifier = dependency.getClassifier();
        this.isOptional = dependency.isOptional();
        this.type = dependency.getType();
        
        String version = dependency.getVersion();
        
        if ( version.startsWith( "[" ) || version.startsWith( "(" ) )
        {
            this.versionRangeSpec = version;
        }
        else
        {
            this.version = version;
        }
        
        if ( Artifact.SCOPE_SYSTEM.equals( this.scope ) )
        {
            this.file = new File( dependency.getSystemPath() );
        }
    }

    public ArtifactQuery copy()
    {
        ArtifactQuery query = new ArtifactQuery( groupId, artifactId );
        
        query.version = version;
        query.classifier = classifier;
        query.type = type;
        query.versionRangeSpec = versionRangeSpec;
        query.scope = scope;
        query.isOptional = isOptional;
        query.file = file;
        
        return query;
    }

    public ArtifactQuery setVersion( String version )
    {
        this.version = version;

        return this;
    }

    public ArtifactQuery setVersionRangeSpec( String versionRangeSpec )
    {
        this.versionRangeSpec = versionRangeSpec;
        return this;
    }

    public ArtifactQuery setClassifier( String classifier )
    {
        this.classifier = classifier;

        return this;
    }

    public ArtifactQuery setScope( String scope )
    {
        this.scope = scope;
        return this;
    }

    public ArtifactQuery setType( String type )
    {
        this.type = type;

        return this;
    }

    public ArtifactQuery setOptional( boolean isOptional )
    {
        this.isOptional = isOptional;

        return this;
    }

    public Artifact createArtifact( ArtifactHandlerManager artifactHandlerManager )
        throws InvalidArtifactSpecificationException
    {
        ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler( type );

        VersionRange versionRange;

        if ( versionRangeSpec != null )
        {
            try
            {
                versionRange = VersionRange.createFromVersionSpec( versionRangeSpec );
            }
            catch ( InvalidVersionSpecificationException e )
            {
                throw new InvalidArtifactSpecificationException( "Artifact query specifies invalid version range: " + versionRangeSpec, e );
            }
        }
        else if ( version != null )
        {
            versionRange = VersionRange.createFromVersion( version );
        }
        else
        {
            throw new InvalidArtifactSpecificationException( "Artifact query is incomplete. Please supply version or versionRangeSpec." );
        }

        Artifact artifact = new DefaultArtifact( groupId, artifactId, versionRange, scope, type, classifier,
                                                 artifactHandler, isOptional );
        
        if ( Artifact.SCOPE_SYSTEM.equals( scope ) && file != null )
        {
            if ( file.exists() )
            {
                artifact.setFile( file );
                artifact.setResolved( true );
            }
            else
            {
                throw new InvalidArtifactSpecificationException( "Artifact query specifies system scope and path, but path does not exist." );
            }
        }

        return artifact;
    }

}
