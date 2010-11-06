package org.apache.maven.shared.scmchanges;

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


import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;


public class MakeScmChangesTest
{

    MakeScmChanges msc;

    @Before
    public void setUp()
    {
        msc = new MakeScmChanges();
        msc.baseDir = new File("").getAbsoluteFile();
        msc.logger = mock(Logger.class);
    }

    @Test( expected = MavenExecutionException.class )
    public void readParametersNoScmConnection()
        throws Exception
    {
        MavenSession session = mock( MavenSession.class );
        Properties props = new Properties();
        when( session.getUserProperties() ).thenReturn( props );

        msc.readParameters( session );
    }

    @Test
    public void readParameters()
        throws Exception
    {
        MavenSession session = mock( MavenSession.class );
        Properties props = new Properties();
        when( session.getUserProperties() ).thenReturn( props );

        Scm scm = mock( Scm.class );
        String scmConnection = "foo";
        when( scm.getConnection() ).thenReturn( scmConnection );

        MavenProject project = new MavenProject();
        project.setScm( scm );
        project.setFile( new File("").getAbsoluteFile() );

        when( session.getTopLevelProject() ).thenReturn( project );

        msc.readParameters( session );

        assertThat( msc.enabled, is( false ) );
        assertThat( msc.ignoreUnknown, is( true ) );
        assertThat( msc.scmConnection, is( scmConnection ) );
    }

    @Test
    public void getChangedFilesFromScm()
        throws Exception
    {
        StatusScmResult result = mock( StatusScmResult.class );
        ScmManager scmManager = mock( ScmManager.class );
        when( scmManager.status( (ScmRepository) any(), (ScmFileSet) any() ) ).thenReturn( result );

        msc.scmManager = scmManager;

        msc.getChangedFilesFromScm( new File( "" ) );

    }

    @Test( expected = MavenExecutionException.class )
    public void getChangedFilesFromScmFailure()
        throws MavenExecutionException
    {
        msc.getChangedFilesFromScm( new File( "" ) );
    }
    
    @Test
    public void disabled() throws MavenExecutionException {
        MavenSession session = mock(MavenSession.class);
        
        msc = PowerMockito.spy( msc );
        
        PowerMockito.doNothing().when( msc ).readParameters( (MavenSession) any() );
        
        msc.afterProjectsRead( session );
    }
    
    @Test(expected = MavenExecutionException.class)
    public void nothingToDo() throws MavenExecutionException {
        MavenSession session = mock(MavenSession.class);
        
        msc.enabled = true;
        
        msc = PowerMockito.spy( msc );
        
        PowerMockito.doNothing().when( msc ).readParameters( (MavenSession) any() );
        
        List<ScmFile> changedFiles = new ArrayList<ScmFile>();
        
        PowerMockito.doReturn( changedFiles ).when( msc ).getChangedFilesFromScm( null );
        
        MavenProject project = mock(MavenProject.class);
        
        when ( session.getTopLevelProject() ).thenReturn( project);
        
        msc.afterProjectsRead( session );
    }
    
    @Test
    public void normalFlow() throws MavenExecutionException {
        MavenSession session = mock(MavenSession.class);
        
        msc.enabled = true;
        
        msc = PowerMockito.spy( msc );
        
        PowerMockito.doNothing().when( msc ).readParameters( (MavenSession) any() );
        
        ScmFile changedFile = new ScmFile("pom.xml", ScmFileStatus.MODIFIED);
        
        List<ScmFile> changedFiles = Arrays.asList( changedFile );
        
        PowerMockito.doReturn( changedFiles ).when( msc ).getChangedFilesFromScm( (File) any() );
        
        MavenProject project = new MavenProject();
        
        project.setFile( new File("pom.xml").getAbsoluteFile() );
        
        when ( session.getTopLevelProject() ).thenReturn( project);
        
        when ( session.getProjects() ).thenReturn( Arrays.asList( project ));
        
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        when (session.getRequest()).thenReturn( request );
        
        msc.afterProjectsRead( session );
        
        List<String> selectedProjects = request.getSelectedProjects();
        assertThat(selectedProjects.toString(), is("[unknown:empty-project]"));
        assertThat(request.getMakeBehavior(), is(MavenExecutionRequest.REACTOR_MAKE_DOWNSTREAM));
    }
    
