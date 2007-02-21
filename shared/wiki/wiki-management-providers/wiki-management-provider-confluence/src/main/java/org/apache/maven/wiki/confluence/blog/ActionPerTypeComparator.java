package org.apache.maven.wiki.confluence.blog;

import org.apache.maven.changes.Action;

import java.util.Comparator;

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
 * Sort actions per action type.
 * <p/>
 * TODO move this to the changes mojo
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public class ActionPerTypeComparator
    implements Comparator
{

    public int compare( Object o1, Object o2 )
    {
        Action a1 = (Action) o1;
        Action a2 = (Action) o2;

        ActionType at1 = ActionType.getIssueActionType( a1.getType() );
        ActionType at2 = ActionType.getIssueActionType( a2.getType() );

        return at1.compareTo( at2 );
    }
}
