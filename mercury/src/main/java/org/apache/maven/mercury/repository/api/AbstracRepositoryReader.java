package org.apache.maven.mercury.repository.api;


/**
 * This is to keep MetadataProcessor for all readers
 *
 * @author Oleg Gusakov
 * @version $Id$
 *
 */
public abstract class AbstracRepositoryReader
implements RepositoryReader
{
  protected MetadataProcessor _mdProcessor;
  
  public void setMetadataProcessor( MetadataProcessor mdProcessor )
  {
    _mdProcessor = mdProcessor;
  }
  
  public MetadataProcessor getMetadataProcessor()
  {
    return _mdProcessor;
  }
  
}