    @Test
    public void alreadyBuildingUpstream() throws MavenExecutionException {
        MavenSession session = mock(MavenSession.class);
        
        msc.enabled = true;
        
        msc = PowerMockito.spy( msc );
        
        PowerMockito.doNothing().when( msc ).readParameters( (MavenSession) any() );
        
        ScmFile changedFile = new ScmFile("pom.xml", ScmFileStatus.MODIFIED);
        
        List<ScmFile> changedFiles = Arrays.asList( changedFile );
        
        PowerMockito.doReturn( changedFiles ).when( msc ).getChangedFilesFromScm( (File) any() );
        
        MavenProject project = new MavenProject();
        
        project.setFile( new File("pom.xml").getAbsoluteFile() );
        
        when ( session.getTopLevelProject() ).thenReturn( project);
        
        when ( session.getProjects() ).thenReturn( Arrays.asList( project ));
        
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setMakeBehavior( MavenExecutionRequest.REACTOR_MAKE_UPSTREAM );
        when (session.getRequest()).thenReturn( request );
        
        msc.afterProjectsRead( session );
        
        List<String> selectedProjects = request.getSelectedProjects();
        assertThat(selectedProjects.toString(), is("[unknown:empty-project]"));
        assertThat(request.getMakeBehavior(), is(MavenExecutionRequest.REACTOR_MAKE_BOTH));
    }

    @Test(expected = MavenExecutionException.class)
    public void nothingToDoBecauseIgnoringRootPom() throws MavenExecutionException {
        MavenSession session = mock(MavenSession.class);
        
        msc.enabled = true;
        msc.ignoreRootPom = true;
        
        msc = PowerMockito.spy( msc );
        
        PowerMockito.doNothing().when( msc ).readParameters( (MavenSession) any() );
        
        ScmFile changedFile = new ScmFile("pom.xml", ScmFileStatus.MODIFIED);
        
        List<ScmFile> changedFiles = Arrays.asList( changedFile );
        
        PowerMockito.doReturn( changedFiles ).when( msc ).getChangedFilesFromScm( (File) any() );
        
        MavenProject project = new MavenProject();
        
        project.setFile( new File("pom.xml").getAbsoluteFile() );
        
        when ( session.getTopLevelProject() ).thenReturn( project);
        
        when ( session.getProjects() ).thenReturn( Arrays.asList( project ));
        
        msc.afterProjectsRead( session );
        
    }
    
    @Test(expected = MavenExecutionException.class)
    public void nothingToDoBecauseIgnoringUnknown() throws MavenExecutionException {
        MavenSession session = mock(MavenSession.class);
        
        msc.enabled = true;
        msc.ignoreUnknown = true;
        
        msc = PowerMockito.spy( msc );
        
        PowerMockito.doNothing().when( msc ).readParameters( (MavenSession) any() );
        
        ScmFile changedFile = new ScmFile("pom.xml", ScmFileStatus.UNKNOWN);
        
        List<ScmFile> changedFiles = Arrays.asList( changedFile );
        
        PowerMockito.doReturn( changedFiles ).when( msc ).getChangedFilesFromScm( (File) any() );
        
        MavenProject project = new MavenProject();
        
        project.setFile( new File("pom.xml").getAbsoluteFile() );
        
        when ( session.getTopLevelProject() ).thenReturn( project);
        
        when ( session.getProjects() ).thenReturn( Arrays.asList( project ));
        
        msc.afterProjectsRead( session );
        
    }
    
    @Test(expected = MavenExecutionException.class)
    public void nothingToDoBecauseIgnoringMissing() throws MavenExecutionException {
        MavenSession session = mock(MavenSession.class);
        
        msc.enabled = true;
        
        msc = PowerMockito.spy( msc );
        
        PowerMockito.doNothing().when( msc ).readParameters( (MavenSession) any() );
        
        ScmFile changedFile = new ScmFile("pom.xml", ScmFileStatus.MISSING);
        
        List<ScmFile> changedFiles = Arrays.asList( changedFile );
        
        PowerMockito.doReturn( changedFiles ).when( msc ).getChangedFilesFromScm( (File) any() );
        
        MavenProject project = new MavenProject();
        
        project.setFile( new File("pom.xml").getAbsoluteFile() );
        
        when ( session.getTopLevelProject() ).thenReturn( project);
        
        when ( session.getProjects() ).thenReturn( Arrays.asList( project ));
        
        msc.afterProjectsRead( session );
        
    }
    
    @Test(expected = MavenExecutionException.class)
    public void nothingToDoBecauseIgnoringRandomSubdirectory() throws MavenExecutionException {
        MavenSession session = mock(MavenSession.class);
        
        msc.enabled = true;
        
        msc = PowerMockito.spy( msc );
        
        PowerMockito.doNothing().when( msc ).readParameters( (MavenSession) any() );
        
        ScmFile changedFile = new ScmFile("foo/pom.xml", ScmFileStatus.MODIFIED);
        
        List<ScmFile> changedFiles = Arrays.asList( changedFile );
        
        PowerMockito.doReturn( changedFiles ).when( msc ).getChangedFilesFromScm( (File) any() );
        
        MavenProject project = new MavenProject();
        
        project.setFile( new File("pom.xml").getAbsoluteFile() );
        
        when ( session.getTopLevelProject() ).thenReturn( project);
        
        when ( session.getProjects() ).thenReturn( Arrays.asList( project ));
        
        msc.afterProjectsRead( session );
        
    }
    
}
