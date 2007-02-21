package test;

import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.CommandExecutor;
import org.apache.maven.wagon.observers.Debug;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;

import java.io.File;

import junit.framework.Assert;

/**
 * Hello world!
 */
public class App
{
    private static long lastCheckpoint;

    private static final int BUFFER_SIZE = 1024 * 20;

    public static void main( String[] args )
        throws Throwable
    {
        if ( args.length != 1 )
        {
            System.err.println( "You must provide a target URL as an argument, eg scp://username@hostname/path" );
            return;
        }

        String url = args[0];

        Repository repository = new Repository( "target", url );

        System.out.println( "URL is " + repository );

        Embedder embedder = new Embedder();
        embedder.start();

        PlexusContainer container = embedder.getContainer();

        CommandExecutor wagon = (CommandExecutor) container.lookup( "org.apache.maven.wagon.Wagon", repository.getProtocol() );
        wagon.addSessionListener( new Debug() );

        try
        {
            lastCheckpoint = System.currentTimeMillis();
            System.out.print( "Connecting... " );
            wagon.connect( repository );
            displayTime();

            String cmd = "mkdir -p " + repository.getBasedir();
            System.out.print( "Executing mkdir command '" + cmd + "'... " );
            wagon.executeCommand( cmd );
            displayTime();

            byte[] buf = new byte[BUFFER_SIZE];
            for ( int i = 0; i < BUFFER_SIZE; i++ )
            {
                buf[i] = (byte)((int) Math.random() & 0xFF);
            }

            File f = File.createTempFile( "foo", "bar" );
            f.deleteOnExit();
            String expected = new String( buf );
            Assert.assertEquals( BUFFER_SIZE, expected.length() );
            FileUtils.fileWrite( f.getAbsolutePath(), expected );
            System.out.print( "Executing upload of '" + f + "' to '" + repository.getUrl() + "/bar'... " );
            wagon.put( f, "bar" );
            displayTime();

            f = File.createTempFile( "foo", "bar" );
            f.deleteOnExit();
            System.out.print( "Executing download of '" + repository.getUrl() + "/bar' to '" + f + "'... " );
            wagon.get( "bar", f );
            String s = FileUtils.fileRead( f );
            Assert.assertEquals( expected.length(), s.length() );
            Assert.assertEquals( expected, s );
            displayTime();

            System.out.print( "Disconnecting... " );
            wagon.disconnect();
            displayTime();
        }
        catch ( Throwable t )
        {
            System.out.println();
            throw t;
        }
    }

    private static void displayTime()
    {
        System.out.println( ( System.currentTimeMillis() - lastCheckpoint ) + "ms" );
        lastCheckpoint = System.currentTimeMillis();
    }
}
