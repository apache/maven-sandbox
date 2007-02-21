package org.apache.maven.wiki.confluence.blog;

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
 * A blog entry renderer for project release.
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public interface ProjectReleaseBlogEntryRenderer
{

    /**
     * Generates the blog entry title for the specified project release.
     *
     * @param project the maven project being released
     * @param release the release
     * @return the blog entry title
     */
    String generateProjectReleaseTitle(final MavenProject project, final Release release);

    /**
     * Generates the blog entry for the specified project release.
     *
     * @param project the maven project being released
     * @param release the release
     * @return the blog entry content
     */
    String generateProjectReleaseContent(final MavenProject project, final Release release);
}
