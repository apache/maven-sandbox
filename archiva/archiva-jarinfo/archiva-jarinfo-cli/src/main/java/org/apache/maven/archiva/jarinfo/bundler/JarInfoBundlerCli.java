package org.apache.maven.archiva.jarinfo.bundler;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

/**
 * JarInfoBundler command line client.
 */
public class JarInfoBundlerCli
{
    private static final String OPT_JARINFO_OUTPUT_DIR = "o";

    private static final String OPT_REPO_DIR = "d";

    private static final String OPT_REPO_URL = "u";

    private static final String OPT_REPO_ID = "i";

    private static final String OPT_TIMESTAMP = "t";

    public static void main( String args[] )
    {
        ( new JarInfoBundlerCli() ).execute( args );
    }

    private Options options;

    private Options getOptions()
    {
        if ( options != null )
        {
            return options;
        }

        Option outputDir = new Option( OPT_JARINFO_OUTPUT_DIR, "outputDir", true, "Jarinfo Output Directory." );
        outputDir.setArgName( "dir" );
        outputDir.setRequired( true );

        Option repoDir = new Option( OPT_REPO_DIR, "repoDir", true, "Repository Root Directory." );
        repoDir.setArgName( "dir" );
        repoDir.setRequired( true );

        Option repoUrl = new Option( OPT_REPO_URL, "repoUrl", true, "URL to Repository." );
        repoUrl.setArgName( "url" );
        repoUrl.setRequired( true );

        Option repoId = new Option( OPT_REPO_ID, "repoId", true, "ID to Repository." );
        repoId.setArgName( "id" );
        repoId.setRequired( true );

        Option timestamp = new Option( OPT_TIMESTAMP, "timestamp", true,
                                       "Timestamp for repo-${id}-jarinfo-${timestamp}.jar files." );
        timestamp.setArgName( "timestamp" );
        timestamp.setRequired( false );

        options = new Options();
        options.addOption( outputDir );
        options.addOption( repoId );
        options.addOption( repoUrl );
        options.addOption( repoDir );
        options.addOption( timestamp );

        return options;
    }

    private void showHelp( Options options )
    {
        HelpFormatter help = new HelpFormatter();
        help.printHelp( "JarInfoBundler [options] -o <dir> -i <id> -u <url> -d <dir>", options );
    }

    private void execute( String[] args )
    {
        CommandLineParser parser = new GnuParser();
        try
        {
            CommandLine cmdline = parser.parse( getOptions(), args );

            /* Ignore Hidden Directories */
            IOFileFilter ignoreHiddenFilter = FileFilterUtils.andFileFilter( FileFilterUtils.directoryFileFilter(),
                                                                             HiddenFileFilter.VISIBLE );

            /* Ignore SCM dirs */
            IOFileFilter ignoreSCM = FileFilterUtils.notFileFilter( new NameFileFilter( new String[] {
                ".svn",
                "CVS",
                "SCCS",
                ".arch-ids",
                ".bzr" } ) );

            IOFileFilter directoryFilter = FileFilterUtils.andFileFilter( ignoreHiddenFilter, ignoreSCM );

            /* Only interested in files ending in JAR */
            IOFileFilter jarFilter = FileFilterUtils.suffixFileFilter( ".jar" );
            IOFileFilter notJavadocOrSourceFilter = FileFilterUtils.notFileFilter( new SuffixFileFilter( new String[] {
                "-sources.jar",
                "-javadoc.jar" } ) );
            IOFileFilter jarBinaryFilter = FileFilterUtils.andFileFilter( jarFilter, notJavadocOrSourceFilter );

            JarInfoBundler bundler = new JarInfoBundler( directoryFilter, jarBinaryFilter, -1 );

            // TODO: log output dir?
            bundler.setOutputDir( new File( cmdline.getOptionValue( OPT_JARINFO_OUTPUT_DIR ) ) );
            bundler.setRepoDir( new File( cmdline.getOptionValue( OPT_REPO_DIR ) ) );
            bundler.setRepoId( cmdline.getOptionValue( OPT_REPO_ID ) );
            bundler.setRepoUrl( cmdline.getOptionValue( OPT_REPO_URL ) );
            if ( cmdline.hasOption( OPT_TIMESTAMP ) )
            {
                bundler.setTimestamp( cmdline.getOptionValue( OPT_TIMESTAMP ) );
            }

            bundler.begin();
        }
        catch ( MissingOptionException e )
        {
            System.err.println( "ERROR: " + e.getMessage() );
            showHelp( options );
        }
        catch ( ParseException e )
        {
            showHelp( options );
            e.printStackTrace( System.err );
        }
        catch ( Throwable t )
        {
            t.printStackTrace( System.err );
        }
    }
}
