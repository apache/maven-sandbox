package org.apache.maven.plugin.it;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
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

import org.apache.maven.BuildFailureException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectSorter;
import org.codehaus.plexus.util.dag.CycleDetectedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Searches for integration test Maven projects, and executes each, collecting a log in the project directory, and outputting
 * the results to the screen.
 * 
 * @goal test
 *
 * @author <a href="mailto:kenney@apache.org">Kenney Westerhof</a>
 * @author <a href="mailto:jdcasey@apache.org">John Casey</a>
 */
public class ForkMojo
    extends AbstractMojo
{
    /**
     * The local repository for caching artifacts.
     * 
     * @parameter default-value="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * Directory to search for integration tests.
     * @parameter default-value="${basedir}/src/it/" expression="${it.testDir}"
     */
    private File integrationTestsDirectory;
    
    /**
     * Comma-separated includes for searching the integration test directory, meant for specifying on the command-line.
     * 
     * @parameter expression="${it.includes}
     */
    private String includesPattern;

    /**
     * Comma-separated excludes for searching the integration test directory, meant for specifying on the command-line.
     * 
     * @parameter expression="${it.excludes}"
     */
    private String excludesPattern;

    /**
     * Includes for searching the integration test directory. This parameter is meant to be set from the POM.
     * 
     * @parameter
     */
    private String[] includes = new String[] { "*/pom.xml" };

    /**
     * Excludes for searching the integration test directory. This parameter is meant to be set from the POM.
     * 
     * @parameter
     */
    private String[] excludes = new String[0];
    
    /**
     * The comma-separated list of goals to execute on each project.
     * 
     * @parameter expression="${it.goals}" default-value="package"
     */
    private String goals;
    
    /**
     * The name of the project-specific file that contains the enumeration of goals to execute for that test.
     * 
     * @parameter
     */
    private String goalFile;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        // ----------------------------------------------------------------------
        // Here we will try to use the embedder
        // ----------------------------------------------------------------------
        //
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
         MavenEmbedder maven = new MavenEmbedder();
        
         maven.setClassLoader( classLoader );
        
        try
        {
            maven.start();
        }
        catch ( MavenEmbedderException e )
        {
            throw new MojoExecutionException( "Cannot start embedder.", e );
        }
        
        String[] include = collectPatterns( includesPattern, includes );
        String[] exclude = collectPatterns( excludesPattern, excludes );
        
        List projects = maven.collectProjects( integrationTestsDirectory, include, exclude );
        
        try
        {
            projects = new ProjectSorter( projects ).getSortedProjects();
        }
        catch ( CycleDetectedException e )
        {
            throw new MojoExecutionException( "Failed to sort integration-test projects. Reason: " + e.getMessage() , e );
        }
        catch ( DuplicateProjectException e )
        {
            throw new MojoExecutionException( "Failed to sort integration-test projects. Reason: " + e.getMessage() , e );
        }
        
        if ( projects.isEmpty() )
        {
            getLog().info( "No test-projects were selected for execution." );
            return;
        }

        List goalList = collectListFromCSV( goals );
        
        if ( goalList.isEmpty() )
        {
            goalList.add( "package" );
        }
        
        Properties buildProperties = new Properties();

        List failures = new ArrayList();
        
        for ( Iterator it = projects.iterator(); it.hasNext(); )
        {
            // run each one, so we can get a separate report.
            MavenProject project = (MavenProject) it.next();
            
            List projectGoals = goalList;
            
            if ( goalFile != null )
            {
                File projectGoalList = new File( project.getFile().getParentFile(), goalFile );
                
                if ( projectGoalList.exists() )
                {
                    List goals = readFromFile( projectGoalList );
                    
                    if ( goals != null && !goals.isEmpty() )
                    {
                        getLog().info( "Using goals specified in file: " + projectGoalList );
                        projectGoals = goals;
                    }
                }
            }
            
            String defaultGoal = project.getDefaultGoal();
            
            if ( defaultGoal != null && defaultGoal.trim().length() > 0 )
            {
                getLog().info( "Executing default goal: " + defaultGoal + " for project: " + project.getId() );
                
                projectGoals = Collections.singletonList( defaultGoal );
            }
            else
            {
                getLog().info( "Executing goals: " + projectGoals + " for project: " + project.getId() );
            }
            
            getLog().info( "Running test: " + project.getId() + "..." );
            
            maven.setInteractiveMode(false);
            maven.setLocalRepositoryDirectory( new File( localRepository.getBasedir() ) );
            maven.setCheckLatestPluginVersion(false);
            
            File outputLog = new File( project.getBasedir(), "build.log" );
            
            MavenEmbedderLogger logger = new ToFileEmbedderLogger( outputLog );
            
            maven.setLogger( logger );
            
            try
            {
                maven.execute( Collections.singletonList( project ), projectGoals, new EmbedderEventMonitor( logger ), new EmbedderTransferListener( logger ), buildProperties, project.getBasedir() );
                
                getLog().info( "...SUCCESS." );
            }
            catch ( CycleDetectedException e )
            {
                getLog().info( "...FAILED: ", e );
                failures.add( project.getId() );
            }
            catch ( LifecycleExecutionException e )
            {
                getLog().info( "...FAILED: ", e );
                failures.add( project.getId() );
            }
            catch ( BuildFailureException e )
            {
                getLog().info( "...FAILED. See " + outputLog.getAbsolutePath() + " for details." );
                failures.add( project.getId() );
            }
            catch ( DuplicateProjectException e )
            {
                getLog().info( "...FAILED: ", e );
                failures.add( project.getId() );
            }
        }
        
        StringBuffer summary = new StringBuffer();
        summary.append( "\n\n" );
        summary.append( "---------------------------------------\n" );
        summary.append( "Integration Test Summary:\n" );
        summary.append( "Tests Passing: " ).append( projects.size() - failures.size() ).append( "\n" );
        summary.append( "Tests Failing: " ).append( failures.size() ).append( "\n" );
        summary.append( "---------------------------------------\n" );
        
        if ( !failures.isEmpty() )
        {
            summary.append( "\nThe following tests failed:\n" );
            
            for ( Iterator it = failures.iterator(); it.hasNext(); )
            {
                String projectId = (String) it.next();
                summary.append( "\n*  " ).append( projectId );
            }
            
            summary.append( "\n" );
        }
        
        getLog().info( summary.toString() );

        
        if ( !failures.isEmpty() )
        {
            throw new MojoFailureException( this, "There were test failures.", failures.size() + " tests failed." );
        }
    }

    private List readFromFile( File projectGoalList )
    {
        BufferedReader reader = null;
        
        List result = null;
        
        try
        {
            reader = new BufferedReader( new FileReader( projectGoalList ) );
            
            result = new ArrayList();
            
            String line = null;
            while( ( line = reader.readLine() ) != null )
            {
                result.addAll( collectListFromCSV( line ) );
            }
        }
        catch( IOException e )
        {
            getLog().warn( "Failed to load goal list from file: " + projectGoalList + ". Using 'goal' parameter configured on this plugin instead." );
            getLog().debug( "Error reading goals file: " + projectGoalList, e );
        }
        finally
        {
            if ( reader != null )
            {
                try
                {
                    reader.close();
                }
                catch ( IOException e )
                {
                }
            }
        }
        
        return result;
    }

    private List collectListFromCSV( String csv )
    {
        List result = new ArrayList();
        
        if ( csv != null && csv.trim().length() > 0 )
        {
            StringTokenizer st = new StringTokenizer( csv, "," );
            
            while( st.hasMoreTokens() )
            {
                result.add( st.nextToken().trim() );
            }
        }
        
        return result;
    }

    private String[] collectPatterns( String pattern, String[] array )
    {
        String[] result;
        
        if ( pattern != null && pattern.trim().length() > 0 )
        {
            StringTokenizer st = new StringTokenizer( pattern, "," );
            
            int tokenCount = st.countTokens();
            
            result = new String[tokenCount];
            
            for ( int i = 0; i < result.length; i++ )
            {
                result[i] = st.nextToken().trim();
            }
        }
        else
        {
            result = array;
        }
        
        return result;
    }

}
