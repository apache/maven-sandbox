package org.apache.maven.shared.model;

import java.util.List;

/**
 * Provides services for determining actions to take: noop, delete, join. For example, say containers with the same ids
 * are joined, otherwise one must be deleted.
<pre>
  ModelContainerA.id = "foo" and
  ModelContainerB.id = "foobar"
</pre>
 * then ModelContainerA.containerAction(ModelContainerB) would return delete action for ModelContainerB.  
 */
public interface ModelContainer {

    /**
     * Returns the model properties contained within the model container. This list must be unmodifiable.
     *
     * @return the model properties contained within the model container
     */
    List<ModelProperty> getProperties();

    /**
     * Returns model container action (noop, delete, join) for the specified model container.
     *
     * @param modelContainer the model container to determine the action of
     * @return model container action (noop, delete, join) for the specified model container
     */
    ModelContainerAction containerAction(ModelContainer modelContainer);

    /**
     * Creates new instance of model container.
     *
     * @param modelProperties
     * @return new instance of model container
     */
    ModelContainer createNewInstance(List<ModelProperty> modelProperties);
    
}
