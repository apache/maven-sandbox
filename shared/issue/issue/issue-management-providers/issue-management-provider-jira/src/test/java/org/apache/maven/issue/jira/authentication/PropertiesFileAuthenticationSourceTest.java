package org.apache.maven.issue.jira.authentication;

import junit.framework.TestCase;

import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class PropertiesFileAuthenticationSourceTest
    extends TestCase
{
    public void testSourceWithSpecifiedPropertiesFile()
        throws Exception
    {
        File f = new File( System.getProperty( "basedir" ), "src/test/resources/jira.properties" );

        AuthenticationSource source = new PropertiesFileAuthenticationSource( f );

        source.initialize();

        assertEquals( "jason", source.getLogin() );

        assertEquals( "monkey", source.getPassword() );
    }
}
