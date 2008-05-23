package org.apache.maven.project;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * Primary context for this package. Provides methods for doing transforms.
 */
public class ModelTransformerContext {

    private Map<String, ModelTransformer> modelTransformers;

    private final static int basePosition = ModelUri.BASE.getUri().length();    

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

    public String marshalModelPropertiesToXml(List<ModelProperty> modelProperties) {
        StringBuffer sb = new StringBuffer();
        String lastTag = null;
        for(ModelProperty mp : modelProperties) {
            String uri = mp.getUri();
            List<String> tagNames = getTagNamesFromUri(uri);
           // lastTag = tagNames.get(tagsNames.)
        }
        return sb.toString();
    }

    private static List<String> getTagNamesFromUri(String uri) {
        List<String> methodNames = new ArrayList<String>();
        for (String name : uri.substring(basePosition).replace("#collection", "").split("/")) {
            methodNames.add(name);
        }
        return methodNames;
    }

    private static class SimpleMarshaller {

        static String getStartTag(String value) {
            StringBuffer sb = new StringBuffer();
            sb.append("<" + value +">");
             return sb.toString();
        }

        static String getEndTag(String value) {
            StringBuffer sb = new StringBuffer();
            sb.append("</" + value +">");
            return sb.toString();
        }
    }
}
