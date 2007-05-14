package org.apache.maven.surefire.testng;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.suite.SurefireTestSuite;

/**
 * A descriptor of the current execution environment.
 * 
 * @author <a href='mailto:the[dot]mindstorm[at]gmail[dot]com'>Alex Popescu</a>
 */
public class ExecEnv {
	private SurefireTestSuite suite;
	private ArtifactVersion version;
	private ReporterManager reportManager;

	public ExecEnv(SurefireTestSuite suite, ArtifactVersion version, ReporterManager reporter) {
		this.suite = suite;
		this.version = version;
		this.reportManager = reporter;
	}

	public SurefireTestSuite getSuite() {
		return suite;
	}

	public ArtifactVersion getVersion() {
		return version;
	}

	public ReporterManager getReportManager() {
		return reportManager;
	}
}
