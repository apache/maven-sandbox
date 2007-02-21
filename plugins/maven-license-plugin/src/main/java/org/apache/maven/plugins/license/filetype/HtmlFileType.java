package org.apache.maven.plugins.license.filetype;

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

import org.apache.maven.plugins.license.LicenseInjectionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * HtmlFileType 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.apache.maven.plugins.license.filetype.AbstractFileType"
 *                   role-hint="html"
 */
public class HtmlFileType
    extends AbstractFileType
{
    public boolean isSupported( File file )
    {
        return isExtension( file, "html" ) || isExtension( file, "htm" );
    }

    public String formatLicense( String unformattedLicense )
    {
        // escape the HTML element characters.
        String escapedLicense = StringUtils.replace( unformattedLicense, "<", "&lt;" );
        escapedLicense = StringUtils.replace( escapedLicense, ">", "&gt;" );
        // escape the comment start/end deliminator
        escapedLicense = StringUtils.replace( escapedLicense, "--", "-&#8722;" );

        // format the license.
        return licenseUtils.formatLicense( escapedLicense, "<!--", "  ~ ", "  -->" );
    }

    public void injectLicense( File htmlFile, String unformattedLicenseText )
        throws LicenseInjectionException
    {
        String htmlComment = formatLicense( unformattedLicenseText );

        File backup = new File( htmlFile.getAbsolutePath() + "~" );
        backup.deleteOnExit();

        FileReader freader = null;
        FileWriter fwriter = null;

        try
        {
            FileUtils.copyFile( htmlFile, backup );

            freader = new FileReader( backup );
            fwriter = new FileWriter( htmlFile );

            String htmlContents = IOUtil.toString( freader );

            if ( StringUtils.isEmpty( htmlContents ) )
            {
                throw new IllegalStateException( "Unable to process empty xml." );
            }

            /* Intentionally NOT using an xml parser here.
             * Code trees can contain XML snippets and partial XML content.
             */

            int headerSkip = 0;
            int contentStart = 0;

            // Skip xml prolog (if present)
            int prologIdx = htmlContents.indexOf( "<?xml " );
            if ( prologIdx >= 0 )
            {
                // find end of prolog.
                headerSkip = htmlContents.indexOf( ">", prologIdx ) + 1;
            }

            // Skip doctype (if present)
            int doctypeIdx = htmlContents.indexOf( "<!DOCTYPE" );
            if ( doctypeIdx >= 0 )
            {
                headerSkip = htmlContents.indexOf( ">", doctypeIdx ) + 1;
            }

            int rootIdx = findRegexIndexOf( htmlContents, "<[a-zA-Z]" );

            if ( rootIdx == ( -1 ) )
            {
                // Unable to find an HTML element.
                contentStart = headerSkip;
            }
            else
            {
                // Found an HTML element.
                contentStart = rootIdx;
            }

            // Is there an old html comment between the header and the first element?
            int oldComment = htmlContents.indexOf( "<!--", headerSkip );
            if ( ( oldComment > headerSkip ) && ( oldComment < rootIdx ) )
            {
                // Found an old html comment.
                // Lets find the end of it now.
                int oldCommentEnd = htmlContents.lastIndexOf( ">", rootIdx - 1 );
                if ( oldCommentEnd > headerSkip )
                {
                    headerSkip = oldCommentEnd;
                }
            }

            StringBuffer pombuf = new StringBuffer();
            if ( headerSkip > 0 )
            {
                pombuf.append( htmlContents.substring( 0, headerSkip ) );
            }
            pombuf.append( LINESEP );
            pombuf.append( htmlComment );
            pombuf.append( LINESEP );
            pombuf.append( htmlContents.substring( contentStart ) );

            if ( !htmlContents.endsWith( LINESEP ) )
            {
                pombuf.append( LINESEP );
            }

            IOUtil.copy( pombuf.toString(), fwriter );
        }
        catch ( IOException e )
        {
            String emsg = "Unable to inject license into " + htmlFile.getAbsolutePath();
            getLogger().info( emsg, e );
            throw new LicenseInjectionException( emsg, e );
        }
        finally
        {
            IOUtil.close( freader );
            IOUtil.close( fwriter );
        }
    }
}
