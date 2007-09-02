package org.apache.maven.doxia.linkcheck.validation;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;

import java.net.URL;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Checks links which are normal URLs
 *
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @version $Id$
 */
public final class OnlineHTTPLinkValidator extends HTTPLinkValidator
{
    /** Log for debug output. */
    private static final Log LOG = LogFactory.getLog( OnlineHTTPLinkValidator.class );

    /** The maximum number of redirections for a link. */
    private static final int MAX_NB_REDIRECT = 10;

    /** Use the get method to test pages. */
    private static final String GET_METHOD = "get";

    /** Use the head method to test pages. */
    private static final String HEAD_METHOD = "head";

    /** The proxy host. */
    private String proxyHost;

    /** The proxy port. */
    private int proxyPort;

    /** The proxy user. */
    private String proxyUser;

    /** The proxy pass. */
    private String proxyPass;

    /** The proxy NtlmHost. */
    private String proxyNtlmHost;

    /** The proxy NtlmDomain. */
    private String proxyNtlmDomain;

    /** The http method to use. */
    private String method = HEAD_METHOD;

    /** The HttpClient. */
    private transient HttpClient cl;

    /**
     * Constructor: initialize settings, use "head" method.
     */
    public OnlineHTTPLinkValidator()
    {
        this( HEAD_METHOD );
    }

    /**
     * Constructor: initialize settings.
     *
     * @param method The http method to use. Should be one of "head" or "get".
     */
    public OnlineHTTPLinkValidator( String method )
    {
        this( method, null, null, null, null, null, null );
    }

