package org.apache.maven.plugins.pom;

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


import java.util.Iterator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Element;


/**
 * @author <a href="mailto:jmcconnell@apache.org">Jesse McConnell</a>
 * @version $Id:$
 */
public abstract class AbstractSingleAlterationMojo extends AbstractMojo
{

    /**
     * @parameter expression="${projectFile}"
     * @required
     * 
     */
    protected String projectFile;

    public AbstractSingleAlterationMojo()
    {
        super();
    }

    protected Element getArtifactId( Element element ) throws MojoExecutionException
    {
        for ( Iterator i = element.elements().iterator(); i.hasNext(); )
        {
            Element e = (Element)i.next();
            if ( e.getName().equals( "artifactId" ) )
            {
                return e;
            }
        }
        
        throw new MojoExecutionException("unable to access groupId in " + element.getPath() );
    }

    protected Element getGroupId( Element element ) throws MojoExecutionException
    {
        for ( Iterator i = element.elements().iterator(); i.hasNext(); )
        {
            Element e = (Element)i.next();
            if ( e.getName().equals( "groupId" ) )
            {
                return e;
            }
        }
        
        throw new MojoExecutionException("unable to access groupId in " + element.getPath() );
    }

}
