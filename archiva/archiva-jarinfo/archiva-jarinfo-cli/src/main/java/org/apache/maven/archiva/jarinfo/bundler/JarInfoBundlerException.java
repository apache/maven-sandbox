package org.apache.maven.archiva.jarinfo.bundler;

public class JarInfoBundlerException
    extends Exception
{

    public JarInfoBundlerException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public JarInfoBundlerException( String message )
    {
        super( message );
    }
}
