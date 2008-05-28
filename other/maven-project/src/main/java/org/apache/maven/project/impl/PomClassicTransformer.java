package org.apache.maven.project.impl;

import org.apache.maven.project.*;
import org.apache.maven.project.ModelProperty;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.introspection.ReflectionValueExtractor;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.util.*;
import java.io.StringReader;
import java.io.IOException;

public class PomClassicTransformer implements ModelTransformer {

    private final static int basePosition = ModelUri.BASE.getUri().length();

    private ModelTransformerContext ctx;

    public DomainModel transformToDomainModel(List<ModelProperty> properties) {

        StringReader sReader = new StringReader(new ModelMarshaller().unmarshalModelPropertiesToXml(properties));
        Model model = null;
        MavenXpp3Reader modelReader = new MavenXpp3Reader();
        try {
            model = modelReader.read(sReader);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return new PomDomainModel(model);
    }

    public List<ModelProperty> transformToModelProperties(List<DomainModel> domainModels) {
        return null;
    }

    public void init(ModelTransformerContext ctx) {
        this.ctx = ctx;
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

