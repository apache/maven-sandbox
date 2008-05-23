package org.apache.maven.project.impl;

import org.apache.maven.project.ModelProperty;
import org.apache.maven.project.ModelUri;
import org.apache.maven.project.ModelProcessor;

import java.util.*;

public class DependencyManagementProcessor implements ModelProcessor {

    public void process(List<ModelProperty> list) {
/*
        Stack<Dependency> p = new Stack<Dependency>();
        int j = 0;
        for (Iterator<ModelProperty> i = list.iterator(); i.hasNext();) {
            ModelProperty mp = i.next(); j++;
            if (ModelUri.matches(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES, mp.getUri())) {

                while (i.hasNext()) {
                    if (mp.getUri().startsWith(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri())) {
                        
                        p.push(mp);
                    } else {
                        break;
                    }
                }
            }
        }

         Collections.reverse();
        List<Dependency> n = new ArrayList<Dependency>();

       // List<Map<String, List<ModelProperty>>> l = new ArrayList<Map<String, List<ModelProperty>>>();
        for (ModelProperty mp : p) {
            String artifactId = "";
            String groupId = "";
            if (ModelUri.matches(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID, mp.getUri())) {
                artifactId = mp.getValue();
            } else
            if (ModelUri.matches(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID, mp.getUri())) {
                groupId = mp.getValue();
            } else if (ModelUri.matches(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY, mp.getUri())) {
                String id = groupId + ":" + artifactId;
                Map<String, List<ModelProperty>> m = new HashMap<String, List<ModelProperty>>();
            }
        }
        System.out.println("-------------");

        System.out.println("-------------");
        */

    }

    private class Dependency {
        private String groupId;

        private String artifactId;

        private String version;

        private int position;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getPosition() {
            return position;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Dependency that = (Dependency) o;

            if (!artifactId.equals(that.artifactId)) return false;
            if (!groupId.equals(that.groupId)) return false;
            if (!version.equals(that.version)) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = groupId.hashCode();
            result = 31 * result + artifactId.hashCode();
            result = 31 * result + version.hashCode();
            result = 31 * result + position;
            return result;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
