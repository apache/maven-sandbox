/*
 * Copyright 2010 Red Hat, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.maven.mae.boot.main;

import org.apache.commons.cli.CommandLine;
import org.apache.maven.mae.DefaultMAEExecutionRequest;
import org.apache.maven.mae.MAEExecutionRequest;
import org.apache.maven.mae.boot.embed.EMBEmbedderBuilder;
import org.codehaus.plexus.classworlds.ClassWorld;

import java.io.PrintStream;
import java.util.Properties;

public final class CliRequest
{
    public String[] args;

    public CommandLine commandLine;

    public String workingDirectory;

    public PrintStream fileStream;

    public Properties userProperties = new Properties();

    public Properties systemProperties = new Properties();

    public MAEExecutionRequest request;

    public EMBEmbedderBuilder builder;

    public CliRequest( final String[] args, final ClassWorld classWorld )
    {
        this.args = args;
        builder = new EMBEmbedderBuilder().withClassWorld( classWorld );
        request = new DefaultMAEExecutionRequest();
    }

}
