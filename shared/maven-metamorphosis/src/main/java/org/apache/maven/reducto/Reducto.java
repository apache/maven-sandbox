package org.apache.maven.reducto;

import org.apache.maven.reducto.analysis.JarAnalysisException;
import org.apache.maven.reducto.analysis.JarAnalysisResult;
import org.apache.maven.reducto.source.JarDataSource;
import org.apache.maven.reducto.source.JarDataSourceRetrievalException;

import java.util.Map;

/**
 * Take a {@link JarDataSource} and return a set {@link org.apache.maven.reducto.analysis.JarAnalysisResult}
 * objects. The information contained in each {@link org.apache.maven.reducto.analysis.JarAnalysisResult} object
 * should be enough to give a property identity to a @{link MavenArtifact}.
 *
 * @author Jason van Zyl
 */
public interface Reducto
{
    String ROLE = Reducto.class.getName();

    Map analyse( JarDataSource dataSource )
        throws JarDataSourceRetrievalException, JarAnalysisException;

    String createKey( JarAnalysisResult result );

    String createKey( String groupId, String artifactId, String version );

    //todo: How to pick what analysis you want to perform: i think want to step through all the available analysers with precedence
    //      where we might want to have various analysers having more precedence where an archiva lookup can happen
    //todo: How to pick which exposers you want to use: exposers are analysers
    //todo: encapsulate the gleaning of information from a JAR with creating the dep info: everything will become an analyser
    //todo: what real use are the exposers other then looking up the hash?: the ordering of analysers
    //todo: we need discovery events to that a UI can hook up to it

    // order
    // hash search
    // class search: may map to one or more classes, archiva may know if there are unique classes for versions of log4j for example
    // manifest
    // filename
    // textfile
    // timestamp
    // main output
}
