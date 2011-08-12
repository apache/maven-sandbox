/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.mae.project.event;

import java.io.File;

import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelSource;
import org.sonatype.aether.RequestTrace;

public class ModelLoaderEvent
{

    private final ModelLoaderEventType type;

    private final Model model;

    private final File pom;

    private final Throwable error;

    private final FullProjectKey key;

    private final ModelSource modelSource;

    private final RequestTrace trace;

    ModelLoaderEvent( final ModelLoaderEventType type, final FullProjectKey key, final ModelSource modelSource,
                      final Model model, final File pom, final RequestTrace trace, final Throwable error )
    {
        this.type = type;
        this.key = key;
        this.modelSource = modelSource;
        this.model = model;
        this.pom = pom;
        this.trace = trace;
        this.error = error;
    }

    public RequestTrace getTrace()
    {
        return trace;
    }

    public ModelLoaderEventType getType()
    {
        return type;
    }

    public ModelSource getModelSource()
    {
        return modelSource;
    }

    public Model getModel()
    {
        return model;
    }

    public File getPom()
    {
        return pom;
    }

    public Throwable getError()
    {
        return error;
    }

    public FullProjectKey getKey()
    {
        return key;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( key == null ) ? 0 : key.hashCode() );
        result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        ModelLoaderEvent other = (ModelLoaderEvent) obj;
        if ( key == null )
        {
            if ( other.key != null )
            {
                return false;
            }
        }
        else if ( !key.equals( other.key ) )
        {
            return false;
        }
        if ( type != other.type )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "ModelLoaderEvent [type=" + type + ", model=" + key + "]";
    }

}
