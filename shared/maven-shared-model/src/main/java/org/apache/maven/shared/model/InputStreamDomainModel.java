package org.apache.maven.shared.model;

import java.io.InputStream;

/**
 * Provides service for obtaining input stream of domain model.
 */
public interface InputStreamDomainModel
    extends DomainModel
{

    /**
     * Returns input stream of domain model.
     *
     * @return input stream of domain model
     */
    InputStream getInputStream();
}
