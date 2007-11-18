package org.apache.maven.plugin.adb;
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
import org.apache.maven.android.CommandExecutor;
import org.apache.maven.android.ExecutionException;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Shane Isbell
 * @goal pull
 * @requiresProject false
 * @description
 */
public class DevicePullerMojo extends AbstractMojo {

    /**
     * @parameter expression="${source}"
     * @required
     */
    private File sourceFileOrDirectory;

    /**
     * @parameter expression="${destination}"
     * @required
     */
    private File destinationFileOrDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        CommandExecutor executor = CommandExecutor.Factory.createDefaultCommmandExecutor();
        executor.setLogger(this.getLog());

        List<String> commands = new ArrayList<String>();
        commands.add("pull");
        commands.add(sourceFileOrDirectory.getAbsolutePath());
        commands.add(destinationFileOrDirectory.getAbsolutePath());

        getLog().info("adb " + commands.toString());
        try {
            executor.executeCommand("adb", commands);
        } catch (ExecutionException e) {
        }
    }
}
