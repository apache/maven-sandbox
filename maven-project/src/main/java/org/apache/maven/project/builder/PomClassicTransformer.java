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
                "http://apache.org/maven/project/profiles/profile/build/plugins/plugin/executions#collection",
                "http://apache.org/maven/project/modules#collection"
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
            StringBuffer sb = new StringBuffer("\r\n");
            for (ModelProperty mp : properties) {
                sb.append(mp).append("\r\n");
            }
            throw new IOException(e + ":\r\n" + xml + sb.toString());
        }
    }

    public List<ModelProperty> transformToModelProperties(List<DomainModel> domainModels) throws IOException {
        if (domainModels == null || domainModels.isEmpty()) {
            throw new IllegalArgumentException("domainModels: null or empty");
        }

        List<ModelProperty> modelProperties = new ArrayList<ModelProperty>();
        List<String> projectNames = new ArrayList<String>();
        StringBuffer scmUrl = new StringBuffer();

        for (DomainModel domainModel : domainModels) {
            if (!(domainModel instanceof PomClassicDomainModel)) {
                throw new IllegalArgumentException("domainModels: Invalid domain model");
            }
            List<ModelProperty> tmp = ModelMarshaller.marshallXmlToModelProperties(
                    ((PomClassicDomainModel) domainModel).getInputStream(), ProjectUri.baseUri, uris);

            //Modules Not Inherited Rule
            if (domainModels.indexOf(domainModel) != 0) {
                ModelProperty modulesProperty = getPropertyFor(ProjectUri.Modules.xUri, tmp);
                if(modulesProperty != null) {
                    tmp.remove(modulesProperty);
                    tmp.removeAll(getPropertiesFor(ProjectUri.Modules.module, tmp));
                }
            }

            //Missing groupId, use parent one Rule
            if (getPropertyFor(ProjectUri.groupId, tmp) == null) {
                ModelProperty parentGroupId = getPropertyFor(ProjectUri.Parent.groupId, tmp);
                tmp.add(new ModelProperty(ProjectUri.groupId, parentGroupId.getValue()));
            }

            //SCM Rule
            ModelProperty scmUrlProperty = getPropertyFor(ProjectUri.Scm.url, tmp);
            if (scmUrl.length() == 0 && scmUrlProperty != null) {
                scmUrl.append(scmUrlProperty.getValue());
                for (String projectName : projectNames) {
                    scmUrl.append("/").append(projectName);
                }
                int index = tmp.indexOf(scmUrlProperty);
                tmp.remove(index);
                tmp.add(index, new ModelProperty(ProjectUri.Scm.url, scmUrl.toString()));
            }
            projectNames.add(0, getPropertyFor(ProjectUri.artifactId, tmp).getValue());

            modelProperties.addAll(tmp);
        }
        return modelProperties;
    }

    private static List<ModelProperty> getPropertiesFor(String uri, List<ModelProperty> properties) {
        List<ModelProperty> modelProperties = new ArrayList<ModelProperty>();
        for (ModelProperty mp : properties) {
            if (uri.equals(mp.getUri())) {
                modelProperties.add(mp);
            }
        }
        return modelProperties;
    }

    private static ModelProperty getPropertyFor(String uri, List<ModelProperty> properties) {
        for (ModelProperty mp : properties) {
            if (uri.equals(mp.getUri())) {
                return mp;
            }
        }
        return null;
    }
}

