package org.apache.maven.project.interpolation;

import junit.framework.TestCase;
import org.apache.maven.model.Model;
import org.apache.maven.model.Build;
import org.apache.maven.project.interpolation.policies.PropertyPolicy;
import org.apache.maven.project.interpolation.policies.BuildPropertyPolicy;
import org.apache.maven.project.path.PathTranslator;
import org.apache.maven.project.path.DefaultPathTranslator;
import org.codehaus.plexus.util.FileUtils;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

public class ModelPropertyInterpolatorTest extends TestCase {
    
    public void testPropertyInterpolation() throws Exception {

        Properties properties = new Properties();
        properties.setProperty("model.txt", "file:${project.build.sourceDirectory}/test.txt");

        Build build = new Build();
        build.setSourceDirectory("${project.basedir}/src/main/uml");

        Model model = new Model();
        model.setBuild(build);
        model.setProperties(properties);

        List policies = new ArrayList();
        policies.add(new BuildPropertyPolicy(new File("/tmp")));

        DefaultModelPropertyInterpolator modelInterpolator = new DefaultModelPropertyInterpolator();
        model = modelInterpolator.interpolate(model, policies);
                 System.out.println(FileUtils.normalize(model.getProperties().getProperty("model.txt")));
        
        PathTranslator pathTranslator = new DefaultPathTranslator();
        assertEquals(new File("file:"+pathTranslator.alignToBaseDirectory("src/main/uml/test.txt",
                new File("/tmp"))), new File(model.getProperties().getProperty("model.txt")));
    }
}
