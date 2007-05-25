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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Adapter class to make a mojo Log instance look like an antcall logger.
 * This adapter is the only API dependency on maven-plugin-api, and therefore
 * may belong somewhere else, in order to keep this APIs dependency set simple.
 */
public class SysoutLogAdapter
    implements AntCallLogger
{
    
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
        StringWriter writer = new StringWriter();
        PrintWriter pWriter = new PrintWriter( writer );
        
        pWriter.println( message );
        error.printStackTrace( pWriter );
        
        System.out.println( writer.toString() );
    }

    private void log( CharSequence message )
    {
        System.out.println( message );
    }

}
