package org.apache.maven.reducto.analysis.archiva;

import org.codehaus.plexus.PlexusTestCase;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;

import java.util.List;

/**
 * @author Jason van Zyl
 */
public class LocalArchivaClientTest
    extends PlexusTestCase
{
    public void testChecksumSearch()
        throws Exception
    {
        ArchivaClient client = (ArchivaClient) lookup( ArchivaClient.ROLE, "local" );

        assertNotNull( client );

        //     groupId: commons-beanutils
        //  artifactId: commons-beanutils
        //     version: 1.7.0
        List records = client.searchFileHash( "0f18acf5fa857f9959675e14d901a7ce" );

        assertTrue( records.size() > 0 );

        StandardArtifactIndexRecord record = (StandardArtifactIndexRecord) records.get( 0 );

        assertEquals( "commons-beanutils", record.getGroupId() );

        assertEquals( "commons-beanutils", record.getArtifactId() );

        assertEquals( "1.7.0", record.getVersion() );
    }
}
