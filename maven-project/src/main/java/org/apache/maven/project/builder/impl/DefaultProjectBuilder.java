package org.apache.maven.project.builder.impl;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.builder.*;
import org.apache.maven.project.validation.ModelValidationResult;
import org.apache.maven.project.validation.ModelValidator;
import org.apache.maven.shared.model.DomainModel;
import org.apache.maven.shared.model.InterpolatorProperty;
import org.apache.maven.shared.model.ModelTransformerContext;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class DefaultProjectBuilder implements ProjectBuilder, LogEnabled {

    private ArtifactFactory artifactFactory;

    private Logger logger;

    private ModelValidator validator;

    public DefaultProjectBuilder() {
    }

    protected DefaultProjectBuilder(ArtifactFactory artifactFactory) {
        if (artifactFactory == null) {
            throw new IllegalArgumentException("artifactFactory: null");
        }
        this.artifactFactory = artifactFactory;
    }

    public MavenProject buildFromLocalPath(InputStream pom, List<Model> inheritedModels,
                                           Collection<InterpolatorProperty> interpolatorProperties,
                                           PomArtifactResolver resolver, File projectDirectory)
            throws IOException {
        logger.info("BuildFromLocalPath");
        long start = System.currentTimeMillis();
        if (pom == null) {
            throw new IllegalArgumentException("pom: null");
        }

        if (resolver == null) {
            throw new IllegalArgumentException("resolver: null");
        }

        if (projectDirectory == null) {
            throw new IllegalArgumentException("projectDirectory: null");
        }

        if(inheritedModels == null) {
            inheritedModels = new ArrayList<Model>();
        } else {
            inheritedModels = new ArrayList<Model>(inheritedModels);
            Collections.reverse(inheritedModels);
        }

        List<InterpolatorProperty> properties;
        if (interpolatorProperties == null) {
            properties = new ArrayList<InterpolatorProperty>();
        } else {
            properties = new ArrayList<InterpolatorProperty>(interpolatorProperties);
        }

        PomClassicDomainModel domainModel = new PomClassicDomainModel(pom);
        List<DomainModel> domainModels = new ArrayList<DomainModel>();
        domainModels.add(domainModel);

        if(domainModel.getModel().getParent() != null) {
            if(isParentLocal(domainModel.getModel().getParent(), projectDirectory )) {
                 domainModels.addAll(getDomainModelParentsFromLocalPath(domainModel, resolver,
                         projectDirectory));
            }  else {
                domainModels.addAll(getDomainModelParentsFromRepository(domainModel, resolver));
            }
        }

        for(Model model : inheritedModels) {
            domainModels.add(new PomClassicDomainModel(model));
        }

        PomClassicTransformer transformer = new PomClassicTransformer();
        ModelTransformerContext ctx = new ModelTransformerContext(
                Arrays.asList(new ArtifactModelContainerFactory(), new IdModelContainerFactory()));
        PomClassicDomainModel transformedDomainModel = ((PomClassicDomainModel) ctx.transform(domainModels, transformer,
                transformer, properties));
        Model model = transformedDomainModel.getModel();
        System.out.println("buildFromLocalPath: Time = " + (System.currentTimeMillis() - start));
        return new MavenProject(model);
    }

    private boolean isParentLocal(Parent parent, File projectDirectory){
        try {
            File f = new File(projectDirectory, parent.getRelativePath()).getCanonicalFile();
            if (f.isDirectory()) {
                f = new File(f, "pom.xml");
            }
            return f.exists();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<DomainModel> getDomainModelParentsFromRepository(PomClassicDomainModel domainModel,
                                                                  PomArtifactResolver artifactResolver) throws IOException {
        if (artifactFactory == null) {
            throw new IllegalArgumentException("artifactFactory: not initialized");
        }

        long start = System.currentTimeMillis();

        List<DomainModel> domainModels = new ArrayList<DomainModel>();

        Parent parent = domainModel.getModel().getParent();

        if (parent == null) {
            return domainModels;
        }

        Artifact artifactParent =
                artifactFactory.createParentArtifact(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
        artifactResolver.resolve(artifactParent);

        PomClassicDomainModel parentDomainModel = new PomClassicDomainModel(new FileInputStream(artifactParent.getFile()));
        if (!parentDomainModel.matchesParent(domainModel.getModel().getParent())) {
            logger.warn("Parent pom ids do not match: File = " + artifactParent.getFile().getAbsolutePath());
            return domainModels;
        } else {
          //  logger.info("Adding pom to hierarchy: Group Id = " + parent.getGroupId() + ", Artifact Id ="
          //      + parent.getArtifactId()  + ", Version = " + parent.getVersion() + ", File" + artifactParent.getFile());
        }

        domainModels.add(parentDomainModel);
        domainModels.addAll(getDomainModelParentsFromRepository(parentDomainModel, artifactResolver));
        System.out.println("getDomainModelParentsFromRepository: Time = " + (System.currentTimeMillis() - start) + ", Gid ="
                + parent.getGroupId() + ", Artifact Id= " + parent.getArtifactId() + ", Version = " + parent.getVersion());
        return domainModels;
    }


    private List<DomainModel> getDomainModelParentsFromLocalPath(PomClassicDomainModel domainModel,
                                                                  PomArtifactResolver artifactResolver,
                                                                  File projectDirectory)
            throws IOException {
        
        if (artifactFactory == null) {
            throw new IllegalArgumentException("artifactFactory: not initialized");
        }
        long start = System.currentTimeMillis();


        List<DomainModel> domainModels = new ArrayList<DomainModel>();

        Parent parent = domainModel.getModel().getParent();

        if (parent == null) {
            return domainModels;
        }

        Model model = domainModel.getModel();
        
        File parentFile = new File(projectDirectory, model.getParent().getRelativePath()).getCanonicalFile();
        if (parentFile.isDirectory()) {
            parentFile = new File(parentFile.getAbsolutePath(), "pom.xml");
        }

        if(!parentFile.exists()) {
            throw new IOException("File does not exist: File =" + parentFile.getAbsolutePath());
        }
        
        PomClassicDomainModel parentDomainModel = new PomClassicDomainModel(new FileInputStream(parentFile));
        if (!parentDomainModel.matchesParent(domainModel.getModel().getParent())) {
            logger.warn("Parent pom ids do not match: File = " + parentFile.getAbsolutePath());
        }

        domainModels.add(parentDomainModel);
        if(parentDomainModel.getModel().getParent() != null) {
            if(isParentLocal( parentDomainModel.getModel().getParent(), parentFile.getParentFile() )) {
                 domainModels.addAll(getDomainModelParentsFromLocalPath(parentDomainModel, artifactResolver, parentFile.getParentFile()));
            }  else {
                domainModels.addAll(getDomainModelParentsFromRepository(parentDomainModel, artifactResolver));
            }
        }

        System.out.println("getDomainModelParentsFromLocalPath: Time = " + (System.currentTimeMillis() - start) + ", Gid ="
                + model.getGroupId() + ", Artifact Id= " + model.getArtifactId() + ", Version = " + model.getVersion());
        return domainModels;
    }


    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    private void validateModel(Model model)
            throws IOException {
        ModelValidationResult validationResult = validator.validate(model);

        if (validationResult.getMessageCount() > 0) {
            throw new IOException("Failed to validate: " + validationResult.toString());
        }
    }


}
