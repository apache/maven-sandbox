package org.apache.maven.project.builder;

//import org.apache.maven.model.Model;
//import org.apache.maven.model.Parent;
import org.apache.maven.shared.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.*;

public class ProjectBuilder {
/*
    private final File localRepository;

    public ProjectBuilder(File localRepository) {
        this.localRepository = localRepository;
    }

    private List<DomainModel> getDomainModelParentsFromRepository(PomClassicDomainModel domainModel) throws IOException {
        Parent parent = domainModel.getModel().getParent();
       // ArtifactRepositoryLayout layout = DefaultArtifactRepositoryLayout();
    }

    public MavenProject buildFrom(InputStream pom, Collection<InterpolatorProperty> interpolatorProperties)
            throws IOException {


        List<DomainModel> domainModels = this.getDomainModelParentsFromRepository(new PomClassicDomainModel(pom));

        PomClassicTransformer transformer = new PomClassicTransformer(null);
        ModelTransformerContext ctx = new ModelTransformerContext(
                (Collection) Arrays.asList(new ArtifactModelContainerFactory()));
        PomClassicDomainModel domainmodel = (PomClassicDomainModel) ctx.transform(domainModels, transformer,
                transformer, interpolatorProperties);
        Model model = domainmodel.getModel();

        return new MavenProject(model);
    }
*/
}
