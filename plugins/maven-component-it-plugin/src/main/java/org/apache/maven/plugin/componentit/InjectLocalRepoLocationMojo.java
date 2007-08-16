package org.apache.maven.plugin.componentit;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.util.Properties;

/**
 * @goal inject-local-repository-path
 * @author jdcasey
 *
 */
public class InjectLocalRepoLocationMojo
    extends AbstractMojo
{

    /**
     * @parameter default-value="${localRepositoryPath}"
     * @required
     */
    private String property;

    /**
     * @parameter default-value="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        Properties properties = project.getProperties();

        if ( properties == null )
        {
            properties = new Properties();
        }

        String localRepoPath = localRepository.getBasedir();

        getLog().info( "Injecting local-repository path: " + localRepoPath + " into project properties under key: " + property );

        properties.setProperty( property, localRepoPath );
        project.getModel().setProperties( properties );
    }

}
