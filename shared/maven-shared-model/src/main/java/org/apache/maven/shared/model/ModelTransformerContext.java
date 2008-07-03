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

    /**
     * Transforms and interpolates specified hierarchical list of domain models (inheritence) to target domain model.
     * Unlike ModelTransformerContext#transform(java.util.List, ModelTransformer, ModelTransformer), this method requires
     * the user to add interpolator properties. It's intended to be used by IDEs.
     *
     * @param domainModels
     * @param fromModelTransformer
     * @param toModelTransformer
     * @param interpolatorProperties properties to use during interpolation.
     * @return
     * @throws IOException
     */
    public DomainModel transform(List<DomainModel> domainModels, ModelTransformer fromModelTransformer,
                                 ModelTransformer toModelTransformer,
                                 Collection<InterpolatorProperty> interpolatorProperties) throws IOException {
        List<InterpolatorProperty> properties = new ArrayList<InterpolatorProperty>(interpolatorProperties);

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
        //  System.out.println("Time= " + (System.currentTimeMillis() - start));

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
        for (ModelProperty mp : mps) {
            if (!mp.isResolved()) {
                unresolvedProperties.add(mp);
            }
        }
        /*
        System.out.println("Properties: " + properties.size());
        for (InterpolatorProperty ip : properties) {
            for (ModelProperty mp : unresolvedProperties) {
                System.out.println(ip);
                mp.resolveWith(ip);
                System.out.println(mp);
                System.out.println("-------------------");
            }
        }

        */
        //System.out.println("Resolve Time = " + (System.currentTimeMillis() - s));
        validate(mps);
        return toModelTransformer.transformToDomainModel(mps);
    }

    /**
     * Transforms and interpolates specified hierarchical list of domain models (inheritence) to target domain model.
     * Uses standard environmental and system properties for intepolation.
     *
     * @param domainModels
     * @param fromModelTransformer
     * @param toModelTransformer
     * @return
     * @throws IOException
     */
    public DomainModel transform(List<DomainModel> domainModels, ModelTransformer fromModelTransformer,
                                 ModelTransformer toModelTransformer)
            throws IOException {
        return this.transform(domainModels, fromModelTransformer, toModelTransformer, systemInterpolatorProperties);
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
    /*
    private static List<ModelProperty> validateWithCorrections(List<ModelProperty> modelProperties) throws IOException {
        List<ModelProperty> mps = new ArrayList<ModelProperty>();
        mps.add(modelProperties.get(0));
        for (int i = 1; i < modelProperties.size(); i++) {
            ModelProperty previous = modelProperties.get(i - 1);
            ModelProperty current = modelProperties.get(i);
            if ((!previous.isParentOf(current) && current.getDepth() > previous.getDepth())
                    || (current.getDepth() - previous.getDepth() > 1)) {
                for (int j = mps.size(); j <= 0; j--) {
                    if (mps.get(j - 1).isParentOf(current)) {
                        mps.add(j - 1, current);
                        break;
                    }
                }
            } else {
                mps.add(current);
            }
        }
        return mps;
    }
    */
    private static void validate(List<ModelProperty> modelProperties) throws IOException {
        for (int i = 1; i < modelProperties.size(); i++) {
            ModelProperty previous = modelProperties.get(i - 1);
            ModelProperty current = modelProperties.get(i);
            if ((!previous.isParentOf(current) && current.getDepth() > previous.getDepth())
                    || (current.getDepth() - previous.getDepth() > 1)) {
                int j = 0;
                for (ModelProperty mp : modelProperties) {
                    System.out.println((j++) + ":" + mp);
                }
                throw new IOException("Invalid Model Property: Property " + current + ", Line = " + i);
            }
        }
    }
}
