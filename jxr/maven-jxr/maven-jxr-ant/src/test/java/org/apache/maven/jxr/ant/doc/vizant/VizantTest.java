package org.apache.maven.jxr.ant.doc.vizant;

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

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.jxr.ant.doc.vizant.Vizant;
import org.apache.tools.ant.Project;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class VizantTest
    extends TestCase
{
    /**
     * Call Vizabt task
     *
     * @throws Exception if any.
     */
    public void testExecute()
        throws Exception
    {
        final String basedir = new File( "" ).getAbsolutePath();

        Project antProject = new Project();
        antProject.setBasedir( basedir );

        File targetDir = new File( basedir, "target/unit/vizant" );

        File build = new File( basedir, "src/test/resources/build.xml" );
        File buildGraph = new File( targetDir, "buildgraph.xml" );

        Vizant task = new Vizant();
        task.setProject( antProject );
        task.init();
        task.setAntfile( build );
        task.setOutfile( buildGraph );
        task.setUniqueref( true );
        task.execute();
    }
}
