package org.apache.maven.dynaweb.display.skin;

import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.ServletContext;

/**
 * @plexus.component role="org.apache.maven.dynaweb.display.skin.SkinManager" role-hint="simple"
 * 
 * @author jdcasey
 */
public class SimpleSkinManager
    implements SkinManager
{

    public SiteRenderingContext createSiteRenderingContext( File skinJar, Locale locale, Renderer siteRenderer,
                                                            Map attributes, DecorationModel decorationModel,
                                                            ServletContext servletContext )
        throws SkinningException
    {
        SiteRenderingContext context;

        try
        {
            context =
                siteRenderer.createContextForSkin( skinJar, attributes, decorationModel,
                                                   servletContext.getServletContextName(), locale );
        }
        catch ( IOException e )
        {
            throw new SkinningException( "Failed to open skin jar: " + skinJar + " for rendering context creation.", e );
        }

        context.setDecoration( decorationModel );

        return context;
    }

    public void initializeSiteSkin( File skinJar, File docRoot )
        throws SkinningException
    {
        JarFile jar;
        
        try
        {
            jar = new JarFile( skinJar );
        }
        catch ( IOException e )
        {
            throw new SkinningException( "Failed to open skin-jar: " + skinJar, e );
        }
        
        Enumeration entries = jar.entries();
        
        while( entries.hasMoreElements() )
        {
            JarEntry entry = (JarEntry) entries.nextElement();
            
            InputStream inStream = null;
            OutputStream outStream = null;
            
            try
            {
                try
                {
                    inStream = jar.getInputStream( entry );
                }
                catch ( IOException e )
                {
                    throw new SkinningException( "Failed to extract: " + entry.getName() + " from: " + skinJar, e );
                }
                
                String name = entry.getName();
                if ( entry.isDirectory() || name.indexOf( "META-INF" ) > -1 )
                {
                    continue;
                }
                
                if ( name.startsWith( "/" ) )
                {
                    name = name.substring( 1 );
                }
                
                File target = new File( docRoot, name );
                target.getParentFile().mkdirs();
                
                try
                {
                    outStream = new FileOutputStream( target );
                }
                catch ( FileNotFoundException e )
                {
                    throw new SkinningException( "Failed to open: " + target, e );
                }
                
                try
                {
                    IOUtil.copy( inStream, outStream );
                }
                catch ( IOException e )
                {
                    throw new SkinningException( "Failed to extract: " + name + " to: " + target, e );
                }
            }
            finally
            {
                IOUtil.close( inStream );
                IOUtil.close( outStream );
            }
        }
    }

}
