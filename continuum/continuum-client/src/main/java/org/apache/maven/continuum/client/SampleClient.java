package org.apache.maven.continuum.client;

import org.apache.maven.continuum.client.project.Project;
import org.apache.maven.continuum.client.project.ProjectSummary;
import org.apache.maven.continuum.client.project.BuildResult;

import java.net.URL;
import java.util.Date;

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

public class SampleClient
{

    public static void main( String[] args )
        throws Exception
    {
        String address = "http://localhost:8000/continuum";

        if ( args.length > 0 && args[0] != null && args[0].length() > 0 )
        {
            address = args[0];
        }

        System.out.println( "Connecting to: " + address );

        ContinuumClient client = new ContinuumClient( new URL( address ) );

        ProjectSummary[] projects = null;

        try
        {
            System.out.println( "******************************" );
            System.out.println( "Projects list" );
            System.out.println( "******************************" );

            projects = client.getProjects();

            for ( int i = 0; i < projects.length; i++ )
            {
                System.out.println( projects[i] + " - Name=" + projects[i].getName() );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        if ( projects != null && projects.length > 0 )
        {

            System.out.println( "******************************" );
            System.out.println( "Project detail" );
            System.out.println( "******************************" );

            for ( int i = 0; i < projects.length; i++ )
            {
                try
                {
                    Project project = client.getProject( projects[i] );

                    System.out.println( "Name for project " + project.getId() + " : " + project.getName() );

                    System.out.println( "State: " + ContinuumClient.getStatusMessage( project.getState() ) );

                    BuildResult build = client.getBuildResult( project.getLatestBuildId() );

                    System.out.println( "Latest build:" );
                    System.out.println( "    BuildId: " + build.getId() );
                    System.out.println( "    Start time: " + new Date( build.getStartTime() ) );
                    System.out.println( "    End time: " + new Date( build.getEndTime() ) );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
