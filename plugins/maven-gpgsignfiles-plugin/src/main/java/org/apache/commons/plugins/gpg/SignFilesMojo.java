/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.apache.commons.plugins.gpg;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.gpg.GpgSigner;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

// N.B. The original version of this file has been attached to MGPG-43 (Codehaus)
//      Please ensure any code changes are applied there too; this copy is intended to be temporary

/**
 * Signs the specified files.
 * Uses the same code as the Maven GPG Plugin, but allows arbitrary lists of files to be signed
 * according to the configured include and exclude settings.
 * Alternatively the list of files can be provided as a command-line parameter.
 */
@Mojo( name = "signfiles" )
public class SignFilesMojo extends AbstractGpgMojo {

    @Component  ( role = MavenProject.class )
    private MavenProject project;

    /**
     * The directory where to store signature files.
     */
    @Parameter( property = "gpg.ascDirectory" )
    private File ascDirectory;

    /**
     * List of files to include, default none.
     */
    @Parameter
    private Set<String> includes;

    /**
     * List of files to exclude, default none.
     */
    @Parameter
    private Set<String> excludes;

    /**
     * List of files to include, comma-separated (intended for command-line usage).
     * Overrides includes and excludes; uses same syntax as for {@code <include>}
     */
    @Parameter (property="gpg.files")
    private String files;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Log logger = getLog();
        String[] sourceFiles = scanForSources();
        if (sourceFiles.length == 0) {
            logger.warn("No files found. Please configure at least one <include> item or use -Dgpg.files");
        } else {
            GpgSigner signer = newSigner( project ); // copies most of the details from AbstractGpgMojo
            signer.setOutputDirectory(ascDirectory);
            signer.setBuildDirectory( new File( project.getBuild().getDirectory() ) );
            signer.setBaseDirectory( project.getBasedir() );
            for(String file : sourceFiles) {
                logger.debug("Signing: "+file);
                File signed = signer.generateSignatureForArtifact( new File(file) );            
                logger.debug("Signed : "+signed);
            }
        }
    }

    private String[]  scanForSources() {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setFollowSymlinks( true );
        ds.setBasedir( project.getBasedir() );
        String[] inc;
        if (files != null) {
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
