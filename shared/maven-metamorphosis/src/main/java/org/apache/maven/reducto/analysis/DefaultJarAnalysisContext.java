package org.apache.maven.reducto.analysis;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class DefaultJarAnalysisContext
    implements JarAnalysisContext
{
    private File jar;

    private JarAnalysisResult jarAnalysisResult;

    public DefaultJarAnalysisContext( File jar,
                                      JarAnalysisResult result )
    {
        this.jar = jar;
        this.jarAnalysisResult = result;
    }

    public File getJar()
    {
        return jar;
    }

    public JarAnalysisResult getJarAnalysisResult()
    {
        return jarAnalysisResult;
    }
}
