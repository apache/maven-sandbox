package org.apache.maven.enterprise.config;

import org.codehaus.plexus.PlexusTestCase;
import org.apache.maven.enterprise.model.EnterpriseConfig;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 1.0
 */
public class ConfigTest
    extends PlexusTestCase
{
    EnterpriseConfig config;

    public void setUp() throws Exception
    {
        super.setUp();
        config = (EnterpriseConfig) lookup( EnterpriseConfig.class.getName() );

        System.out.println( "Using " + ( (DefaultConfig) config).configFile );
    }

    public void testClean() throws Exception
    {
        assertTrue( config.getWebdav().isPublicRepositories() );
        assertFalse( config.getWebdav().isPublicIDisk() );
    }

    public void testFromFile() throws Exception
    {
        assertTrue( config.getWebdav().isPublicRepositories() );
        assertFalse( config.getWebdav().isPublicIDisk() );
    }
}
