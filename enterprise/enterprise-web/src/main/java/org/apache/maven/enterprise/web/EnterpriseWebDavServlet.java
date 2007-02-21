package org.apache.maven.enterprise.web;

import org.codehaus.plexus.webdav.servlet.basic.BasicWebDavServlet;
import org.codehaus.plexus.webdav.servlet.DavServerRequest;
import org.codehaus.plexus.webdav.util.WebdavMethodUtil;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.policy.MustChangePasswordException;
import org.codehaus.plexus.security.ui.web.filter.authentication.HttpAuthenticator;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.enterprise.model.EnterpriseConfig;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.io.File;
import java.io.IOException;

/**
 * A basic WebDav servlet for Enterprise.
 * Hooks into the authentication system with a flag for allowing anonymous read.
 *
 * @uthor: Andrew Williams
 * @since: 31-Jan-2007
 * @version: $Id$
 */
public class  EnterpriseWebDavServlet
    extends BasicWebDavServlet
{
    protected HttpAuthenticator httpAuth;

    protected File enterpriseDavRoot;

    protected EnterpriseConfig config;

    public File getRootDirectory( ServletConfig config )
        throws ServletException
    {
        String davName = config.getInitParameter( INIT_ROOT_DIRECTORY );

        if ( StringUtils.isEmpty( davName ) )
        {
            throw new ServletException( "Init Parameter '" + INIT_ROOT_DIRECTORY + "' is empty." );
        }

        File serverRoot = new File( enterpriseDavRoot, davName );

        if ( serverRoot.exists() )
        {
            if ( !serverRoot.isDirectory() )
            {
                throw new ServletException( "Unable to create webdav server, " + serverRoot.getAbsolutePath() +
                    " is not a directory ");
            }
        }
        else
        {
            serverRoot.mkdirs();
        }

        return serverRoot;
    }

    public void initComponents()
        throws ServletException
    {
        setDebug(false); // TODO perhaps debug should be off by default?
        super.initComponents();

        httpAuth = (HttpAuthenticator) lookup( HttpAuthenticator.ROLE, "basic" );

        String plexusHome = "";

        try
        {
            plexusHome = (String) (new InitialContext()).lookup( "java:comp/env/enterprise/dataDir" );
        }
        catch ( NamingException e )
        {
            /* default to the current directory */
        }

        enterpriseDavRoot = new File( plexusHome, "webdav" );

        config = (EnterpriseConfig) this.lookup( "org.apache.maven.enterprise.model.EnterpriseConfig" );
    }

    public boolean isAuthenticated( DavServerRequest davRequest, HttpServletResponse response )
        throws ServletException, IOException
    {
        HttpServletRequest request = davRequest.getRequest();

        /* we don't always need to authenticate read requests */
        if ( isAnonRequest( davRequest ) )
        {
            return true;
        }

        try
        {
            AuthenticationResult result = httpAuth.getAuthenticationResult( request, response );

            if ( ( result == null ) || !result.isAuthenticated() )
            {
                httpAuth.challenge( request, response, "Enterprise Repository",
                                    new AuthenticationException( "User credentials are invalid" ) );
                return false;
            }

        }
        catch ( AuthenticationException e )
        {
            log( "Fatal Http Authentication Error.", e );
            throw new ServletException( "Fatal Http Authentication Error.", e );
        }
        catch ( AccountLockedException e )
        {
            httpAuth.challenge( request, response, "Enterprise Repository",
                                new AuthenticationException( "User account is locked" ) );
        }
        catch ( MustChangePasswordException e )
        {
            httpAuth.challenge( request, response, "Enterprise Repository",
                                new AuthenticationException( "User account password expired" ) );
        }
        return true;
    }

    public boolean isAuthorized( DavServerRequest davRequest, HttpServletResponse response )
        throws ServletException, IOException
    {
        /* we don't always need to authenticate read requests */
        if ( isAnonRequest( davRequest ) )
        {
            return true;
        }

        return true;
    }

    /**
     * Tell if the passed request can be treated anonymously (no authentication needed).
     * Only read requests can be anonymous, clearly!
     *
     * @param davRequest the request to check
     * @return true if the request is a read request and the area we are requesting is publicly visible
     */
    protected boolean isAnonRequest( DavServerRequest davRequest )
    {
        HttpServletRequest request = davRequest.getRequest();
        boolean isRead = WebdavMethodUtil.isReadMethod( request.getMethod() );

        if ( isRead )
        {
            /* if the repositories are public we do not require authentication for non-idisk requests */
            if ( config.getWebdav().isPublicRepositories() )
            {
                return true;
            }
        }

        return false;
    }
}
