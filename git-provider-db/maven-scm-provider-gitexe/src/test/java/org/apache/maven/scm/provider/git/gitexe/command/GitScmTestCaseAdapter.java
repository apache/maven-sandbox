package org.apache.maven.scm.provider.git.gitexe.command;

import org.apache.maven.scm.ScmTestCase;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;

public class GitScmTestCaseAdapter extends ScmTestCase
{
    public void assertCommandLine( String expectedCommand, File expectedWorkingDirectory, Commandline actualCommand )
    {
        Commandline cl = new Commandline( expectedCommand );
        if ( expectedWorkingDirectory != null )
        {
            cl.setWorkingDirectory( expectedWorkingDirectory.getAbsolutePath() );
        }
        assertEquals( cl.toString(), actualCommand.toString() );
    }
}
