package org.apache.maven.dynaweb.cms.document;

import org.apache.maven.doxia.siterenderer.DocumentRenderer;

public interface CMSDocument
{
    
    CMSDocumentInfo getDocumentInfo();
    
    DocumentRenderer getRenderer();

}
