package org.apache.maven.dynaweb.display.filter;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.io.xpp3.DecorationXpp3Reader;
import org.apache.maven.doxia.siterenderer.DocumentRenderer;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.dynaweb.display.DisplayConstants;
import org.apache.maven.dynaweb.display.loader.DocumentAccessException;
import org.apache.maven.dynaweb.display.loader.DocumentLoader;
import org.apache.maven.dynaweb.display.skin.SkinManager;
import org.apache.maven.dynaweb.display.skin.SkinningException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Render a page as requested.
 * 
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class DoxiaContentFilter
    implements Filter
{
    
    private Logger log = LogManager.getLogger( DoxiaContentFilter.class.getName() );
    
    private Renderer siteRenderer;

    private DecorationModel decorationModel;

    private DocumentLoader documentLoader;

    private ServletContext servletContext;

    private File skinJar;

    private SkinManager skinManager;

    private String filteredExtensions;

    private FilterConfig filterConfig;

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        this.filterConfig = filterConfig;
        
        servletContext = filterConfig.getServletContext();
        
        filteredExtensions = filterConfig.getInitParameter( DisplayConstants.FILTERED_EXTENSIONS_PARAM );
        
        if ( filteredExtensions == null )
        {
            log( "ATTENTION! No document extensions are registered with Doxia filter!" );
            filteredExtensions = "";
        }

        PlexusContainer container =
            ( PlexusContainer ) servletContext.getAttribute( DisplayConstants.MASTER_PLEXUS_CONTAINER_KEY );

        initSkin( container );

        try
        {
            siteRenderer = ( Renderer ) container.lookup( Renderer.ROLE );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Error retrieving site-renderer instance: " + e.getMessage(), e );
        }

        initDocumentLoader( container );

        initSiteDescriptor();
    }

    private void initSiteDescriptor()
        throws ServletException
    {
        String siteDescriptorPath =
            filterConfig.getInitParameter( DisplayConstants.SITE_DESCRIPTOR_PATH_INIT_PARAM );

        if ( siteDescriptorPath == null )
        {
            siteDescriptorPath = DisplayConstants.SITE_DESCRIPTOR_PATH_INIT_PARAM_DEFAULT;
        }

        siteDescriptorPath = servletContext.getRealPath( siteDescriptorPath );

        FileReader reader = null;

        try
        {
            reader = new FileReader( siteDescriptorPath );

            decorationModel = new DecorationXpp3Reader().read( reader );
        }
        catch ( IOException e )
        {
            throw new ServletException( "Failed to load site descriptor from: " + siteDescriptorPath, e );
        }
        catch ( XmlPullParserException e )
        {
            throw new ServletException( "Failed to load site descriptor from: " + siteDescriptorPath, e );
        }
        finally
        {
            IOUtil.close( reader );
        }
    }

    private void initDocumentLoader( PlexusContainer container )
        throws ServletException
    {
        String loaderHint = servletContext.getInitParameter( DisplayConstants.DOCUMENT_LOADER_ROLE_HINT_PARAM );

        if ( loaderHint == null )
        {
            loaderHint = "default";
        }

        try
        {
            documentLoader = ( DocumentLoader ) container.lookup( DocumentLoader.ROLE, loaderHint );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Failed to initialize document-loader for: " + loaderHint, e );
        }
    }

    private void initSkin( PlexusContainer container )
        throws ServletException
    {
        String skinMgrHint = filterConfig.getInitParameter( DisplayConstants.SKIN_MANAGER_ROLE_HINT_PARAM );

        if ( skinMgrHint == null )
        {
            throw new ServletException( "You must supply the context-param: "
                            + DisplayConstants.SKIN_MANAGER_ROLE_HINT_PARAM );
        }

        try
        {
            skinManager = ( SkinManager ) container.lookup( SkinManager.ROLE );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Failed to lookup SkinManager instance for: " + skinMgrHint );
        }

        String skinPath = filterConfig.getInitParameter( DisplayConstants.SKIN_JAR_PARAM );

        if ( skinPath == null )
        {
            throw new ServletException( "You must supply the context-param: " + DisplayConstants.SKIN_JAR_PARAM );
        }

        skinJar = new File( servletContext.getRealPath( skinPath ) );

        if ( !skinJar.exists() || !skinJar.isFile() )
        {
            throw new ServletException( "Invalid value for " + DisplayConstants.SKIN_JAR_PARAM + " parameter. "
                            + skinPath + " is missing or is not a normal file." );
        }
        
        String contextPath = servletContext.getRealPath( "/" );
        
        File contextRoot = new File( contextPath );

        try
        {
            skinManager.initializeSiteSkin( skinJar, contextRoot );
        }
        catch ( SkinningException e )
        {
            throw new ServletException( "Failed to load site skin from: " + skinPath + ".", e );
        }
    }

    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain )
        throws IOException, ServletException
    {
        HttpServletRequest httpRequest = ( HttpServletRequest ) servletRequest;
        
        String ext = FileUtils.extension( httpRequest.getServletPath() );
        
        if ( filteredExtensions.indexOf( ext ) < 0 )
        {
            filterChain.doFilter( httpRequest, servletResponse );
            return;
        }

        DocumentRenderer renderer;
        try
        {
            renderer = documentLoader.load( httpRequest, servletContext );
        }
        catch ( DocumentAccessException e )
        {
            log( "Error retrieving document: " + httpRequest.getServletPath() );
            throw new ServletException( "Error retrieving document." );
        }

        if ( renderer != null )
        {
            render( servletRequest, servletResponse, renderer );
        }
        else
        {
            log( "Doxia document not found for: " + httpRequest.getServletPath() );
        }

//        filterChain.doFilter( servletRequest, servletResponse );
    }

    private void render( ServletRequest request, ServletResponse servletResponse, DocumentRenderer renderer )
        throws ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        String requestedDocumentPath = httpRequest.getServletPath();
        Locale locale = request.getLocale();
        
        Map attributes = new HashMap();
        
        attributes.put( "servletContext", servletContext );
        attributes.put( "request", request );
        attributes.put( "contextPath", servletContext.getContextPath() );
        
        SiteRenderingContext context;
        try
        {
            context = skinManager.createSiteRenderingContext( skinJar, locale, siteRenderer, attributes, decorationModel, servletContext );
        }
        catch ( SkinningException e )
        {
            log( "Error initializing rendering context for document: " + requestedDocumentPath, e );

            throw new ServletException( "Error rendering document." );
        }
        
        try
        {
            renderer.renderDocument( servletResponse.getWriter(), siteRenderer, context );
        }
        catch ( RendererException e )
        {
            log( "Error rendering document: " + requestedDocumentPath, e );

            throw new ServletException( "Error rendering document." );
        }
        catch ( UnsupportedEncodingException e )
        {
            log( "Error rendering document: " + requestedDocumentPath, e );

            throw new ServletException( "Error rendering document." );
        }
        catch ( IOException e )
        {
            log( "Error rendering document: " + requestedDocumentPath, e );

            throw new ServletException( "Error rendering document." );
        }
    }
    
    private void log( String message, Throwable error )
    {
        log.info( message, error );
    }

    private void log( String message )
    {
        log.info( message );
    }

    public void destroy()
    {
    }
}
