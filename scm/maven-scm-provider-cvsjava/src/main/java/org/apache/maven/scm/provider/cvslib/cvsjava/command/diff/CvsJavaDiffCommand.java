package org.apache.maven.scm.provider.cvslib.cvsjava.command.diff;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.diff.DiffScmResult;
import org.apache.maven.scm.provider.cvslib.command.diff.AbstractCvsDiffCommand;
import org.apache.maven.scm.provider.cvslib.command.diff.CvsDiffConsumer;
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
public class CvsJavaDiffCommand
    extends AbstractCvsDiffCommand
{
    protected DiffScmResult executeCvsCommand( Commandline cl )
        throws ScmException
    {
        CvsLogListener logListener = new CvsLogListener();

        CvsDiffConsumer consumer = new CvsDiffConsumer( getLogger(), cl.getWorkingDirectory() );

        try
        {
            boolean isSuccess = CvsConnection.processCommand( cl.getArguments(),
                                                              cl.getWorkingDirectory().getAbsolutePath(), logListener,
                                                              getLogger() );

            if ( !isSuccess )
            {
                return new DiffScmResult( cl.toString(), "The cvs command failed.", logListener.getStderr().toString(),
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
            return new DiffScmResult( cl.toString(), "The cvs command failed.", logListener.getStdout().toString(),
                                      false );
        }

        return new DiffScmResult( cl.toString(), consumer.getChangedFiles(), consumer.getDifferences(),
                                  consumer.getPatch() );
    }

    protected boolean isSupportNewFileParameter()
    {
        return false;
    }
}
