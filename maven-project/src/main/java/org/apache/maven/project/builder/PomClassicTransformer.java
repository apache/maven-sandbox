package org.apache.maven.project.builder;

import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.model.*;
import org.apache.maven.shared.model.impl.DefaultModelDataSource;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Provides methods for transforming model properties into a domain model for the pom classic format and vice versa.
 */
public final class PomClassicTransformer implements ModelTransformer {

    private Set<String> uris;

    public String getBaseUri() {
        return ProjectUri.baseUri;
    }

    public PomClassicTransformer() {
        this.uris = new HashSet<String>(Arrays.asList(
                ProjectUri.Build.Extensions.xUri,
                ProjectUri.Build.PluginManagement.Plugins.xUri,
                ProjectUri.Build.Plugins.xUri,
                ProjectUri.Build.Plugins.Plugin.Dependencies.xUri,
                ProjectUri.Build.Plugins.Plugin.Executions.xUri,
                ProjectUri.Build.Resources.xUri,
                ProjectUri.Build.TestResources.xUri,

                ProjectUri.CiManagement.Notifiers.xUri,

                ProjectUri.Contributors.xUri,

                ProjectUri.Dependencies.xUri,
                ProjectUri.Dependencies.Dependency.Exclusions.xUri,

                ProjectUri.DependencyManagement.Dependencies.xUri,
                ProjectUri.DependencyManagement.Dependencies.Dependency.Exclusions.xUri,

                ProjectUri.Developers.xUri,
                ProjectUri.Licenses.xUri,
                ProjectUri.MailingLists.xUri,
                ProjectUri.Modules.xUri,
                ProjectUri.PluginRepositories.xUri,

                ProjectUri.Profiles.xUri,
                ProjectUri.Profiles.Profile.Build.Plugins.xUri,
                ProjectUri.Profiles.Profile.Build.Plugins.Plugin.Dependencies.xUri,
                ProjectUri.Profiles.Profile.Build.Resources.xUri,
                ProjectUri.Profiles.Profile.Build.TestResources.xUri,
                ProjectUri.Profiles.Profile.Dependencies.xUri,
                ProjectUri.Profiles.Profile.Dependencies.Dependency.Exclusions.xUri,
                ProjectUri.Profiles.Profile.DependencyManagement.Dependencies.xUri,
                ProjectUri.Profiles.Profile.PluginRepositories.xUri,
                ProjectUri.Profiles.Profile.Reporting.Plugins.xUri,
                ProjectUri.Profiles.Profile.Repositories.xUri,

                ProjectUri.Reporting.Plugins.xUri,
                ProjectUri.Reporting.Plugins.Plugin.ReportSets.xUri,

                ProjectUri.Repositories.xUri,

                "http://apache.org/maven/project/profiles/profile/build/pluginManagement/plugins/plugin/dependencies#collection",
                "http://apache.org/maven/project/profiles/profile/build/pluginManagement/plugins/plugin/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/profiles/profile/build/pluginManagement/plugins/plugin/executions#collection",
                "http://apache.org/maven/project/profiles/profile/build/pluginManagement/plugins#collection",
                "http://apache.org/maven/project/profiles/profile/build/plugins/plugin/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/profiles/profile/dependencyManagement/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/profiles/profile/reporting/plugins/plugin/reportSets#collection",
                "http://apache.org/maven/project/profiles/profile/build/plugins/plugin/executions#collection",

                "http://apache.org/maven/project/build/plugins/plugin/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/build/pluginManagement/plugins/plugin/dependencies/dependency/exclusions#collection",
                "http://apache.org/maven/project/build/pluginManagement/plugins/plugin/executions#collection",
                "http://apache.org/maven/project/build/pluginManagement/plugins/plugin/dependencies#collection"

        ));
    }

    public DomainModel transformToDomainModel(List<ModelProperty> properties) throws IOException {
        if (properties == null) {
            throw new IllegalArgumentException("properties: null");
        }

        List<ModelProperty> props = new ArrayList<ModelProperty>();
        for (ModelProperty mp : properties) { //TODO: Resolved values
            if (mp.getValue() != null && mp.getValue().contains("=")) {
                props.add(new ModelProperty(mp.getUri(), "<![CDATA[" + mp.getValue() + "]]>"));
            } else {
                props.add(mp);
            }
        }

        String xml = null;
        try {
            xml = ModelMarshaller.unmarshalModelPropertiesToXml(props, ProjectUri.baseUri);
            return new PomClassicDomainModel(new MavenXpp3Reader().read(new StringReader(xml)));
        } catch (XmlPullParserException e) {
            throw new IOException(e + ":\r\n" + xml);
        }
    }

