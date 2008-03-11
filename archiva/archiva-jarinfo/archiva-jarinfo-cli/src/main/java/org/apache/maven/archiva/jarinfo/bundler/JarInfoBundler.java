package org.apache.maven.archiva.jarinfo.bundler;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.archiva.common.utils.DateUtil;
import org.apache.maven.archiva.jarinfo.JarInfoException;
import org.apache.maven.archiva.jarinfo.analysis.JarAnalysis;
import org.apache.maven.archiva.jarinfo.model.JarDetails;
import org.apache.maven.archiva.jarinfo.model.io.JarDetailsWriter;
import org.apache.maven.archiva.repository.content.ArtifactException;
import org.apache.maven.archiva.repository.content.ArtifactRef;
import org.apache.maven.archiva.repository.content.DefaultPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Jar Info Bundler.
 * 
 * Scan a repository root, creating a JarInfo bundle for the most recent
 * timestamp'd jarinfo file.
 */
public class JarInfoBundler
    extends DirectoryWalker
{
    private static final Logger LOG = LoggerFactory.getLogger( JarInfoBundler.class );

    private File repoDir;

    private String repoUrl;

    private String repoId;

    private File outputDir;

    private File bundleJar;

    private JarOutputStream jarOut;

    private String timestamp;

    private JarAnalysis jarAnalysis;

    private long startTime;

    private int analysisCount;

    public JarInfoBundler()
    {
        super();
    }

    public JarInfoBundler( FileFilter filter, int depthLimit )
    {
        super( filter, depthLimit );
    }

    public JarInfoBundler( IOFileFilter directoryFilter, IOFileFilter fileFilter, int depthLimit )
    {
        super( directoryFilter, fileFilter, depthLimit );
    }

    public File getRepoDir()
    {
        return repoDir;
    }

    public void setRepoDir( File repoDir )
    {
        this.repoDir = repoDir;
    }

    public String getRepoUrl()
    {
        return repoUrl;
    }

    public void setRepoUrl( String repoUrl )
    {
        this.repoUrl = repoUrl;
    }

    public String getRepoId()
    {
        return repoId;
    }

    public void setRepoId( String repoId )
    {
        this.repoId = repoId;
    }

    public File getOutputDir()
    {
        return outputDir;
    }

    public void setOutputDir( File outputDir )
    {
        this.outputDir = outputDir;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( String timestamp )
    {
        this.timestamp = timestamp;
    }

    public void begin()
        throws JarInfoBundlerException, IOException
    {
        if ( !outputDir.exists() )
        {
            outputDir.mkdirs();
        }
        bundleJar = new File( outputDir, "jarinfo-bundle.jar" );
        jarOut = new JarOutputStream( new FileOutputStream( bundleJar ) );

        boolean performInspection = false;
        jarAnalysis = new JarAnalysis( performInspection );
        jarAnalysis.addDefaultVisitors();

        walk( this.repoDir, Collections.emptyList() );

        // TODO: Gather Statistics.
    }

    @Override
    protected void handleStart( File startDirectory, Collection results )
        throws IOException
    {
        LOG.info( "Starting Directory Walking for " + startDirectory.getAbsolutePath() );
        startTime = System.currentTimeMillis();
        analysisCount = 0;
    }

    @Override
    protected void handleEnd( Collection results )
        throws IOException
    {
        long endTime = System.currentTimeMillis();
        LOG.info( "Reached end of Directory Walk: Analyzed " + analysisCount + " jars in "
            + DateUtil.getDuration( startTime, endTime ) );

        IOUtils.closeQuietly( jarOut );
    }

    @Override
    protected void handleCancelled( File startDirectory, Collection results, CancelException cancel )
        throws IOException
    {
        LOG.info( "Walk Cancelled: " + cancel.getMessage() );
        IOUtils.closeQuietly( jarOut );
    }

    @Override
    protected void handleFile( File file, int depth, Collection results )
        throws IOException
    {
        LOG.info( "Handle File " + file.getAbsolutePath() + ", depth:" + depth );

        try
        {
            ArtifactRef ref = toArtifactRef( file );
            LOG.info( "Artifact: " + ref );
            File outputFile = new File( file.getAbsolutePath() + "info" );

            if ( !needsUpdating( outputFile ) )
            {
                LOG.info( "Skipping (up-to-date): " + outputFile.getName() );
                return;
            }
            
            analysisCount++;

            long start = System.nanoTime();
            LOG.info( "Analyzing: " + file.getName() + " ... " );
            JarDetails details = jarAnalysis.analyze( file );
            if ( !details.getAssignedId().valid() )
            {
                // Inspected Ids have no value now.
                details.getInspectedIds().clearAll();
                // Set Assigned Ids.
                details.getAssignedId().setGroupId( ref.getGroupId() );
                details.getAssignedId().setArtifactId( ref.getArtifactId() );
                details.getAssignedId().setVersion( ref.getVersion() );
            }
            long mid = System.nanoTime();
            double seconds = ( ( (double) ( mid - start ) / 100000 ) / 1000 );
            LOG.info( "Scan of " + file.getName() + " completed in " + seconds + " second(s)" );

            ( new JarDetailsWriter() ).write( details, outputFile );
            addToBundle( outputFile );
        }
        catch ( ArtifactException e )
        {
            LOG.warn( "Invalid Artifact [" + file.getAbsolutePath() + "]: " + e.getMessage() );
        }
        catch ( IOException e )
        {
            LOG.warn( "Unable to analyze jar file [" + file.getAbsolutePath() + "]: " + e.getMessage(), e );
        }
        catch ( JarInfoException e )
        {
            LOG.warn( "Unable to write jarinfo for file [" + file.getAbsolutePath() + "]: " + e.getMessage(), e );
        }
    }

    private void addToBundle( File file )
        throws IOException
    {
        FileInputStream stream = null;
        try
        {
            jarOut.putNextEntry( new JarEntry( pathRelativeToRoot( file ) ) );
            stream = new FileInputStream( file );
            IOUtils.copy( stream, jarOut );
        }
        finally
        {
            jarOut.closeEntry();
            IOUtils.closeQuietly( stream );
        }
    }

    private String pathRelativeToRoot( File file )
    {
        String path = file.getAbsolutePath();
        String root = repoDir.getAbsolutePath();

        if ( path.startsWith( root ) )
        {
            path = path.substring( root.length() );
        }

        return path;
    }

    private ArtifactRef toArtifactRef( File file )
        throws ArtifactException
    {
        return DefaultPathParser.toArtifactRef( pathRelativeToRoot( file ) );
    }

    private boolean needsUpdating( File outputFile )
    {
        return true;
    }
}
