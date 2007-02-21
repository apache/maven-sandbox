package org.apache.maven.shared.artifact.tools.resolve;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;

import java.util.Iterator;
import java.util.Map;

public class ResolutionException
    extends Exception
{

    public static final String TYPE_METADATA = "metadata";

    public static final String TYPE_ARTIFACT = "artifact";

    private static final long serialVersionUID = 1L;

    private Map resolutionExceptionsByRepository;

    private String type;

    public ResolutionException( Artifact artifact, String message, Throwable cause )
    {
        super( "Failed to resolve: " + artifact.getId() + ". Reason: " + message, cause );
        this.type = TYPE_ARTIFACT;
    }

    public ResolutionException( Artifact artifact, String message )
    {
        super( "Failed to resolve: " + artifact.getId() + ". Reason: " + message );
        this.type = TYPE_ARTIFACT;
    }

    public ResolutionException( Artifact artifact, Map resolutionExceptionsByRepository )
    {
        super( "Failed to resolve: " + artifact.getId() + " from:\n  - "
            + formatRepositories( resolutionExceptionsByRepository, "\n  - " ) + "\n" );

        this.type = TYPE_ARTIFACT;
        this.resolutionExceptionsByRepository = resolutionExceptionsByRepository;
    }

    public ResolutionException( ArtifactMetadata metadata, String message, Throwable cause )
    {
        super( "Failed to resolve: " + metadata.getClass().getName() + "[" + metadata.getGroupId() + ", "
            + metadata.getArtifactId() + "]. Reason: " + message, cause );
        this.type = TYPE_METADATA;
    }

    public ResolutionException( ArtifactMetadata metadata, String message )
    {
        super( "Failed to resolve: " + metadata.getClass().getName() + "[" + metadata.getGroupId() + ", "
            + metadata.getArtifactId() + "]. Reason: " + message );
        this.type = TYPE_METADATA;
    }

    public ResolutionException( ArtifactMetadata metadata, Map resolutionExceptionsByRepository )
    {
        super( "Failed to resolve: " + metadata.getClass().getName() + "[" + metadata.getGroupId() + ", "
            + metadata.getArtifactId() + "] from:\n  - "
            + formatRepositories( resolutionExceptionsByRepository, "\n  - " ) + "\n" );

        this.type = TYPE_METADATA;
        this.resolutionExceptionsByRepository = resolutionExceptionsByRepository;
    }
    
    private static String formatRepositories( Map repositoryKeyedMap, String separator )
    {
        StringBuffer buffer = new StringBuffer();
        for ( Iterator it = repositoryKeyedMap.keySet().iterator(); it.hasNext(); )
        {
            ArtifactRepository repo = (ArtifactRepository) it.next();
            
            buffer.append( separator );
            buffer.append( repo.getId() ).append( " (" ).append( repo.getUrl() ).append( ')' );
            
        }
        
        return buffer.toString();
    }

    public boolean isMetadataError()
    {
        return TYPE_METADATA.equals( type );
    }
    
    public boolean isArtifactError()
    {
        return TYPE_ARTIFACT.equals( type );
    }
    
    public Map getResolutionExceptionsByRepository()
    {
        return resolutionExceptionsByRepository;
    }

}
