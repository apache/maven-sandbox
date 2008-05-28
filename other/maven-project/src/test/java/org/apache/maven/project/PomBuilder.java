package org.apache.maven.project;

import junit.framework.TestCase;
import org.apache.maven.project.impl.PomClassicTransformer;
import org.apache.maven.project.impl.PomDomainModel;
import org.apache.maven.project.impl.DependencyManagementProcessor;
import org.apache.maven.project.interpolation.ModelInterpolationException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.util.LinkedList;
import java.util.List;
import java.io.StringWriter;
import java.io.IOException;
import java.io.StringReader;


public class PomBuilder extends TestCase {

    public void testA() {
        PomClassicTransformer b = new PomClassicTransformer();

        LinkedList<ModelProperty> mp = new LinkedList<ModelProperty>();
        mp.add(new ModelProperty(ModelUri.PROJECT.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_ARTIFACTID.getUri(), "org.apache.maven"));
        mp.add(new ModelProperty(ModelUri.PROJECT.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_ARTIFACTID.getUri(), "org.apache.maven.new"));

        List<ModelProperty> p = ModelPropertySorter.sort(mp);
        for (ModelProperty prop : p) {
            System.out.println(prop);
        }
    }

    public void testB() {
        PomClassicTransformer b = new PomClassicTransformer();

        LinkedList<ModelProperty> mp = new LinkedList<ModelProperty>();
        mp.add(new ModelProperty(ModelUri.PROJECT.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "org.apache.maven.dep"));

        mp.add(new ModelProperty(ModelUri.PROJECT_ARTIFACTID.getUri(), "org.apache.maven"));

        mp.add(new ModelProperty(ModelUri.PROJECT.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "org.apache.maven.dep1"));

        List<ModelProperty> p = ModelPropertySorter.sort(mp);
        for (ModelProperty prop : p) {
            System.out.println(prop);
        }
    }

    public void testC() {
        PomClassicTransformer b = new PomClassicTransformer();

        LinkedList<ModelProperty> mp = new LinkedList<ModelProperty>();
        mp.add(new ModelProperty(ModelUri.PROJECT.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_ARTIFACTID.getUri(), "org.apache.maven"));
        mp.add(new ModelProperty(ModelUri.PROJECT.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_ARTIFACTID.getUri(), "org.apache.maven.new"));

        Model model = ((PomDomainModel) b.transformToDomainModel(mp)).getModel();
        assertEquals("org.apache.maven", model.getArtifactId());
    }

    public void testD() {
        PomClassicTransformer b = new PomClassicTransformer();

        LinkedList<ModelProperty> mp = new LinkedList<ModelProperty>();
        mp.add(new ModelProperty(ModelUri.PROJECT.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "org.apache.maven.dep"));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.bogus"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "bogus-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.0"));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_SCOPE.getUri(), "compile"));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.4"));

        List<ModelProperty> p = ModelPropertySorter.sort(mp);
        for (ModelProperty prop : p) {
            System.out.println(prop);
        }
    }

    public void testE() {
        PomClassicTransformer b = new PomClassicTransformer();

        LinkedList<ModelProperty> mp = new LinkedList<ModelProperty>();
        mp.add(new ModelProperty(ModelUri.PROJECT.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "org.apache.maven.dep"));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.bogus"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "bogus-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.0"));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_SCOPE.getUri(), "compile"));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.4"));
       for (ModelProperty prop : mp) {
            System.out.println(prop);
        }
        System.out.println("-------");
       List<ModelProperty> p = ModelPropertySorter.sort(mp);
       for (ModelProperty prop : p) {
            System.out.println(prop);
        }

        DependencyManagementProcessor pr = new DependencyManagementProcessor();
        pr.process(p);
          System.out.println("Processed List:");
         for (ModelProperty prop : p) {
           // System.out.println(prop);
        }

        String xml = new ModelMarshaller().unmarshalModelPropertiesToXml(p);
        System.out.println("XML OUTPUT---------------");
        System.out.println(xml);
        StringReader sReader = new StringReader(xml);
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


        if (model == null) {
            System.out.println("Model null");
        }
        StringWriter sWriter = new StringWriter();

        MavenXpp3Writer writer = new MavenXpp3Writer();
        try {
            writer.write(sWriter, model);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sWriter);

    }
}
