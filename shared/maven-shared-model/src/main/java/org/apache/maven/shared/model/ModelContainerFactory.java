package org.apache.maven.shared.model;

import java.util.Collection;
import java.util.List;

/**
 * Factory for returning model container instances. Unlike most factories, implementations of this class are meant to
 * create only one type of model container instance.
 */
public interface ModelContainerFactory {

    /**
     * Returns collection of URIs asscociated with this factory. 
     *
     * @return collection of URIs asscociated with this factory
     */
    Collection<String> getUris();

    /**
     * Creates a model container instance that contains the specified model properties. The implementing class instance may
     * modify, add, delete or reorder the list of model properties before placing them into the returned model
     * container.
     *
     * @param modelProperties the model properties to be contained within the model container
     * @return the model container
     */
    ModelContainer create(List<ModelProperty> modelProperties);
}
