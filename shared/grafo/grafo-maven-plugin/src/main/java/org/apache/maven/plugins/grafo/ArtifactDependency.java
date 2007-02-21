package org.apache.maven.plugins.grafo;

import org.apache.maven.artifact.Artifact;

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

public class ArtifactDependency
{

    private Artifact origin, destination;

    public ArtifactDependency()
    {
    }

    public ArtifactDependency(Artifact origin, Artifact destination)
    {
        this.origin = origin;
        this.destination = destination;
    }

    public void setOrigin( Artifact origin )
    {
        this.origin = origin;
    }

    public Artifact getOrigin()
    {
        return origin;
    }

    public void setDestination( Artifact destination )
    {
        this.destination = destination;
    }

    public Artifact getDestination()
    {
        return destination;
    }
}
