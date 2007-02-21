package org.apache.maven.dynaweb.cms.filter;

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
        
        container = ( PlexusContainer ) servletContext.getAttribute( DynaWebEnvironmentKeys.MASTER_PLEXUS_CONTAINER_KEY );
        
        if ( container == null )
        {
            try
            {
                container = new DefaultPlexusContainer( DynaWebEnvironmentKeys.MASTER_PLEXUS_CONTAINER_KEY );
                container.start();
                
                servletContext.setAttribute( DynaWebEnvironmentKeys.MASTER_PLEXUS_CONTAINER_KEY, container );
            }
            catch ( PlexusContainerException e )
            {
                throw new ServletException( "Failed to initialize Plexus container. Reason: " + e.getMessage(), e );
            }
        }
    }

}
