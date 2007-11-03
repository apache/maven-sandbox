package org.apache.maven.wagon.providers.scm;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import java.io.IOException;

/**
 * Test for ScmWagon using CVS as underlying SCM
 * 
 * @author <a href="brett@apache.org">Brett Porter</a>
 * @version $Id$
 */
public class AbstractScmCvsWagonTest
    extends AbstractScmWagonTest
{

    protected String getScmId()
    {
        return "cvs";
    }

    protected String getTestRepositoryUrl()
        throws IOException
    {
        String repository = getTestFile( "target/test-classes/test-repo-cvs" ).getAbsolutePath();

        return "scm:cvs|local|" + repository + "|repository/newfolder";
    }
}