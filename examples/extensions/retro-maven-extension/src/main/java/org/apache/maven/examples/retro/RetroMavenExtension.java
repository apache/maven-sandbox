package org.apache.maven.examples.retro;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

@Component( role = AbstractMavenLifecycleParticipant.class, hint = "retro" )
public class RetroMavenExtension
    extends AbstractMavenLifecycleParticipant
{
    @Requirement
    private Logger logger;

    @Requirement
    RuntimeInformation runtime;

    public void afterProjectsRead( MavenSession session ) {
        logger.info( " __  __" );
        logger.info( "|  \\/  |__ _Apache__ ___" );
        logger.info( "| |\\/| / _` \\ V / -_) ' \\  ~ intelligent projects ~" );
        logger.info( "|_|  |_\\__,_|\\_/\\___|_||_|  v. " + runtime.getApplicationVersion() );
        logger.info( "" );
    }

}
