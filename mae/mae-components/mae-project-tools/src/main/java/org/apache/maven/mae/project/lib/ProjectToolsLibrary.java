/*
 * Copyright 2011 Red Hat, Inc.
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

package org.apache.maven.mae.project.lib;

import org.apache.maven.mae.conf.AbstractMAELibrary;
import org.apache.maven.mae.conf.MAELibrary;
import org.apache.maven.mae.conf.MavenPomVersionProvider;
import org.commonjava.atservice.annotation.Service;

@Service( MAELibrary.class )
public class ProjectToolsLibrary
    extends AbstractMAELibrary
{

    public ProjectToolsLibrary()
    {
        super( "project", "Project-Tools", new MavenPomVersionProvider( "org.commonjava.emb.components",
                                                                        "emb-project-tools" ) );
    }

}
