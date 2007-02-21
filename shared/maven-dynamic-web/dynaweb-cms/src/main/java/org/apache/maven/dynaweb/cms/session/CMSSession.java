package org.apache.maven.dynaweb.cms.session;

import org.apache.maven.dynaweb.cms.document.CMSDocument;
import org.apache.maven.dynaweb.cms.document.CMSDocumentInfo;
import org.apache.maven.dynaweb.cms.request.CMSRequest;

public interface CMSSession
{

    String ROLE = CMSSession.class.getName();

    CMSDocument getDocument( CMSRequest request )
        throws CMSAccessException;

    CMSDocumentInfo getDocumentInfo( CMSRequest request )
        throws CMSAccessException;

}
