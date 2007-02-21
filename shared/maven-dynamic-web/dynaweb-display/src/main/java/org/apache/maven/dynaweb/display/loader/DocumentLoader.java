package org.apache.maven.dynaweb.display.loader;

import org.apache.maven.doxia.siterenderer.DocumentRenderer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public interface DocumentLoader
{
    String ROLE = DocumentLoader.class.getName();
    
    DocumentRenderer load( HttpServletRequest request, ServletContext context )
        throws DocumentAccessException;

}
