package org.apache.maven.issue.jira.authentication;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface AuthenticationSource
{
    String getLogin();

    String getPassword();

    void initialize()
        throws AuthenticationSourceInitializationException;
}
