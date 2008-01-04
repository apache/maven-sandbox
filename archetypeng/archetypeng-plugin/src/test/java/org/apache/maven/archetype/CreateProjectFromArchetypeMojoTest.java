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

package org.apache.maven.archetype;

import org.apache.maven.archetype.common.ArchetypeRegistryManager;
import org.apache.maven.archetype.common.Constants;
import org.apache.maven.archetype.mojos.CreateProjectFromArchetypeMojo;
import org.apache.maven.archetype.ui.ArchetypeGenerationConfigurator;
import org.apache.maven.archetype.ui.ArchetypeSelector;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.shared.invoker.Invoker;

import java.io.File;

import java.util.Properties;

/**
 * @author  rafale
 */
public class CreateProjectFromArchetypeMojoTest
extends AbstractMojoTestCase
{
    public void testAllPrompted ()
    throws Exception
    {
        String projectName = "project-1";

        MockPrompter prompter = new MockPrompter ();
        prompter.addAnswer ( "1" );
        prompter.addAnswer ( "value-1" );
        prompter.addAnswer ( "value-2" );
        prompter.addAnswer ( "value-3" );
        prompter.addAnswer ( "value-4" );
        prompter.addAnswer ( "com.company" );
        prompter.addAnswer ( projectName );
        prompter.addAnswer ( "1.0" );
        prompter.addAnswer ( "com.company.project1" );
        prompter.addAnswer ( "Y" );

        CreateProjectFromArchetypeMojo mojo = lookupCreateProjectFromArchetypeMojo ( prompter );

        String archetypeGroupId = null;
        String archetypeArtifactId = null;
        String archetypeVersion = null;
        String archetypeRepository = null;
        String archetypeCatalog =
            "file://"
            + new File ( getBasedir (), "target/test-classes/local-repository" ).getAbsolutePath ();
        ArtifactRepository localRepository =
            ( (ArchetypeRegistryManager) lookup ( ArchetypeRegistryManager.class ) )
            .createRepository (
                new File ( getBasedir (), "target/test-classes/local-repository" ).toURI ().toURL ()
                .toExternalForm (),
                "local-repo"
            );
        Boolean interactiveMode = Boolean.TRUE;
        File basedir = new File ( getBasedir (), "target/projects" );
        basedir.mkdirs ();

        Properties executionProperties = new Properties ();
        String goals = null;
        configureCreateProjectFromArchetypeMojo (
            mojo,
            archetypeGroupId,
            archetypeArtifactId,
            archetypeVersion,
            archetypeRepository,
            archetypeCatalog,
            localRepository,
            interactiveMode,
            basedir,
            executionProperties,
            goals
        );

        mojo.execute ();

        assertTrue ( new File ( basedir, projectName + "/pom.xml" ).exists () );
    }

    public void testBatchMode ()
    throws Exception
    {
        String projectName = "project-2";

        CreateProjectFromArchetypeMojo mojo = lookupCreateProjectFromArchetypeMojo ( null );

        String archetypeGroupId = "archetypes";
        String archetypeArtifactId = "fileset";
        String archetypeVersion = null;
        String archetypeRepository = null;
        String archetypeCatalog =
            "file://"
            + new File ( getBasedir (), "target/test-classes/local-repository" ).getAbsolutePath ();
        ArtifactRepository localRepository =
            ( (ArchetypeRegistryManager) lookup ( ArchetypeRegistryManager.class ) )
            .createRepository (
                new File ( getBasedir (), "target/test-classes/local-repository" ).toURI ().toURL ()
                .toExternalForm (),
                "local-repo"
            );
        Boolean interactiveMode = Boolean.FALSE;
        File basedir = new File ( getBasedir (), "target/projects" );
        basedir.mkdirs ();

        Properties executionProperties = new Properties ();
        executionProperties.setProperty ( "property-without-default-1", "value-1" );
        executionProperties.setProperty ( "property-without-default-2", "value-2" );
        executionProperties.setProperty ( "property-without-default-3", "value-3" );
        executionProperties.setProperty ( "property-without-default-4", "value-4" );
        executionProperties.setProperty ( Constants.GROUP_ID, "com.company" );
        executionProperties.setProperty ( Constants.ARTIFACT_ID, projectName );
        executionProperties.setProperty ( Constants.VERSION, "1" );
        executionProperties.setProperty ( Constants.PACKAGE, "com.company." + projectName );

        String goals = null;
        configureCreateProjectFromArchetypeMojo (
            mojo,
            archetypeGroupId,
            archetypeArtifactId,
            archetypeVersion,
            archetypeRepository,
            archetypeCatalog,
            localRepository,
            interactiveMode,
            basedir,
            executionProperties,
            goals
        );

        mojo.execute ();

        assertTrue ( new File ( basedir, projectName + "/pom.xml" ).exists () );
    }

    protected void tearDown ()
    throws Exception
    {
        super.tearDown ();
    }

    protected void setUp ()
    throws Exception
    {
        super.setUp ();

//        start mock repository to target/remote-repository
    }

    private void configureCreateProjectFromArchetypeMojo (
        CreateProjectFromArchetypeMojo mojo,
        String archetypeGroupId,
        String archetypeArtifactId,
        String archetypeVersion,
        String archetypeRepository,
        String archetypeCatalog,
        ArtifactRepository localRepository,
        Boolean interactiveMode,
        File basedir,
        Properties executionProperties,
        String goals
    )
    throws IllegalAccessException
    {
        setVariableValueToObject ( mojo, "archetypeGroupId", archetypeGroupId );
        setVariableValueToObject ( mojo, "archetypeArtifactId", archetypeArtifactId );
        setVariableValueToObject ( mojo, "archetypeVersion", archetypeVersion );
        setVariableValueToObject ( mojo, "archetypeRepository", archetypeRepository );
        setVariableValueToObject ( mojo, "archetypeCatalog", archetypeCatalog );
        setVariableValueToObject ( mojo, "localRepository", localRepository );
        setVariableValueToObject ( mojo, "interactiveMode", interactiveMode );
        setVariableValueToObject ( mojo, "basedir", basedir );
        setVariableValueToObject ( mojo, "session", new MockMavenSession ( executionProperties ) );
        setVariableValueToObject ( mojo, "goals", goals );
    }

    private CreateProjectFromArchetypeMojo lookupCreateProjectFromArchetypeMojo (
        MockPrompter prompter
    )
    throws Exception, IllegalAccessException
    {
        ArchetypeSelector selector = (ArchetypeSelector) lookup ( ArchetypeSelector.class );
        ArchetypeRegistryManager archetypeRegistryManager =
            (ArchetypeRegistryManager) lookup ( ArchetypeRegistryManager.class );
        Archetype archetype = (Archetype) lookup ( Archetype.class );
        ArchetypeGenerationConfigurator configurator =
            (ArchetypeGenerationConfigurator) lookup ( ArchetypeGenerationConfigurator.class );
        Invoker invoker = (Invoker) lookup ( Invoker.class );

        CreateProjectFromArchetypeMojo mojo = new CreateProjectFromArchetypeMojo ();

        setVariableValueToObject (
            getVariableValueFromObject ( selector, "archetypeSelectionQueryer" ),
            "prompter",
            prompter
        );
        setVariableValueToObject (
            getVariableValueFromObject ( configurator, "archetypeGenerationQueryer" ),
            "prompter",
            prompter
        );
        setVariableValueToObject ( mojo, "selector", selector );
        setVariableValueToObject ( mojo, "archetypeRegistryManager", archetypeRegistryManager );
        setVariableValueToObject ( mojo, "archetype", archetype );
        setVariableValueToObject ( mojo, "configurator", configurator );
        setVariableValueToObject ( mojo, "invoker", invoker );

//        System.err.println ( "selector " + selector );
//        System.err.println ( "archetypeRegistryManager " + archetypeRegistryManager );
//        System.err.println ( "archetype " + archetype );
//        System.err.println ( "configurator " + configurator );
//        System.err.println ( "invoker " + invoker );
        return mojo;
    }

    protected class MockMavenSession
    extends MavenSession
    {
        Properties executionProperties = new Properties ();

        public MockMavenSession ( Properties executionProperties )
        {
            super ( null, null, null, null );
            setExecutionProperties ( executionProperties );
        }

        public Properties getExecutionProperties ()
        {
            return this.executionProperties;
        }

        public void setExecutionProperties ( Properties executionProperties )
        {
            this.executionProperties = executionProperties;
        }
    }
}
