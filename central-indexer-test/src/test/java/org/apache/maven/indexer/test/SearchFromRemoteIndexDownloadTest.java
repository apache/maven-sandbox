package org.apache.maven.indexer.test;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHeaders;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.maven.index.FlatSearchRequest;
import org.apache.maven.index.FlatSearchResponse;
import org.apache.maven.index.MAVEN;
import org.apache.maven.index.NexusIndexer;
import org.apache.maven.index.OSGI;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.expr.StringSearchExpression;
import org.apache.maven.index.updater.IndexUpdateRequest;
import org.apache.maven.index.updater.IndexUpdater;
import org.apache.maven.index.updater.ResourceFetcher;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.providers.http.HttpWagon;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * setuUp: download remote index from central and do some search on it
 */
public class SearchFromRemoteIndexDownloadTest
    extends PlexusTestCase
{

    IndexingContext context;

    public void setUp( )
        throws Exception
    {
        super.setUp( );
        NexusIndexer nexusIndexer = lookup( NexusIndexer.class );
        final File tempIndexDirectory = new File( getBasedir( ), "target/.tmpIndex" );
        File repo = new File( getBasedir( ), "target/src/test/foo" );
        repo.mkdirs( );
        assertTrue( repo.exists( ) );
        File indexDirectory =
            new File( getBasedir( ), "target/index/test-" + Long.toString( System.currentTimeMillis( ) ) );
        indexDirectory.deleteOnExit( );

        List<IndexCreator> indexCreators =
            new ArrayList<IndexCreator>( getContainer( ).lookupList( IndexCreator.class ) );

        System.out.println( "indexCreators: " + indexCreators );

        FileUtils.deleteDirectory( indexDirectory );
        context =
            nexusIndexer.addIndexingContext( "id", "id", repo, indexDirectory, repo.toURI( ).toURL( ).toExternalForm( ),
                                             indexDirectory.toURI( ).toURL( ).toString( ), indexCreators );

        final HttpWagon httpWagon = (HttpWagon) lookup( Wagon.class, "http" );

        httpWagon.setTimeout( 10000 );

        httpWagon.connect( new Repository( "central", "http://repo.maven.apache.org/maven2/.index" ) );

        ResourceFetcher resourceFetcher = new ResourceFetcher( )
        {
            public void connect( String id, String url )
                throws IOException
            {
                //no op
            }

            public void disconnect( )
                throws IOException
            {
                // no op
            }

            public InputStream retrieve( String name )
                throws IOException, FileNotFoundException
            {
                try
                {
                    System.out.println( "index update retrieve file, name: " + name );
                    File file = new File( tempIndexDirectory, name );
                    if ( file.exists( ) )
                    {
                        file.delete( );
                    }
                    file.deleteOnExit( );
                    httpWagon.get( name, file );
                    return new FileInputStream( file );
                }
                catch ( AuthorizationException e )
                {
                    throw new IOException( e.getMessage( ) );
                }
                catch ( TransferFailedException e )
                {
                    throw new IOException( e.getMessage( ) );
                }
                catch ( ResourceDoesNotExistException e )
                {
                    throw new FileNotFoundException( e.getMessage( ) );
                }
            }
        };

        IndexUpdateRequest request = new IndexUpdateRequest( context, resourceFetcher );
        File indexCacheDir = new File( getBasedir( ), "target/indexCacheDir" );
        indexCacheDir.mkdirs( );
        request.setLocalIndexCacheDir( indexCacheDir );
        request.setForceFullUpdate( false );

        IndexUpdater indexUpdater = lookup( IndexUpdater.class );

        indexUpdater.fetchAndUpdateIndex( request );
    }

    public void testSearchArtifactId( )
        throws Exception
    {

        NexusIndexer indexer = lookup( NexusIndexer.class );
        BooleanQuery q = new BooleanQuery( );
        q.add( indexer.constructQuery( MAVEN.ARTIFACT_ID, new StringSearchExpression( "commons-lang" ) ),
               BooleanClause.Occur.MUST );

        FlatSearchRequest searchRequest = new FlatSearchRequest( q );
        searchRequest.setContexts( Arrays.asList( context ) );
        FlatSearchResponse response = indexer.searchFlat( searchRequest );
        System.out.println(
            "artifactId commons-lang response getReturnedHitsCount : " + response.getReturnedHitsCount( ) );
        assertTrue( response.getReturnedHitsCount( ) > 0 );


    }

    // org/apache/karaf/features/org.apache.karaf.features.command/2.2.2/org.apache.karaf.features.command-2.2.2.jar
    public void testSearchWithSymbolicName( )
        throws Exception
    {

        NexusIndexer indexer = lookup( NexusIndexer.class );
        BooleanQuery q = new BooleanQuery( );
        q.add( indexer.constructQuery( OSGI.SYMBOLIC_NAME,
                                       new StringSearchExpression( "org.apache.karaf.features.command" ) ),
               BooleanClause.Occur.MUST );

        FlatSearchRequest searchRequest = new FlatSearchRequest( q );
        searchRequest.setContexts( Arrays.asList( context ) );
        FlatSearchResponse response = indexer.searchFlat( searchRequest );
        System.out.println( "symbolic name org.apache.karaf.features.command response getReturnedHitsCount : "
                                + response.getReturnedHitsCount( ) );
        assertTrue( response.getReturnedHitsCount( ) > 0 );
    }

    public void testSearchWithExportService( )
        throws Exception
    {

        NexusIndexer indexer = lookup( NexusIndexer.class );
        BooleanQuery q = new BooleanQuery( );
        q.add( indexer.constructQuery( OSGI.EXPORT_SERVICE, new StringSearchExpression(
            "org.apache.felix.bundlerepository.RepositoryAdmin" ) ), BooleanClause.Occur.MUST );

        FlatSearchRequest searchRequest = new FlatSearchRequest( q );
        searchRequest.setContexts( Arrays.asList( context ) );
        FlatSearchResponse response = indexer.searchFlat( searchRequest );
        System.out.println(
            "export service org.apache.felix.bundlerepository.RepositoryAdmin response getReturnedHitsCount : "
                + response.getReturnedHitsCount( ) );
        assertTrue( response.getReturnedHitsCount( ) > 0 );
    }
}
