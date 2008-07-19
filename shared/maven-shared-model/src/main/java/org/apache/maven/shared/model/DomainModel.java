package org.apache.maven.shared.model;

/**
 * Extensions or implementations of this interface can be used to provide wrappers around existing models or can be
 * used to expose model elements directly. Each respective ModelTransformer implementation should know how to cast to
 * the appropriate domain model type(s).
 */
public interface DomainModel
{

    String getEventHistory();

    void setEventHistory( String history );
}
