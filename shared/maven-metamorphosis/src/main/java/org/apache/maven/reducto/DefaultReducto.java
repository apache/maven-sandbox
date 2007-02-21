package org.apache.maven.reducto;

import org.apache.maven.reducto.analysis.DefaultJarAnalysisContext;
import org.apache.maven.reducto.analysis.DefaultJarAnalysisResult;
import org.apache.maven.reducto.analysis.JarAnalysisContext;
import org.apache.maven.reducto.analysis.JarAnalysisException;
import org.apache.maven.reducto.analysis.JarAnalyzer;
import org.apache.maven.reducto.analysis.JarAnalysisResult;
import org.apache.maven.reducto.source.JarDataSource;
import org.apache.maven.reducto.source.JarDataSourceRetrievalException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * @author Jason van Zyl
 * @plexus.component
 */
public class DefaultReducto
    extends AbstractLogEnabled
    implements Reducto
{
    /**
     * @plexus.requirement role="org.apache.maven.reducto.analysis.JarAnalyzer"
     */
    private Map jarAnalyzers;

    public Map analyse( JarDataSource dataSource )
        throws JarDataSourceRetrievalException, JarAnalysisException
    {
        Map jarAnalysisResults = new LinkedHashMap();

        for ( Iterator i = dataSource.retrieveJars().iterator(); i.hasNext(); )
        {
            File jar = (File) i.next();

            //todo: reducto found event

            // For each JAR file that we encounter we want to create an analysis context, place the JAR file
            // and a blank JAR analysis result into the context and then pass that through a chain of
            // JAR analysers.

            JarAnalysisContext context = new DefaultJarAnalysisContext( jar, new DefaultJarAnalysisResult( jar ) );

            for ( Iterator j = jarAnalyzers.values().iterator(); j.hasNext(); )
            {
                JarAnalyzer analyzer = (JarAnalyzer) j.next();

                analyzer.analyze( context );

                if ( context.getJarAnalysisResult().isResolved() )
                {
                    break;
                }
            }

            jarAnalysisResults.put( createKey( context.getJarAnalysisResult() ), context.getJarAnalysisResult() );
        }

        return jarAnalysisResults;
    }

    public String createKey( JarAnalysisResult result )
    {
        return result.getGroupId() + result.getArtifactId() + result.getVersion();
    }

    public String createKey( String groupId, String artifactId, String version )
    {
        return groupId + artifactId + version;
    }
}
