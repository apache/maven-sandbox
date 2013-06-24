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

package org.apache.commons.plugins.digest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Creates digests (MD5 and SHA1 by default) for files specified
 * by the configured includes and excludes.
 * Also allows specification of a list of files on the command line.
 */
@Mojo( name = "digest")
public class DigestMojo extends AbstractMojo {

    @Component
    private MavenProject project;

    /**
     * List of files to include, default none.
     * Standard Maven wildcard patterns apply.
     * Patterns are assumed to be relative to the project base directory.
     */
    @Parameter
    private Set<String> includes;

    /**
     * List of files to exclude, default none.
     * Standard Maven wildcard patterns apply.
     * Patterns are assumed to be relative to the project base directory.
     */
    @Parameter
    private Set<String> excludes;

    /**
     * List of files to include, comma-separated (intended for command-line usage).
     * Overrides includes and excludes; uses same syntax as for {@code <include>}
     * Patterns are assumed to be relative to the project base directory.
     */
    @Parameter (property="digest.files")
    private String files;

    /**
     * Whether to create the MD5 hash, default {@code true}
     */
    @Parameter( property = "digest.md5", defaultValue = "true" )
    private boolean createMD5;

    /**
     * Whether to create the SHA1 hash, default {@code true}
     */
    @Parameter( property = "digest.sha1", defaultValue = "true" )
    private boolean createSHA1;

    /**
     * Whether to append ' *filename' to the hash in the generated file, default {@code false}
     */
    @Parameter( property = "digest.appendFilename", defaultValue = "false" )
    private boolean appendFilename;

    public void execute() throws MojoExecutionException {
        String files[] = scanForSources();
        Log log = getLog();
        if (files.length == 0) {
            log.warn("No files found. Please configure at least one <include> item or use -Ddigest.files");
        } else {
            try {
                for(String file : files) {
                    if (createMD5) {
                        createDigest("MD5", ".md5", file);
                    }
                    if (createSHA1) {
                        createDigest("SHA-1", ".sha1", file);
                    }
                }
            } catch (Exception ex) {
                throw new MojoExecutionException("Failed to create hash", ex);
            }
        }
    }

    private void createDigest(String algorithm, String extension, String file) throws Exception {
        FileInputStream is = new FileInputStream(file);
        PrintWriter pw = new PrintWriter(file+extension, "UTF-8");
        // Unfortunately DigestUtils.getDigest is not public
        pw.print(digestHex(MessageDigest.getInstance(algorithm), is));
        if (appendFilename) {
            pw.println(" *" + file);
        } else {
            pw.println();            
        }
        is.close();
        pw.close();
    }

    private static String digestHex(MessageDigest digest, InputStream data) throws IOException {
        return Hex.encodeHexString(digest(digest, data));
    }

    // Unfortunately, the Codec version is private
    private static byte[] digest(MessageDigest digest, InputStream data) throws IOException {
        byte[] buffer = new byte[1024];
        int read = data.read(buffer);
        while (read > -1) {
            digest.update(buffer, 0, read);
            read = data.read(buffer);
        }
        return digest.digest();
    }

    private String[]  scanForSources() {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setFollowSymlinks( true );
        ds.setBasedir( project.getBasedir() ); // Cannot be omitted; implies that includes/excludes are relative
        String[] inc;
        if (files != null) { // Overrides includes / excludes
            getLog().debug("files="+files);
            inc=files.split(",");
        } else {
            if ( includes == null || includes.isEmpty() ) {
                inc = new String[0]; // overrides default of **
            } else {
                inc = includes.toArray( new String[includes.size()] );
            }
            if (excludes != null) {
                String[] excl = excludes.toArray( new String[excludes.size()] );
                ds.setExcludes( excl );
            }
        }
        ds.setIncludes( inc );
        ds.addDefaultExcludes(); // TODO should this be optional?
        ds.scan();
        return ds.getIncludedFiles();
    }
}
