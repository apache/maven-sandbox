package org.apache.maven.shared.model;

import org.apache.maven.shared.model.impl.DefaultModelDataSource;

import java.io.IOException;
import java.util.*;


/**
 * Primary context for this package. Provides methods for doing transforms.
 */
public final class ModelTransformerContext {

    private final Collection<ModelContainerFactory> factories;

    private final static List<InterpolatorProperty> systemInterpolatorProperties = new ArrayList<InterpolatorProperty>();

    static {
        for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
            systemInterpolatorProperties.add(new InterpolatorProperty("${" + e.getKey() + "}", (String) e.getValue()));
        }

        for (Map.Entry<String, String> e : System.getenv().entrySet()) {
            systemInterpolatorProperties.add(new InterpolatorProperty("${env." + e.getKey() + "}", e.getValue()));
        }
    }

    /**
     * Default constructor
     */
    public ModelTransformerContext(Collection<ModelContainerFactory> factories) {
        this.factories = (factories == null) ? Collections.EMPTY_LIST : factories;
    }

    public DomainModel transform(List<DomainModel> domainModels, ModelTransformer fromModelTransformer,
                                 ModelTransformer toModelTransformer,
                                 Collection<InterpolatorProperty> interpolatorProperties) throws IOException {
        List<InterpolatorProperty> properties = new ArrayList<InterpolatorProperty>(systemInterpolatorProperties);
        properties.addAll(interpolatorProperties);

        String baseUriForModel = fromModelTransformer.getBaseUri();
        List<ModelProperty> modelProperties = sort(fromModelTransformer.transformToModelProperties(domainModels),
                baseUriForModel);
        ModelDataSource modelDataSource = new DefaultModelDataSource();
        modelDataSource.init(modelProperties, factories);
        long start = System.currentTimeMillis();
        for (ModelContainerFactory factory : factories) {
            for (String uri : factory.getUris()) {
                List<ModelContainer> modelContainers = modelDataSource.queryFor(uri);
                Collections.reverse(modelContainers);
                for (int i = 0; i < modelContainers.size(); i++) {
                    ModelContainer mcA = modelContainers.get(i);
                    for (ModelContainer mcB : modelContainers.subList(i + 1, modelContainers.size())) {
                        ModelContainerAction action = mcA.containerAction(mcB);
                        if (ModelContainerAction.DELETE.equals(action)) {
                            modelDataSource.delete(mcB);
                        } else if (ModelContainerAction.JOIN.equals(action)) {
                            modelDataSource.join(mcA, mcB);
                        }
                    }
                }
            }
        }
        System.out.println("Time= " + (System.currentTimeMillis() - start));

        //interpolator
        List<ModelProperty> mps = modelDataSource.getModelProperties();
        long s = System.currentTimeMillis();
        for (ModelProperty mp : mps) {
            InterpolatorProperty ip = mp.asInterpolatorProperty(baseUriForModel);
            if (ip != null) {
                properties.add(ip);
            }
        }

        List<ModelProperty> unresolvedProperties = new ArrayList<ModelProperty>();
        for(ModelProperty mp : mps) {
            if(!mp.isResolved()) {
                unresolvedProperties.add(mp);
            }
        }

        for (InterpolatorProperty ip : properties) {
            for (ModelProperty mp : unresolvedProperties) {
                mp.resolveWith(ip);
            }
        }
        System.out.println("Resolve Time = " + (System.currentTimeMillis() - s));
        return toModelTransformer.transformToDomainModel(mps);
    }


    /**
     * Transforms specified hierarchical list of domain models (inheritence) to target domain model.
     *
     * @param domainModels
     * @param fromModelTransformer
     * @param toModelTransformer
     * @return
     */
    public DomainModel transform(List<DomainModel> domainModels, ModelTransformer fromModelTransformer,
                                 ModelTransformer toModelTransformer)
            throws IOException {
        return this.transform(domainModels, fromModelTransformer, toModelTransformer, new ArrayList<InterpolatorProperty>());
    }

    /**
     * Sorts specified list of model properties. Typically the list contain property information from the entire
     * hierarchy of models, with most specialized model first in the list.
     * <p/>
     * Define Sorting Rules: Sorting also removes duplicate values (same URI) unless the value contains a parent with
     * a #collection (http://apache.org/model/project/dependencyManagement/dependencies#collection/dependency)
     *
     * @param properties unsorted list of model properties. List may not be null.
     * @param baseUri
     * @return sorted list of model properties
     */
    protected List<ModelProperty> sort(List<ModelProperty> properties, String baseUri) {
        if (properties == null) {
            throw new IllegalArgumentException("properties");
        }
        LinkedList<ModelProperty> processedProperties = new LinkedList<ModelProperty>();
        List<String> position = new ArrayList<String>();
        boolean projectIsContained = false;

        for (ModelProperty p : properties) {
            String uri = p.getUri();
            String parentUri = uri.substring(0, uri.lastIndexOf("/"));
            if (!projectIsContained && uri.equals(baseUri)) {
                projectIsContained = true;
                processedProperties.add(p);
                position.add(0, uri);
            } else if (!position.contains(uri) || parentUri.contains("#collection")) {
                int pst = position.indexOf(parentUri) + 1;
                processedProperties.add(pst, p);
                position.add(pst, uri);
            }
        }
        return processedProperties;
    }
}