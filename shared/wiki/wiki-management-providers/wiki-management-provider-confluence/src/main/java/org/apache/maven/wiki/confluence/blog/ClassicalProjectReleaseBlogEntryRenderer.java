package org.apache.maven.wiki.confluence.blog;

import org.apache.maven.changes.Action;
import org.apache.maven.changes.Release;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wiki.confluence.ConfluenceMarkupUtil;

import java.util.Iterator;
import java.util.List;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Displays a table of issues being resolved in the given release.
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public class ClassicalProjectReleaseBlogEntryRenderer
    extends AbstractProjectReleaseBlogEntryRenderer
{

    public String generateProjectReleaseContent( final MavenProject project, final Release release )
    {
        StringBuffer sb = new StringBuffer();
        ConfluenceMarkupUtil.appendProjectReleaseDefaultIntroduction( sb, project, release );

        sb.append( ConfluenceMarkupUtil.NEW_LINE );
        sb.append( ConfluenceMarkupUtil.NEW_LINE );

        appendIssueActions( sb, release.getAction() );

        sb.append( ConfluenceMarkupUtil.NEW_LINE );

        ConfluenceMarkupUtil.appendProjectReleaseDefaultConclusion( sb, project );
        return sb.toString();
    }

    private void appendIssueActions( StringBuffer sb, List issueActions )
    {
        ConfluenceMarkupUtil.initializeTableHeader( sb, new String[]{"issue", "changes", "author"} );
        sb.append( ConfluenceMarkupUtil.NEW_LINE );
        final Iterator it = issueActions.iterator();
        while ( it.hasNext() )
        {
            Action issueAction = (Action) it.next();
            sb.append( "|" );
            ConfluenceMarkupUtil.appendIssueId( sb, issueAction.getIssue(), "jira" );
            sb.append( "|" );
            ConfluenceMarkupUtil.appendDescription( sb, issueAction );
            sb.append( "|" );
            ConfluenceMarkupUtil.appendAuthor( sb, issueAction.getDev(), true );
            sb.append( "|" );
            sb.append( ConfluenceMarkupUtil.NEW_LINE );
        }
    }
}
