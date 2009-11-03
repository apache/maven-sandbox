package org.apache.maven.util.pluginenforcer;
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

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 * Enforces that a specific version of a plugin is used throughout a build.
 *
 * @author Stephen Connolly
 * @since 03-Nov-2009 21:52:08
 */
@Component(role = AbstractMavenLifecycleParticipant.class)
public class PluginEnforcingMavenLifecycleParticipant
    extends AbstractMavenLifecycleParticipant
{

    @Requirement
    private Logger logger;
    
    public void afterProjectsRead( MavenSession mavenSession )
        throws MavenExecutionException
    {
        logger.info( "Hello " + mavenSession.getUserProperties().getProperty("name", "world!!!" ));
    }
}
