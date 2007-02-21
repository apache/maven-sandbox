package org.apache.maven.taxonomy.store.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.maven.taxonomy.model.Taxon;
import org.apache.maven.taxonomy.store.TaxonomyStore;
import org.apache.maven.taxonomy.store.TaxonomyStoreException;
import org.apache.maven.taxonomy.TaxonNode;
import org.apache.maven.taxonomy.Taxonomy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Jason van Zyl
 */
public class XStreamTaxonomyStore
    implements TaxonomyStore
{
    private XStream xstream;

    private Taxonomy store;

    private File storeFile;

    public XStreamTaxonomyStore()
    {
        xstream = new XStream( new DomDriver() );

        xstream.alias( "taxonomy", Taxonomy.class );

        xstream.alias( "taxon", TaxonNode.class );
    }

    public int addTaxon( TaxonNode taxon )
    {
        return store.addTaxon( taxon );
    }

    public TaxonNode getTaxon( int id )
        throws TaxonomyStoreException
    {
        return store.getTaxon( id );
    }

    public void store()
        throws TaxonomyStoreException
    {
        try
        {
            Writer writer = new FileWriter( storeFile );

            xstream.toXML( store, writer );

            writer.close();

        }
        catch ( IOException e )
        {
            throw new TaxonomyStoreException( "Error storing xml store.", e );
        }
    }

    public void load()
        throws TaxonomyStoreException
    {
        if ( storeFile.exists() )
        {
            try
            {
                Reader reader = new FileReader( storeFile );

                store = new Taxonomy();

                xstream.fromXML( reader, store );

                reader.close();
            }
            catch ( FileNotFoundException e )
            {
                throw new TaxonomyStoreException( "Specified store file doesn't exist.", e );
            }
            catch ( IOException e )
            {
                throw new TaxonomyStoreException( "Error loading xml store.", e );
            }
        }
        else
        {
            store = new Taxonomy();
        }
    }
}
