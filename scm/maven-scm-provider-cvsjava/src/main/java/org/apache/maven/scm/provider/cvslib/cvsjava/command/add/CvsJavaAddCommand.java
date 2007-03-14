package org.apache.maven.scm.provider.cvslib.cvsjava.command.add;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.add.AddScmResult;
import org.apache.maven.scm.provider.cvslib.command.add.AbstractCvsAddCommand;
import org.apache.maven.scm.provider.cvslib.cvsjava.util.CvsConnection;
import org.apache.maven.scm.provider.cvslib.cvsjava.util.CvsLogListener;
import org.codehaus.plexus.util.cli.Commandline;

import java.util.List;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class CvsJavaAddCommand
    extends AbstractCvsAddCommand
{
    protected AddScmResult executeCvsCommand( Commandline cl, List addedFiles )
        throws ScmException
    {
        CvsLogListener logListener = new CvsLogListener();

        try
        {
            boolean isSuccess = CvsConnection.processCommand( cl.getArguments(),
                                                              cl.getWorkingDirectory().getAbsolutePath(), logListener,
                                                              getLogger() );

            // TODO: actually it may have partially succeeded - should we cvs update the files and parse "A " responses?
            if ( !isSuccess )
            {
                return new AddScmResult( cl.toString(), "The cvs command failed.", logListener.getStdout().toString(),
                                         false );
            }

            return new AddScmResult( cl.toString(), addedFiles );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return new AddScmResult( cl.toString(), "The cvs command failed.", logListener.getStdout().toString(),
                                     false );
        }
    }
}
