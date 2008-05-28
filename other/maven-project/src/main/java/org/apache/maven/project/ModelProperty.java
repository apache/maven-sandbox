package org.apache.maven.project;

import java.util.UUID;

/**
 * Maps a URI to a string value, which may be null.
 */
public class ModelProperty {

    private String uri;

    private String value;

    private UUID id;

    /**
     *
     * @param uri URI of the resource
     * @param value Value associated with specified uri. Value may be null if uri does not map to primitive type.
     */
    public ModelProperty(String uri, String value) {
        if(uri == null) {
            throw new IllegalArgumentException("uri");
        }
        this.uri = uri;
        this.value = value;
        //String x = uri;
   //     if(value != null) {
   //         x = x + value;
   //     }
//        id = UUID.fromString(x);
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
}
