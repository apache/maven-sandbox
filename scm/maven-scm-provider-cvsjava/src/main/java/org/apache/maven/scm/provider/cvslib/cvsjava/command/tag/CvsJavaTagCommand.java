package org.apache.maven.scm.provider.cvslib.cvsjava.command.tag;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.tag.TagScmResult;
import org.apache.maven.scm.provider.cvslib.command.tag.AbstractCvsTagCommand;
import org.apache.maven.scm.provider.cvslib.command.tag.CvsTagConsumer;
import org.apache.maven.scm.provider.cvslib.cvsjava.util.CvsConnection;
import org.apache.maven.scm.provider.cvslib.cvsjava.util.CvsLogListener;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class CvsJavaTagCommand
    extends AbstractCvsTagCommand
{
    protected TagScmResult executeCvsCommand( Commandline cl )
        throws ScmException
    {
        CvsLogListener logListener = new CvsLogListener();

        CvsTagConsumer consumer = new CvsTagConsumer( getLogger() );

        try
        {
            boolean isSuccess = CvsConnection.processCommand( cl.getArguments(),
                                                              cl.getWorkingDirectory().getAbsolutePath(), logListener,
                                                              getLogger() );

            if ( !isSuccess )
            {
                return new TagScmResult( cl.toString(), "The cvs tag command failed.",
                                         logListener.getStderr().toString(), false );
            }
            BufferedReader stream = new BufferedReader(
                new InputStreamReader( new ByteArrayInputStream( logListener.getStdout().toString().getBytes() ) ) );

            String line;

            while ( ( line = stream.readLine() ) != null )
            {
                consumer.consumeLine( line );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return new TagScmResult( cl.toString(), "The cvs tag command failed.", logListener.getStderr().toString(),
                                     false );
        }

        return new TagScmResult( cl.toString(), consumer.getTaggedFiles() );
    }
}
