package org.apache.maven.continuum.client;

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

import org.apache.maven.continuum.client.project.ContinuumProjectState;
import org.apache.maven.continuum.client.ProjectsReader;
import org.apache.maven.continuum.client.ClientException;
import org.apache.maven.continuum.client.project.Project;
import org.apache.maven.continuum.client.project.ProjectSummary;
import org.apache.maven.continuum.client.project.BuildResult;

import java.util.Hashtable;
import java.net.URL;

/**
 * A clean class for accessing the Continuum server.
 * Contains a few utility methods for clients too.
 *
 * @author Andrew Williams
 */
public class ContinuumClient
{
    private static Hashtable statusMap;

    private ProjectsReader source;

    static
    {
        statusMap = new Hashtable();
        statusMap.put( new Integer( ContinuumProjectState.NEW ), "New" );
        statusMap.put( new Integer( ContinuumProjectState.CHECKEDOUT ), "New" );
        statusMap.put( new Integer( ContinuumProjectState.OK ), "OK" );
        statusMap.put( new Integer( ContinuumProjectState.FAILED ), "Failed" );
        statusMap.put( new Integer( ContinuumProjectState.ERROR ), "Error" );
        statusMap.put( new Integer( ContinuumProjectState.BUILDING ), "Building" );
        statusMap.put( new Integer( ContinuumProjectState.CHECKING_OUT ), "Checking out" );
        statusMap.put( new Integer( ContinuumProjectState.UPDATING ), "Updating" );
        statusMap.put( new Integer( ContinuumProjectState.WARNING ), "Warning" );
    }

    /**
     * Get the textual representation (in English) of a status code.
     *
     * @param status The status code to look up.
     * @return The title of the status code.
     */
    public static String getStatusMessage( int status )
    {
        Integer statusInt = new Integer( status );

        if ( statusMap.containsKey( statusInt ) )
        {
            return (String) statusMap.get( new Integer( status ) );
        }

        return "Unknown";
    }

    public ContinuumClient( URL serverUrl )
    {
        source = new ProjectsReader( serverUrl );
    }

    /**
     * Get a list of the configured projects from the connected XMLRPC server.
     * Note that the returned project objects are not fully populated. To get all the project information one must
     * call <code>getProject( id )</code>.
     *
     * @return A list of project objects containing summary information about each project on the server.
     * @throws ClientException
     * @see #getProject(int)
     */
    public ProjectSummary[] getProjects()
        throws ClientException
    {
        try
        {
            return source.readProjects();
        }
        catch ( Exception e )
        {
            throw new ClientException( e );
        }
    }

    /**
     * Get a project with a complete set of information retrieved from the XMLRPC server.
     *
     * @param id The id of the project to look up.
     * @return The project object populated with all information available from the server.
     * @throws ClientException
     */
    public Project getProject( int id )
        throws ClientException
    {
        if ( id < 1 )
        {
            return null;
        }

        try
        {
            Project ret = new Project();

            ret.setId( id );
            source.refreshProject( ret );

            return ret;
        }
        catch ( Exception e )
        {
            throw new ClientException( e );
        }
    }

    public Project getProject( ProjectSummary summary )
        throws ClientException
    {
        return getProject( summary.getId() );
    }

    public BuildResult[] getBuildResultsForProject( int projectId )
        throws ClientException
    {
        try
        {
            return source.readBuildResultsForProject( projectId );
        }
        catch ( Exception e )
        {
            throw new ClientException( e );
        }
    }

    public BuildResult[] getBuildResultsForProject( Project project )
        throws ClientException
    {
        return getBuildResultsForProject( project.getId() );
    }

    public BuildResult getBuildResult( int buildId )
        throws ClientException
    {
        if ( buildId < 1 )
        {
            return null;
        }

        try
        {
            BuildResult ret = new BuildResult();

            ret.setId( buildId );
            source.refreshBuildResult( ret );

            return ret;
        }
        catch ( Exception e )
        {
            throw new ClientException( e );
        }
    }
}
