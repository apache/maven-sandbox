package org.apache.maven.taxonomy.store.xstream;

import org.apache.maven.taxonomy.TaxonNode;
import org.apache.maven.taxonomy.store.TaxonomyStore;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author Jason van Zyl
 */
public class XStreamTaxonomyStoreTest
    extends PlexusTestCase
{
    public void testXStreamTaxonomyStore()
        throws Exception
    {
        TaxonomyStore store = (TaxonomyStore) lookup( TaxonomyStore.ROLE );

        store.load();

        store.addTaxon( new TaxonNode( "Ant Tasks", "" ).addAttribute( "jira-component", "11352" ) );

        store.addTaxon( new TaxonNode( "Command Line", "" ).addAttribute( "jira-component", "11982" ) );

        store.addTaxon( new TaxonNode( "Artifacts", "" ).addAttribute( "jira-component", "11338" ) );

        store.addTaxon( new TaxonNode( "Design Patterns & Best Practices", "" ).addAttribute( "jira-component", "11341" ) );

        store.addTaxon( new TaxonNode( "Embedding", "" ).addAttribute( "jira-component", "11850" ) );

        store.addTaxon( new TaxonNode( "Inheritance", "" ).addAttribute( "jira-component", "11570" ) );

        store.addTaxon( new TaxonNode( "Interpolation", "" ).addAttribute( "jira-component", "11570" ) );

        store.addTaxon( new TaxonNode( "Plugins", "11521" ).addAttribute( "jira-component", "11352" ) );

        store.addTaxon( new TaxonNode( "Lifecycle", "" ).addAttribute( "jira-component", "11352" ) );

        store.addTaxon( new TaxonNode( "POM", "" ).addAttribute( "jira-component", "11352" ) );

        store.addTaxon( new TaxonNode( "Reactor/Workspace", "" ).addAttribute( "jira-component", "11938" ) );

        store.addTaxon( new TaxonNode( "Repositories", "" ).addAttribute( "jira-component", "12034" ) );

        store.addTaxon( new TaxonNode( "Sites/Reporting", "" ).addAttribute( "jira-component", "12030" ) );

        store.addTaxon( new TaxonNode( "Settings", "" ).addAttribute( "jira-component", "0" ) );

        store.addTaxon( new TaxonNode( "Profiles", "").addAttribute( "jira-component", "0" ) );

        store.addTaxon( new TaxonNode( "Tools", "" ).addAttribute( "jira-component", "0" ) );

        // Conversion tools

        store.addTaxon( new TaxonNode( "Language Support", "" ).addAttribute( "jira-component", "0" ) );

        store.addTaxon( new TaxonNode( "Reusable Resources", "" ).addAttribute( "jira-component", "0" ) );

        store.store();

        /*
        int id = store.addTaxon( t );

        store.store();

        store.load();

        store.addTaxon( store.getTaxon( id );

        assertEquals( "dependencies", t.getName() );

        assertEquals( "description", t.getDescription() );
        */
    }
}