    public List<ModelProperty> transformToModelProperties(List<DomainModel> domainModels) throws IOException {
        if (domainModels == null || domainModels.isEmpty()) {
            throw new IllegalArgumentException("domainModels: null or empty");
        }

        List<ModelProperty> modelProperties = new ArrayList<ModelProperty>();
        List<String> projectNames = new ArrayList<String>();
        StringBuffer scmUrl = new StringBuffer();
        StringBuffer scmConnectionUrl = new StringBuffer();
        StringBuffer scmDeveloperUrl = new StringBuffer();
        for (DomainModel domainModel : domainModels) {
            if (!(domainModel instanceof PomClassicDomainModel)) {
                throw new IllegalArgumentException("domainModels: Invalid domain model");
            }

            List<ModelProperty> tmp = ModelMarshaller.marshallXmlToModelProperties(
                    ((PomClassicDomainModel) domainModel).getInputStream(), ProjectUri.baseUri, uris);

            //Missing Version Rule
            if (getPropertyFor(ProjectUri.version, tmp) == null) {
                ModelProperty parentVersion = getPropertyFor(ProjectUri.Parent.version, tmp);
                tmp.add(new ModelProperty(ProjectUri.version, parentVersion.getValue()));
            }

            //Modules Not Inherited Rule
            if (domainModels.indexOf(domainModel) != 0) {
                ModelProperty modulesProperty = getPropertyFor(ProjectUri.Modules.xUri, tmp);
                if (modulesProperty != null) {
                    tmp.remove(modulesProperty);
                    tmp.removeAll(getPropertiesFor(ProjectUri.Modules.module, tmp));
                }
            }

            //Missing groupId, use parent one Rule
            if (getPropertyFor(ProjectUri.groupId, tmp) == null) {
                ModelProperty parentGroupId = getPropertyFor(ProjectUri.Parent.groupId, tmp);
                tmp.add(new ModelProperty(ProjectUri.groupId, parentGroupId.getValue()));
            }

            //Not inherited plugin execution rule            
            if (domainModels.indexOf(domainModel) > 0) {
                List<ModelProperty> removeProperties = new ArrayList<ModelProperty>();
                ModelDataSource source = new DefaultModelDataSource();
                source.init(tmp, Arrays.asList(new ArtifactModelContainerFactory(), new IdModelContainerFactory()));
                List<ModelContainer> containers = source.queryFor(ProjectUri.Build.Plugins.Plugin.Executions.Execution.xUri);
                for (ModelContainer container : containers) {
                    for (ModelProperty mp : container.getProperties()) {
                        if (mp.getUri().equals(ProjectUri.Build.Plugins.Plugin.Executions.Execution.inherited)
                                && mp.getValue() != null && mp.getValue().equals("false")) {
                            removeProperties.addAll(container.getProperties());
                            for (int j = tmp.indexOf(mp); j >= 0; j--) {
                                System.out.println("------" + tmp.get(j));
                                if (tmp.get(j).getUri().equals(ProjectUri.Build.Plugins.Plugin.Executions.xUri)) {
                                    removeProperties.add(tmp.get(j));
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                tmp.removeAll(removeProperties);
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

            //SCM Connection Rule
            scmUrlProperty = getPropertyFor(ProjectUri.Scm.connection, tmp);
            if (scmConnectionUrl.length() == 0 && scmUrlProperty != null) {
                scmConnectionUrl.append(scmUrlProperty.getValue());
                for (String projectName : projectNames) {
                    scmConnectionUrl.append("/").append(projectName);
                }
                int index = tmp.indexOf(scmUrlProperty);
                tmp.remove(index);
                tmp.add(index, new ModelProperty(ProjectUri.Scm.connection, scmConnectionUrl.toString()));
            }

            //SCM Developer Rule
            scmUrlProperty = getPropertyFor(ProjectUri.Scm.developerConnection, tmp);
            if (scmDeveloperUrl.length() == 0 && scmUrlProperty != null) {
                scmDeveloperUrl.append(scmUrlProperty.getValue());
                for (String projectName : projectNames) {
                    scmDeveloperUrl.append("/").append(projectName);
                }
                int index = tmp.indexOf(scmUrlProperty);
                tmp.remove(index);
                tmp.add(index, new ModelProperty(ProjectUri.Scm.developerConnection, scmDeveloperUrl.toString()));
            }

            //Ordered Dependency Rule
            if (domainModels.size() > 1) {
                ModelDataSource source = new DefaultModelDataSource();
                source.init(tmp, Arrays.asList(new ArtifactModelContainerFactory(), new IdModelContainerFactory()));
                List<ModelContainer> containers = source.queryFor(ProjectUri.Dependencies.Dependency.xUri);
                int index = tmp.indexOf(getPropertyFor(ProjectUri.Dependencies.xUri, tmp));
                if (index > -1) {
                    for (ModelContainer container : containers) {
                        tmp.removeAll(container.getProperties());
                        tmp.addAll(index + 1, container.getProperties());
                    }
                }
            }

            projectNames.add(0, getPropertyFor(ProjectUri.artifactId, tmp).getValue());

            modelProperties.addAll(tmp);

            //Remove Parent Info
            for (ModelProperty mp : tmp) {
                if (mp.getUri().startsWith(ProjectUri.Parent.xUri)) {
                    modelProperties.remove(mp);
                }
            }
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

