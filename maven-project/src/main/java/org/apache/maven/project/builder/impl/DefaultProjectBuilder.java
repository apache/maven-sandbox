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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    public MavenProject buildFromRepository(InputStream pom, Collection<InterpolatorProperty> interpolatorProperties,
                                            PomArtifactResolver resolver)
            throws IOException {

        if (pom == null) {
            throw new IllegalArgumentException("pom: null");
        }

        if (resolver == null) {
            throw new IllegalArgumentException("resolver: null");
        }

        List<InterpolatorProperty> properties;
        if (interpolatorProperties == null) {
            properties = new ArrayList<InterpolatorProperty>();
        } else {
            properties = new ArrayList<InterpolatorProperty>(interpolatorProperties);
        }

        DomainModel domainModel = new PomClassicDomainModel(pom);
        List<DomainModel> domainModels = new ArrayList<DomainModel>();
        domainModels.add(domainModel);
        domainModels.addAll(getDomainModelParentsFromRepository((PomClassicDomainModel) domainModel, resolver));

        PomClassicTransformer transformer = new PomClassicTransformer();
        ModelTransformerContext ctx = new ModelTransformerContext(
                Arrays.asList(new ArtifactModelContainerFactory(), new IdModelContainerFactory()));
        Model model = ((PomClassicDomainModel) ctx.transform(domainModels, transformer,
                transformer, properties)).getModel();
                System.out.println("*:" + new PomClassicDomainModel(model).asString());
        return new MavenProject(model);
    }

    public MavenProject buildFromLocalPath(InputStream pom, Collection<InterpolatorProperty> interpolatorProperties,
                                        PomArtifactResolver resolver, File projectDirectory)
            throws IOException {

        if (pom == null) {
            throw new IllegalArgumentException("pom: null");
        }

        if (resolver == null) {
            throw new IllegalArgumentException("resolver: null");
        }

        if (projectDirectory == null) {
            throw new IllegalArgumentException("projectDirectory: null");
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

        PomClassicTransformer transformer = new PomClassicTransformer();
        ModelTransformerContext ctx = new ModelTransformerContext(
                Arrays.asList(new ArtifactModelContainerFactory(), new IdModelContainerFactory()));
        Model model = ((PomClassicDomainModel) ctx.transform(domainModels, transformer,
                transformer, properties)).getModel();
        System.out.println(new PomClassicDomainModel(model).asString());
        return new MavenProject(model);
    }

    private boolean isParentLocal(Parent parent, File projectDirectory){
        try {
            File f = new File(projectDirectory, parent.getRelativePath()).getCanonicalFile();
            if (f.isDirectory()) {
                f = new File(f, "pom.xml");
            }
          //  logger.info("File: " + f.getAbsolutePath());
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
        }

        domainModels.add(parentDomainModel);
        domainModels.addAll(getDomainModelParentsFromRepository(parentDomainModel, artifactResolver));
        return domainModels;
    }


    private List<DomainModel> getDomainModelParentsFromLocalPath(PomClassicDomainModel domainModel,
                                                                  PomArtifactResolver artifactResolver,
                                                                  File projectDirectory)
            throws IOException {
        
        if (artifactFactory == null) {
            throw new IllegalArgumentException("artifactFactory: not initialized");
        }

        List<DomainModel> domainModels = new ArrayList<DomainModel>();

        Parent parent = domainModel.getModel().getParent();

        if (parent == null) {
            return domainModels;
        }

        Model model = domainModel.getModel();
        /*
        logger.info("-----------------");
        logger.info("Project Directory =" + projectDirectory.getAbsolutePath());
        logger.info("Parent Path = " + model.getParent().getRelativePath());
        logger.info("Relative Path = " + new File(projectDirectory, model.getParent().getRelativePath()));
        */
        File parentFile = new File(projectDirectory, model.getParent().getRelativePath()).getCanonicalFile();
        //logger.info("Parent File = " + parentFile.getAbsolutePath());
        if (parentFile.isDirectory()) {
          //  logger.info("Is directory = " + parentFile.getAbsolutePath());
            parentFile = new File(parentFile.getAbsolutePath(), "pom.xml");
            //logger.info("New Directory = " + parentFile.getAbsolutePath());
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
              //  logger.info("Parent Local: " + parentFile.getParentFile());
                 domainModels.addAll(getDomainModelParentsFromLocalPath(parentDomainModel, artifactResolver, parentFile.getParentFile()));
            }  else {
                //logger.info("Parent Repo: ");
                domainModels.addAll(getDomainModelParentsFromRepository(parentDomainModel, artifactResolver));
            }
        }
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
