package org.apache.maven.wagon.mercury;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.mercury.crypto.api.StreamObserverFactory;
import org.apache.maven.mercury.crypto.api.StreamVerifierAttributes;
import org.apache.maven.mercury.crypto.api.StreamVerifierException;
import org.apache.maven.mercury.crypto.api.StreamVerifierFactory;
import org.apache.maven.mercury.crypto.pgp.PgpStreamVerifierFactory;
import org.apache.maven.mercury.spi.http.client.HttpClientException;
import org.apache.maven.mercury.spi.http.client.deploy.DefaultDeployRequest;
import org.apache.maven.mercury.spi.http.client.deploy.DefaultDeployer;
import org.apache.maven.mercury.spi.http.client.deploy.DeployResponse;
import org.apache.maven.mercury.spi.http.client.retrieve.DefaultRetrievalRequest;
import org.apache.maven.mercury.spi.http.client.retrieve.DefaultRetriever;
import org.apache.maven.mercury.spi.http.client.retrieve.RetrievalResponse;
import org.apache.maven.mercury.transport.api.Binding;
import org.apache.maven.mercury.transport.api.Credentials;
import org.apache.maven.mercury.transport.api.Server;
import org.apache.maven.wagon.AbstractWagon;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferEventSupport;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.observers.ChecksumObserver;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.repository.Repository;
import org.apache.maven.wagon.resource.Resource;
import org.codehaus.plexus.i18n.DefaultLanguage;
import org.codehaus.plexus.i18n.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *    plexus.component
 *    role-hint="http"
 *    instantiation-strategy="per-lookup"
 *    description="Mercury based wagon implementation"
 *
 * @author Oleg Gusakov
 * @version $Id$
 *
 */
