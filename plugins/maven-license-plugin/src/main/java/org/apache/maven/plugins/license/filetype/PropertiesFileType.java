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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * PropertiesFileType 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.apache.maven.plugins.license.filetype.AbstractFileType"
 *                   role-hint="properties"
 */
public class PropertiesFileType
    extends AbstractFileType
{
    public boolean isSupported( File file )
    {
        return isExtension( file, "properties" );
    }

    public String formatLicense( String unformattedLicenseText )
    {
        // Escape the XML element characters.
        String escapedLicense = StringUtils.replace( unformattedLicenseText, "<", "&lt;" );
        escapedLicense = StringUtils.replace( escapedLicense, ">", "&gt;" );
        // escape the comment start/end deliminator
        escapedLicense = StringUtils.replace( escapedLicense, "--", "- -" );

        // format the license.
        return licenseUtils.formatLicense( escapedLicense, "#", "# ", "#" );
    }

    public void injectLicense( File propertiesFile, String unformattedLicenseText )
        throws LicenseInjectionException
    {
        String xmlComment = formatLicense( unformattedLicenseText );

        File backup = new File( propertiesFile.getAbsolutePath() + "~" );
        backup.deleteOnExit();

        FileReader freader = null;
        FileWriter fwriter = null;

        try
        {
            FileUtils.copyFile( propertiesFile, backup );

            freader = new FileReader( backup );
            fwriter = new FileWriter( propertiesFile );

            boolean foundContent = false;

            PrintWriter propOut = new PrintWriter( fwriter );
            BufferedReader buf = new BufferedReader( freader );
            String line = buf.readLine();
            while ( line != null )
            {
                if ( foundContent )
                {
                    propOut.println( line );
                }
                else
                {
                    if ( !line.trim().startsWith( "#" ) )
                    {
                        foundContent = true;
                        propOut.println( xmlComment );
                    }
                }

                line = buf.readLine();
            }
            propOut.flush();
        }
        catch ( IOException e )
        {
            String emsg = "Unable to inject license into " + propertiesFile.getAbsolutePath();
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
