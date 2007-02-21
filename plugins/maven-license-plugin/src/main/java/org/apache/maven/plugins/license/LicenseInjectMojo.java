package org.apache.maven.plugins.license;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.license.filetype.AbstractFileType;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.Iterator;

/**
 * LicenseInjectMojo 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @goal inject
 */
public class LicenseInjectMojo
    extends AbstractLicenseMojo
{
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        String unformattedLicense = licenseUtils.getLicenseContent( settings, project, 0 );

        if ( StringUtils.isEmpty( unformattedLicense ) )
        {
            throw new MojoExecutionException( "Not able to inject license.  License file not found." );
        }

        ensureDefaultFilesets();

        fileSetManager = new FileSetManager( getLog(), verbose );

        if ( filesets != null && !filesets.isEmpty() )
        {
            for ( Iterator it = filesets.iterator(); it.hasNext(); )
            {
                Fileset fileset = (Fileset) it.next();
                File filesetDir = new File( fileset.getDirectory() );

                String files[] = fileSetManager.getIncludedFiles( fileset );

                for ( int i = 0; i < files.length; i++ )
                {
                    processFile( new File( filesetDir, files[i] ), unformattedLicense );
                }
            }
        }
    }

    private void processFile( File sourceFile, String unformattedLicense )
    {
        Iterator it = filetypeHandlers.iterator();
        while ( it.hasNext() )
        {
            AbstractFileType filetype = (AbstractFileType) it.next();
            if ( filetype.isSupported( sourceFile ) )
            {
                try
                {
                    filetype.injectLicense( sourceFile, unformattedLicense );
                }
                catch ( LicenseInjectionException e )
                {
                    getLog().warn( "Unable to inject license file (error processing): " + sourceFile.getAbsolutePath() );
                }
                return;
            }
        }

        // if we reached this point, we didn't inject
        getLog().info( "Unable to inject license file (unhandled file type): " + sourceFile.getAbsolutePath() );
    }
}
