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

package org.apache.maven.mae.io;

import org.apache.maven.mae.conf.MAEConfiguration;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.StringUtils;

import javax.inject.Inject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import jline.ConsoleReader;

@Component( role = Prompter.class, hint = "emb" )
public class MAEPrompter
    implements Prompter
{
    @Requirement
    private final MAEConfiguration embConfig;

    @Inject
    public MAEPrompter( final MAEConfiguration embConfig )
    {
        this.embConfig = embConfig;
    }

    public String prompt( final String message )
        throws PrompterException
    {
        try
        {
            writePrompt( message );
        }
        catch ( final IOException e )
        {
            throw new PrompterException( "Failed to present prompt", e );
        }

        try
        {
            return readLine();
        }
        catch ( final IOException e )
        {
            throw new PrompterException( "Failed to read user response", e );
        }
    }

    private String readLine()
        throws IOException
    {
        return new BufferedReader( new InputStreamReader( embConfig.getStandardIn() ) ).readLine();
    }

    public String prompt( final String message, final String defaultReply )
        throws PrompterException
    {
        try
        {
            writePrompt( formatMessage( message, null, defaultReply ) );
        }
        catch ( final IOException e )
        {
            throw new PrompterException( "Failed to present prompt", e );
        }

        try
        {
            String line = readLine();

            if ( StringUtils.isEmpty( line ) )
            {
                line = defaultReply;
            }

            return line;
        }
        catch ( final IOException e )
        {
            throw new PrompterException( "Failed to read user response", e );
        }
    }

    @SuppressWarnings( "rawtypes" )
    public String prompt( final String message, final List possibleValues, final String defaultReply )
        throws PrompterException
    {
        final String formattedMessage = formatMessage( message, possibleValues, defaultReply );

        String line;

        do
        {
            try
            {
                writePrompt( formattedMessage );
            }
            catch ( final IOException e )
            {
                throw new PrompterException( "Failed to present prompt", e );
            }

            try
            {
                line = readLine();
            }
            catch ( final IOException e )
            {
                throw new PrompterException( "Failed to read user response", e );
            }

            if ( StringUtils.isEmpty( line ) )
            {
                line = defaultReply;
            }

            if ( line != null && !possibleValues.contains( line ) )
            {
                writeLine( "Invalid selection." );
            }
        }
        while ( line == null || !possibleValues.contains( line ) );

        return line;
    }

    private void writeLine( final String message )
    {
        embConfig.getStandardOut().println( message );
    }

    @SuppressWarnings( "rawtypes" )
    public String prompt( final String message, final List possibleValues )
        throws PrompterException
    {
        return prompt( message, possibleValues, null );
    }

    public String promptForPassword( final String message )
        throws PrompterException
    {
        try
        {
            writePrompt( message );
        }
        catch ( final IOException e )
        {
            throw new PrompterException( "Failed to present prompt", e );
        }

        try
        {
            return new ConsoleReader( embConfig.getStandardIn(), new OutputStreamWriter( embConfig.getStandardOut() ) ).readLine( new Character(
                                                                                                                                                 '*' ) );
        }
        catch ( final IOException e )
        {
            throw new PrompterException( "Failed to read user response", e );
        }
    }

    @SuppressWarnings( "rawtypes" )
    private String formatMessage( final String message, final List possibleValues, final String defaultReply )
    {
        final StringBuffer formatted = new StringBuffer( message.length() * 2 );

        formatted.append( message );

        if ( possibleValues != null && !possibleValues.isEmpty() )
        {
            formatted.append( " (" );

            for ( final Iterator it = possibleValues.iterator(); it.hasNext(); )
            {
                final String possibleValue = (String) it.next();

                formatted.append( possibleValue );

                if ( it.hasNext() )
                {
                    formatted.append( '/' );
                }
            }

            formatted.append( ')' );
        }

        if ( defaultReply != null )
        {
            formatted.append( ' ' ).append( defaultReply ).append( ": " );
        }

        return formatted.toString();
    }

    private void writePrompt( final String message )
        throws IOException
    {
        embConfig.getStandardOut().print( message + ": " );
    }

    public void showMessage( final String message )
        throws PrompterException
    {
        try
        {
            writePrompt( message );
        }
        catch ( final IOException e )
        {
            throw new PrompterException( "Failed to present prompt", e );
        }

    }

}
