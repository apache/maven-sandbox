package org.apache.maven.shared.model.impl;

import org.apache.maven.shared.model.*;

import java.util.*;

/**
 * Default implementation of the ModelDataSource.
 */
public final class DefaultModelDataSource implements ModelDataSource {

    private List<ModelProperty> modelProperties;

    private Map<String, ModelContainerFactory> modelContainerFactoryMap;

    public ModelContainer join(ModelContainer a, ModelContainer b) throws DataSourceException {
        if (a == null || a.getProperties() == null || a.getProperties().size() == 0) {
            throw new IllegalArgumentException("a or a.properties: empty");
        }
        if (b == null || b.getProperties() == null) {
            throw new IllegalArgumentException("b: null or b.properties: empty");
        }
        if (!modelProperties.containsAll(a.getProperties())) {
            throw new DataSourceException("ModelContainer 'a' contains elements not within datasource");
        }
        if (a.equals(b) || b.getProperties().size() == 0) {
            return a;
        }

        int startIndex = modelProperties.indexOf(b.getProperties().get(0));
        delete(a);
        delete(b);

        List<ModelProperty> joinedProperties = mergeModelContainers(a, b);
        if(modelProperties.size() == 0) {
            startIndex = 0;    
        }
        modelProperties.addAll(startIndex, joinedProperties);
        return a.createNewInstance(joinedProperties);
    }

    public void delete(ModelContainer modelContainer) {
        if (modelContainer == null) {
            throw new IllegalArgumentException("modelContainer: null");
        }
        if (modelContainer.getProperties() == null) {
            throw new IllegalArgumentException("modelContainer.properties: null");
        }
        modelProperties.removeAll(modelContainer.getProperties());
    }

    public List<ModelProperty> getModelProperties() {
        return new ArrayList<ModelProperty>(modelProperties);
    }

    public List<ModelContainer> queryFor(String uri) throws DataSourceException {
        if (uri == null) {
            throw new IllegalArgumentException("uri");
        }

        if (modelProperties.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        ModelContainerFactory factory = modelContainerFactoryMap.get(uri);
        if (factory == null) {
            throw new DataSourceException("Unable to find factory for uri: URI = " + uri);
        }

        List<ModelContainer> modelContainers = new LinkedList<ModelContainer>();

        final int NO_TAG = 0;
        final int START_TAG = 1;
        final int END_START_TAG = 2;
        final int END_TAG = 3;
        int state = NO_TAG;

        List<ModelProperty> tmp = new ArrayList<ModelProperty>();

        for (Iterator<ModelProperty> i = modelProperties.iterator(); i.hasNext();) {
            ModelProperty mp = i.next();
            if (state == START_TAG && (!i.hasNext() || !mp.getUri().startsWith(uri))) {
                state = END_TAG;
            } else if (state == START_TAG && mp.getUri().equals(uri)) {
                state = END_START_TAG;
            } else if (mp.getUri().startsWith(uri)) {
                state = START_TAG;
            } else {
                state = NO_TAG;
            }
            switch (state) {
                case START_TAG: {
                    tmp.add(mp);
                    if (!i.hasNext()) {
                        modelContainers.add(factory.create(tmp));
                    }
                    break;
                }
                case END_START_TAG: {
                    modelContainers.add(factory.create(tmp));
                    tmp.clear();
                    tmp.add(mp);
                    state = START_TAG;
                    break;
                }
                case END_TAG: {
                    modelContainers.add(factory.create(tmp));
                    tmp.clear();
                    state = NO_TAG;
                }
            }
        }
        return modelContainers;
    }

    public void init(List<ModelProperty> modelProperties, Collection<ModelContainerFactory> modelContainerFactories) {
        if (modelProperties == null) {
            throw new IllegalArgumentException("modelProperties: null");
        }
        if (modelContainerFactories == null) {
            throw new IllegalArgumentException("modeContainerFactories: null");
        }
        this.modelProperties = new LinkedList<ModelProperty>(modelProperties);
        this.modelContainerFactoryMap = new HashMap<String, ModelContainerFactory>();

        for (ModelContainerFactory factory : modelContainerFactories) {
            Collection<String> uris = factory.getUris();
            if (uris == null) {
                throw new IllegalArgumentException("factory.uris: null");
            }

            for (String uri : uris) {
                modelContainerFactoryMap.put(uri, factory);
            }
        }
    }

    /**
     * Removes duplicate model properties from the containers and return list.
     *
     * @param a container A
     * @param b container B
     * @return
     */
    private static List<ModelProperty> mergeModelContainers(ModelContainer a, ModelContainer b) {
        List<ModelProperty> m = new ArrayList<ModelProperty>();
        m.addAll(a.getProperties());
        m.addAll(b.getProperties());

        LinkedList<ModelProperty> processedProperties = new LinkedList<ModelProperty>();
        List<String> uris = new ArrayList<String>();

        for (ModelProperty p : m) {
            if (!uris.contains(p.getUri())) {
                processedProperties.add(p);
                uris.add(p.getUri());
            }
        }
        return processedProperties;
    }
}
