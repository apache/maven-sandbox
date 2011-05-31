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

package org.apache.maven.mae.prompt;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.mae.conf.MAEConfiguration;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import javax.inject.Inject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import jline.ConsoleReader;

@Component( role = Prompt.class, hint = MAEPrompt.NAME )
public class MAEPrompt
    implements Prompt
{
    
    public static final String NAME = "mae";
    
    @Requirement
    private final MAEConfiguration config;

    @Inject
    public MAEPrompt( final MAEConfiguration config )
    {
        this.config = config;
    }

    public String getInput( final String message )
        throws PromptException
    {
        try
        {
            writePrompt( message );
        }
        catch ( final IOException e )
        {
            throw new PromptException( "Failed to present prompt", e );
        }

        try
        {
            return readLine();
        }
        catch ( final IOException e )
        {
            throw new PromptException( "Failed to read user response", e );
        }
    }

    private String readLine()
        throws IOException
    {
        return new BufferedReader( new InputStreamReader( config.getStandardIn() ) ).readLine();
    }

    public String getInput( final String message, final String defaultReply )
        throws PromptException
    {
        try
        {
            writePrompt( formatMessage( message, defaultReply ) );
        }
        catch ( final IOException e )
        {
            throw new PromptException( "Failed to present prompt", e );
        }

        try
        {
            String line = readLine();

            if ( isEmpty( line ) )
            {
                line = defaultReply;
            }

            return line;
        }
        catch ( final IOException e )
        {
            throw new PromptException( "Failed to read user response", e );
        }
    }

    public int getSelection( final String message, final List<?> possibleValues, final int defaultSelection )
        throws PromptException
    {
        final String formattedMessage = formatMessage( message, possibleValues, defaultSelection );

        int result = -1;
        String line;
        do
        {
            try
            {
                writePrompt( formattedMessage );
            }
            catch ( final IOException e )
            {
                throw new PromptException( "Failed to present prompt", e );
            }

            try
            {
                line = readLine();
            }
            catch ( final IOException e )
            {
                throw new PromptException( "Failed to read user response", e );
            }

            if ( isEmpty( line ) )
            {
                result = defaultSelection;
            }
            else
            {
                line = line.trim();
                
                if ( !possibleValues.contains( line ) )
                {
                    writeLine( "Invalid selection." );
                }
                else
                {
                    result = possibleValues.indexOf( line ) - 1;
                }
            }
        }
        while ( result < 0 );

        return result;
    }

    private boolean isEmpty( String line )
    {
        return ( StringUtils.isEmpty( line ) || StringUtils.isEmpty( line.trim() ) );
    }

    private void writeLine( final String message )
    {
        config.getStandardOut().println( message );
    }

    public int getSelection( final String message, final List<?> possibleValues )
        throws PromptException
    {
        return getSelection( message, possibleValues, -1 );
    }

    public String getPassword( final String message )
        throws PromptException
    {
        try
        {
            writePrompt( message );
        }
        catch ( final IOException e )
        {
            throw new PromptException( "Failed to present prompt", e );
        }

        try
        {
            return new ConsoleReader( config.getStandardIn(), new OutputStreamWriter( config.getStandardOut() ) ).readLine( new Character(
                                                                                                                                                 '*' ) );
        }
        catch ( final IOException e )
        {
            throw new PromptException( "Failed to read user response", e );
        }
    }

    private String formatMessage( final String message, final String defaultReply )
    {
        final StringBuilder formatted = new StringBuilder();

        formatted.append( message );

        if ( defaultReply != null )
        {
            formatted.append( ' ' ).append( defaultReply ).append( ": " );
        }

        return formatted.toString();
    }
    
    private String formatMessage( final String message, final List<?> possibleValues, final int defaultReply )
    {
        final StringBuilder formatted = new StringBuilder();

        if ( possibleValues != null && !possibleValues.isEmpty() )
        {
            for( int i =0; i< possibleValues.size(); i++ )
            {
                Object possibleValue = possibleValues.get( i );

                formatted.append( i+1 ).append( ". " ).append( possibleValue );

                if ( i+1 < possibleValues.size() )
                {
                    formatted.append( '\n' );
                }
            }

            formatted.append( "\n\n" );
        }

        formatted.append( message );

        if ( defaultReply > -1 )
        {
            formatted.append( ' ' ).append( defaultReply ).append( ": " );
        }

        return formatted.toString();
    }

    private void writePrompt( final String message )
        throws IOException
    {
        config.getStandardOut().print( message + ": " );
    }

}
