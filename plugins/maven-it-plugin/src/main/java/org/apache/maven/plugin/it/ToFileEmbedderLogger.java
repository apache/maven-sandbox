package org.apache.maven.plugin.it;

import org.apache.maven.embedder.AbstractMavenEmbedderLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class ToFileEmbedderLogger
    extends AbstractMavenEmbedderLogger
{
    
    private final File outputFile;
    
    public ToFileEmbedderLogger( File outputFile )
    {
        this.outputFile = outputFile;
    }

    public void debug( String message, Throwable throwable )
    {
        write( "debug", message, throwable );
    }

    public void info( String message, Throwable throwable )
    {
        write( "info", message, throwable );
    }

    public void warn( String message, Throwable throwable )
    {
        write( "warn", message, throwable );
    }

    public void error( String message, Throwable throwable )
    {
        write( "error", message, throwable );
    }

    public void fatalError( String message, Throwable throwable )
    {
        write( "fatal", message, throwable );
    }


    private void write( String levelNotation, String message, Throwable throwable )
    {
        String formatted = "[" + levelNotation + "] " + message;
        
        FileWriter writer = null;
        
        Logger logger = LogManager.getLogManager().getLogger( ToFileEmbedderLogger.class.getName() );
        
        try
        {
            writer = new FileWriter( outputFile, true );
            writer.write( formatted );
            
            if ( throwable != null )
            {
                writer.write( " Message: " + throwable.getMessage() + "\n" );
                throwable.printStackTrace( new PrintWriter( writer ) );
            }
        }
        catch ( IOException e )
        {
            String errormessage = "ERROR: Cannot open output file: " + outputFile + " for logging. Original log message:\n\n" + formatted + "\n\nError: " + e.getMessage();
            
            if ( logger != null )
            {
                logger.warning( errormessage );
            }
            else
            {
                System.out.println( errormessage );
                e.printStackTrace();
            }
        }
        finally
        {
            if ( writer != null )
            {
                try
                {
                    writer.flush();
                    writer.close();
                }
                catch( IOException e )
                {
                    if ( logger != null )
                    {
                        logger.finest( "Error closing output file writer: " + e.getMessage() );
                    }
                    else
                    {
                        System.out.println( "Error closing output file writer." );
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
