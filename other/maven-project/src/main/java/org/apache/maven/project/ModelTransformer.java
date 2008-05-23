package org.apache.maven.project;

import java.util.List;

public interface ModelTransformer {

    DomainModel transformToDomainModel(List<ModelProperty> properties);

    List<ModelProperty> transformToModelProperties(List<DomainModel> domainModels);
    
}
