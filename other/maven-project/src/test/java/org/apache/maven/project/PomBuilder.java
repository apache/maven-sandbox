package org.apache.maven.project;

import junit.framework.TestCase;
import org.apache.maven.project.impl.PomClassicTransformer;
import org.apache.maven.project.impl.PomDomainModel;
import org.apache.maven.project.interpolation.ModelInterpolationException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.util.LinkedList;
import java.util.List;
import java.io.StringWriter;
import java.io.IOException;


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
        PomClassicTransformer t = new PomClassicTransformer();
        PomDomainModel dm = (PomDomainModel) t.transformToDomainModel(p);
        Model model = dm.getModel();
        if(model ==  null) {
            System.out.println("Model null");
        }
        StringWriter sWriter = new StringWriter();

        MavenXpp3Writer writer = new MavenXpp3Writer();
        try
        {
            writer.write( sWriter, model );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        System.out.println(sWriter);
        
    }
}
