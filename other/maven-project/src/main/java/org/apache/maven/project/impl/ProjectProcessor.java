package org.apache.maven.project.impl;

import org.apache.maven.project.ModelProcessor;
import org.apache.maven.project.ModelProperty;
import org.apache.maven.project.ModelPropertySorter;

import java.util.List;

public class ProjectProcessor implements ModelProcessor {

    public void process(List<ModelProperty> list) {
        List<ModelProperty> properties = ModelPropertySorter.sort(list);
        DependencyManagementProcessor processor = new DependencyManagementProcessor();
        processor.process(properties);
    }
}
