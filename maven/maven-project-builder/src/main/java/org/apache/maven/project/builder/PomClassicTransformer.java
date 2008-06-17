package org.apache.maven.project.builder;

import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import org.apache.maven.shared.model.*;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Provides methods for transforming model properties into a domain model for the pom classic format and vice versa.
 */
public final class PomClassicTransformer implements ModelTransformer {

    private Set<String> uris;

    public PomClassicTransformer() {

    }

    public String getBaseUri() {
        return ProjectUri.baseUri;
    }

    public PomClassicTransformer(Set<String> uris) {
        this.uris = new HashSet<String>(Arrays.asList(
                "http://apache.org/maven/project/build/resources#collection",
                "http://apache.org/maven/project/build/plugins/plugin/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/profiles/profile/build/pluginManagement/plugins#collection",
                "http://apache.org/maven/project/profiles/profile/build/plugins/plugin/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/profiles/profile/reporting/plugins#collection",
                "http://apache.org/maven/project/dependencyManagement/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/profiles/profile/build/testResources#collection",
                "http://apache.org/maven/project/reporting/plugins/plugin/reportSets#collection",
                "http://apache.org/maven/project/pluginRepositories#collection",
                "http://apache.org/maven/project/profiles/profile/build/pluginManagement/plugins/plugin/dependencies#collection",
                "http://apache.org/maven/project/profiles/profile/build/resources#collection",
                "http://apache.org/maven/project/profiles/profile/build/pluginManagement/plugins/plugin/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/licenses#collection",
                "http://apache.org/maven/project/build/plugins/plugin/dependencies#collection",
                "http://apache.org/maven/project/profiles/profile/dependencyManagement/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/profiles/profile/build/plugins/plugin/dependencies#collection",
                "http://apache.org/maven/project/build/testResources#collection",
                "http://apache.org/maven/project/profiles/profile/pluginRepositories#collection",
                "http://apache.org/maven/project/build/pluginManagement/plugins#collection",
                "http://apache.org/maven/project/profiles#collection",
                "http://apache.org/maven/project/reporting/plugins#collection",
                "http://apache.org/maven/project/build/pluginManagement/plugins/plugin/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/build/pluginManagement/plugins/plugin/executions#collection",
                "http://apache.org/maven/project/profiles/profile/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/dependencies#collection",
                "http://apache.org/maven/project/contributors#collection",
                "http://apache.org/maven/project/developers#collection",
                "http://apache.org/maven/project/build/plugins#collection",
                "http://apache.org/maven/project/profiles/profile/build/pluginManagement/plugins/plugin/executions#collection",
                "http://apache.org/maven/project/profiles/profile/dependencies#collection",
                "http://apache.org/maven/project/mailingLists#collection",
                "http://apache.org/maven/project/profiles/profile/dependencyManagement/dependencies#collection",
                "http://apache.org/maven/project/profiles/profile/repositories#collection",
                "http://apache.org/maven/project/build/extensions#collection",
                "http://apache.org/maven/project/build/plugins/plugin/executions#collection",
                "http://apache.org/maven/project/repositories#collection",
                "http://apache.org/maven/project/ciManagement/notifiers#collection",
                "http://apache.org/maven/project/dependencyManagement/dependencies#collection",
                "http://apache.org/maven/project/build/pluginManagement/plugins/plugin/dependencies#collection",
                "http://apache.org/maven/project/profiles/profile/reporting/plugins/plugin/reportSets#collection",
                "http://apache.org/maven/project/profiles/profile/build/plugins#collection",
                "http://apache.org/maven/project/profiles/profile/build/plugins/plugin/executions#collection"
        ));
    }

    public DomainModel transformToDomainModel(List<ModelProperty> properties) throws IOException {
        if (properties == null) {
            throw new IllegalArgumentException("properties: null");
        }
        String xml = null;
        try {
            xml = ModelMarshaller.unmarshalModelPropertiesToXml(properties, ProjectUri.baseUri);
            return new PomClassicDomainModel(new MavenXpp3Reader().read(new StringReader(xml)));
        } catch (XmlPullParserException e) {
            throw new IOException(e + ":" + xml);
        }
    }

    public List<ModelProperty> transformToModelProperties(List<DomainModel> domainModels) throws IOException {
        if (domainModels == null || domainModels.isEmpty()) {
            throw new IllegalArgumentException("domainModels: null or empty");
        }
        List<ModelProperty> modelProperties = new ArrayList<ModelProperty>();
        for (DomainModel domainModel : domainModels) {
            if (!(domainModel instanceof PomClassicDomainModel)) {
                throw new IllegalArgumentException("domainModels: Invalid domain model");
            }
            modelProperties.addAll(ModelMarshaller.marshallXmlToModelProperties(
                    ((PomClassicDomainModel) domainModel).getInputStream(), ProjectUri.baseUri, uris));
        }
        return modelProperties;
    }
}

