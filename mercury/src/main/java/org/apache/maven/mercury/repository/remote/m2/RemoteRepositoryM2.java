package org.apache.maven.mercury.repository.remote.m2;

import org.apache.maven.mercury.repository.api.AbstractRepository;
import org.apache.maven.mercury.repository.api.MetadataProcessor;
import org.apache.maven.mercury.repository.api.NonExistentProtocolException;
import org.apache.maven.mercury.repository.api.RemoteRepository;
import org.apache.maven.mercury.repository.api.RepositoryReader;
import org.apache.maven.mercury.repository.api.RepositoryWriter;
import org.apache.maven.mercury.transport.api.Server;


public class RemoteRepositoryM2
extends AbstractRepository
implements RemoteRepository
{
    private Server _server;
    //----------------------------------------------------------------------------------
    public RemoteRepositoryM2( String id, Server server, MetadataProcessor processor  )
    {
        super( id, DEFAULT_REPOSITORY_TYPE, processor );
        this._server = server;
    }
    //----------------------------------------------------------------------------------
    public Server getServer()
    {
        return _server;
    }
    //----------------------------------------------------------------------------------
    public RepositoryReader getReader()
    {
      return null;
    }
    //----------------------------------------------------------------------------------
    public RepositoryReader getReader( String protocol )
    {
      // TODO Auto-generated method stub
      return null;
    }
    //----------------------------------------------------------------------------------
    public RepositoryWriter getWriter()
    {
      // TODO Auto-generated method stub
      return null;
    }
    //----------------------------------------------------------------------------------
    public RepositoryWriter getWriter( String protocol )
        throws NonExistentProtocolException
    {
      // TODO Auto-generated method stub
      return null;
    }
    //----------------------------------------------------------------------------------
    public boolean isLocal()
    {
     return false;
    }
    //----------------------------------------------------------------------------------
    public boolean isReadOnly()
    {
      return false;
    }
    //----------------------------------------------------------------------------------
    public String getType()
    {
      return DEFAULT_REPOSITORY_TYPE;
    }
    //----------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------
}
