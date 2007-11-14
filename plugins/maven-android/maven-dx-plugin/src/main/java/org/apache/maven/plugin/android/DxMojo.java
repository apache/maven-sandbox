package org.apache.maven.plugin.android;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.android.ExecutionException;
import org.apache.maven.android.CommandExecutor;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * @author Shane Isbell
 * @goal dx
 * @phase process-classes
 * @description
 */
public class DxMojo extends AbstractMojo {

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {

        CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
        executor.setLogger(this.getLog());
        File outputFile = new File("target/classes.dex");
        File inputFile = new File("target/" + project.getArtifactId() + "-" + project.getVersion() + ".jar");

        List<String> commands = new ArrayList<String>();
        commands.add("--dex");
        commands.add("--output=" + outputFile.getAbsolutePath());
        commands.add(inputFile.getAbsolutePath());
        getLog().info("dx " + commands.toString());
        try {
            executor.executeCommand("dx", commands);
        } catch (ExecutionException e) {
            throw new MojoExecutionException("", e);
        }
    }
}
