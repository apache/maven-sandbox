package org.apache.maven.project.interpolation.policies;

import junit.framework.TestCase;

import java.io.File;

import org.apache.maven.project.interpolation.ModelInterpolationException;
import org.apache.maven.project.interpolation.ModelProperty;
import org.apache.maven.model.Model;
import org.apache.maven.model.Build;

public class BuildPropertyPolicyTest extends TestCase {

    public void testNullProjectDirectory() {
        try {
            new BuildPropertyPolicy(null);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Should throw illegal argument exception on null project directory");
    }

    public void testEvaluateWithNullModel() throws ModelInterpolationException {
        BuildPropertyPolicy policy = new BuildPropertyPolicy(new File("/tmp"));
        try {
            policy.evaluate(new ModelProperty(), null);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Should throw illegal argument exception on null model");
    }

    public void testEvaluateWithModelProperty() throws ModelInterpolationException {
        BuildPropertyPolicy policy = new BuildPropertyPolicy(new File("/tmp"));
        try {
            policy.evaluate(null, new Model());
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Should throw illegal argument exception on null model property");
    }

    public void testAlignPathofBuildSourceDirectory() throws ModelInterpolationException {
        ModelProperty property = new ModelProperty();
        property.setExpression("build.sourceDirectory");

        Build build = new Build();
        build.setSourceDirectory("src/main/java");
        Model model = new Model();
        model.setBuild(build);

        BuildPropertyPolicy policy = new BuildPropertyPolicy(new File("/tmp"));
        policy.evaluate(property, model);
        assertEquals(new File("/tmp/src/main/java").getAbsolutePath(), property.getValue());
    }

    public void testResolvedBuildSourceDirectory() throws ModelInterpolationException {
        ModelProperty property = new ModelProperty();
        property.setExpression("build.sourceDirectory");
        property.setValue("/tmp/src/main/java");

        Build build = new Build();
        Model model = new Model();
        model.setBuild(build);

        BuildPropertyPolicy policy = new BuildPropertyPolicy(new File("/tmp"));
        policy.evaluate(property, model);
        assertEquals(new File("/tmp/src/main/java"), new File(property.getValue()));
    }

    public void testInterpolatedBuildSourceDirectory() throws ModelInterpolationException {
        ModelProperty property = new ModelProperty();
        property.setExpression("build.sourceDirectory");
        property.setValue("/tmp/${project.build.sourceDirectory}");

        Build build = new Build();
        build.setSourceDirectory("src/main/java");
        Model model = new Model();
        model.setBuild(build);

        BuildPropertyPolicy policy = new BuildPropertyPolicy(new File("/tmp"));
        policy.evaluate(property, model);
        assertEquals(new File("/tmp/src/main/java"), new File(property.getValue()));
    }

    public void testMultipleInterpolatedBuildSourceDirectory() throws ModelInterpolationException {
        ModelProperty property = new ModelProperty();
        property.setExpression("basedir");
        property.setValue("${basedir}/${project.build.sourceDirectory}");

        Build build = new Build();
        build.setSourceDirectory("src/main/java");
        Model model = new Model();
        model.setBuild(build);

        BuildPropertyPolicy policy = new BuildPropertyPolicy(new File("/tmp"));
        policy.evaluate(property, model);
        assertEquals(new File("/tmp/${project.build.sourceDirectory}"), new File(property.getValue()));
    }
      
    public void testUnknownProperty() throws ModelInterpolationException {
        ModelProperty property = new ModelProperty();
        property.setExpression("foobar");
        property.setValue("${basedir}/${project.build.sourceDirectory}");

        Build build = new Build();
        build.setSourceDirectory("src/main/java");
        Model model = new Model();
        model.setBuild(build);

        BuildPropertyPolicy policy = new BuildPropertyPolicy(new File("/tmp"));
        policy.evaluate(property, model);
        assertEquals(new File("${basedir}/${project.build.sourceDirectory}"), new File(property.getValue()));
    }
}
