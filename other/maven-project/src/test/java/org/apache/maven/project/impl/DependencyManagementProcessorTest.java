package org.apache.maven.project.impl;

import junit.framework.TestCase;
import org.apache.maven.project.ModelProperty;
import org.apache.maven.project.ModelUri;
import org.apache.maven.project.ModelPropertySorter;

import java.util.*;

public class DependencyManagementProcessorTest extends TestCase {

    public void testSimpleOveriddenDependency() {
        LinkedList<ModelProperty> mp = new LinkedList<ModelProperty>();

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.4"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        LinkedList<ModelProperty> workQueue = new LinkedList<ModelProperty>(mp);

        Stack<ModelProperty> result = new Stack<ModelProperty>();
        DependencyManagementProcessor pr = new DependencyManagementProcessor();
        pr.pushDependenciesFromQueue(workQueue, result);
        assertEquals("Incorrect size of result.", 4, result.size());
        assertTrue("Result does not contain correct version: ",
                contains(result, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        assertEquals("Incorrect size of work queue", 0, workQueue.size());
    }

    public void testOveriddenDependency() {
        LinkedList<ModelProperty> mp = new LinkedList<ModelProperty>();

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.bogus"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "bogus-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.0"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.4"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        LinkedList<ModelProperty> workQueue = new LinkedList<ModelProperty>(mp);

        Stack<ModelProperty> result = new Stack<ModelProperty>();
        DependencyManagementProcessor pr = new DependencyManagementProcessor();
        pr.pushDependenciesFromQueue(workQueue, result);
        for (ModelProperty p : result) {
            System.out.println("P:" + p);
        }
        assertEquals("Incorrect size of result.", 4, result.size());
        assertTrue("Does not contain correct version: ",
                contains(result, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        //   assertEquals("Incorrect size of work queue", 4, workQueue.size());
        //   assertTrue("Work queue has incorrect entry" ,
        //           contains(result, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.bogus"));
    }

    public void testJoinedDependency() {
        LinkedList<ModelProperty> mp = new LinkedList<ModelProperty>();

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.bogus"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "bogus-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.0"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_SCOPE.getUri(), "compile"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        LinkedList<ModelProperty> workQueue = new LinkedList<ModelProperty>(mp);

        Stack<ModelProperty> result = new Stack<ModelProperty>();
        DependencyManagementProcessor pr = new DependencyManagementProcessor();
        pr.pushDependenciesFromQueue(workQueue, result);
        for (ModelProperty p : result) {
            System.out.println("P:" + p);
        }
        assertEquals("Incorrect size of result.", 5, result.size());
        assertTrue("Does not contain correct version: ",
                contains(result, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        assertTrue("Does not contain joined URIs: ",
                contains(result, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_SCOPE.getUri(), "compile"));
    }

    public void testTwoJoinedDependencies() {
        LinkedList<ModelProperty> mp = new LinkedList<ModelProperty>();

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.bogus"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "bogus-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.0"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.apache.maven.dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "artifact-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_SCOPE.getUri(), "compile"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID.getUri(), "org.bogus"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID.getUri(), "bogus-dep"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.0"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_SCOPE.getUri(), "runtime"));
        mp.add(new ModelProperty(ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY.getUri(), null));

        LinkedList<ModelProperty> workQueue = new LinkedList<ModelProperty>(mp);

        Stack<ModelProperty> result = new Stack<ModelProperty>();
        DependencyManagementProcessor pr = new DependencyManagementProcessor();
        pr.pushDependenciesFromQueue(workQueue, result);
        for (ModelProperty p : result) {
            System.out.println("P:" + p);
        }
        assertEquals("Incorrect size of result.", 5, result.size());
        assertTrue("Does not contain correct version: ",
                contains(result, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION.getUri(), "1.1"));
        assertTrue("Does not contain joined URIs: ",
                contains(result, ModelUri.PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_SCOPE.getUri(), "compile"));
    }

    private static boolean contains(Collection<ModelProperty> c, String uri, String value) {
        for (ModelProperty mp : c) {
            if (mp.getUri().equals(uri) && mp.getValue() != null && mp.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
