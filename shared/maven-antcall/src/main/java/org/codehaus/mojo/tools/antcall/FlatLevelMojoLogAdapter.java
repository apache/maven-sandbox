package org.codehaus.mojo.tools.antcall;

/*
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
 *
 */

import org.apache.maven.plugin.logging.Log;

/**
 * Adapter class to make a mojo Log instance look like an antcall logger.
 * This adapter is the only API dependency on maven-plugin-api, and therefore
 * may belong somewhere else, in order to keep this APIs dependency set simple.
 */
public class FlatLevelMojoLogAdapter
    implements AntCallLogger
{
    
    private final Log mojoLog;
    private final String level;

    public FlatLevelMojoLogAdapter( Log mojoLog, String level )
    {
        this.mojoLog = mojoLog;
        this.level = level;
    }

    /**
     * Never sink below the radar...only go as low as INFO.
     */
    public void debug( CharSequence message, Throwable error )
    {
        log( message, error );
    }

    /**
     * Never sink below the radar...only go as low as INFO.
     */
    public void debug( CharSequence message )
    {
        log( message );
    }

    public void error( CharSequence message, Throwable error )
    {
        log( message, error );
    }

    public void error( CharSequence message )
    {
        log( message );
    }

    public void info( CharSequence message, Throwable error )
    {
        log( message, error );
    }

    public void info( CharSequence message )
    {
        log( message );
    }

    public void warn( CharSequence message, Throwable error )
    {
        log( message, error );
    }

    public void warn( CharSequence message )
    {
        log( message );
    }

    private void log( CharSequence message, Throwable error )
    {
        if ( MESSAGE_LEVEL_DEBUG.equalsIgnoreCase( level ) )
        {
            mojoLog.debug( message, error );
        }
        else if ( MESSAGE_LEVEL_ERROR.equalsIgnoreCase( level ) )
        {
            mojoLog.error(  message, error );
        }
        else if ( MESSAGE_LEVEL_VERBOSE.equalsIgnoreCase( level ) )
        {
            mojoLog.debug(  message, error );
        }
        else if ( MESSAGE_LEVEL_WARN.equalsIgnoreCase( level ) )
        {
            mojoLog.warn(  message, error );
        }
        else if ( !MESSAGE_LEVEL_SUPPRESS.equalsIgnoreCase( level ) )
        {
            mojoLog.info(  message, error );
        }
    }

    private void log( CharSequence message )
    {
        if ( MESSAGE_LEVEL_DEBUG.equalsIgnoreCase( level ) )
        {
            mojoLog.debug( message );
        }
        else if ( MESSAGE_LEVEL_ERROR.equalsIgnoreCase( level ) )
        {
            mojoLog.error(  message );
        }
        else if ( MESSAGE_LEVEL_VERBOSE.equalsIgnoreCase( level ) )
        {
            mojoLog.debug(  message );
        }
        else if ( MESSAGE_LEVEL_WARN.equalsIgnoreCase( level ) )
        {
            mojoLog.warn(  message );
        }
        else if ( !MESSAGE_LEVEL_SUPPRESS.equalsIgnoreCase( level ) )
        {
            mojoLog.info(  message );
        }
    }

}
