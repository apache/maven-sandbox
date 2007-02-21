package org.apache.maven.issue.jira.project;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ProjectRecordRetrievalException
    extends Exception
{
    public ProjectRecordRetrievalException( String message )
    {
        super( message );
    }

    public ProjectRecordRetrievalException( Throwable cause )
    {
        super( cause );
    }

    public ProjectRecordRetrievalException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
