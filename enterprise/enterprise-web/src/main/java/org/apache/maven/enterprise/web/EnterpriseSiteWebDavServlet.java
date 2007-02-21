package org.apache.maven.enterprise.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.File;

/**
 * Extends the basic WebDav servlet.
 * If a GET request is sent for a directory listing the repository will redirect to index.html.
 *
 * @uthor: Andrew Williams
 * @since: 01-Feb-2007
 * @version: $Id$
 */
public class EnterpriseSiteWebDavServlet
    extends EnterpriseWebDavServlet
{

    protected void service( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        String path = request.getPathInfo();
        if ( path == null )
        {
            path = "/";
        }

        if ( request.getMethod().toUpperCase().equals( "GET" ) )
        {
            if ( path.endsWith( "/" ) )
            {
                File index = new File( this.getRootDirectory( this.getServletConfig() ), path + "index.html" );
                if ( index.exists() )
                {
                    response.sendRedirect( request.getServletPath() + path + "index.html" );

                    return;
                }
            }
        }
        super.service( request, response );
    }
}
