package org.apache.maven.plugin.reactor;

/*
 * Copyright 2008 The Apache Software Foundation.
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


import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.dag.Vertex;

/**
 * Goal to build a project X and all of the reactor projects that depend on X 
 *
 * @author <a href="mailto:dfabulich@apache.org">Dan Fabulich</a>
 * @goal makeDependents
 * @aggregator
 * @phase process-sources
 */
public class MakeDependentsMojo
    extends MakeMojo
{
    // gather parents instead of children
    protected Set gatherProjects( Vertex v, SuperProjectSorter ps, Set visited, Set out )
    {
        visited.add(v);
        out.add(ps.getProjectMap().get(v.getLabel()));
        List parents = v.getParents();
        for (int i = 0; i < parents.size(); i++) {
            Vertex parent = (Vertex) parents.get(i);
            if (visited.contains(parent)) continue;
            gatherProjects(parent, ps, visited, out);
        }
        return out;
    }
}
