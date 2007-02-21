package org.apache.maven.issue.jira;

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

import org.apache.maven.issue.jira.authentication.PropertiesFileAuthenticationSource;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class JiraSoapClientTest
    extends TestCase
{
    private JiraSoapClient client;

    private String basedir;

    protected void setUp()
        throws Exception
    {
        basedir = System.getProperty( "basedir" );

        client = new JiraSoapClient();

        client.setEndpoint( new URL( "http://jira.codehaus.org/rpc/soap/jirasoapservice-v2" ) );

        client.setAuthenticationSource( new PropertiesFileAuthenticationSource() );

        client.initialize();
    }

    public void xtestIssueCreationWithAttachment()
        throws Exception
    {
        File pom = new File( basedir, "pom.xml" );

        RemoteIssue issue = client.createIssue( "MNGTEST", "test summary", "test description", "1", "1", "11797", "11704", "jason", pom );

        client.addComment( issue.getKey(), "patch: http://www.codehaus.org/foo.diff" );
    }

    public void testProjectCreation()
        throws Exception
    {
        String key = client.createProject( "AAA", "AAA", "This project is rockin!", "jason", "10010", "10001" );
    }
}
