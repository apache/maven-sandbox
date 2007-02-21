package org.apache.maven.dynaweb.display.skin;

import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

public interface SkinManager
{
    
    String ROLE = SkinManager.class.getName();

    void initializeSiteSkin( File skinJar, File docRoot )
        throws SkinningException;

    SiteRenderingContext createSiteRenderingContext( File skinJar, Locale locale, Renderer siteRenderer,
                                                     Map attributes, DecorationModel decorationModel,
                                                     ServletContext servletContext )
        throws SkinningException;

}
