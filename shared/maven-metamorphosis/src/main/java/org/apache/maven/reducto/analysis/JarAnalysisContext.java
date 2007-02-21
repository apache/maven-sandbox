package org.apache.maven.reducto.analysis;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public interface JarAnalysisContext
{
    public File getJar();

    public JarAnalysisResult getJarAnalysisResult();
}
