package org.apache.maven.project.impl;

import org.apache.maven.project.DomainModel;
import org.apache.maven.model.Model;

public class PomDomainModel implements DomainModel {
    private Model model;

    public PomDomainModel(Model model) {
        if(model == null) {
            throw new IllegalArgumentException("model");
        }
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PomDomainModel that = (PomDomainModel) o;

        if (!model.equals(that.model)) return false;

        return true;
    }

    public int hashCode() {
        return model.hashCode();
    }
}
