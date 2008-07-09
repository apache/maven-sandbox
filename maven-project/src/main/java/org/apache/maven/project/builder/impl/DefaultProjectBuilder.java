package org.apache.maven.project.builder.impl;

import org.apache.maven.shared.model.*;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.validation.ModelValidationResult;
import org.apache.maven.project.validation.ModelValidator;
import org.apache.maven.project.builder.*;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.*;

public final class DefaultProjectBuilder implements ProjectBuilder, LogEnabled {

    private ArtifactFactory artifactFactory;

    private ArtifactResolver artifactResolver;

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
        resolver.resolve(artifact);
        return buildFromStream(new FileInputStream(artifact.getFile()), interpolatorProperties, resolver, null);//TODO: Fix
    }

    public MavenProject buildFromStream(InputStream pom, Collection<InterpolatorProperty> interpolatorProperties, PomArtifactResolver resolver, File projectDirectory)
            throws IOException {

        if (pom == null) {
            throw new IllegalArgumentException("pom: null");
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

        PomClassicTransformer transformer = new PomClassicTransformer(null);
        ModelTransformerContext ctx = new ModelTransformerContext(
                Arrays.asList(new ArtifactModelContainerFactory(), new IdModelContainerFactory()));
        Model model = ((PomClassicDomainModel) ctx.transform(domainModels, transformer,
                transformer, properties)).getModel();

        //validateModel(model);

        MavenProject mavenProject = new MavenProject(model);
        mavenProject.setArtifact(artifactFactory.createProjectArtifact(model.getGroupId(), model.getArtifactId(),
                model.getVersion()));
        //System.out.println(((PomClassicDomainModel) ctx.transform(domainModels, transformer,
        //        transformer, properties)).asString());
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
            e.printStackTrace();
            throw new IOException("getDomainModelFromRepository");
        }

        if (!artifactParent.getFile().exists()) {
            logger.info("Parent pom does not exist in repository: File = " + artifactParent.getFile().getAbsolutePath());
            Model model = domainModel.getModel();
            System.out.println("PATH = " + projectDirectory.getAbsolutePath() + ":" + model.getParent().getRelativePath());
            System.out.println(new File(projectDirectory, model.getParent().getRelativePath()).getCanonicalFile());
            File parentFile = new File(projectDirectory, model.getParent().getRelativePath()).getCanonicalFile();
            if( parentFile.isDirectory()) {
                parentFile = new File(parentFile, "pom.xml");
            }
            if (!parentFile.exists()) {
                logger.warn("Parent pom does not exist on local path: File = " + parentFile.getAbsolutePath());
                return domainModels;
                //  throw new IOException("Parent pom does not exist: File = " + artifactParent.getFile() + ", Child Id = " +
                //          model.getGroupId() + ":" + model.getArtifactId() + ":" + model.getVersion());
            }
            artifactParent.setFile(parentFile);
        }
        PomClassicDomainModel parentDomainModel = new PomClassicDomainModel(new FileInputStream(artifactParent.getFile()));
        if (!parentDomainModel.matchesParent(domainModel.getModel().getParent())) {
            logger.warn("Parent pom ids do not match: File = " + artifactParent.getFile().getAbsolutePath());
           // return domainModels;
        }

        domainModels.add(parentDomainModel);
        domainModels.addAll(getDomainModelParentsFromRepository(parentDomainModel, artifactResolver, projectDirectory));
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
