package org.apache.maven.archiva.jarinfo.analysis.visitors;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.archiva.jarinfo.analysis.IdentificationWeights;
import org.apache.maven.archiva.jarinfo.analysis.JarEntryVisitor;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.InspectedIds;
import org.apache.maven.archiva.jarinfo.model.JarDetails;

public class IdentificationFilename
    extends AbstractJarEntryVisitor
    implements JarEntryVisitor
{
    private static final String ORIGIN = "filename";

    private static final Pattern VERSION_PATTERN = Pattern.compile( "-[0-9]" );

    @Override
    public void visitStart( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitStart( details, jar );

        String filename = jar.getName();
        
        // Strip off paths (if any)
        int idxPath = filename.lastIndexOf( File.separatorChar );
        if ( idxPath > 0 )
        {
            filename = filename.substring( idxPath + 1 );
        }

        // Remove any extension.
        int idxExt = filename.lastIndexOf( '.' );
        if ( idxExt > 0 )
        {
            filename = filename.substring( 0, idxExt );
        }

        InspectedIds inspectedIds = details.getInspectedIds();
        IdentificationWeights weights = IdentificationWeights.getInstance();

        Matcher mat = VERSION_PATTERN.matcher( filename );
        if ( mat.find() )
        {
            String prefix = filename.substring( 0, mat.start() );
            inspectedIds.addArtifactId( prefix, weights.getWeight( "filename.artifactId" ), ORIGIN );
            inspectedIds.addName( prefix, weights.getWeight( "filename.name" ), ORIGIN );
            inspectedIds.addVersion( filename.substring( mat.end() - 1 ), 
                                       weights.getWeight( "filename.version" ),
                                       ORIGIN );
        }
        else
        {
            inspectedIds.addArtifactId( filename, weights.getWeight( "filename.artifactId" ), ORIGIN );
            inspectedIds.addName( filename, weights.getWeight( "filename.name" ), ORIGIN );
        }
    }

    public void visitJarEntry( EntryDetail entry, JarEntry jarEntry )
        throws IOException
    {
        /* do nothing */
    }
}
