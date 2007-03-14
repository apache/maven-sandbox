package org.apache.maven.scm.provider.cvslib.cvsjava.command.remove;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.remove.RemoveScmResult;
import org.apache.maven.scm.provider.cvslib.command.remove.AbstractCvsRemoveCommand;
import org.apache.maven.scm.provider.cvslib.cvsjava.util.CvsConnection;
import org.apache.maven.scm.provider.cvslib.cvsjava.util.CvsLogListener;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class CvsJavaRemoveCommand
    extends AbstractCvsRemoveCommand
{
    protected RemoveScmResult executeCvsCommand( Commandline cl, List removedFiles )
        throws ScmException
    {
        CvsLogListener logListener = new CvsLogListener();

        try
        {
            boolean isSuccess = CvsConnection.processCommand( cl.getArguments(),
                                                              cl.getWorkingDirectory().getAbsolutePath(), logListener,
                                                              getLogger() );

            if ( !isSuccess )
            {
                return new RemoveScmResult( cl.toString(), "The cvs command failed.",
                                            logListener.getStderr().toString(), false );
            }
            BufferedReader stream = new BufferedReader(
                new InputStreamReader( new ByteArrayInputStream( logListener.getStdout().toString().getBytes() ) ) );

            String line;

            while ( ( line = stream.readLine() ) != null )
            {
                getLogger().debug( line );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return new RemoveScmResult( cl.toString(), "The cvs command failed.", logListener.getStderr().toString(),
                                        false );
        }

        return new RemoveScmResult( cl.toString(), removedFiles );
    }
}
