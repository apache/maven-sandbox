package org.apache.maven.plugins.grafo;


/*
 * Copyright 2006 The Apache Software Foundation.
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

import org.codehaus.plexus.PlexusTestCase;

/**
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class GrafoTest
    extends PlexusTestCase
{
    
    private GrafoMojo mojo;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        mojo = new GrafoMojo();
    }

    public void testExecute() throws Exception
    {
        //TODO
        //mojo.execute();
    }

}
