package org.apache.maven.archiva.repository.content;

import java.io.IOException;

public class ArtifactException extends IOException {
	public ArtifactException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArtifactException(String message) {
		super(message);
	}
}
