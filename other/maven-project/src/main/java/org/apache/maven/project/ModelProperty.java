package org.apache.maven.project;

import java.util.UUID;

public class ModelProperty {

    private String uri;

    private String value;

    private UUID id;

    /**
     *
     * @param uri
     * @param value Value associated with specified uri. Value may be null if uri does not map to primitive type.
     */
    public ModelProperty(String uri, String value) {
        this(uri, value, UUID.randomUUID());
    }

    public ModelProperty(String uri, String value, String id) {
        this(uri, value,  UUID.fromString(id));
    }

    public String toString() {
        return "Uri = " + uri + ", Value = " + value;
    }

    public ModelProperty(String uri, String value, UUID id) {
        if(uri == null) {
            throw new IllegalArgumentException("uri");
        }

        if(id  == null) {
            throw new IllegalArgumentException("id") ;
        }
        
        this.uri = uri;
        this.value = value;
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public String getValue() {
        return value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelProperty that = (ModelProperty) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
