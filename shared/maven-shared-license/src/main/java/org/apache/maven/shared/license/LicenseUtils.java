package org.apache.maven.shared.license;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.validator.UrlValidator;
import org.apache.maven.model.License;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * LicenseUtils 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.apache.maven.shared.license.LicenseUtils"
 */
public class LicenseUtils
    extends AbstractLogEnabled
{
    private final static String LINESEP = System.getProperty( "line.separator" );
    
    public String getLicenseContent( Settings settings, MavenProject project )
    {
        return getLicenseContent( settings, project, 0 );
    }

    public String getLicenseContent( Settings settings, MavenProject project, int index )
    {
        List licenses = project.getLicenses();

        if ( ( licenses == null ) || licenses.isEmpty() )
        {
            getLogger().warn( "No licenses have been defined in the pom." );
            return null;
        }

        License license = (License) licenses.get( index );

        String url = license.getUrl();

        if ( url != null )
        {
            URL licenseUrl = null;
            UrlValidator urlValidator = new UrlValidator( UrlValidator.ALLOW_ALL_SCHEMES );
            // UrlValidator does not accept file URLs because the file
            // URLs do not contain a valid authority (no hostname).
            // As a workaround accept license URLs that start with the
            // file scheme.
            if ( urlValidator.isValid( url ) || url.startsWith( "file://" ) )
            {
                try
                {
                    licenseUrl = new URL( url );
                }
                catch ( MalformedURLException e )
                {
                    getLogger().error( "The license url [" + url + "] seems to be invalid: " + e.getMessage() );
                    return null;
                }
            }
            else
            {
                File scanDir = project.getBasedir();
                File licenseFile = new File( scanDir, url );

                while ( true )
                {
                    if ( licenseFile.exists() )
                    {
                        // Found it!
                        break;
                    }

                    if ( !licenseFile.exists() )
                    {
                        // Workaround to allow absolute path names while
                        // staying compatible with the way it was...
                        licenseFile = new File( url );
                    }

                    if ( !licenseFile.exists() )
                    {
                        scanDir = scanDir.getParentFile();
                        if ( scanDir == null )
                        {
                            getLogger().error( "Maven can't find the file " + licenseFile + " on the system." );
                            return null;
                        }

                        // Try the directory above this.
                        licenseFile = new File( scanDir, url );
                    }
                }

                try
                {
                    licenseUrl = licenseFile.toURL();
                }
                catch ( MalformedURLException e )
                {
                    getLogger().error( "The license url [" + url + "] seems to be invalid: " + e.getMessage() );
                    return null;
                }
            }

            if ( licenseUrl != null )
            {
                String licenseContent = null;
                try
                {
                    licenseContent = getLicenseInputStream( settings, licenseUrl );
                }
                catch ( IOException e )
                {
                    getLogger().error( "Can't read the url [" + licenseUrl + "] : " + e.getMessage() );
                    return null;
                }

                if ( licenseContent != null )
                {
                    // TODO: we should check for a text/html mime type instead, and possibly use a html parser to do this a bit more cleanly/reliably.
                    String licenseContentLC = licenseContent.toLowerCase();
                    int bodyStart = licenseContentLC.indexOf( "<body" );
                    int bodyEnd = licenseContentLC.indexOf( "</body>" );
                    if ( ( licenseContentLC.startsWith( "<!doctype html" ) || licenseContentLC.startsWith( "<html>" ) )
                        && bodyStart >= 0 && bodyEnd >= 0 )
                    {
                        bodyStart = licenseContentLC.indexOf( ">", bodyStart ) + 1;
                        String body = licenseContent.substring( bodyStart, bodyEnd );

                        return body;
                    }
                    else
                    {
                        return licenseContent;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the content of the license Url
     *
     * @param licenseUrl
     * @return the content of the licenseUrl
     */
    private String getLicenseInputStream( Settings settings, URL licenseUrl )
        throws IOException
    {
        String scheme = licenseUrl.getProtocol();
        if ( !"file".equals( scheme ) )
        {
            Proxy proxy = settings.getActiveProxy();
            if ( proxy != null )
            {
                if ( "http".equals( scheme ) || "https".equals( scheme ) )
                {
                    scheme = "http.";
                }
                else if ( "ftp".equals( scheme ) )
                {
                    scheme = "ftp.";
                }
                else
                {
                    scheme = "";
                }

                String host = proxy.getHost();
                if ( !StringUtils.isEmpty( host ) )
                {
                    Properties p = System.getProperties();
                    p.setProperty( scheme + "proxySet", "true" );
                    p.setProperty( scheme + "proxyHost", host );
                    p.setProperty( scheme + "proxyPort", String.valueOf( proxy.getPort() ) );
                    if ( !StringUtils.isEmpty( proxy.getNonProxyHosts() ) )
                    {
                        p.setProperty( scheme + "nonProxyHosts", proxy.getNonProxyHosts() );
                    }

                    final String userName = proxy.getUsername();
                    if ( !StringUtils.isEmpty( userName ) )
                    {
                        final String pwd = StringUtils.isEmpty( proxy.getPassword() ) ? "" : proxy.getPassword();
                        Authenticator.setDefault( new Authenticator()
                        {
                            protected PasswordAuthentication getPasswordAuthentication()
                            {
                                return new PasswordAuthentication( userName, pwd.toCharArray() );
                            }
                        } );
                    }
                }
            }
        }

        InputStream in = null;
        try
        {
            in = licenseUrl.openStream();
            // TODO: All licenses are supposed to be in English ??
            return IOUtil.toString( in, "ISO-8859-1" );
        }
        finally
        {
            IOUtil.close( in );
        }
    }

    public String formatLicense( String licenseContent, String header, String prefix, String footer )
    {
        StringBuffer sb = new StringBuffer();

        StringReader sreader = new StringReader( licenseContent );
        BufferedReader buf = new BufferedReader( sreader );

        try
        {
            sb.append( header ).append( LINESEP );
            String line = buf.readLine();
            while ( line != null )
            {
                sb.append( prefix ).append( StringUtils.stripEnd( line, null ) ).append( LINESEP );
                line = buf.readLine();
            }
            sb.append( footer ).append( LINESEP );
        }
        catch ( IOException e )
        {
            // ignore
        }

        return sb.toString();
    }
}
