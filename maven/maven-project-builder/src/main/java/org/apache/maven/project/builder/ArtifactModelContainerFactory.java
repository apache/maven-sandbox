package org.apache.maven.project.builder;

import org.apache.maven.shared.model.*;
import java.util.*;

public final class ArtifactModelContainerFactory implements ModelContainerFactory {

    private static final Collection<String> uris = Collections.unmodifiableList(Arrays.asList(
            ProjectUri.DependencyManagement.Dependencies.Dependency.xUri,
            ProjectUri.Dependencies.Dependency.xUri,
            ProjectUri.Profiles.Profile.DependencyManagement.Dependencies.Dependency.xUri,
            ProjectUri.Profiles.Profile.Dependencies.Dependency.xUri,
            ProjectUri.Build.PluginManagement.Plugins.Plugin.xUri,
            ProjectUri.Build.PluginManagement.Plugins.Plugin.Dependencies.Dependency.xUri,
            ProjectUri.Build.Plugins.Plugin.xUri,
            ProjectUri.Build.Plugins.Plugin.Dependencies.Dependency.xUri,
            ProjectUri.Build.Plugins.Plugin.Dependencies.Dependency.Exclusions.Exclusion.xUri
    ));

    public Collection<String> getUris() {
        return uris;
    }

    public ModelContainer create(List<ModelProperty> modelProperties) {
        if (modelProperties == null || modelProperties.size() == 0) {
            throw new IllegalArgumentException("modelProperties: null or empty");
        }
        return new ArtifactModelContainer(modelProperties);
    }

    private static class ArtifactModelContainer implements ModelContainer {

        private String groupId;

        private String artifactId;

        private String version;

        private List<ModelProperty> properties;

        private ArtifactModelContainer(List<ModelProperty> properties) {
            this.properties = new ArrayList<ModelProperty>(properties);
            //Collections.sort(this.properties, new ModelPropertyComparator());
            this.properties = Collections.unmodifiableList(this.properties);

            for (ModelProperty mp : properties) {
                if (mp.getUri().endsWith("version")) {
                    this.version = mp.getValue();
                } else if (mp.getUri().endsWith("artifactId")) {
                    this.artifactId = mp.getValue();
                } else if (mp.getUri().endsWith("groupId")) {
                    this.groupId = mp.getValue();
                }
            }
            if (groupId == null) {
                throw new IllegalArgumentException("properties does not contain group id:");
            }

            if (artifactId == null) {
                throw new IllegalArgumentException("properties does not contain artifact id");
            }
        }

        public ModelContainerAction containerAction(ModelContainer modelContainer) {
            if (modelContainer == null) {
                throw new IllegalArgumentException("modelContainer: null");
            }

            if (!(modelContainer instanceof ArtifactModelContainer)) {
                throw new IllegalArgumentException("modelContainer: wrong type");
            }

            ArtifactModelContainer c = (ArtifactModelContainer) modelContainer;
            if (c.groupId.equals(groupId) && c.artifactId.equals(artifactId)) {
                return (c.version.equals(version)) ? ModelContainerAction.JOIN : ModelContainerAction.DELETE;
            } else {
                return ModelContainerAction.NOP;
            }
        }

        public ModelContainer createNewInstance(List<ModelProperty> modelProperties) {
            return new ArtifactModelContainer(modelProperties);
        }

        public List<ModelProperty> getProperties() {
            return properties;
        }

        public String toString() {
            return "Group ID = " + groupId + ", Artifact ID = " + artifactId + ", Version = " + version;
        }
    }
  }
