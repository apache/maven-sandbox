package org.apache.maven.archiva.jarinfo.analysis.visitors;

import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.archiva.jarinfo.analysis.IdentificationWeights;
import org.apache.maven.archiva.jarinfo.analysis.JarEntryVisitor;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.JarDetails;
import org.apache.maven.archiva.jarinfo.utils.EmptyUtils;

public class IdentificationEmbeddedMavenProperties
    extends AbstractJarEntryVisitor
    implements JarEntryVisitor
{
    private static final Pattern MAVEN_POM_FILTER = Pattern.compile( "META-INF/maven/.*/pom\\.properties$" );

    private IdentificationWeights weights;

    private boolean performInspection = false;

    public IdentificationEmbeddedMavenProperties()
    {
        this( true );
    }

    public IdentificationEmbeddedMavenProperties( boolean performInspection )
    {
        this.performInspection = performInspection;
    }

    @Override
    public void visitStart( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitStart( details, jar );
        weights = IdentificationWeights.getInstance();
    }

    public void visitJarEntry( EntryDetail entry, JarEntry jarEntry )
        throws IOException
    {
        Matcher matcher = MAVEN_POM_FILTER.matcher( jarEntry.getName() );
        if ( !matcher.matches() )
        {
            return;
        }

        Properties props = new Properties();
        props.load( jar.getInputStream( jarEntry ) );

        String groupId = props.getProperty( "groupId" );
        if ( !EmptyUtils.isEmpty( groupId ) )
        {
            if ( performInspection )
            {
                String weightKey = "embedded.pom.groupId";
                details.getInspectedIds().addGroupId( groupId, weights.getWeight( weightKey ), weightKey );
            }
            details.getAssignedId().setGroupId( groupId );
        }

        String artifactId = props.getProperty( "artifactId" );
        if ( !EmptyUtils.isEmpty( artifactId ) )
        {
            if ( performInspection )
            {
                String weightKey = "embedded.pom.artifactId";
                details.getInspectedIds().addArtifactId( artifactId, weights.getWeight( weightKey ), weightKey );
            }
            details.getAssignedId().setArtifactId( artifactId );
        }

        String version = props.getProperty( "version" );
        if ( !EmptyUtils.isEmpty( version ) )
        {
            if ( performInspection )
            {
                String weightKey = "embedded.pom.version";
                details.getInspectedIds().addVersion( version, weights.getWeight( weightKey ), weightKey );
            }
            details.getAssignedId().setVersion( version );
        }
    }

    public boolean isPerformInspection()
    {
        return performInspection;
    }

    public void setPerformInspection( boolean performInspection )
    {
        this.performInspection = performInspection;
    }
}
