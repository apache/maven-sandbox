package org.apache.maven.project.builder;

import org.apache.maven.shared.model.ModelContainer;
import org.apache.maven.shared.model.ModelContainerAction;
import org.apache.maven.shared.model.ModelContainerFactory;
import org.apache.maven.shared.model.ModelProperty;

import java.util.*;

public class IdModelContainerFactory implements ModelContainerFactory {

    private static final Collection<String> uris = Collections.unmodifiableList(Arrays.asList(
            ProjectUri.PluginRepositories.PluginRepository.xUri,
            ProjectUri.Repositories.Repository.xUri,
            ProjectUri.Reporting.Plugins.Plugin.ReportSets.ReportSet.xUri,
            ProjectUri.Profiles.Profile.xUri,
            ProjectUri.Build.Plugins.Plugin.Executions.Execution.xUri
    ));

    public Collection<String> getUris() {
        return uris;
    }

    public ModelContainer create(List<ModelProperty> modelProperties) {
        if (modelProperties == null || modelProperties.size() == 0) {
            throw new IllegalArgumentException("modelProperties: null or empty");
        }
        return new IdModelContainer(modelProperties);
    }

    private static class IdModelContainer implements ModelContainer {

        private String id;

        private List<ModelProperty> properties;

        private IdModelContainer(List<ModelProperty> properties) {
            this.properties = new ArrayList<ModelProperty>(properties);
            this.properties = Collections.unmodifiableList(this.properties);

            for (ModelProperty mp : properties) {
                if (mp.getUri().endsWith("/id")) {
                    this.id = mp.getValue();
                }
            }
        }

        public ModelContainerAction containerAction(ModelContainer modelContainer) {
            if (modelContainer == null) {
                throw new IllegalArgumentException("modelContainer: null");
            }

            if (!(modelContainer instanceof IdModelContainer)) {
                throw new IllegalArgumentException("modelContainer: wrong type");
            }

            IdModelContainer c = (IdModelContainer) modelContainer;
            if (c.id == null || id == null) {
                return ModelContainerAction.NOP;
            }
            return (c.id.equals(id)) ? ModelContainerAction.JOIN : ModelContainerAction.NOP;
        }

        public ModelContainer createNewInstance(List<ModelProperty> modelProperties) {
            return new IdModelContainer(modelProperties);
        }

        public void sort(List<ModelProperty> modelProperties) {

        }

        public List<ModelProperty> getProperties() {
            return properties;
        }

        public String toString() {
            return "ID = " + id;
        }
    }
}
