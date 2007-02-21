package org.apache.maven.dynaweb.display.filter;

import org.apache.maven.dynaweb.display.DisplayConstants;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class PlexusInitializerFilter
    implements Filter
{
    
    private PlexusContainer container;

    public void destroy()
    {
        if ( container != null )
        {
            container.dispose();
        }
    }

    public void doFilter( ServletRequest serverRequest, ServletResponse serverResponse, FilterChain chain )
        throws IOException, ServletException
    {
        chain.doFilter( serverRequest, serverResponse );
    }

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        ServletContext servletContext = filterConfig.getServletContext();
        
        container = ( PlexusContainer ) servletContext.getAttribute( DisplayConstants.MASTER_PLEXUS_CONTAINER_KEY );
        
        if ( container == null )
        {
            try
            {
                ClassLoader cloader = Thread.currentThread().getContextClassLoader();
                
                servletContext.log( "Initializing plexus container with classloader: " + cloader );
                
                container = new DefaultPlexusContainer( DisplayConstants.MASTER_PLEXUS_CONTAINER_KEY, cloader );
                container.start();
                
                servletContext.setAttribute( DisplayConstants.MASTER_PLEXUS_CONTAINER_KEY, container );
            }
            catch ( PlexusContainerException e )
            {
                throw new ServletException( "Failed to initialize Plexus container. Reason: " + e.getMessage(), e );
            }
        }
    }

}
