package org.apache.maven.project;

import java.util.List;

/**
 * Provides services for transforming domain models to property lists and vice versa.
 * ModelTransformer.transformToDomainModel == ModelTransformer.transformToModelProperties if list of model
 * properties specified in transformToDomainModel contains only one property with a uri of http://apache.org/model/project.
 */
public interface ModelTransformer {

    /**
     * Transforms specified list of model properties into a single domain model. The list may contain a hierarchy (inheritance) of
     * model information.
     *
     * @param properties list of model properties to transform into domain model.
     * @return domain model
     */
    DomainModel transformToDomainModel(List<ModelProperty> properties);

    /**
     * Transforms specified list of domain models to a property list. The list of domain models should be in order of
     * most specialized to least specialized model.
     *
     * @param domainModels list of domain models to transform to a list of model properties
     * @return list of model properties
     */
    List<ModelProperty> transformToModelProperties(List<DomainModel> domainModels);

    void init(ModelTransformerContext ctx);

}
