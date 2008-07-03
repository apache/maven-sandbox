package org.apache.maven.shared.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class ModelPropertyTest {

    @Test
    public void isParent() {
        ModelProperty mp0 = new ModelProperty("http://apache.org/maven/project/profiles#collection/profile/id", "1");
        ModelProperty mp1 = new ModelProperty("http://apache.org/maven/project/profiles#collection/profile/build/plugins/plugin/groupId", "org");
        assertFalse(mp0.isParentOf(mp1));
        assertTrue(mp0.getDepth() < mp1.getDepth());
    }

    @Test
    public void isParent1() {
        ModelProperty mp0 = new ModelProperty("http://apache.org/maven/project/profiles#collection/profile/id", "1");
        ModelProperty mp1 = new ModelProperty("http://apache.org/maven/project/profiles#collection/profile/id/a/b", "org");
        assertFalse(mp0.isParentOf(mp1));
    }
}
