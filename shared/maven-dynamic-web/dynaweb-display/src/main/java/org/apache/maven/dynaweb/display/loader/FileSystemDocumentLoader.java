package org.apache.maven.dynaweb.display.loader;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.siterenderer.DocumentRenderer;
import org.apache.maven.doxia.siterenderer.DoxiaDocumentRenderer;
import org.apache.maven.dynaweb.display.DisplayConstants;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @plexus.component role="org.apache.maven.dynaweb.display.loader.DocumentLoader" role-hint="file-system"
 * @author jdcasey
 * 
 */
public class FileSystemDocumentLoader
    implements DocumentLoader
{

    private Logger log = LogManager.getLogger( FileSystemDocumentLoader.class.getName() );
    
    private Map documentRoots;

    public DocumentRenderer load( HttpServletRequest request, ServletContext context )
        throws DocumentAccessException
    {
        Map docRoots = getDocumentRoots( context );

        String pathInfo = request.getServletPath();
        
        log.info( "Servlet-Path: \'" + pathInfo + "\'" );
        
//        if ( "".equals( pathInfo ) || pathInfo.endsWith( "/" ) )
//        {
//            pathInfo += "index.html";
//        }

        String docPath = normalizeFileSeparators( pathInfo );

        String docDir;
        String docBase;
        
        if ( docPath.matches( DisplayConstants.PATH_WITH_EXTENSION_PATTERN ) )
        {
            docDir = dirname( docPath );
            docBase = basename( docPath );
        }
        else
        {
            if ( !docPath.endsWith( "/" ) )
            {
                docDir = docPath + "/";
            }
            else
            {
                docDir = docPath;
            }
            
            docBase = "index";
        }
        
        File directory = null;
        String file = null;
        
        String type = null;

        for ( Iterator it = docRoots.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = ( Map.Entry ) it.next();

            String ext = ( String ) entry.getKey();
            File docRoot = ( File ) entry.getValue();

            log.info( "Searching doc-root: \'" + docRoot.getAbsolutePath() + "\' for basename: \'" + docBase + "\' in dir: \'"
                            + docDir + "\' of type: \'" + ext + "\'" );

            File dir = new File( docRoot, docDir );
            if ( dir.exists() )
            {
                String filename = docBase + "." + ext;
                
                File doc = new File( dir, filename );

                if ( doc.exists() )
                {
                    directory = dir;
                    file = filename;
                    type = ext;

                    break;
                }
            }
        }

        DocumentRenderer docRenderer = null;

        if ( directory != null && file != null )
        {
            RenderingContext rContext = new RenderingContext( directory, file, type );

            docRenderer = new DoxiaDocumentRenderer( rContext );
        }
        
        return docRenderer;
    }

    private String normalizeFileSeparators( String src )
    {
        String path = StringUtils.replace( src, '/', File.separatorChar );
        path = StringUtils.replace( path, '\\', File.separatorChar );

        return path;
    }

    private String basename( String docPath )
    {
        int lastFileSep = docPath.lastIndexOf( File.separatorChar );
        if ( lastFileSep < 0 )
        {
            lastFileSep = -1;
        }

        int lastDot = docPath.lastIndexOf( '.' );

        if ( lastDot <= lastFileSep )
        {
            lastDot = docPath.length();
        }

        String base = docPath.substring( lastFileSep + 1, lastDot );

        return base;
    }

    private String dirname( String docPath )
    {
        int lastFileSep = docPath.lastIndexOf( File.separatorChar );
        if ( lastFileSep < 0 )
        {
            lastFileSep = docPath.length();
        }

        String base = docPath.substring( 0, lastFileSep );

        return base;
    }

    private Map getDocumentRoots( ServletContext context )
    {
        if ( documentRoots == null )
        {
            documentRoots = new LinkedHashMap();

            Enumeration e = context.getInitParameterNames();

            while ( e.hasMoreElements() )
            {
                String paramName = ( String ) e.nextElement();

                if ( paramName.startsWith( DisplayConstants.DOCUMENT_ROOT_PARAM_PREFIX ) )
                {
                    String sourceType =
                        paramName.substring( DisplayConstants.DOCUMENT_ROOT_PARAM_PREFIX.length() );

                    String path = context.getInitParameter( paramName );

                    path = context.getRealPath( path );

                    path = normalizeFileSeparators( path );

                    if ( !path.endsWith( File.separator ) )
                    {
                        path += File.separator;
                    }

                    documentRoots.put( sourceType, new File( path ) );
                }
            }
        }

        return documentRoots;
    }
}
