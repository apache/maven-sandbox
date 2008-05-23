package org.apache.maven.project.impl;

import org.apache.maven.project.DomainModel;
import org.apache.maven.model.Model;

public class PomDomainModel implements DomainModel {
    private Model model;

    public PomDomainModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}
