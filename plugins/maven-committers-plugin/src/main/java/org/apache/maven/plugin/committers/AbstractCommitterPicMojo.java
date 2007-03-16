package org.apache.maven.plugin.committers;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.IOUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

public abstract class AbstractCommitterPicMojo
    extends AbstractMojo
{

    public void printPic( String resourceLocation, String committerNick )
        throws MojoExecutionException
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        
        URL url = cloader.getResource( resourceLocation );
        
        if ( url == null )
        {
            throw new MojoExecutionException( "Failed to load " + committerNick + "'s picture from: " + resourceLocation );
        }
        
        StringWriter stringWriter = new StringWriter();
        try
        {
            IOUtil.copy( url.openStream(), stringWriter );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to read " + committerNick + "'s picture from: " + resourceLocation );
        }
        
        getLog().info( "\n" + stringWriter.toString() );
    }

}
