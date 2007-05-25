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

import java.util.Arrays;
import java.util.List;

/**
 * Interface for logging within maven-antcall. CharSequence's are used to avoid the need
 * to convert StringBuffers to Strings.
 */
public interface AntCallLogger
{

    static final String MESSAGE_LEVEL_VERBOSE = "verbose";
    static final String MESSAGE_LEVEL_DEBUG = "debug";
    static final String MESSAGE_LEVEL_INFO = "info";
    static final String MESSAGE_LEVEL_WARN = "warn";
    static final String MESSAGE_LEVEL_ERROR = "error";
    static final String MESSAGE_LEVEL_SUPPRESS = "suppress";
    
    static final List MESSAGE_LEVELS = Arrays.asList( new String[]{
        MESSAGE_LEVEL_VERBOSE,
        MESSAGE_LEVEL_DEBUG,
        MESSAGE_LEVEL_INFO,
        MESSAGE_LEVEL_WARN,
        MESSAGE_LEVEL_ERROR,
        MESSAGE_LEVEL_SUPPRESS
    } );

    void debug( CharSequence message );
    
    void debug( CharSequence message, Throwable error );
    
    void info( CharSequence message );
    
    void info( CharSequence message, Throwable error );
    
    void warn( CharSequence message );
    
    void warn( CharSequence message, Throwable error );
    
    void error( CharSequence message );
    
    void error( CharSequence message, Throwable error );
    
}
