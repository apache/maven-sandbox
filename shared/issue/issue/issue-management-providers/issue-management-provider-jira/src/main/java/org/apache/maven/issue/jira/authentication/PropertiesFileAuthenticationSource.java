package org.apache.maven.issue.jira.authentication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * <p>
 * A simple authentication source that uses a properties file. If you format the properties
 * file using the ":" as the delimiter then Ruby's YAML package can also use the configuration.
 * So the following would work with both Java and Ruby:
 * </p>
 *
 * <pre>
 * user: jason
 * password: monkey
 * </pre>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class PropertiesFileAuthenticationSource
    implements AuthenticationSource
{
    private String login;

    private String password;

    private File propertiesFile;

    public PropertiesFileAuthenticationSource()
    {
    }

    public PropertiesFileAuthenticationSource( File properties )
    {
        this.propertiesFile = properties;
    }

    public void initialize()
        throws AuthenticationSourceInitializationException
    {
        if ( propertiesFile == null )
        {
            propertiesFile = new File( System.getProperty( "user.home" ), "jira.properties" );
        }

        Properties p = new Properties();

        try
        {
            p.load( new FileInputStream( propertiesFile ) );

            login = p.getProperty( "user" );

            if ( login == null )
            {
                throw new AuthenticationSourceInitializationException( "Source contains no login information." );
            }

            password = p.getProperty( "password" );

            if ( password == null )
            {
                throw new AuthenticationSourceInitializationException( "Source contains no password information." );
            }

        }
        catch ( IOException e )
        {
            throw new AuthenticationSourceInitializationException( "Cannot find " + propertiesFile + "for login and password information." );
        }
    }

    public String getLogin()
    {
        return login;
    }

    public String getPassword()
    {
        return password;
    }
}
