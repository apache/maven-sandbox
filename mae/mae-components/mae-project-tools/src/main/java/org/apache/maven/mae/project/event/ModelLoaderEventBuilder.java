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

import static org.apache.maven.mae.project.event.ModelLoaderEventType.BUILT;
import static org.apache.maven.mae.project.event.ModelLoaderEventType.ERROR;
import static org.apache.maven.mae.project.event.ModelLoaderEventType.RESOLVED;

import java.io.File;

import org.apache.maven.mae.project.ProjectToolsException;
import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelSource;
import org.sonatype.aether.RequestTrace;

public class ModelLoaderEventBuilder
{

    private final ModelLoaderEventType type;

    private Model model;

    private File pom;

    private FullProjectKey key;

    private Throwable error;

    private ModelSource modelSource;

    private final RequestTrace trace;

    private ModelLoaderEventBuilder( final ModelLoaderEventType type, final RequestTrace trace )
    {
        this.type = type;
        this.trace = trace;
    }

    public ModelLoaderEventBuilder withModelSource( final ModelSource modelSource )
    {
        this.modelSource = modelSource;
        return this;
    }

    public ModelLoaderEventBuilder withModel( final Model model )
        throws ProjectToolsException
    {
        this.model = model;
        this.key = new FullProjectKey( model );
        this.pom = model.getPomFile();
        return this;
    }

    public ModelLoaderEventBuilder withPom( final File pom )
    {
        this.pom = pom;
        return this;
    }

    public ModelLoaderEventBuilder withKey( final FullProjectKey key )
    {
        this.key = key;
        return this;
    }

    public ModelLoaderEventBuilder withError( final Throwable error )
    {
        this.error = error;
        return this;
    }

    public ModelLoaderEvent build()
    {
        if ( type == ERROR && error == null )
        {
            throw new IllegalArgumentException( "Cannot build error event when error has not been set!" );
        }
        else if ( type == BUILT )
        {
            if ( model == null )
            {
                throw new IllegalArgumentException( "Cannot build " + type + " event when model is missing!" );
            }
            else if ( key == null )
            {
                throw new IllegalArgumentException( "Cannot build " + type + " event when key is missing!" );
            }
        }
        else if ( type == RESOLVED )
        {
            if ( modelSource == null )
            {
                throw new IllegalArgumentException( "Cannot build " + type + " event when modelSource is missing!" );
            }
            else if ( key == null )
            {
                throw new IllegalArgumentException( "Cannot build " + type + " event when key is missing!" );
            }
        }
        else if ( type != ERROR && error != null )
        {
            throw new IllegalArgumentException( "Cannot build " + type + " event when error is set!" );
        }

        return new ModelLoaderEvent( type, key, modelSource, model, pom, trace, error );
    }

    public static final ModelLoaderEventBuilder newBuiltModelEvent( final RequestTrace trace )
    {
        return new ModelLoaderEventBuilder( BUILT, trace );
    }

    public static final ModelLoaderEventBuilder newResolvedModelEvent( final RequestTrace trace )
    {
        return new ModelLoaderEventBuilder( RESOLVED, trace );
    }

    public static final ModelLoaderEventBuilder newErrorEvent( final RequestTrace trace )
    {
        return new ModelLoaderEventBuilder( ERROR, trace );
    }

}
