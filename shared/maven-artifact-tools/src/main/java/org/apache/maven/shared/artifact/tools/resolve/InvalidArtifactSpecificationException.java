package org.apache.maven.shared.artifact.tools.resolve;

public class InvalidArtifactSpecificationException
    extends Exception
{

    public InvalidArtifactSpecificationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public InvalidArtifactSpecificationException( String message )
    {
        super( message );
    }

}
