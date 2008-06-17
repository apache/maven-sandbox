package org.apache.maven.shared.model;

public final class InterpolatorProperty {

    private final String key;

    private final String value;

    public InterpolatorProperty(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key: null");
        }

        if (value == null) {
            throw new IllegalArgumentException("value: null");
        }
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterpolatorProperty that = (InterpolatorProperty) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    public int hashCode() {
        return key.hashCode();
    }

}
