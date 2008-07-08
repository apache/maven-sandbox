package org.apache.maven.shared.model;

import java.util.Collection;
import java.util.List;

/**
 * Provides services for joining, deleting and querying model containers.
 */
public interface ModelDataSource {

    /**
     * Join model properties of the specified container a with the specified container b. Any elements of model container
     * a must take precendence over model container b. All elements of model container A must exist in the data source;
     * elements of model container b may or may not exist.
     *
     * @param a model container with precedence
     * @param b model container without precedence
     * @return joined model container
     */
    ModelContainer join(ModelContainer a, ModelContainer b) throws DataSourceException;

    /**
     * Deletes properties of the specified model container from the data source.
     *
     * @param modelContainer the model container that holds the properties to be deleted
     */
    void delete(ModelContainer modelContainer);


    /**
     * Return copy of underlying model properties. No changes in this list will be reflected in the data source.
     *
     * @return copy of underlying model properties
     */
    List<ModelProperty> getModelProperties();

    /**
     * Returns model containers for the specified URI.
     *
     * @param uri
     * @return
     */
    List<ModelContainer> queryFor(String uri) throws DataSourceException;


    /**
     * Initializes the object with model properties.
     *
     * @param modelProperties the model properties that back the data source
     */
    void init(List<ModelProperty> modelProperties, Collection<ModelContainerFactory> modelContainerFactories);

    String getEventHistory();
}
