/*
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

package org.apache.maven.plugins.digest;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Checks digests (MD5 and SHA1 by default) for files specified by the configured includes and excludes.
 * Also allows specification of a list of files on the command line.
 */
@Mojo( name = "check", requiresProject=false )
public class DigestCheckMojo
    extends AbstractDigestMojo
{

    // ----------------------------------------------------------------------
    // Mojo components
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Mojo options
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException
    {
        try
        {
            if (!super.process()) {
                throw new MojoExecutionException( "Failed one or more digest checks");                
            }
        }
        catch ( Exception ex )
        {
            throw new MojoExecutionException( "Failed to check digest", ex );
        }
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    @Override
    protected boolean processFile( String algorithm, String extension, String file )
        throws Exception
    {
        final Log log = getLog();
        final String algoPath = algorithm + " (" + extension + ")";
        final String digest = createDigest( algorithm, extension, file );
        boolean success = false;
        try
        {
            final FileInputStream is = new FileInputStream( file + extension );
            final byte buffer[] = new byte[1024];// should be enough for any hash file
            final int size = is.read(buffer);
            is.close();
            final String hash = new String(buffer, 0, size, "UTF-8").trim();
            if (hash.contains( digest ))
            {
                success = true;
                log.info( file + ": " + algoPath + " is OK" );
            }
            else
            {
                log.warn( file + ": " + algoPath + ". Expected " + digest + " Actual " + hash);
            }
        }
        catch ( IOException e )
        {
            log.warn( file + ": could not process " + algoPath + " : " + e.getMessage() );
        }
        return success;
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Static methods
    // ----------------------------------------------------------------------
}
