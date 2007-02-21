package org.apache.maven.modulo;

import org.apache.maven.reducto.source.JarDataSourceRetrievalException;
import org.apache.maven.reducto.analysis.JarAnalysisException;
import org.apache.maven.pomo.ModelGenerationException;
import org.apache.maven.pomo.ModelWritingException;

import java.io.File;

//todo: leaving a multi module build in place and using different POMs at different levels (mauro)
//tood: creating a local repository in the project itself vs making a local repository for all maven projects (mauro)
//      possbily per project or global as an options
//todo: an option in Maven itself to use a local library of JARs
//todo: mapping a directory of JARs using system dependencies
//todo: for plugins that are created by a migrating project, to create a stand-alone repository that can be installed
//      so that everything will work locally

/**
 * @author Jason van Zyl
 */
public interface Modulo
{
    String ROLE = Modulo.class.getName();

    void convert( File sourceDirectory,
                  MappingModelSource source,
                  File outputDirectory )
        throws ModuleConversionException, MappingModelRetrievalException, JarDataSourceRetrievalException,
        JarAnalysisException, ModelGenerationException, ModelWritingException;
}
