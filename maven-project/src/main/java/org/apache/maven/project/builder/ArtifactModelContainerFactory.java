package org.apache.maven.project.builder;

import org.apache.maven.shared.model.ModelContainer;
import org.apache.maven.shared.model.ModelContainerAction;
import org.apache.maven.shared.model.ModelContainerFactory;
import org.apache.maven.shared.model.ModelProperty;

import java.util.*;

public final class ArtifactModelContainerFactory implements ModelContainerFactory {

    private static final Collection<String> uris = Collections.unmodifiableList(Arrays.asList(

            ProjectUri.DependencyManagement.Dependencies.Dependency.xUri,
            ProjectUri.Dependencies.Dependency.xUri,

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

        private String type;

        private List<ModelProperty> properties;

        private ArtifactModelContainer(List<ModelProperty> properties) {
            this.properties = new ArrayList<ModelProperty>(properties);
            this.properties = Collections.unmodifiableList(this.properties);

            for (ModelProperty mp : properties) {
                if (mp.getUri().endsWith("version")) {
                    this.version = mp.getValue();
                } else if (mp.getUri().endsWith("artifactId")) {
                    this.artifactId = mp.getValue();
                } else if (mp.getUri().endsWith("groupId")) {
                    this.groupId = mp.getValue();
                } else if(mp.getUri().equals(ProjectUri.Dependencies.Dependency.type)) {
                    this.type = mp.getValue();
                }
            }
            if (groupId == null) {
                groupId = "org.apache.maven.plugins";
                //  throw new IllegalArgumentException("properties does not contain group id. Artifact ID = "
                //          + artifactId + ", Version = " + version);
            }

            if (artifactId == null) {
                throw new IllegalArgumentException("Properties does not contain artifact id. Group ID = " + groupId +
                        ", Version = " + version);
            }

            if(type == null) {
                type = "";
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
                if (c.version == null) {
                    return ModelContainerAction.NOP;
                }

                if(c.version.equals(version)) {
                    if(c.type.equals(type)) {
                        return ModelContainerAction.JOIN;
                    } else {
                        return ModelContainerAction.NOP; 
                    }
                } else {
                   return ModelContainerAction.DELETE;
                }
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

        public void sort(List<ModelProperty> modelProperties) {
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("Group ID = ").append(groupId).append(", Artifact ID = ").append(artifactId)
                    .append(", Version").append(version).append("\r\n");
            for (ModelProperty mp : properties) {
                sb.append(mp).append("\r\n");
            }
            return sb.toString();
        }
    }
}
