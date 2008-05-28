package org.apache.maven.project;

import java.util.List;

/**
 * Process the list of properties in some way: inserting version values (dependency management), removing properties that
 * have been overriden, copying info from profiles to repsective parts of pom, interpolation etc.
 */
public interface ModelProcessor {
    
    void process(List<ModelProperty> list);
}
