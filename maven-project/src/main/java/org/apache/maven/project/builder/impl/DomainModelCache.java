package org.apache.maven.project.builder.impl;

import org.apache.maven.model.Model;
import org.apache.maven.project.builder.PomClassicDomainModel;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

final class DomainModelCache {

    private Set<PomClassicDomainModel> cache;

    DomainModelCache() {
        cache = new HashSet<PomClassicDomainModel>();
    }

    public PomClassicDomainModel findDomainModelFor(Model model) {
        for (PomClassicDomainModel domainModel : cache) {
            if (domainModel.matchesModel(model)) {
                System.out.println("Return domain model from cache: " + model.getGroupId() + ":" + model.getArtifactId()
                        + model.getVersion());
                return domainModel;
            }
        }
        return null;
    }

    public PomClassicDomainModel findDomainModelParentFor(Model model) {
        for (PomClassicDomainModel domainModel : cache) {
            if (domainModel.matchesParent(model.getParent())) {
                return domainModel;
            }
        }
        return null;
    }

    public void storeDomainModel(PomClassicDomainModel domainModel) {
        if (!cache.contains(domainModel)) {
            cache.add(domainModel);
        }
    }

    public boolean contains(PomClassicDomainModel domainModel) {
        Model model;
        try {
            model = domainModel.getModel();
        } catch (IOException e) {
            return false;
        }

        for (PomClassicDomainModel dm : cache) {
            if (dm.matchesModel(model)) {
                return true;
            }
        }
        return false;
    }
}
