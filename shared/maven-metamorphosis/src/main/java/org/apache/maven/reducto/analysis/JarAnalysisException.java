package org.apache.maven.reducto.analysis;

/**
 * @author Jason van Zyl
 */
public class JarAnalysisException
    extends Exception
{
    public JarAnalysisException( String id )
    {
        super( id );
    }

    public JarAnalysisException( String id,
                                 Throwable throwable )
    {
        super( id, throwable );
    }

    public JarAnalysisException( Throwable throwable )
    {
        super( throwable );
    }
}
