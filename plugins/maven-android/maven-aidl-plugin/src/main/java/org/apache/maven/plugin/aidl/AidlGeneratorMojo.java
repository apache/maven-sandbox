package org.apache.maven.plugin.aidl;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.android.CommandExecutor;
import org.apache.maven.android.ExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * @author Shane Isbell
 * @goal generate
 * @requiresProject true
 * @description
 */
public class AidlGeneratorMojo extends AbstractMojo {

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setBasedir(project.getBuild().getSourceDirectory());

        List<String> excludeList = new ArrayList<String>();
        //target files
        excludeList.add("target/**");

        List<String> includeList = new ArrayList<String>();
        includeList.add("**/*.aidl");
        String[] includes = new String[includeList.size()];
        directoryScanner.setIncludes((includeList.toArray(includes)));
        directoryScanner.addDefaultExcludes();

        directoryScanner.scan();
        String[] files = directoryScanner.getIncludedFiles();
        getLog().info("ANDROID-904-002: Found aidl files: Count = " + files.length);
        if (files.length == 0) {
            return;
        }

        CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
        executor.setLogger(this.getLog());

        for (String file : files) {
            List<String> commands = new ArrayList<String>();
            commands.add("-I" + project.getBuild().getSourceDirectory());
            commands.add((new File(project.getBuild().getSourceDirectory(), file).getAbsolutePath()));
            try {
                executor.executeCommand("aidl", commands, project.getBasedir(), false);
            } catch (ExecutionException e) {
                throw new MojoExecutionException("", e);
            }

        }
    }
}
