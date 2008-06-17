package org.apache.maven.shared.model;

/**
 * Model container actions
 */
public enum ModelContainerAction {
    /**
     * Join two containers
     */
    JOIN,

    /**
     * Delete container
     */
    DELETE,

    /**
     * No operation
     */
    NOP
}
