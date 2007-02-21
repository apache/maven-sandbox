package org.apache.maven.dynaweb.cms.filter;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.io.xpp3.DecorationXpp3Reader;
import org.apache.maven.doxia.siterenderer.DocumentRenderer;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.dynaweb.cms.document.CMSDocument;
import org.apache.maven.dynaweb.cms.request.CMSRequest;
import org.apache.maven.dynaweb.cms.request.HttpCMSRequest;
import org.apache.maven.dynaweb.cms.session.AnonymousJSR170Session;
import org.apache.maven.dynaweb.cms.session.CMSAccessException;
import org.apache.maven.dynaweb.cms.session.CMSSession;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Render a page as requested.
 * 
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class DoxiaContentFilter
    implements Filter
{
    private Renderer siteRenderer;

    private DecorationModel decorationModel;

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        ServletContext servletContext = filterConfig.getServletContext();

        PlexusContainer container =
            ( PlexusContainer ) servletContext.getAttribute( DynaWebEnvironmentKeys.MASTER_PLEXUS_CONTAINER_KEY );

        try
        {
            siteRenderer = ( Renderer ) container.lookup( Renderer.ROLE );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Error retrieving site-renderer instance: " + e.getMessage(), e );
        }

        try
        {
            initDecorationModel( servletContext );
        }
        catch ( IOException e )
        {
            throw new ServletException( "Error reading site descriptor: " + e.getMessage(), e );
        }
        catch ( XmlPullParserException e )
        {
            throw new ServletException( "Error reading site descriptor: " + e.getMessage(), e );
        }
    }

    private void initDecorationModel( ServletContext servletContext )
        throws IOException, XmlPullParserException
    {
        String siteDescriptorPath =
            servletContext.getInitParameter( DynaWebEnvironmentKeys.SITE_DESCRIPTOR_PATH_INIT_PARAM );

        if ( siteDescriptorPath == null )
        {
            siteDescriptorPath = DynaWebEnvironmentKeys.SITE_DESCRIPTOR_PATH_INIT_PARAM_DEFAULT;
        }

        siteDescriptorPath = servletContext.getRealPath( siteDescriptorPath );

        FileReader reader = null;

        try
        {
            reader = new FileReader( siteDescriptorPath );

            decorationModel = new DecorationXpp3Reader().read( reader );
        }
        finally
        {
            IOUtil.close( reader );
        }
    }

    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain )
        throws IOException, ServletException
    {
        CMSDocument document = loadDocument( servletRequest, servletResponse );

        render( servletResponse, document );

        filterChain.doFilter( servletRequest, servletResponse );
    }

    private CMSDocument loadDocument( ServletRequest servletRequest, ServletResponse servletResponse )
        throws ServletException
    {
        HttpServletRequest req = ( HttpServletRequest ) servletRequest;

        HttpSession webSession = req.getSession();

        CMSSession session = null;

        if ( webSession != null )
        {
            session = ( CMSSession ) webSession.getAttribute( DynaWebEnvironmentKeys.CMS_SESSION_ATTRIBUTE );
        }

        if ( session == null )
        {
            session = new AnonymousJSR170Session();
        }

        CMSRequest request = new HttpCMSRequest( req );

        CMSDocument document;
        
        try
        {
            document = session.getDocument( request );
        }
        catch ( CMSAccessException e )
        {
            throw new ServletException( e.getMessage(), e );
        }

        return document;
    }

    private void render( ServletResponse servletResponse, CMSDocument document )
        throws ServletException
    {
        SiteRenderingContext ctx = new SiteRenderingContext();

        ctx.setDecoration( decorationModel );

        try
        {
            DocumentRenderer renderer = ( DocumentRenderer ) document.getRenderer();

            renderer.renderDocument( servletResponse.getWriter(), siteRenderer, ctx );
        }
        catch ( RendererException e )
        {
            throw new ServletException( "Error rendering document: " + e.getMessage(), e );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new ServletException( "Error rendering document: " + e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new ServletException( "Error rendering document: " + e.getMessage(), e );
        }
    }

    public void destroy()
    {
    }
}
