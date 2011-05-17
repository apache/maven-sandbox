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

package org.apache.maven.mae.conf;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class MavenPomVersionProvider
    implements VersionProvider
{

    private static final Logger logger = Logger.getLogger( MAEConfiguration.STANDARD_LOG_HANDLE_LOADER );

    private final String groupId;

    private final String artifactId;

    private String version;

    public MavenPomVersionProvider( final String groupId, final String artifactId )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public final synchronized String getVersion()
    {
        if ( version == null )
        {
            final String path = "META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
            final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream( path );

            if ( stream != null )
            {
                final Properties p = new Properties();
                try
                {
                    p.load( stream );
                    version = p.getProperty( "version" );
                }
                catch ( final IOException e )
                {
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug( "Failed to load version for: " + groupId + ":" + artifactId );
                    }
                }
            }

            if ( version == null )
            {
                version = "-UNKNOWN-";
            }
        }

        return version;
    }

}
