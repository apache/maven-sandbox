package org.apache.maven.dynaweb.cms.document;

import org.apache.maven.dynaweb.cms.request.CMSRequest;

import javax.jcr.Node;

public class NodeBackedCMSDocumentInfo
    implements CMSDocumentInfo
{

    private final Node node;
    private final CMSRequest request;

    public NodeBackedCMSDocumentInfo( CMSRequest request, Node node )
    {
        this.request = request;
        this.node = node;
    }

}
