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

import org.apache.maven.plugins.license.LicenseCheckException;
import org.apache.maven.plugins.license.LicenseInjectionException;
import org.apache.maven.shared.license.LicenseUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AbstractFileType 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractFileType
    extends AbstractLogEnabled
{
    public static final String ROLE = AbstractFileType.class.getName();

    protected final static String LINESEP = System.getProperty( "line.separator" );

    /**
     * @plexus.requirement
     */
    protected LicenseUtils licenseUtils;

    /**
     * Tests the provided file to see if it is supported by this file type filter.
     * 
     * @param file the file to test.
     * @return true if supported.
     */
    public abstract boolean isSupported( File file );

    /**
     * Injects the current liense into the file.
     * 
     * @param file the file to inject the license into.
     * @param unformattedLicenseText the unformatted license to inject.
     * @throws LicenseInjectionException if there was a problem injecting the license.
     */
    public abstract void injectLicense( File file, String unformattedLicenseText )
        throws LicenseInjectionException;

    public abstract String formatLicense( String unformattedLicense );

    /**
     * Tests the provided file to see if it has any of the provided licenses 
     * contained within the header of the file.
     * 
     * @param file the file to test.
     * @param unformattedLicenseTexts the array of potential licenses to check against.
     * @return true if a license was found that matched one of the provided license texts.
     */
    public boolean hasLicense( File xmlFile, String unformattedLicenseTexts[] )
        throws LicenseCheckException
    {
        FileReader freader = null;

        try
        {
            freader = new FileReader( xmlFile );
            String fileContents = IOUtil.toString( freader );

            for ( int i = 0; i < unformattedLicenseTexts.length; i++ )
            {
                String formattedLicense = formatLicense( unformattedLicenseTexts[i] );
                if ( hasSpecificLicenseText( fileContents, formattedLicense ) )
                {
                    return true;
                }
            }
        }
        catch ( IOException e )
        {
            String emsg = "Unable to check for license in " + xmlFile.getAbsolutePath();
            getLogger().info( emsg, e );
            throw new LicenseCheckException( emsg, e );
        }
        finally
        {
            IOUtil.close( freader );
        }

        return false;
    }

    protected boolean isExtension( File file, String extension )
    {
        if ( file == null )
        {
            return false;
        }

        String ext = FileUtils.extension( file.getName() );

        return StringUtils.equalsIgnoreCase( extension, ext );
    }

    public int findRegexIndexOf( String haystack, String regexNeedle )
    {
        return findRegexStart( haystack, regexNeedle, 0 );
    }
    
    public int findRegexStart( String haystack, String regexNeedle, int start )
    {
        Pattern pat = Pattern.compile( regexNeedle );
        Matcher mat = pat.matcher( haystack );

        if ( mat.find( start ) )
        {
            return mat.start();
        }

        return -1;
    }
    
    public int findRegexEnd( String haystack, String regexNeedle, int start )
    {
        Pattern pat = Pattern.compile( regexNeedle );
        Matcher mat = pat.matcher( haystack );

        if ( mat.find( start ) )
        {
            return mat.end();
        }

        return -1;
    }

    public boolean hasSpecificLicenseText( String fileContents, String licenseText )
    {
        return ( fileContents.indexOf( licenseText ) >= 0 );
    }
}
