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
 * XmlFileType 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.apache.maven.plugins.license.filetype.AbstractFileType"
 *                   role-hint="xml"
 */
public class XmlFileType
    extends AbstractFileType
{
    public boolean isSupported( File file )
    {
        return isExtension( file, "xml" );
    }

    public String formatLicense( String unformattedLicenseText )
    {
        // Escape the XML element characters.
        String escapedLicense = StringUtils.replace( unformattedLicenseText, "<", "&lt;" );
        escapedLicense = StringUtils.replace( escapedLicense, ">", "&gt;" );
        // escape the comment start/end deliminator
        escapedLicense = StringUtils.replace( escapedLicense, "--", "- -" );

        // format the license.
        return licenseUtils.formatLicense( escapedLicense, "<!--", "  ~ ", "  -->" );
    }

    public void injectLicense( File xmlFile, String unformattedLicenseText )
        throws LicenseInjectionException
    {
        String xmlComment = formatLicense( unformattedLicenseText );

        File backup = new File( xmlFile.getAbsolutePath() + "~" );
        backup.deleteOnExit();

        FileReader freader = null;
        FileWriter fwriter = null;

        try
        {
            FileUtils.copyFile( xmlFile, backup );

            freader = new FileReader( backup );
            fwriter = new FileWriter( xmlFile );

            String xmlContents = IOUtil.toString( freader );

            if ( StringUtils.isEmpty( xmlContents ) )
            {
                throw new IllegalStateException( "Unable to process empty xml." );
            }

            /* Intentionally NOT using an xml parser here.
             * Code trees can contain XML snippets and partial XML content.
             */

            int prologIdx = xmlContents.indexOf( "<?xml " );
            int rootIdx = findRegexIndexOf( xmlContents, "<[a-zA-Z]" );

            if ( prologIdx >= 0 )
            {
                // find end of prolog.
                prologIdx = xmlContents.indexOf( ">", prologIdx );
            }

            if ( rootIdx == ( -1 ) )
            {
                throw new IllegalStateException( "Unable to find root xml node element.  Is this really an xml file?" );
            }

            StringBuffer pombuf = new StringBuffer();
            if ( prologIdx >= 0 )
            {
                pombuf.append( xmlContents.substring( 0, prologIdx + 2 ) ); // +2 for ">\n"
            }
            pombuf.append( LINESEP );
            pombuf.append( xmlComment );
            pombuf.append( LINESEP );
            pombuf.append( xmlContents.substring( rootIdx ) );

            if ( !xmlContents.endsWith( LINESEP ) )
            {
                pombuf.append( LINESEP );
            }

            IOUtil.copy( pombuf.toString(), fwriter );
        }
        catch ( IOException e )
        {
            String emsg = "Unable to inject license into " + xmlFile.getAbsolutePath();
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