    /**
     * Constructor: initialize settings.
     *
     * @param method The http method to use. Should be one of "head" or "get".
     * @param pHost The proxy host.
     * @param pPort The proxy port.
     * @param pUser The proxy user.
     * @param pPass The proxy pass.
     * @param pNtlmHost The proxy NtlmHost.
     * @param pNtlmDomain The proxy NtlmDomain.
     */
    public OnlineHTTPLinkValidator( String method, String pHost, String pPort, String pUser,
                                    String pPass, String pNtlmHost, String pNtlmDomain )
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "Will use method : [" + method + "]" );
        }

        this.method = method;

        if ( pHost == null || pHost.trim().equals( "" ) )
        {
            this.proxyHost = null;
        }
        else
        {
            this.proxyHost = pHost;

            if ( pPort != null )
            {
                try
                {
                    this.proxyPort = Integer.parseInt( pPort );
                }
                catch ( NumberFormatException e )
                {
                    LOG.warn( "Invalid number for Proxy Port:" + pPort );
                    LOG.warn( "Proxy Port won't be used." );
                    this.proxyPort = -1;
                }
            }

            this.proxyUser = pUser;

            this.proxyPass = pPass;

            if ( pNtlmHost != null && !pNtlmHost.trim().equals( "" ) )
            {
                this.proxyNtlmHost = pNtlmHost;
                this.proxyNtlmDomain = pNtlmDomain;
            }

        }

        initHttpClient();
    }

    /** {@inheritDoc} */
    public LinkValidationResult validateLink( LinkValidationItem lvi )
    {
        if ( this.cl == null )
        {
            initHttpClient();
        }

        try
        {
            String link = lvi.getLink();

            HttpMethod hm = null;

            try
            {
                hm = checkLink( link, 0 );
            }
            catch ( Throwable t )
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.error( "Received: [" + t + "] for [" + lvi.getLink() + "] in page [" + lvi.getSource() + "]", t );
                }
                else
                {
                    LOG.error( "Received: [" + t + "] for [" + lvi.getLink() + "] in page [" + lvi.getSource() + "]" );
                }

                return new LinkValidationResult( LinkValidationResult.ERROR, false, t.getClass().getName() + " : "
                                + t.getMessage() );
            }

            if ( hm == null )
            {
                return new LinkValidationResult( LinkValidationResult.ERROR, false, "Cannot retreive HTTP Status" );
            }

            if ( hm.getStatusCode() == HttpStatus.SC_OK )
            {
                return new LinkValidationResult( LinkValidationResult.VALID, true, hm.getStatusCode() + " "
                                + hm.getStatusText() );
            }
            else
            {
                // If there's a redirection ... add a warning
                if ( hm.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
                                || hm.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
                                || hm.getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT )
                {
                    LOG.warn( "Received: [" + hm.getStatusCode() + "] for [" + lvi.getLink() + "] in page ["
                                    + lvi.getSource() + "]" );

                    return new LinkValidationResult( LinkValidationResult.WARNING, true, hm.getStatusCode() + " "
                                    + hm.getStatusText() );
                }
                else
                {
                    LOG.error( "Received: [" + hm.getStatusCode() + "] for [" + lvi.getLink() + "] in page ["
                                    + lvi.getSource() + "]" );

                    return new LinkValidationResult( LinkValidationResult.ERROR, false, hm.getStatusCode() + " "
                                    + hm.getStatusText() );
                }
            }

        }
        catch ( Throwable t )
        {
            if ( LOG.isDebugEnabled() )
            {
                LOG.error( "Received: [" + t + "] for [" + lvi.getLink() + "] in page [" + lvi.getSource() + "]", t );
            }
            else
            {
                LOG.error( "Received: [" + t + "] for [" + lvi.getLink() + "] in page [" + lvi.getSource() + "]" );
            }

            return new LinkValidationResult( LinkValidationResult.ERROR, false, t.getMessage() );
        }
    }

    /** Initialize the HttpClient. */
    private void initHttpClient()
    {
        LOG.debug( "A new HttpClient instance is needed ..." );

        // Some web servers don't allow the default user-agent sent by httpClient
        System.setProperty( "httpclient.useragent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)" );

        this.cl = new HttpClient( new MultiThreadedHttpConnectionManager() );

        HostConfiguration hc = new HostConfiguration();

        HttpState state = new HttpState();

        if ( this.proxyHost != null )
        {
            hc.setProxy( this.proxyHost, this.proxyPort );

            if ( LOG.isDebugEnabled() )
            {
                LOG.debug( "Proxy Host:" + this.proxyHost );
                LOG.debug( "Proxy Port:" + this.proxyPort );
            }

            if ( this.proxyUser != null && this.proxyPass != null )
            {
                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "Proxy User:" + this.proxyUser );
                }

                Credentials credentials;

                if ( this.proxyNtlmHost != null )
                {
                    credentials =
                        new NTCredentials( this.proxyUser, this.proxyPass, this.proxyNtlmHost, this.proxyNtlmDomain );
                }
                else
                {
                    credentials = new UsernamePasswordCredentials( this.proxyUser, this.proxyPass );
                }

                state.setProxyCredentials( null, null, credentials );
            }

        }
        else
        {
            LOG.debug( "Not using a proxy" );
        }

        this.cl.setHostConfiguration( hc );

        this.cl.setState( state );

        LOG.debug( "New HttpClient instance created." );
    }

    /**
     * Checks the given link.
     *
     * @param link the link to check.
     * @param nbRedirect the number of current redirects.
     * @return HttpMethod
     * @throws IOException if something goes wrong.
     */
    private HttpMethod checkLink( String link, int nbRedirect )
        throws IOException
    {
        if ( nbRedirect > MAX_NB_REDIRECT )
        {
            throw new HttpException( "Maximum number of redirections (" + MAX_NB_REDIRECT + ") exceeded" );
        }

        HttpMethod hm;

        if ( HEAD_METHOD.equals( this.method ) )
        {
            hm = new HeadMethod( link );
        }
        else if ( GET_METHOD.equals( this.method ) )
        {
            hm = new GetMethod( link );
        }
        else
        {
            LOG.error( "Unsupported method: " + method + ", using 'get'." );
            hm = new GetMethod( link );
        }

        try
        {
            // We want to do it manually
            hm.setFollowRedirects( false );

            URL url = new URL( link );

            cl.getHostConfiguration().setHost( url.getHost(), url.getPort(), url.getProtocol() );

            cl.executeMethod( hm );

            StatusLine sl = hm.getStatusLine();

            if ( sl == null )
            {
                LOG.error( "Unknown error validating link : " + link );
                return null;
            }

            if ( hm.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
                            || hm.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
                            || hm.getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT )
            {
                Header locationHeader = hm.getResponseHeader( "location" );

                if ( locationHeader == null )
                {
                    LOG.error( "Site sent redirect, but did not set Location header" );
                    return hm;
                }
                else
                {
                    String newLink = locationHeader.getValue();

                    // Be careful to absolute/relative links
                    if ( !newLink.startsWith( "http://" ) && !newLink.startsWith( "https://" ) )
                    {
                        if ( newLink.startsWith( "/" ) )
                        {
                            URL oldUrl = new URL( link );

                            newLink =
                                oldUrl.getProtocol() + "://" + oldUrl.getHost()
                                                + ( oldUrl.getPort() > 0 ? ":" + oldUrl.getPort() : "" ) + newLink;
                        }
                        else
                        {
                            newLink = link + newLink;
                        }
                    }

                    HttpMethod oldHm = hm;

                    if ( LOG.isDebugEnabled() )
                    {
                        LOG.debug( "[" + link + "] is redirected to [" + newLink + "]" );
                    }

                    oldHm.releaseConnection();

                    hm = checkLink( newLink, nbRedirect + 1 );

                    // Restore the hm to "Moved permanently" | "Moved temporarily" | "Temporary redirect"
                    // if the new location is found to allow us to report it
                    if ( hm.getStatusCode() == HttpStatus.SC_OK && nbRedirect == 0 )
                    {
                        return oldHm;
                    }
                }
            }

        }
        finally
        {
            hm.releaseConnection();
        }

        return hm;
    }

}
