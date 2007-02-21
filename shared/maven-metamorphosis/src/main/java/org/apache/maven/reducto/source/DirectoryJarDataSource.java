package org.apache.maven.reducto.source;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jason van Zyl
 */
public class DirectoryJarDataSource
    implements JarDataSource
{
    private File directory;

    public DirectoryJarDataSource( File directory )
    {
        this.directory = directory;
    }

    public Set retrieveJars()
        throws JarDataSourceRetrievalException
    {
        try
        {
            return new HashSet( FileUtils.getFiles( directory, "*.jar", null ) );
        }
        catch ( IOException e )
        {
            throw new JarDataSourceRetrievalException( "Error retrieving JARS from " + directory + "." );
        }
    }
}
