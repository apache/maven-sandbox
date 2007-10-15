package org.apache.maven.jxr.js.doc;

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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task responsible for creating automatic documentation for .js files
 *
 * @version $Id$
 */
public class JSDocTask
    extends Task
{

    private String jsDir;

    private String destDir;

    private GenerateHTMLIndex index;

    /**
     * @see Task#execute()
     */
    public void execute()
        throws BuildException
    {
        try
        {
            index = new GenerateHTMLIndex( jsDir, destDir );
        }
        catch ( IllegalArgumentException e )
        {
            throw new BuildException( e.getMessage(), getLocation() );
        }
    }

    /**
     * Sets the destDir.
     *
     * @param destDir The destDir to set
     */
    public void setDestDir( String destDir )
    {
        this.destDir = destDir;
    }

    /**
     * Sets the jsDir.
     *
     * @param jsDir The jsDir to set
     */
    public void setJSDir( String jsDir )
    {
        this.jsDir = jsDir;
    }
}
