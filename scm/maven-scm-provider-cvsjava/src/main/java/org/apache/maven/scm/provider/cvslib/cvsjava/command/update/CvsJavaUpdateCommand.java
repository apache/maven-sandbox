package org.apache.maven.scm.provider.cvslib.cvsjava.command.update;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.changelog.ChangeLogCommand;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.provider.cvslib.command.update.AbstractCvsUpdateCommand;
import org.apache.maven.scm.provider.cvslib.command.update.CvsUpdateConsumer;
import org.apache.maven.scm.provider.cvslib.cvsjava.command.changelog.CvsJavaChangeLogCommand;
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
public class CvsJavaUpdateCommand
    extends AbstractCvsUpdateCommand
{
    protected UpdateScmResult executeCvsCommand( Commandline cl )
        throws ScmException
    {
        CvsLogListener logListener = new CvsLogListener();

        CvsUpdateConsumer consumer = new CvsUpdateConsumer( getLogger() );

        try
        {
            boolean isSuccess = CvsConnection.processCommand( cl.getArguments(),
                                                              cl.getWorkingDirectory().getAbsolutePath(), logListener,
                                                              getLogger() );

            if ( !isSuccess )
            {
                return new UpdateScmResult( cl.toString(), "The cvs command failed.",
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
            return new UpdateScmResult( cl.toString(), "The cvs command failed.", logListener.getStderr().toString(),
                                        false );
        }

        return new UpdateScmResult( cl.toString(), consumer.getUpdatedFiles() );
    }

    protected ChangeLogCommand getChangeLogCommand()
    {
        CvsJavaChangeLogCommand command = new CvsJavaChangeLogCommand();

        command.setLogger( getLogger() );

        return command;
    }
}
