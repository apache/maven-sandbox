package org.apache.maven.reducto;

import org.apache.maven.reducto.analysis.JarAnalysisResult;
import org.apache.maven.reducto.source.DirectoryJarDataSource;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.util.Map;

/**
 * @author Jason van Zyl
 */
public class ReductoTest
    extends PlexusTestCase
{
    public void testDirectoryAnalysisWithASingleJar()
        throws Exception
    {
        Reducto r = (Reducto) lookup( Reducto.ROLE );

        assertNotNull( r );

        Map analyses = r.analyse( new DirectoryJarDataSource( new File( getBasedir(), "src/test/jar-sets/single" ) ) );

        assertTrue( analyses.size() == 1 );

        JarAnalysisResult result = (JarAnalysisResult) analyses.values().iterator().next();

        //     groupId: commons-beanutils
        //  artifactId: commons-beanutils
        //     version: 1.7.0

        assertTrue( result.isResolved() );

        assertEquals( "0f18acf5fa857f9959675e14d901a7ce", result.getMd5Checksum() );

        assertEquals( "commons-beanutils", result.getGroupId() );

        assertEquals( "commons-beanutils", result.getArtifactId() );

        assertEquals( "1.7.0", result.getVersion() );
    }

    public void testDirectoryAnalysisWithMultipleJars()
        throws Exception
    {
        Reducto r = (Reducto) lookup( Reducto.ROLE );

        assertNotNull( r );

        Map analyses = r.analyse( new DirectoryJarDataSource( new File( getBasedir(), "src/test/jar-sets/multiple" ) ) );

        assertTrue( analyses.size() == 3 );

        JarAnalysisResult r0 = (JarAnalysisResult) analyses.get( r.createKey( "commons-codec", "commons-codec", "1.3" ) );

        assertNotNull( r0 );

        assertEquals( "8e149c1053741c03736a52df83974dcc", r0.getMd5Checksum() );

        JarAnalysisResult r1 = (JarAnalysisResult) analyses.get( r.createKey( "commons-io", "commons-io", "1.1" ) );

        assertNotNull( r1 );

        assertEquals( "ffd27d70f20214e49f9b796bf6fe32f4", r1.getMd5Checksum() );

        JarAnalysisResult r2 = (JarAnalysisResult) analyses.get( r.createKey( "commons-pool", "commons-pool", "1.0.1" ) );

        assertNotNull( r2 );

        assertEquals( "6d55437b18d48ee331e9766f798d145c", r2.getMd5Checksum() );
    }
}
