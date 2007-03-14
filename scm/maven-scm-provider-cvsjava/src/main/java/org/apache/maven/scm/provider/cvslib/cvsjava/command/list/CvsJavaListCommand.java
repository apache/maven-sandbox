package org.apache.maven.scm.provider.cvslib.cvsjava.command.list;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.list.ListScmResult;
import org.apache.maven.scm.provider.cvslib.command.list.AbstractCvsListCommand;
import org.apache.maven.scm.provider.cvslib.command.status.CvsStatusConsumer;
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
public class CvsJavaListCommand
    extends AbstractCvsListCommand
{
    protected ListScmResult executeCvsCommand( Commandline cl )
        throws ScmException
    {
        CvsLogListener logListener = new CvsLogListener();

        CvsStatusConsumer consumer = new CvsStatusConsumer( getLogger(), cl.getWorkingDirectory() );

        try
        {
            boolean isSuccess = CvsConnection.processCommand( cl.getArguments(),
                                                              cl.getWorkingDirectory().getAbsolutePath(), logListener,
                                                              getLogger() );

            if ( !isSuccess )
            {
                return new ListScmResult( cl.toString(), "The cvs command failed.", logListener.getStderr().toString(),
                                          false );
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
            return new ListScmResult( cl.toString(), "The cvs command failed.", logListener.getStderr().toString(),
                                      false );
        }

        return new ListScmResult( cl.toString(), consumer.getChangedFiles() );
    }
}