public class MercuryWagon
extends AbstractWagon
implements Wagon
{
  public static final String SYSTEM_PARAMETER_DEBUG_TRANSFER = "maven.mercury.wagon.debug.transfer";
  private boolean debugTransfer = Boolean.parseBoolean( System.getProperty( SYSTEM_PARAMETER_DEBUG_TRANSFER, "false" ) );

  public static final String SYSTEM_PARAMETER_PGP_CONGIG = "maven.mercury.wagon.pgp.config";
  private String pgpConfig = System.getProperty( SYSTEM_PARAMETER_PGP_CONGIG, null );

  private static final Logger _log = LoggerFactory.getLogger(MercuryWagon.class);
  private static final Language _lang = new DefaultLanguage( MercuryWagon.class );
  
  private Server server;
  private DefaultRetriever retriever;
  private DefaultDeployer deployer;
  
  private List<TransferEvent> events = new ArrayList<TransferEvent>(8);
  
  /**
   * 
   */
  public MercuryWagon()
  {
    _log.debug( "\n===============> MercuryWagon instantiated, repository "+repository);
  }
  
  public MercuryWagon( Server server )
  throws IllegalArgumentException
  {
    init( server );
  }

  private void init( Server server )
  throws IllegalArgumentException
  {
    if( server == null )
      throw new IllegalArgumentException( _lang.getMessage( "null.read.server" ) );
    
    if( server.getURL() == null )
      throw new IllegalArgumentException( _lang.getMessage( "null.read.server.url", server.getId() ) );
    
    this.server = server;
    
    Set<StreamVerifierFactory>[] pgpFac = null;
    
    try
    {
      pgpFac = readPgpConfig();
      this.server.setReaderStreamVerifierFactories( pgpFac[0] );
      this.server.setWriterStreamVerifierFactories( pgpFac[1] );
//_log.info( "ReaderFacs="+this.server.getReaderStreamVerifierFactories()+"\nWriterFacs="+this.server.getWriterStreamVerifierFactories() );
    }
    catch( Exception ex )
    {
      throw new IllegalArgumentException(ex);
    }

    Set<StreamObserverFactory> rf = server.getReaderStreamObserverFactories();
    if( rf == null )
    {
      rf = new HashSet<StreamObserverFactory>(1);
      this.server.setReaderStreamObserverFactories( rf );
    }
    rf.add( new StupidWagonObserverFactory( this ) );
    
    HashSet<Server> servers = new HashSet<Server>(1);
    servers.add( this.server );
    
    Set<StreamObserverFactory> wf = this.server.getWriterStreamObserverFactories();
    if( wf == null )
    {
      wf = new HashSet<StreamObserverFactory>(1);
      this.server.setReaderStreamObserverFactories( wf );
    }
    wf.add( new StupidWagonObserverFactory( this ) );

    try
    {
      retriever = new DefaultRetriever();
    }
    catch( HttpClientException e )
    {
      throw new IllegalArgumentException(e);
    }
    retriever.setServers( servers );
    
    try
    {
      deployer = new DefaultDeployer();
    }
    catch( HttpClientException e )
    {
      throw new IllegalArgumentException(e);
    }
    deployer.setServers( servers );
  }

  public void get( String resourceName, File destination )
  throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
  {
    if( _log.isDebugEnabled())
      _log.debug("\n------------\nMercuryWagon getting "+resourceName+" into "+destination);

    Binding binding = null;
    
    try
    {
      binding = new Binding( new URL(server.getURL().toString()+'/'+resourceName), destination );
    }
    catch( MalformedURLException e )
    {
      throw new TransferFailedException( e.getMessage() );
    }
    
    DefaultRetrievalRequest request = new DefaultRetrievalRequest();
    request.addBinding( binding );
    
    Resource resource = new Resource( resourceName );

    fireGetInitiated( resource, destination );

    resource.setLastModified( 0l );

    fireGetStarted( resource, destination );
    
    pushEvent( new TransferEvent(this, resource, TransferEvent.TRANSFER_PROGRESS, TransferEvent.REQUEST_GET) );

    RetrievalResponse response = retriever.retrieve( request );
    
    fireGetCompleted( resource, destination );

    if( response.hasExceptions() )
    {
      _log.error( response.getExceptions().toString() );
      throw new ResourceDoesNotExistException( response.getExceptions().toString() );
    }

    if( _log.isDebugEnabled())
      _log.debug("\n------------\nMercuryWagon got "+resourceName+" into "+destination);
    
  }

  public boolean getIfNewer( String resourceName, File destination, long timestamp )
  throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
  {
    get( resourceName, destination );
    return true;
  }

  public void put( File source, String destination )
  throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
  {
    
    Resource resource = new Resource( destination );
    
    firePutInitiated( resource, source );
    
    resource.setContentLength( source.length() );
    
    resource.setLastModified( source.lastModified() );

    Binding binding = null;
    try
    {
      binding = new Binding( new URL(repository.getUrl()+'/'+destination), source );
    }
    catch( MalformedURLException e )
    {
      throw new TransferFailedException( e.getMessage() );
    }
    
    HashSet<Binding> bindings = new HashSet<Binding>(1);
    bindings.add( binding );
    
    DefaultDeployRequest request = new DefaultDeployRequest();
    request.setBindings( bindings );
    
    firePutStarted( resource, source );
    
    pushEvent( new TransferEvent(this, resource, TransferEvent.TRANSFER_PROGRESS, TransferEvent.REQUEST_PUT) );

    DeployResponse response = deployer.deploy( request );

    firePutCompleted( resource, source );

    if( response.hasExceptions() )
      throw new TransferFailedException( response.getExceptions().toString() );
  }

  protected void closeConnection()
  throws ConnectionException
  {
    fireSessionLoggedOff();
    fireSessionDisconnecting();
    fireSessionDisconnected();
  }
  
  @Override
  public void openConnection()
  throws ConnectionException, AuthenticationException
  {
    openConnectionInternal();
  }
  
  protected void openConnectionInternal()
  throws ConnectionException, AuthenticationException
  {
    if(_log.isDebugEnabled())
      _log.debug( "Opening connection to repository "+repository );

    try
    {
      String url = 'h'+repository.getUrl().substring( 1 );
      boolean requireEncryption = url.startsWith( "https" ) ? true : false;
      Credentials user = null;

      if( repository.getUsername() != null )
        user = new Credentials( repository.getUsername(), repository.getPassword() );
      
      Server server = new Server( repository.getId(), new URL( url ), requireEncryption, false, user );
      
      init( server );
      
      if( debugTransfer )
        transferEventSupport.addTransferListener( new TransferEventDebugger() );
    }
    catch( MalformedURLException e )
    {
      throw new ConnectionException( e.getMessage() );
    }
  }
  
  @Override
  public void addTransferListener( TransferListener listener )
  {
    super.addTransferListener( listener );
  }

  void bytesReady( TransferEvent transferEvent, byte [] buf, int len )
  {
    fireTransferProgress( transferEvent, buf, len );
  }

  /**
   * @return
   */
  public TransferEvent popEvent()
  {
    if( events.isEmpty() )
      return null;
    
    TransferEvent event = events.get( 0 );
    events.remove( 0 );
    
    return event;
  }

  public void pushEvent( TransferEvent event)
  {
    events.add( 0, event );
  }
  
  private final Set<StreamVerifierFactory>[] readPgpConfig()
  throws FileNotFoundException, IOException, StreamVerifierException
  {
    Set<StreamVerifierFactory> [] res = new Set [] { null, null };
    
    if( pgpConfig == null )
      return res;
    
    _log.info( "PGP signature configuration will be read from "+pgpConfig );
    
    Properties pgpProps = new Properties();
    pgpProps.load( new FileInputStream(pgpConfig) );

    StreamVerifierAttributes readerAttr = new StreamVerifierAttributes( PgpStreamVerifierFactory.DEFAULT_EXTENSION
        , Boolean.parseBoolean( pgpProps.getProperty( "reader.lenient", "true" ) )
        , false
                                                      );
    String readerKeyring = pgpProps.getProperty( "reader.keyring" );
    
    if( readerKeyring != null )
    {
      StreamVerifierFactory rf = new PgpStreamVerifierFactory( readerAttr, new FileInputStream(readerKeyring) );
if( _log.isDebugEnabled() )
  _log.debug( "public key file: "+new File(readerKeyring).getAbsolutePath() );
      
      Set<StreamVerifierFactory> rs = new HashSet<StreamVerifierFactory>(1);
      rs.add( rf );
      res[0] = rs;
    }

    StreamVerifierAttributes writerAttr = new StreamVerifierAttributes( PgpStreamVerifierFactory.DEFAULT_EXTENSION
        , Boolean.parseBoolean( pgpProps.getProperty( "writer.lenient", "true" ) )
        , false
                                                      );
    String writerKeyring = pgpProps.getProperty( "writer.keyring" );
    String writerKeyId = pgpProps.getProperty( "writer.key.id" );
    String writerKeyringPass = pgpProps.getProperty( "writer.key.pass" );

    if( writerKeyring != null && writerKeyId != null && writerKeyringPass != null )
    {
if( _log.isDebugEnabled() )
  _log.debug( "secret key file: "+new File(writerKeyring).getAbsolutePath() );
      StreamVerifierFactory wf = new PgpStreamVerifierFactory( writerAttr, new FileInputStream(writerKeyring)
                                                              , writerKeyId, writerKeyringPass );
      
      Set<StreamVerifierFactory> ws = new HashSet<StreamVerifierFactory>(1);
      ws.add( wf );
      res[1] = ws;
    }
    
    return res;
  }

}
