package org.apache.maven.dynaweb.display;

public interface DisplayConstants
{

    String MASTER_PLEXUS_CONTAINER_KEY = "dynaweb-container";
    
    String DOCUMENT_ROOT_PARAM_PREFIX = "document-root:";
    
    String SITE_DESCRIPTOR_PATH_INIT_PARAM = "site-descriptor";
    
    String DOCUMENT_LOADER_ROLE_HINT_PARAM = "document-loader";

    String SITE_DESCRIPTOR_PATH_INIT_PARAM_DEFAULT = "/WEB-INF/site.xml";

    String SKIN_JAR_PARAM = "skin-jar";

    String SKIN_MANAGER_ROLE_HINT_PARAM = "skin-manager";

    String FILTERED_EXTENSIONS_PARAM = "filtered-extensions";

    String PATH_WITH_EXTENSION_PATTERN = ".+\\.[a-zA-Z0-9]+";

}
