package org.apache.maven.wagon.mercury;

import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Oleg Gusakov
 * @version $Id$
 *
 */
public class TransferEventDebugger
implements TransferListener
{
  public static final String SYSTEM_PARAMETER_DEBUG_TRANSFER_BYTES = "maven.mercury.wagon.debug.transfer.bytes";
  private boolean debugTransferBytes = Boolean.parseBoolean( System.getProperty( SYSTEM_PARAMETER_DEBUG_TRANSFER_BYTES, "false" ) );

  private static final Logger _log = LoggerFactory.getLogger(TransferEventDebugger.class);

  /* (non-Javadoc)
   * @see org.apache.maven.wagon.events.TransferListener#debug(java.lang.String)
   */
  public void debug(
      String message )
  {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see org.apache.maven.wagon.events.TransferListener#transferCompleted(org.apache.maven.wagon.events.TransferEvent)
   */
  public void transferCompleted(
      TransferEvent transferEvent )
  {
    _log.info("|=============>   completed: "+transferEvent.getResource().getName() );
  }

  /* (non-Javadoc)
   * @see org.apache.maven.wagon.events.TransferListener#transferError(org.apache.maven.wagon.events.TransferEvent)
   */
  public void transferError(
      TransferEvent transferEvent )
  {
    _log.info("|=============>   error: "+transferEvent.getResource().getName() );
  }

  /* (non-Javadoc)
   * @see org.apache.maven.wagon.events.TransferListener#transferInitiated(org.apache.maven.wagon.events.TransferEvent)
   */
  public void transferInitiated(
      TransferEvent transferEvent )
  {
    _log.info("|=============>   initialized: "+transferEvent.getResource().getName() );
  }

  /* (non-Javadoc)
   * @see org.apache.maven.wagon.events.TransferListener#transferProgress(org.apache.maven.wagon.events.TransferEvent, byte[], int)
   */
  public void transferProgress(
      TransferEvent transferEvent,
      byte[] buffer,
      int length )
  {
    if( debugTransferBytes )
      _log.info("|=============>   ready "+length+" bytes : "+transferEvent.getResource().getName() );
  }

  /* (non-Javadoc)
   * @see org.apache.maven.wagon.events.TransferListener#transferStarted(org.apache.maven.wagon.events.TransferEvent)
   */
  public void transferStarted(
      TransferEvent transferEvent )
  {
    _log.info("|=============>   started: "+transferEvent.getResource().getName() );
  }

}
