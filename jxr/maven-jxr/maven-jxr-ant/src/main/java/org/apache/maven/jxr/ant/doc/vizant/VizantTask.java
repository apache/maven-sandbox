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
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Vizant task.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class VizantTask
    extends Task
{
    /** Vizant object */
    private Vizant vizant;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void init()
    {
        vizant = new Vizant();
    }

    /**
     * @param antfile
     * @throws BuildException if any
     */
    public void setAntfile( File antfile )
        throws BuildException
    {
        try
        {
            vizant.setAntfile( antfile );
        }
        catch ( IOException e )
        {
            throw new BuildException( "IOException: " + e.getMessage(), getLocation() );
        }
    }

    /**
     * @param outfile
     */
    public void setOutfile( File outfile )
    {
        vizant.setOutfile( outfile );
    }

    /**
     * @param graphid
     */
    public void setGraphid( String graphid )
    {
        vizant.setGraphid( graphid );
    }

    /**
     * @param targetName
     */
    public void setFrom( String targetName )
    {
        vizant.setFrom( targetName );
    }

    /**
     * @param targetName
     */
    public void setTo( String targetName )
    {
        vizant.setTo( targetName );
    }

    /**
     * @param noclustor
     */
    public void setNocluster( boolean noclustor )
    {
        vizant.setNocluster( noclustor );
    }

    /**
     * @param uniqueref
     */
    public void setUniqueref( boolean uniqueref )
    {
        vizant.setUniqueref( uniqueref );
    }

    /**
     * @param opt
     */
    public void setIgnoreant( boolean opt )
    {
        vizant.setIgnoreant( opt );
    }

    /**
     * @param opt
     */
    public void setIgnoreantcall( boolean opt )
    {
        vizant.setIgnoreantcall( opt );
    }

    /**
     * @param opt
     */
    public void setIgnoredepends( boolean opt )
    {
        vizant.setIgnoredepends( opt );
    }

    /** {@inheritDoc} */
    public String getTaskName()
    {
        return "vizant";
    }

    /** {@inheritDoc} */
    public String getDescription()
    {
        return "Generate Graphviz DOT source code from an Ant buildfile.";
    }

    /** {@inheritDoc} */
    public void execute()
        throws BuildException
    {
        try
        {
            vizant.execute();
        }
        catch ( IOException e )
        {
            throw new BuildException( "IOException: " + e.getMessage(), getLocation() );
        }
    }
}
