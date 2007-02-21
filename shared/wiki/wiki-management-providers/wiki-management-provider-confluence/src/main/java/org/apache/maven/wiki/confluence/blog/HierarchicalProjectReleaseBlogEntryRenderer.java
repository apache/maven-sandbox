package org.apache.maven.wiki.confluence.blog;

import org.apache.maven.changes.Action;
import org.apache.maven.changes.Release;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wiki.confluence.ConfluenceMarkupUtil;

import java.util.ArrayList;
import java.util.Collections;
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
 * Displays one table per issue type with the issues being resolved
 * in the given release.
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public class HierarchicalProjectReleaseBlogEntryRenderer
    extends AbstractProjectReleaseBlogEntryRenderer
{

    public String generateProjectReleaseContent( final MavenProject project, final Release release )
    {
        StringBuffer sb = new StringBuffer();
        ConfluenceMarkupUtil.appendProjectReleaseDefaultIntroduction( sb, project, release );

        sb.append( ConfluenceMarkupUtil.NEW_LINE );

        // clone and sort the actions
        List sortedActions = new ArrayList( release.getAction() );
        Collections.sort( sortedActions, new ActionPerTypeComparator() );

        int currentIndex = 0;
        // Append fixes
        sb.append( ConfluenceMarkupUtil.NEW_LINE );
        currentIndex = appendIssueActions( sb, sortedActions, ActionType.FIX, "Bug fixes:", currentIndex );
        sb.append( ConfluenceMarkupUtil.NEW_LINE );

        // Append changes
        sb.append( ConfluenceMarkupUtil.NEW_LINE );
        currentIndex = appendIssueActions( sb, sortedActions, ActionType.ADD, "New Features:", currentIndex );
        sb.append( ConfluenceMarkupUtil.NEW_LINE );

        // Append improvements
        sb.append( ConfluenceMarkupUtil.NEW_LINE );
        currentIndex = appendIssueActions( sb, sortedActions, ActionType.UPDATE, "Changes:", currentIndex );
        sb.append( ConfluenceMarkupUtil.NEW_LINE );

        // Append removal
        sb.append( ConfluenceMarkupUtil.NEW_LINE );
        currentIndex = appendIssueActions( sb, sortedActions, ActionType.REMOVE, "Removed features:", currentIndex );
        sb.append( ConfluenceMarkupUtil.NEW_LINE );

        ConfluenceMarkupUtil.appendProjectReleaseDefaultConclusion( sb, project );
        return sb.toString();
    }

    private int appendIssueActions( StringBuffer sb, List issueActions, ActionType expectedType, String header,
                                    int currentIndex )
    {
        if ( currentIndex >= issueActions.size() )
        {
            return issueActions.size();
        }

        boolean encouteredElement = false;
        for ( int i = currentIndex; i < issueActions.size(); i++ )
        {
            Action issueAction = (Action) issueActions.get( i );
            if ( expectedType.equals( ActionType.getIssueActionType( issueAction.getType() ) ) )
            {
                if ( !encouteredElement )
                {
                    encouteredElement = true;
                    sb.append( "h2. " ).append( header );
                    sb.append( ConfluenceMarkupUtil.NEW_LINE );
                    ConfluenceMarkupUtil.initializeTableHeader( sb, new String[]{"issue", "changes", "author"} );
                    sb.append( ConfluenceMarkupUtil.NEW_LINE );
                }
                sb.append( "|" );
                ConfluenceMarkupUtil.appendIssueId( sb, issueAction.getIssue(), "jira" );
                sb.append( "|" );
                ConfluenceMarkupUtil.appendDescription( sb, issueAction );
                sb.append( "|" );
                ConfluenceMarkupUtil.appendAuthor( sb, issueAction.getDev(), true );
                sb.append( "|" );
                sb.append( ConfluenceMarkupUtil.NEW_LINE );
            }
            else
            {
                return i;
            }
        }
        return issueActions.size();
    }
}
