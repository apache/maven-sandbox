package org.apache.maven.dynaweb.cms.document;

import org.apache.maven.doxia.siterenderer.DocumentRenderer;
import org.apache.maven.dynaweb.cms.request.CMSRequest;

import javax.jcr.Node;

public class NodeBackedCMSDocument
    implements CMSDocument
{

    private final Node node;
    private final CMSRequest request;
    
    private CMSDocumentInfo docInfo;

    public NodeBackedCMSDocument( CMSRequest request, Node node )
    {
        this.request = request;
        this.node = node;
    }

    public CMSDocumentInfo getDocumentInfo()
    {
        if ( docInfo == null )
        {
            docInfo = new NodeBackedCMSDocumentInfo( request, node );
        }
        
        return docInfo;
    }

    public DocumentRenderer getRenderer()
    {
        // TODO Implement this.
        return null;
    }

}
