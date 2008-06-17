package org.apache.maven.shared.model;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelTransformerContextTest {


    @Test
    public void sortWithDuplicateProperty() {
        ModelProperty dup0 = new ModelProperty("http://apache.org/maven/project/version", "1.1");
        ModelProperty dup1 = new ModelProperty("http://apache.org/maven/project/version", "1.2");
        List<ModelProperty> modelProperties = Arrays.asList(dup0, dup1);

        ModelTransformerContext ctx = new ModelTransformerContext(new ArrayList<ModelContainerFactory>());
        List<ModelProperty> sortedProperties = ctx.sort(modelProperties, "http://apache.org/maven");
        assertTrue(sortedProperties.contains(dup0));
        assertFalse(sortedProperties.contains(dup1));
    }

    @Test
    public void sortWithDuplicateCollectionProperty() {
        ModelProperty dup0 = new ModelProperty("http://apache.org/maven/project/test#collection/version", "1.1");
        ModelProperty dup1 = new ModelProperty("http://apache.org/maven/project/test#collection/version", "1.2");
        List<ModelProperty> modelProperties = Arrays.asList(dup0, dup1);

        ModelTransformerContext ctx = new ModelTransformerContext(new ArrayList<ModelContainerFactory>());
        List<ModelProperty> sortedProperties = ctx.sort(modelProperties, "http://apache.org/maven");
        assertTrue(sortedProperties.contains(dup0));
        assertTrue(sortedProperties.contains(dup1));
    }

    @Test
    public void sortCollection() {
        ModelProperty a = new ModelProperty("http://apache.org/maven/project", null);
        ModelProperty b = new ModelProperty("http://apache.org/maven/project/test#collection", null);
        ModelProperty c = new ModelProperty("http://apache.org/maven/project/test#collection/version", "1.1");
        ModelProperty d = new ModelProperty("http://apache.org/maven/project/test#collection/version", "1.2");
        List<ModelProperty> modelProperties = Arrays.asList(a, b, c, d);

        ModelTransformerContext ctx = new ModelTransformerContext(new ArrayList<ModelContainerFactory>());
        List<ModelProperty> sortedProperties = ctx.sort(modelProperties, "http://apache.org/maven");
        assertEquals(a, sortedProperties.get(0));
        assertEquals(b, sortedProperties.get(1));
        assertEquals(d, sortedProperties.get(2));
        assertEquals(c, sortedProperties.get(3));
    }

    @Test
    public void sortCollectionWithDualBaseUris() {
        ModelProperty a = new ModelProperty("http://apache.org/maven/project", null);
        ModelProperty b = new ModelProperty("http://apache.org/maven/project/test#collection", null);
        ModelProperty c = new ModelProperty("http://apache.org/maven/project/test#collection/version", "1.1");
        ModelProperty d = new ModelProperty("http://apache.org/maven/project", null);
        ModelProperty e = new ModelProperty("http://apache.org/maven/project/test#collection", null);
        ModelProperty f = new ModelProperty("http://apache.org/maven/project/test#collection/version", "1.2");
        List<ModelProperty> modelProperties = Arrays.asList(a, b, c, d, e, f);

        ModelTransformerContext ctx = new ModelTransformerContext(new ArrayList<ModelContainerFactory>());
        List<ModelProperty> sortedProperties = ctx.sort(modelProperties, "http://apache.org/maven");
        assertEquals(a, sortedProperties.get(0));
        assertEquals(b, sortedProperties.get(1));
        assertEquals(f, sortedProperties.get(2));
        assertEquals(c, sortedProperties.get(3));
    }

    @Test
    public void sortCollectionWithSubcollections() {
        ModelProperty a = new ModelProperty("http://apache.org/maven/project", null);
        ModelProperty b = new ModelProperty("http://apache.org/maven/project/test#collection", null);
        ModelProperty c = new ModelProperty("http://apache.org/maven/project/test#collection/version", "1.1");
        ModelProperty d = new ModelProperty("http://apache.org/maven/project/test#collection/version/test2#collection", null);
        ModelProperty e = new ModelProperty("http://apache.org/maven/project/test#collection/version/test2#collection/a", "a");
        ModelProperty f = new ModelProperty("http://apache.org/maven/project/test#collection/version/test2#collection/b", "b");

        ModelProperty g = new ModelProperty("http://apache.org/maven/project/test#collection/version", "1.2");
        ModelProperty h = new ModelProperty("http://apache.org/maven/project/test#collection/version/test2#collection", null);
        ModelProperty i = new ModelProperty("http://apache.org/maven/project/test#collection/version/test2#collection/c", "c");
        ModelProperty j = new ModelProperty("http://apache.org/maven/project/test#collection/version/test2#collection/d", "d");
        List<ModelProperty> modelProperties = Arrays.asList(a, b, c, d, e, f, g, h, i, j);

        ModelTransformerContext ctx = new ModelTransformerContext(new ArrayList<ModelContainerFactory>());
        List<ModelProperty> sortedProperties = ctx.sort(modelProperties, "http://apache.org/maven");

        assertEquals(a, sortedProperties.get(0));
        assertEquals(b, sortedProperties.get(1));

        assertEquals(g, sortedProperties.get(2));
        assertEquals(h, sortedProperties.get(3));
        assertEquals(j, sortedProperties.get(4));
        assertEquals(i, sortedProperties.get(5));

        assertEquals(c, sortedProperties.get(6));
        assertEquals(d, sortedProperties.get(7));
        assertEquals(f, sortedProperties.get(8));
        assertEquals(e, sortedProperties.get(9));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sortWithNullProperties() {
        ModelTransformerContext ctx = new ModelTransformerContext(new ArrayList<ModelContainerFactory>());
        ctx.sort(null, "http://apache.org/maven");
    }

    @Test
    public void sortWithEmptyProperties() {
        ModelTransformerContext ctx = new ModelTransformerContext(new ArrayList<ModelContainerFactory>());
        assertEquals(0, ctx.sort(new ArrayList<ModelProperty>(), "http://apache.org/maven").size());
    }
}
