package org.apache.maven.dynaweb.cms.session;

import org.apache.maven.dynaweb.cms.document.CMSDocument;
import org.apache.maven.dynaweb.cms.document.CMSDocumentInfo;
import org.apache.maven.dynaweb.cms.document.NodeBackedCMSDocument;
import org.apache.maven.dynaweb.cms.document.NodeBackedCMSDocumentInfo;
import org.apache.maven.dynaweb.cms.request.CMSRequest;
import org.codehaus.plexus.util.StringUtils;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public abstract class AbstractJSR170Session
    implements CMSSession
{

    private Session session;

    protected void init( Session session )
    {
        this.session = session;
    }

    public CMSDocument getDocument( CMSRequest request )
        throws CMSAccessException
    {
        List<String> pathElements = request.getPath();

        Node node = getNode( pathElements );

        return new NodeBackedCMSDocument( request, node );
    }

    private Node getNode( List<String> pathElements )
        throws CMSAccessException
    {
        String path = StringUtils.join( pathElements.iterator(), "/" );

        Node node;
        try
        {
            node = session.getRootNode().getNode( path );
        }
        catch ( PathNotFoundException e )
        {
            throw new CMSPathNotFoundException( "Cannot find path: " + path, e );
        }
        catch ( RepositoryException e )
        {
            throw new CMSAccessException( "Error accessing CMS repository: " + e.getMessage(), e );
        }

        return node;
    }

    public CMSDocumentInfo getDocumentInfo( CMSRequest request )
        throws CMSAccessException
    {
        List<String> pathElements = request.getPath();

        Node node = getNode( pathElements );

        return new NodeBackedCMSDocumentInfo( request, node );
    }
}
