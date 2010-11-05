package org.apache.maven.shared.scmchanges;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 * Build only projects containing files that that you personally have changed (according to SCM)
 * 
 * @author <a href="mailto:dfabulich@apache.org">Dan Fabulich</a>
 */
@Component( role = AbstractMavenLifecycleParticipant.class, hint = "scm-changes" )
public class MakeScmChanges
    extends AbstractMavenLifecycleParticipant
{
    @Requirement
    private Logger logger;

    @Requirement
    ScmManager scmManager;

    /** SCM connection from root project */
    String scmConnection;

    /** make.ignoreUnknown: Ignore files in the "unknown" status (created but not added to source control) */
    boolean ignoreUnknown = true;
    
    /** make.ignoreRootPom: Ignore changes in the root POM file, which would normally cause a full rebuild */
    boolean ignoreRootPom = false;

    /** Disabled by default; activate via -Dmake.scmChanges=true */
    boolean enabled = false;

    public void afterProjectsRead( MavenSession session )
        throws MavenExecutionException
    {
        readParameters( session );

        if ( !enabled )
        {
            logger.debug( "make.scmChanges = false, not modifying project list" );
            return;
        }
            

        List<ScmFile> changedFiles = getChangedFilesFromScm( session.getTopLevelProject().getBasedir() );

        List<String> includedProjects = new ArrayList<String>();

        for ( ScmFile changedScmFile : changedFiles )
        {
            logger.debug( changedScmFile.toString() );
            ScmFileStatus status = changedScmFile.getStatus();
            if ( !status.isStatus() ) // isStatus means "isUnknown or isDiff"
            {
                logger.debug( "Not a diff: " + status );
                continue;
            }
            if ( ignoreUnknown && ScmFileStatus.UNKNOWN.equals( status ) )
            {
                logger.debug( "Ignoring unknown" );
                continue;
            }

            File changedFile = new File( changedScmFile.getPath() );
            
            if ( ignoreRootPom && session.getTopLevelProject().getFile().getAbsoluteFile().equals( changedFile.getAbsoluteFile() ) )
            {
                continue;
            }
            
            boolean found = false;
            // TODO There's a cleverer/faster way to code this, right? This is O(n^2)
            for ( MavenProject project : session.getProjects() )
            {
                File projectDirectory = project.getFile().getParentFile();
                if ( changedFile.getAbsolutePath().startsWith( projectDirectory.getAbsolutePath() + File.separator ) )
                {
                    if ( !includedProjects.contains( project ) )
                    {
                        logger.debug( "Including " + project );
                    }
                    includedProjects.add( project.getGroupId() + ":" + project.getArtifactId() );
                    found = true;
                    break;
                }
            }
            if ( !found )
            {
                logger.debug( "Couldn't find file in any project root: " + changedFile.getAbsolutePath() );
            }
        }

        if ( includedProjects.isEmpty() )
            throw new MavenExecutionException( "No SCM changes detected; nothing to do!",
                                               session.getTopLevelProject().getFile() );

        MavenExecutionRequest request = session.getRequest();
        String makeBehavior = request.getMakeBehavior();
        if ( makeBehavior == null )
        {
            request.setMakeBehavior( MavenExecutionRequest.REACTOR_MAKE_DOWNSTREAM );
        }
        if ( MavenExecutionRequest.REACTOR_MAKE_UPSTREAM.equals( makeBehavior ) )
        {
            request.setMakeBehavior( MavenExecutionRequest.REACTOR_MAKE_BOTH );
        }

        request.setSelectedProjects( includedProjects );

    }

    private void readParameters( MavenSession session )
        throws MavenExecutionException
    {
        Properties sessionProps = session.getUserProperties();
        scmConnection = sessionProps.getProperty( "make.scmConnection" );
        if ( scmConnection == null )
        {
            try
            {
                scmConnection = session.getTopLevelProject().getScm().getConnection();
            }
            catch ( NullPointerException e )
            {
                String message =
                    "No SCM connection specified.  You must specify an SCM "
                        + "connection by adding a <connection> element to your <scm> element in your POM";
                throw new MavenExecutionException( message, e );
            }
        }
        enabled = Boolean.parseBoolean( sessionProps.getProperty( "make.scmChanges", "false" ) );
        ignoreUnknown = Boolean.parseBoolean( sessionProps.getProperty( "make.ignoreUnknown", "true" ) );
        ignoreRootPom = Boolean.parseBoolean( sessionProps.getProperty( "make.ignoreRootPom", "false" ) );
    }

    @SuppressWarnings( "unchecked" )
    private List<ScmFile> getChangedFilesFromScm( File baseDir )
        throws MavenExecutionException
    {
        StatusScmResult result = null;
        try
        {
            ScmRepository repository = scmManager.makeScmRepository( scmConnection );
            result = scmManager.status( repository, new ScmFileSet( baseDir ) );
        }
        catch ( Exception e )
        {
            throw new MavenExecutionException( "Couldn't configure SCM repository: " + e.getLocalizedMessage(), e );
        }

        return (List<ScmFile>) result.getChangedFiles();
    }
}
