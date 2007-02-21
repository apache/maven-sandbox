package org.apache.maven.wiki.confluence.blog;

import org.apache.maven.project.MavenProject;
import org.apache.maven.changes.Release;

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
 * A base implementation of the {@link ProjectReleaseBlogEntryRenderer} interface.
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public abstract class AbstractProjectReleaseBlogEntryRenderer implements ProjectReleaseBlogEntryRenderer
{

    public String generateProjectReleaseTitle( final MavenProject project, final Release release )
    {
        StringBuffer sb = new StringBuffer();
        sb.append(project.getName()).append(" ").append(release.getVersion()).append(" released");
        return sb.toString();
    }
}
