package org.apache.maven.project;

/**
 * Defines all the unique ids for canonical data model.
 */
public enum ModelUri {
    BASE("http://apache.org/model/"),
    PROJECT("http://apache.org/model/project"),
    PROJECT_MODELVERSION("http://apache.org/model/project/modelVersion"),
    PROJECT_PREREQUSITIES_MAVEN("http://apache.org/model/project/prerequisites/maven"),
    PROJECT_ISSUEMANAGEMENT_SYSTEM("http://apache.org/model/project/issueManagement/system"),
    PROJECT_ISSUEMANAGEMENT_URL("http://apache.org/model/project/issueManagment/url"),
    PROJECT_ARTIFACTID("http://apache.org/model/project/artifactId"),
    PROJECT_BUILD("http://apache.org/model/project/build"),
    PROJECT_BUILD_TESTSOURCEDIRECTORY("http://apache.org/model/project/build/testSourceDirectory"),
    PROJECT_BUILD_OUTPUTDIRECTORY("http://apache.org/model/project/build/outputDirectory"),
    PROJECT_BUILD_SCRIPTSOURCEDIRECTORY("http://apache.org/model/project/build/scriptSourceDirectory"),
    PROJECT_BUILD_SOURCEDIRECTORY("http://apache.org/model/project/build/sourceDirectory"),
    PROJECT_DESCRIPTION("http://apache.org/model/project/description"),
    
    PROJECT_DEPENDENCIES("http://apache.org/model/project/dependencies#collection"),
    PROJECT_DEPENDENCIES_DEPENDENCY("http://apache.org/model/project/dependencies#collection/dependency"),
    PROJECT_DEPENDENCIES_DEPENDENCY_VERSION("http://apache.org/model/project/dependencies#collection/dependency/version"),
    PROJECT_DEPENDENCIES_DEPENDENCY_ARTIFACTID("http://apache.org/model/project/dependencies#collection/dependency/artifactId"),

    PROJECT_DEPENDENCYMANAGEMENT("http://apache.org/model/project/dependencyManagement"),    
    PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES("http://apache.org/model/project/dependencyManagement/dependencies#collection"),
    PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY("http://apache.org/model/project/dependencyManagement/dependencies#collection/dependency"),
    PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_VERSION("http://apache.org/model/project/dependencyManagement/dependencies#collection/dependency/version"),
    PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_ARTIFACTID("http://apache.org/model/project/dependencyManagement/dependencies#collection/dependency/artifactId"),
    PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_GROUPID("http://apache.org/model/project/dependencyManagement/dependencies#collection/dependency/groupId"),
    PROJECT_DEPENDENCYMANAGEMENT_DEPENDENCIES_DEPENDENCY_SCOPE("http://apache.org/model/project/dependencyManagement/dependencies#collection/dependency/scope"),

    PROJECT_GROUPID("http://apache.org/model/project/groupId"),
    PROJECT_ISSUEMANAGEMENT("http://apache.org/model/project/issueManagement"),
    PROJECT_NAME("http://apache.org/model/project/name"),
    PROJECT_MODULES("http://apache.org/model/project/modules#collection"),
    PROJECT_PACKAGING("http://apache.org/model/packaging"),
    PROJECT_PROFILES("http://apache.org/model/project/profiles#collection"),
    PROJECT_PROPERTIES("http://apache.org/model/project/properties#collection"),
    PROJECT_PREREQUISITES("http://apache.org/model/project/prerequisites"),
    PROJECT_URL("http://apache.org/model/project/url"),
    PROJECT_VERSION("http://apache.org/model/project/version");

    private String uri;

    ModelUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public static boolean matches(ModelUri domainUri, String uri) {
        return uri.equals(domainUri.getUri());
    }

    public ModelProperty getModelProperty() {
        return new ModelProperty(uri, null);
    }
}
