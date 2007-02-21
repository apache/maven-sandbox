package org.apache.maven.pomo;

import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.reducto.analysis.JarAnalysisResult;

import java.util.Map;
import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;

/**
 * @author Jason van Zyl
 * @plexus.component
 */
public class DefaultPomo
    implements Pomo
{
    public Model generateModel( PomInfoSource source,
                                Map jarAnalysisResults )
        throws ModelGenerationException
    {
        Model model = new Model();

        model.setModelVersion( source.getModelVersion() );

        model.setGroupId( source.getGroupId() );

        model.setArtifactId( source.getArtifactId() );

        model.setVersion( source.getVersion() );

        for ( Iterator i = jarAnalysisResults.values().iterator(); i.hasNext(); )
        {
            JarAnalysisResult r = (JarAnalysisResult) i.next();

            Dependency d = new Dependency();

            d.setGroupId( r.getGroupId() );

            d.setArtifactId( r.getArtifactId() );

            d.setVersion( r.getVersion() );

            model.addDependency( d );
        }

        return model;
    }

    public void writeModel( Writer writer,
                            Model model )
        throws ModelWritingException
    {
        MavenXpp3Writer w = new MavenXpp3Writer();

        try
        {
            w.write( writer, model );
        }
        catch ( IOException e )
        {
            throw new ModelWritingException( "Error writing model.", e );
        }
    }
}
