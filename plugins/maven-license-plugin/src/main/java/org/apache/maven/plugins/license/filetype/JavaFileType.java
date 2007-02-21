package org.apache.maven.plugins.license.filetype;

import org.apache.maven.plugins.license.LicenseInjectionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaFileType 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.apache.maven.plugins.license.filetype.AbstractFileType"
 *                   role-hint="java"
 */
public class JavaFileType
    extends AbstractFileType
{
    public boolean isSupported( File file )
    {
        return isExtension( file, "java" );
    }

    public String formatLicense( String unformattedLicense )
    {
        // break java closing quote.  
        String escapedLicense = StringUtils.replace( unformattedLicense, "*/", "* /" );

        // format the license.
        return licenseUtils.formatLicense( escapedLicense, "/*", " * ", " */" );
    }

    public void injectLicense( File javaFile, String unformattedLicenseText )
        throws LicenseInjectionException
    {
        String javaComment = formatLicense( unformattedLicenseText );

        File backup = new File( javaFile.getAbsolutePath() + "~" );
        backup.deleteOnExit();

        FileReader freader = null;
        FileWriter fwriter = null;

        try
        {
            FileUtils.copyFile( javaFile, backup );

            freader = new FileReader( backup );
            fwriter = new FileWriter( javaFile );

            String javaContents = IOUtil.toString( freader );

            if ( StringUtils.isEmpty( javaContents ) )
            {
                throw new IllegalStateException( "Unable to process empty java." );
            }

            int packageSkip = 0;
            int contentStart = 0;

            // Skip package declaration
            int packageIdx = findRegexEnd( javaContents, "\\s?package\\s*[a-zA-Z0-9.]*;\\s*", 0 );
            if ( packageIdx >= 0 )
            {
                // find end of package definition.
                packageSkip = packageIdx;
                contentStart = packageIdx;
            }

            KeywordFinder finder = new KeywordFinder( javaContents );
            boolean found = false;

            while ( !found )
            {
                String keyword = finder.getNextKeyword();
                if ( keyword == null )
                {
                    break;
                }

                if ( StringUtils.equals( "import", keyword ) || StringUtils.equals( "class", keyword )
                    || StringUtils.equals( "interface", keyword ) )
                {

                }
            }

            int importIdx = findRegexStart( javaContents, "\\simport\\s", contentStart );
            if ( importIdx >= 0 )
            {
                // Found an import line!
                contentStart = importIdx;
            }

            // Is there an old java comment between the header and the first element?
            int oldComment = javaContents.indexOf( "/*", packageSkip );
            if ( oldComment > packageSkip )
            {
                // Found an old java comment.
                // Lets find the end of it now.
                int oldCommentEnd = javaContents.lastIndexOf( "*/", contentStart - 1 );
                if ( oldCommentEnd > packageSkip )
                {
                    packageSkip = oldCommentEnd;
                }
            }

            StringBuffer pombuf = new StringBuffer();
            if ( packageSkip > 0 )
            {
                pombuf.append( javaContents.substring( 0, packageSkip ) );
            }

            pombuf.append( javaComment );
            pombuf.append( LINESEP );
            pombuf.append( javaContents.substring( contentStart ) );

            if ( !javaContents.endsWith( LINESEP ) )
            {
                pombuf.append( LINESEP );
            }

            IOUtil.copy( pombuf.toString(), fwriter );
        }
        catch ( IOException e )
        {
            String emsg = "Unable to inject license into " + javaFile.getAbsolutePath();
            getLogger().info( emsg, e );
            throw new LicenseInjectionException( emsg, e );
        }
        finally
        {
            IOUtil.close( freader );
            IOUtil.close( fwriter );
        }
    }

    class KeywordFinder
    {
        private Pattern pat;

        private Matcher mat;

        private int offset;

        public KeywordFinder( String javasource )
        {
            pat = Pattern.compile( "\\b\\([a-zA-Z0-9_]{1,}\\)\\b" );
            mat = pat.matcher( javasource );
        }

        public String getNextKeyword()
        {
            if ( mat.find( offset ) )
            {
                offset = mat.end();
                return mat.group();
            }

            return null;
        }

        public void skipToClose()
        {
            
        }
    }
}
