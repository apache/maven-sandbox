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

import org.apache.log4j.Logger;
import org.apache.maven.mae.conf.MAEConfiguration;
import org.apache.maven.mae.conf.MAELibrary;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;

public class ServiceLibraryLoader
    implements MAELibraryLoader
{

    @SuppressWarnings( "unused" )
    private static final Logger logger = Logger.getLogger( MAEConfiguration.STANDARD_LOG_HANDLE_LOADER );

    public Collection<MAELibrary> loadLibraries( final MAEConfiguration embConfig )
        throws IOException
    {
        final LinkedHashSet<MAELibrary> libraries = new LinkedHashSet<MAELibrary>();

        final ServiceLoader<MAELibrary> loader = ServiceLoader.load( MAELibrary.class );
        for ( final MAELibrary library : loader )
        {
            libraries.add( library );
        }

        return libraries;
    }

}
