package org.apache.maven.shared.model;

import java.io.IOException;

public class DataSourceException extends IOException {
    static final long serialVersionUID = 8738495672439L;

    public DataSourceException() {
        super();
    }

    public DataSourceException(String message) {
        super(message);
    }

    public DataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceException(Throwable cause) {
        super(cause);
    }
}
