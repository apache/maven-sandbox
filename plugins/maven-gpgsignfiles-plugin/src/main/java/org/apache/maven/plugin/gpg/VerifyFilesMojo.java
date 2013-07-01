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

package org.apache.maven.plugin.gpg;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

////////////////// DRAFT DRAFT DRAFT ///////////////////////

/**
 * Verifies the specified files.
 *  
 * TODO - should it check all files of all types in a folder? Like digest verify?
 * i.e. if name does not end with .asc or .sig then append that and pass to gpg
 */
@Mojo( name = "verifyfiles", requiresProject=false )
public class VerifyFilesMojo extends AbstractGpgMojo {

    @Component  ( role = MavenProject.class )
    private MavenProject project;

    /**
     * The path to the GnuPG executable to use for artifact signing. Defaults to either "gpg" or "gpg.exe" depending on
     * the operating system.
     * Overrides the super-class field which is private and so not accessible
     */
    @Parameter( property = "gpg.executable" )
    private String executable;

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
        final Log log = getLog();
        final String[] sourceFiles = scanForSources();
        if (sourceFiles.length == 0) {
            log.warn("No files found. Please configure at least one <include> item or use -Dgpg.files");
        } else {
        	// This seems to work:
        	// mkdir gpg # work area
        	// gpg --no-default-keyring --home gpg --primary-keyring key.pub --import keys.txt
        	// gpg --no-default-keyring --home gpg --primary-keyring key.pub --verify file.asc
        	// rmdir gpg
        	//
        	final File gpgDir = new File("gpg.tmp"); // TODO make this an option/move elsewere?
        	if (!gpgDir.mkdir() || !gpgDir.isDirectory()) {
        		throw new MojoExecutionException("Could not create " + gpgDir);
        	}
        	try {
        		importKeys(gpgDir, new File("KEYS.txt"), getLog()); // TODO option
        		verifyFiles(log, sourceFiles, gpgDir);
        	} finally {
        		for(File t : gpgDir.listFiles()) {
        			if (!t.delete() || t.exists()) {
        				log.warn("Failed to delete: " + t);
        			}
        		}
    			if (!gpgDir.delete() || gpgDir.exists()) {
    				log.warn("Failed to delete: " + gpgDir);
    			}
        	}
        }
    }

	private void importKeys(File gpgDir, File keys, Log log) throws MojoExecutionException {
	    Commandline cmd = createCommandStem(gpgDir);
		cmd.createArg().setValue("--import");
	    cmd.createArg().setFile( keys);
	    if (!exec(cmd, log)) {
	    	log.warn("Failed to import " + keys);
	    }
	}

	private void verifyFiles(final Log log, final String[] sourceFiles, File gpgDir)
			throws MojoExecutionException {
		int sigCount = 0; // number of sigs checked
		int sigFail = 0; // number of sigs failed
		for (String file : sourceFiles) {
			sigCount++;
		    log.info("Verifying: "+file);
		    Commandline cmd = createCommandStem(gpgDir);
			cmd.createArg().setValue("--verify");
		    cmd.createArg().setValue( file );
		    if (!exec(cmd, log)) {
		    	sigFail++;
		    }
		}
		log.info("Number of files checked: " + sigCount);
		if (sigFail > 0) {
			throw new MojoExecutionException("Number of files which failed to verify: " + sigFail);
		}
	}

	private Commandline createCommandStem(File gpgDir) {
		Commandline cmd = new Commandline();

		if ( StringUtils.isNotEmpty( executable ) ) {
		    cmd.setExecutable( executable );
		} else {
		    cmd.setExecutable( "gpg" + ( Os.isFamily( Os.FAMILY_WINDOWS ) ? ".exe" : "" ) );
		}

		cmd.createArg().setValue("--no-default-keyring");
		cmd.createArg().setValue("--home");
		cmd.createArg().setFile(gpgDir);
		cmd.createArg().setValue("--primary-keyring");
		cmd.createArg().setValue( "keys.pub" );
		return cmd;
	}

	private boolean exec(Commandline cmd, Log log) throws MojoExecutionException {
		boolean success = false;
	    try
	    {
	    	final List<String> lines = new ArrayList<String>();
	    	final StreamConsumer sc = new StreamConsumer() {
				public void consumeLine(String line) {
					lines.add(line);
				}
	    	};
	        int exitCode = CommandLineUtils.executeCommandLine( cmd, null, null, sc );

	        if ( exitCode != 0 )
	        {
	            for(String line : lines) {
	            	log.warn(line);
	            }
	            log.warn("Exit code: " + exitCode);
	        } else {
	            success = true;
	            for(String line : lines) { // TODO add quiet option
	            	log.info(line);
	            }	                	
	        }
	    }
	    catch ( CommandLineException e )
	    {
	        throw new MojoExecutionException( "Unable to execute gpg command", e );
	    }
	    return success;
	}

	private String[]  scanForSources() {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setFollowSymlinks( true );
        File basedir = project.getBasedir();
        if (basedir == null) {
            basedir = new File("."); // current directory
        }
        ds.setBasedir( basedir ); // Cannot be omitted; implies that includes/excludes are relative
        String[] inc;
        final Log log = getLog();
		if (files != null) {
            log.debug("files="+files);
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
        log.debug(Arrays.toString(inc));
        ds.setIncludes( inc );
        ds.addDefaultExcludes(); // TODO should this be optional?
        ds.scan();
        return ds.getIncludedFiles();
    }
}
