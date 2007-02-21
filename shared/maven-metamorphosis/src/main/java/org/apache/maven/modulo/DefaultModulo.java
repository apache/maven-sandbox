package org.apache.maven.modulo;

import org.apache.maven.pomo.Pomo;
import org.apache.maven.pomo.PomInfoSource;
import org.apache.maven.pomo.SimplePomInfoSource;
import org.apache.maven.pomo.ModelGenerationException;
import org.apache.maven.pomo.ModelWritingException;
import org.apache.maven.reducto.Reducto;
import org.apache.maven.reducto.analysis.JarAnalysisException;
import org.apache.maven.reducto.analysis.JarAnalysisResult;
import org.apache.maven.reducto.source.DirectoryJarDataSource;
import org.apache.maven.reducto.source.JarDataSourceRetrievalException;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * @author Jason van Zyl
 * @plexus.component
 */
public class DefaultModulo
    implements Modulo
{
    /**
     * @plexus.requirement
     */
    private Reducto reducto;

    /**
     * @plexus.requirement
     */
    private Pomo pomo;

    public void convert( File sourceDirectory,
                         MappingModelSource source,
                         File outputDirectory )
        throws ModuleConversionException, MappingModelRetrievalException, JarDataSourceRetrievalException,
        JarAnalysisException, ModelGenerationException, ModelWritingException
    {
        if ( !sourceDirectory.exists() )
        {
            throw new ModuleConversionException(
                "The specified source directory " + sourceDirectory + " does not exist." );
        }

        if ( !outputDirectory.exists() )
        {
            //todo: this should return a boolean/throw an exception and take a file.
            FileUtils.mkdir( outputDirectory.getAbsolutePath() );
        }

        MappingModel m = source.getMappingModel();

        // ----------------------------------------------------------------------------
        // Walk through all the directories of JARs that the user may specify and try
        // to analyze them all to determine if we have entries for them in the
        // repository metadata.
        // ----------------------------------------------------------------------------

        Map jarAnalysisResults = new LinkedHashMap();

        Map unresolvedJars = new LinkedHashMap();

        for ( Iterator i = m.getJarDirectories().iterator(); i.hasNext(); )
        {
            Resource r = (Resource) i.next();

            Map results = reducto.analyse( new DirectoryJarDataSource( new File( sourceDirectory, r.getDirectory() ) ) );

            for ( Iterator j = results.values().iterator(); j.hasNext(); )
            {
                JarAnalysisResult result = (JarAnalysisResult) j.next();

                if ( result.isResolved() )
                {
                    jarAnalysisResults.put( reducto.createKey( result ), result );
                }
                else
                {
                    unresolvedJars.put( reducto.createKey( result ), result );
                }
            }
        }

        //todo: do something with the unresolved JARs
        //      create some events of create stubs in the POM so that when people manually figure
        //      out what the deps are they can flesh them out

        PomInfoSource pomInfo = new SimplePomInfoSource( "4.0.0", m.getGroupId(), m.getArtifactId(), m.getVersion() );

        Model model = pomo.generateModel( pomInfo, jarAnalysisResults );

        Writer writer;

        try
        {
            writer = new FileWriter( new File( outputDirectory, "pom.xml" ) );

            pomo.writeModel( writer, model );
        }
        catch ( IOException e )
        {
            throw new ModuleConversionException( "Error writing out POM.", e );
        }

        // ----------------------------------------------------------------------------
        // Translation
        // ----------------------------------------------------------------------------
        
        // sources
        // resources
        // allow maven compliance or to adapt to the existing structure
    }
}
