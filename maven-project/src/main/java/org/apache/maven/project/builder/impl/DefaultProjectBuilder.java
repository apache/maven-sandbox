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

    public MavenProject buildFromArtifact(Artifact artifact, Collection<InterpolatorProperty> interpolatorProperties, PomArtifactResolver resolver)
            throws IOException {
        if (resolver == null) {
            throw new IllegalArgumentException("resolver: null");
        }
        resolver.resolve(artifact);
        return buildFromStream(new FileInputStream(artifact.getFile()), interpolatorProperties, resolver, null);//TODO: Fix
    }

    public MavenProject buildFromStream(InputStream pom, Collection<InterpolatorProperty> interpolatorProperties,
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

        DomainModel domainModel = new PomClassicDomainModel(pom);
        List<DomainModel> domainModels = new ArrayList<DomainModel>();
        domainModels.add(domainModel);
        domainModels.addAll(getDomainModelParentsFromRepository((PomClassicDomainModel) domainModel, resolver, projectDirectory));

        PomClassicTransformer transformer = new PomClassicTransformer();
        ModelTransformerContext ctx = new ModelTransformerContext(
                Arrays.asList(new ArtifactModelContainerFactory(), new IdModelContainerFactory()));
        Model model = ((PomClassicDomainModel) ctx.transform(domainModels, transformer,
                transformer, properties)).getModel();

        //  validateModel(model);
        for (DomainModel dm : domainModels) {
            //     System.out.println(dm.getEventHistory());
        }
        MavenProject mavenProject = new MavenProject(model);
        Artifact artifact = artifactFactory.createProjectArtifact(model.getGroupId(), model.getArtifactId(),
                model.getVersion());
        if (mavenProject.getBuild() != null && mavenProject.getBuild().getOutputDirectory() != null
                &&  mavenProject.getBuild().getFinalName() != null) {
            File artifactFile = new File(mavenProject.getBuild().getOutputDirectory(), mavenProject.getBuild().getFinalName());
            if (!artifactFile.exists()) {
                throw new IOException("Artifact does not exist: File = " + artifactFile.getAbsolutePath());
            }
            artifact.setFile(artifactFile);
        }  else {
            logger.warn("Build section of pom is null");
        }

        mavenProject.setArtifact(artifact);

        return mavenProject;
    }

    private List<DomainModel> getDomainModelParentsFromRepository(PomClassicDomainModel domainModel,
                                                                  PomArtifactResolver artifactResolver, File projectDirectory) throws IOException {
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

        try {
            artifactResolver.resolve(artifactParent);
        } catch (IOException e) {
            // throw new IOException("getDomainModelFromRepository");
        }

        if (!artifactParent.getFile().exists()) {
            logger.info("Parent pom does not exist in repository: File = " + artifactParent.getFile().getAbsolutePath());
            Model model = domainModel.getModel();

            File parentFile = new File(projectDirectory, model.getParent().getRelativePath()).getCanonicalFile();
            if (parentFile.isDirectory()) {
                parentFile = new File(parentFile, "pom.xml");
            }

            //logger.info("Project Directory = " + projectDirectory.getAbsolutePath()) ;
            //logger.info("Relative PATH = " + model.getParent().getRelativePath());
            //logger.info("File:" + new File(projectDirectory, model.getParent().getRelativePath()).getAbsolutePath());
            //logger.info("Canonical Parent File: = " + parentFile.getAbsolutePath());

            if (!parentFile.exists()) {
                logger.warn("Parent pom does not exist on local path: File = " + parentFile.getAbsolutePath());
//                  throw new IOException("Parent pom does not exist: File = " + artifactParent.getFile() + ", Child Id = " +
                //                         model.getGroupId() + ":" + model.getArtifactId() + ":" + model.getVersion());
            }
            artifactParent.setFile(parentFile);
        }
        PomClassicDomainModel parentDomainModel = new PomClassicDomainModel(new FileInputStream(artifactParent.getFile()));
        if (!parentDomainModel.matchesParent(domainModel.getModel().getParent())) {
            logger.warn("Parent pom ids do not match: File = " + artifactParent.getFile().getAbsolutePath());
        }

        domainModels.add(parentDomainModel);
        domainModels.addAll(getDomainModelParentsFromRepository(parentDomainModel, artifactResolver, artifactParent.getFile().getParentFile()));
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
