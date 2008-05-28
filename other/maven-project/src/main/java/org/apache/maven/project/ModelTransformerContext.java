package org.apache.maven.project;

import java.util.*;


/**
 * Primary context for this package. Provides methods for doing transforms.
 */
public class ModelTransformerContext {

    private Map<String, ModelTransformer> modelTransformers;

    public ModelTransformerContext() {
        this.modelTransformers = new HashMap<String, ModelTransformer>();
    }

    /**
     * Transforms specified hierarchical list of domain models (inheritence) to target domain model.
     *
     * @param domainModels
     * @param fromModelTransformer
     * @param toModelTransformer
     * @return
     */
    public DomainModel transform(List<DomainModel> domainModels, ModelTransformer fromModelTransformer, ModelTransformer toModelTransformer) {
        List<ModelProperty> modelProperties = fromModelTransformer.transformToModelProperties(domainModels);
        ModelPropertySorter.sort(modelProperties);
        return toModelTransformer.transformToDomainModel(modelProperties);
    }

    public DomainModel transformToDomainModel(String transformerName, List<ModelProperty> modelProperties) {
        ModelTransformer modelTransformer = modelTransformers.get(transformerName);
        return modelTransformer.transformToDomainModel(ModelPropertySorter.sort(modelProperties));
    }

    public ModelTransformer getModelTransformerFor(String transformerName) {
        return modelTransformers.get(transformerName);
    }

    public void addModelTransformer(String name, ModelTransformer modelTransformer) {
        modelTransformers.put(name, modelTransformer);
    }
}
