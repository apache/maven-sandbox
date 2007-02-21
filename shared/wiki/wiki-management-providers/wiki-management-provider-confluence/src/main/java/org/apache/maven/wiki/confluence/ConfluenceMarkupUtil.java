package org.apache.maven.wiki.confluence;

import org.apache.maven.changes.Action;
import org.apache.maven.changes.Release;
import org.apache.maven.project.MavenProject;

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
 * Markup utilities.
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public class ConfluenceMarkupUtil
{
    public static final String NEW_LINE = System.getProperty( "line.separator" );

    /**
     * Appends the issue id. If a <tt>shortcutKey</tt> is specified, a link to
     * the issue is created.
     * <p/>
     * If the issue id is <tt>null</tt>, appends <tt>{{none}}</tt>.
     *
     * @param sb          the stringbuffer to use
     * @param issueId     the issue id
     * @param shortcutKey the shortcut key or null if no shortcut should be created
     */
    public static void appendIssueId( StringBuffer sb, String issueId, String shortcutKey )
    {
        if ( issueId == null )
        {
            sb.append( "{{none}}" );
        }
        else if ( shortcutKey != null )
        {
            sb.append( "[" ).append( issueId ).append( "|" ).append( issueId ).append( "@" ).append(
                shortcutKey ).append( "]" );
        }
        else
        {
            sb.append( issueId );
        }
    }

    /**
     * Appends the author. If <tt>addLinkToProfile</tt> is true, a link to the
     * confluence profile of the specified author is created.
     *
     * @param sb               the stringbuffer to use
     * @param author           the author id
     * @param addLinkToProfile whether or not a link to the user profile should be created
     */
    public static void appendAuthor( StringBuffer sb, String author, boolean addLinkToProfile )
    {
        if ( addLinkToProfile )
        {
            sb.append( "[~" ).append( author ).append( "]" );
        }
        else
        {
            sb.append( author );
        }
    }

    /**
     * Appends the description.
     *
     * @param sb          the stringbuffer to use
     * @param issueAction the issue action
     */
    public static void appendDescription( StringBuffer sb, Action issueAction )
    {
        sb.append( issueAction.getAction() );
    }

    /**
     * Appends the default introduction for a project release.
     *
     * @param sb      the stringbuffer to use
     * @param project the project being released
     * @param release the release content
     */
    public static void appendProjectReleaseDefaultIntroduction( StringBuffer sb, MavenProject project, Release release )
    {
        sb.append( "The " ).append( project.getGroupId() ).append( " team is pleased to announce the " );

        // Generate a link towards the project home page
        sb.append( "[" ).append( project.getName() ).append( "|" ).append( project.getUrl() ).append( "]" );
        sb.append( release.getVersion() ).append( " release!" );
    }

    /**
     * Appends the default conclusion for a project release.
     *
     * @param sb      the stringbuffer to use
     * @param project the project being released
     */
    public static void appendProjectReleaseDefaultConclusion( StringBuffer sb, MavenProject project )
    {
        sb.append( "Have fun!" );
        sb.append( NEW_LINE );
        sb.append( "-The " ).append( project.getGroupId() ).append( " team" );
    }

    /**
     * Initializes a new table with the specified headers.
     *
     * @param sb      the stringbuffer to use
     * @param headers the table headers
     */
    public static void initializeTableHeader( StringBuffer sb, String[] headers )
    {
        if ( headers == null || headers.length == 0 )
        {
            return;
        }

        sb.append( "||" );
        for ( int i = 0; i < headers.length; i++ )
        {
            sb.append( headers[i] ).append( "||" );
        }
    }
}
