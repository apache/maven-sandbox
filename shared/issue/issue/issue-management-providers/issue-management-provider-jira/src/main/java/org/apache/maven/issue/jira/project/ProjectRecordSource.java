package org.apache.maven.issue.jira.project;

import java.util.Iterator;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface ProjectRecordSource
{
    Iterator getRecords()
        throws ProjectRecordRetrievalException;
}
