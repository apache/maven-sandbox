package org.apache.maven.plugin.it;

import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;


public class EmbedderTransferListener
    implements TransferListener
{
    
    private final MavenEmbedderLogger logger;

    public EmbedderTransferListener( MavenEmbedderLogger logger )
    {
        this.logger = logger;
    }

    public void transferInitiated( TransferEvent event )
    {
    }

    public void transferStarted( TransferEvent event )
    {
        logger.info( "Resource: " + event.getResource().getName() + " transfer started." );
    }

    public void transferProgress( TransferEvent event, byte[] buffer, int length )
    {
    }

    public void transferCompleted( TransferEvent event )
    {
        logger.info( "Resource: " + event.getResource().getName() + " transfer complete. Local file: " + event.getLocalFile() );
    }

    public void transferError( TransferEvent event )
    {
        logger.error( "Resource: " + event.getResource().getName() + " transfer failed. Reason: " + event.getException().getMessage() );
    }

    public void debug( String event )
    {
        logger.debug( event );
    }

}
