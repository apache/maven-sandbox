package org.apache.maven.issue.jira.project;

import junit.framework.TestCase;

import java.io.File;
import java.util.Iterator;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class CsvFileProjectRecordSourceTest
    extends TestCase
{
    public void testSource()
        throws Exception
    {
        File file = new File( System.getProperty( "basedir" ), "projects.txt" );

        ProjectRecordSource source = new CsvFileProjectRecordSource( file );

        Iterator i = source.getRecords();

        ProjectRecord record = null;

        if ( i.hasNext() )
        {
            record = (ProjectRecord) i.next();
        }
        else
        {
            fail( "Unable to capture line from file based project record source." );
        }

        assertEquals( "MNGPCLOVER", record.getKey() );

        assertEquals( "jason", record.getLeadId() );

        assertEquals( "Clover Maven Plugin", record.getName() );

        assertEquals( "Description", record.getDescription() );

        assertEquals( "10010", record.getPermissionSchemeId() );

        assertEquals( "10001", record.getNotificationSchemeId() );
    }
}
