package org.apache.maven.shared.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Maps a URI to a string value, which may be null. This class is immutable.
 */
public final class ModelProperty {

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{(pom\\.|project\\.|env\\.)?([^}]+)\\}");

    /**
     * URI of the resource
     */
    private final String uri;

    /**
     * Value associated with the uri
     */
    private final String value;

    private String resolvedValue;

    private final List<String> expressions;

    /**
     * Constructor
     *
     * @param uri   URI of the resource. May not be null
     * @param value Value associated with specified uri. Value may be null if uri does not map to primitive type.
     */
    public ModelProperty(String uri, String value) {
        if (uri == null) {
            throw new IllegalArgumentException("uri");
        }
        this.uri = uri;
        this.value = value;
        resolvedValue = value;

        expressions = new ArrayList<String>();
        if (value != null) {
            Matcher matcher = EXPRESSION_PATTERN.matcher(value);
            while (matcher.find()) {
                expressions.add(matcher.group(0));
            }
        }
    }

    /**
     * Returns URI key
     *
     * @return URI key
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns value for the key. Value may be null.
     *
     * @return value for the key. Value may be null.
     */
    public String getValue() {
        return value;
    }

    public String getResolvedValue() {
        return resolvedValue;
    }

    public boolean isResolved() {
        return expressions.isEmpty();
    }

    public ModelProperty createCopyOfOriginal() {
        return new ModelProperty(uri, value);
    }

    public InterpolatorProperty asInterpolatorProperty(String baseUri) {
        if(uri.contains("#collection") || value == null) {
            return null;
        }
        String key = "${" + uri.replace(baseUri +"/", "").replace("/", ".") + "}";
        return new InterpolatorProperty(key, value);
    }

    public void resolveWith(InterpolatorProperty property) {
        if (property == null) {
            throw new IllegalArgumentException("property: null");
        }
        if (isResolved()) {
            return;
        }
        boolean isModified = false;
        for (String expression : expressions) {
            if (property.getKey().equals(expression)) {
                resolvedValue = resolvedValue.replace(property.getKey(), property.getValue());
                isModified = true;
                break;
            }
        }
        if (isModified) {
            expressions.clear();
            Matcher matcher = EXPRESSION_PATTERN.matcher(value);
            while (matcher.find()) {
                expressions.add(matcher.group(0));
            }
        }
    }

    public String toString() {
        return "Uri = " + uri + ", Value = " + value + ", Resolved Value = " + resolvedValue;
    }
}