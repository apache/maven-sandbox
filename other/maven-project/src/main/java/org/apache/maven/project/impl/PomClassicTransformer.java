package org.apache.maven.project.impl;

import org.apache.maven.project.*;
import org.apache.maven.project.ModelProperty;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.introspection.ReflectionValueExtractor;

import java.util.*;

public class PomClassicTransformer implements ModelTransformer {

    private final static int basePosition = ModelUri.BASE.getUri().length();

    public DomainModel transformToDomainModel(List<ModelProperty> properties) {
        Model model = new Model();
        Class m = Model.class;
      //  m.getMethod()
        for (ModelProperty mp : properties) {  //Use Reflection

            for (String s : getMethodNamesFromUri(mp.getUri())) {
                if (mp.getValue() == null) {
                    try {
                        Class c = Class.forName("org.apache.maven.model." + s);
                        System.out.println("Class = " + c.getName());
                       
                    } catch (ClassNotFoundException e) {
                       // e.printStackTrace();
                    }

                } else {

                }

            }

            /*
            if(ModelUri.matches(ModelUri.PROJECT_ARTIFACTID, mp.getUri())) {
                model.setArtifactId(mp.getValue());
            }
            */
        }

        return new PomDomainModel(model);
    }

    public List<ModelProperty> transformToModelProperties(List<DomainModel> domainModels) {
        /*
              if(mp.getValue() != null) {

            }
            System.out.println(getDotNotationFromUri(mp.getUri()));
            try {
                model = (Model) ReflectionValueExtractor.evaluate(getDotNotationFromUri(mp.getUri()), model, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
               */
        return null;
    }

    private static String getDotNotationFromUri(String uri) {
        return uri.substring(basePosition).replace("#collection", "").replace("/", ".");
    }

    private static List<String> getMethodNamesFromUri(String uri) {
        List<String> methodNames = new ArrayList<String>();
        for (String name : uri.substring(basePosition).replace("#collection", "").split("/")) {
            methodNames.add(name.substring(0, 1).toUpperCase() + name.substring(1));
        }
        return methodNames;
    }

}

