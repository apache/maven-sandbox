/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugins.pom;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Alter a Mojo by the specified elements
 * @author eredmond
 * 
 * @goal alter-mojo
 * @phase process-resources
 */
public class AlterMojo extends AbstractMultipleAlterationMojo
{
    /**
     * @parameter
     */
    Map alteredProperties;

    /**
     * @parameter
     */
    ArrayList dependencies;

    /**
     * @parameter
     */
    Parent parent;

    /**
     * @parameter
     */
    boolean applyToSubprojects;

    /**
     * @parameter
     */
    String projectFile;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if( parent != null )
        {
            AlterParentMojo alterParent = new AlterParentMojo();

            alterParent.newParentGroupId = parent.getGroupId();
            alterParent.newParentArtifactId = parent.getArtifactId();
            alterParent.newParentVersion = parent.getVersion();
            alterParent.relativePath = parent.getRelativePath();
            alterParent.suppressRelativePath = parent.isRemoveRelativePath();
            alterParent.projectFile = projectFile;

            alterParent.execute();
        }

        if( alteredProperties != null && !alteredProperties.isEmpty() )
        {
            AlterPropertiesMojo alterProperties = new AlterPropertiesMojo();

            alterProperties.alteredProperties = new TreeMap( alteredProperties );
            alterProperties.projectFile = projectFile;

            alterProperties.execute();
        }

        if ( dependencies != null && !dependencies.isEmpty() )
        {
            AlterDependenciesMojo alterDependencies = new AlterDependenciesMojo();
    
            alterDependencies.dependencies = new ArrayList( dependencies );
            alterDependencies.target = target;
            alterDependencies.projectExcludes = projectExcludes;
    
            alterDependencies.execute();
        }

    }
}
