package org.apache.maven.archiva.consumer.plugin;

import java.util.List;

import org.apache.maven.archiva.indexer.search.CrossRepositorySearch;
import org.apache.maven.archiva.indexer.search.SearchResultLimits;
import org.apache.maven.archiva.indexer.search.SearchResults;
import org.codehaus.plexus.PlexusTestCase;

public class MockRepositorySearch
    implements CrossRepositorySearch
{
    /**
     * @plexus.configuration default-value="mock-repo-search"
     */
    private String id;

    private SearchResults searchResults = new SearchResults();

    private String principal;

    private List selectedRepos;

    private String term;

    /**
     * Allow a set of mocked {@link SearchResults} to be injected. These get stub returned from each search call
     * 
     * @param searchResults
     */
    public void setSearchResults( SearchResults searchResults )
    {
        this.searchResults = searchResults;
    }

    public SearchResults searchForBytecode( String principal, List selectedRepos, String term,
                                            SearchResultLimits limits )
    {
        storeParams( principal, selectedRepos, term );
        return searchResults;
    }

    public SearchResults searchForChecksum( String principal, List selectedRepos, String checksum,
                                            SearchResultLimits limits )
    {
        storeParams( principal, selectedRepos, term );
        return searchResults;
    }

    public SearchResults searchForTerm( String principal, List selectedRepos, String term,
                                        SearchResultLimits limits )
    {
        storeParams( principal, selectedRepos, term );
        return searchResults;
    }
    

    private void storeParams( String principal, List selectedRepos, String term )
    {
        this.principal = principal;
        this.selectedRepos = selectedRepos;
        this.term = term;
    }

    public void verify( String principal, List selectedRepos, String term )
    {
        if(principal != null)
        {
            PlexusTestCase.assertEquals( principal, this.principal );
        }
        
        if(selectedRepos != null)
        {
            PlexusTestCase.assertEquals( selectedRepos, this.selectedRepos );
        }
        
        if(term != null)
        {
            PlexusTestCase.assertEquals( term, this.term );
        }
    }

    public void setId( String id )
    {
        this.id = id;
    }

}
