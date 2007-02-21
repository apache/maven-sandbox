package org.apache.maven.plugin.plugit;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.plugit.tools.CodeGenerator;
import org.apache.maven.plugin.plugit.tools.ToolException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Generate JUnit wrappers for each integration-test build matched.
 * 
 * @goal junit-wrappers
 * @author jdcasey
 *
 */
public class GenerateJUnitWrapperMojo
    extends AbstractMojo
{

    private static final String JUNIT_GROUP_ID = "junit";

    private static final String JUNIT_ARTIFACT_ID = "junit";
    
    private static final String ABSTRACT_TEST_CLASS_TEMPLATE = "AbstractJUnitWrapperTest.vm";
    
    private static final String BUILD_WRAPPER_TEMPLATE = "JUnitWrapperTest.vm";

    private static final String ABSTRACT_TEST_CLASS = "AbstractJUnitWrapperTest";
    
    private static final String BUILD_WRAPPER_CLASS_BASENAME = "JUnitWrapperTest";
    
    /**
     * This is the Java package name for the generated wrapper classes.
     * 
     * @parameter expression="wrapperPackage" default-value="integrationTests"
     * @required
     */
    private String wrapperPackage;

    /**
     * List of direct dependencies of this project, to verify that junit is present.
     * 
     * @parameter default-value="${project.dependencies}"
     * @required
     * @readonly
     */
    private List dependencies;

    /**
     * Location of the integration-test project set's base directory.
     * @parameter expression="itBasedir" default-value="src/it"
     * @required
     */
    private File itBasedir;

    /**
     * List of included integration-test POM path patterns. By default, includes all poms under the
     * itBasedir directory.
     * 
     * @parameter expression="itIncludes"
     */
    private List itPomIncludes = Collections.singletonList( "**/pom.xml" );

    /**
     * List of excluded integration-test POM path patterns, such as child-projects of multimodule
     * integration tests. By default, this will exclude all poms under a child* directory structure.
     * 
     * @parameter expression="itExcludes"
     */
    private List itPomExcludes = Collections.singletonList( "**/child*/**/pom.xml" );

    /**
     * @component
     */
    private VelocityComponent velocity;

    /**
     * Source directory where generated wrapper classes will be located.
     * 
     * @parameter expression="wrapperSourceDir" default-value="${project.build.directory}/generated-sources/plug-it"
     * @required
     */
    private File wrapperSourceDir;

    // calculated.
    private CodeGenerator codeGenerator;

    private int wrapperCounter;
    
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        verifyJUnitDependencyPresence();

        String[] poms;
        try
        {
            poms = collectITPoms();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Search for IT projects failed.", e );
        }

        if ( poms != null && poms.length > 0 )
        {
            wrapperCounter = 0;
            
            initializeCodeGenerator();
            
            try
            {
                generateAbstractTestClass();
            }
            catch ( ToolException e )
            {
                throw new MojoExecutionException( "Error generating abstract test-wrapper parent class.", e );
            }

            for ( int i = 0; i < poms.length; i++ )
            {
                String pom = poms[i];

                File pomFile = new File( pom );

                if ( !pomFile.isAbsolute() )
                {
                    pomFile = new File( itBasedir, pom );
                }

                try
                {
                    generateTestWrapper( pomFile );
                }
                catch ( ToolException e )
                {
                    throw new MojoExecutionException( "Error generating test-wrapper for: " + pomFile, e );
                }
            }
        }
    }

    private void initializeCodeGenerator()
    {
        codeGenerator = new CodeGenerator( velocity, wrapperSourceDir, wrapperPackage, Collections.EMPTY_MAP );
    }

    private void generateTestWrapper( File pomFile ) throws ToolException
    {
        String wrapperName = BUILD_WRAPPER_CLASS_BASENAME + wrapperCounter++;
        
        codeGenerator.generateCode( pomFile.getPath(), wrapperName, BUILD_WRAPPER_TEMPLATE );
    }

    private void generateAbstractTestClass() throws ToolException
    {
        codeGenerator.generateCode( ABSTRACT_TEST_CLASS, ABSTRACT_TEST_CLASS_TEMPLATE );
    }

    private void verifyJUnitDependencyPresence()
        throws MojoFailureException
    {
        boolean foundJUnit = false;

        for ( Iterator it = dependencies.iterator(); it.hasNext(); )
        {
            Dependency dependency = (Dependency) it.next();

            if ( JUNIT_GROUP_ID.equals( dependency.getGroupId() )
                && JUNIT_ARTIFACT_ID.equals( dependency.getArtifactId() ) )
            {
                foundJUnit = true;
                break;
            }
        }

        if ( !foundJUnit )
        {
            throw new MojoFailureException( "plug-it:junit-wrappers", "JUnit wrapper generation failed.",
                                            "You need to add the JUnit dependency to your project, and you'll probably need to set its scope to \'test\'." );
        }
    }

    private String[] collectITPoms()
        throws IOException
    {
        final FileSet fs = new FileSet();

        fs.setIncludes( itPomIncludes );
        fs.setExcludes( itPomExcludes );
        fs.setDirectory( itBasedir.getCanonicalPath() );
        fs.setFollowSymlinks( false );
        fs.setUseDefaultExcludes( true );

        final FileSetManager fsm = new FileSetManager( getLog() );

        return fsm.getIncludedFiles( fs );
    }

}
