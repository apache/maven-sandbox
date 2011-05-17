/*
 * Copyright 2010 Red Hat, Inc.
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

package org.apache.maven.mae.conf.loader;

import org.apache.maven.mae.conf.MAEConfiguration;
import org.apache.maven.mae.conf.MAELibrary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class InstanceLibraryLoader
    implements MAELibraryLoader
{

    private final List<MAELibrary> libraries;

    public InstanceLibraryLoader( final MAELibrary... libraries )
    {
        this.libraries =
            libraries == null ? new ArrayList<MAELibrary>() : new ArrayList<MAELibrary>( Arrays.asList( libraries ) );
    }

    public InstanceLibraryLoader( final List<MAELibrary> libraries )
    {
        this.libraries = libraries;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.mae.conf.loader.MAELibraryLoader#loadLibraries(org.apache.maven.mae.conf.MAEConfiguration)
     */
    @Override
    public Collection<MAELibrary> loadLibraries( final MAEConfiguration embConfig )
        throws IOException
    {
        return libraries;
    }

}
