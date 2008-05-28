package org.apache.maven.project;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class ModelPropertySorter {

    /**
     * Sorts specified list of model properties. Typically the list contain property information from the entire
     * hierarchy of models, with most specialized model first in the list.
     * <p/>
     * Define Sorting Rules: Sorting also removes duplicate values (same URI) unless the value contains a parent with
     * a #collection (http://apache.org/model/project/dependencyManagement/dependencies#collection/dependency)
     *
     * @param properties list of model properties
     * @return
     */
    public static List<ModelProperty> sort(List<ModelProperty> properties) {
        LinkedList<ModelProperty> processedProperties = new LinkedList<ModelProperty>();
        List<String> position = new ArrayList<String>();

        for (ModelProperty p : properties) {
            if (ModelUri.matches(ModelUri.PROJECT, p.getUri())) {
                if (!processedProperties.contains(p)) {
                    processedProperties.add(p);
                    position.add(0, p.getUri());
                }

            } else {
                boolean isContained = position.contains(p.getUri());
                if (p.getUri().endsWith("#collection") && isContained) {
                   //  System.out.println("COND1: " + p);
                    continue;
                }

                String parentUri = getParentUri(p.getUri());
                if (!isContained || parentUri.contains("#collection")) {
                    int pst = position.indexOf(parentUri);
                    processedProperties.add(pst + 1, p);
                    position.add(pst + 1, p.getUri());
                    //  System.out.println("Added: " + p   + ":" + pst + ":" + parentUri);
                }
            }
        }
        return processedProperties;
    }

    private static String getParentUri(String uri) {
        return uri.substring(0, uri.lastIndexOf("/"));
    }
}
