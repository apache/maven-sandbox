package org.apache.maven.wiki.confluence.blog;

import java.util.Map;
import java.util.HashMap;

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
 * The action type.
 *
 * TODO: move this to the changes mojo
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
final class ActionType implements Comparable {
    private static final Map types = new HashMap();

    /**
     * An action representing a fix.
     */
    public static final ActionType FIX = newIssueActionType("fix", 1);

    /**
     * An action representing a new feature.
     */
    public static final ActionType ADD = newIssueActionType("add", 2);

    /**
     * An action representing an improvement.
     */
    public static final ActionType UPDATE = newIssueActionType("update", 3);

    /**
     * An action representing a removal.
     */
    public static final ActionType REMOVE = newIssueActionType("remove", 4);


    /**
     * Returns the <code>IssuActionType</code> based on its name.
     *
     * @param name the name of the action
     * @return the issue action or null if no such action exists.
     */
    public static ActionType getIssueActionType(final String name) {
        if (name == null) {
            throw new NullPointerException("Issue type could not be null.");
        } else {
            return (ActionType) types.get(name.toLowerCase());
        }
    }

    private static ActionType newIssueActionType(String name, int position) {
        ActionType iat = new ActionType(name, position);
        types.put(name, iat);

        return iat;
    }

    private final String name;
    private final Integer position;

    private ActionType(String name, int position) {
        this.name = name;
        this.position = new Integer(position);
    }

    public int compareTo(Object o) {
        if (o == null) {
            throw new NullPointerException("Could not compare to null.");
        }
        if (ActionType.class.isInstance(o)) {
            ActionType iat = (ActionType) o;
            return position.compareTo(iat.position);
        } else {
            throw new IllegalArgumentException("Could not compare with["+o.getClass().getName()+"] " +
                    "expected["+getClass().getName()+"]");
        }
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ActionType that = (ActionType) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}