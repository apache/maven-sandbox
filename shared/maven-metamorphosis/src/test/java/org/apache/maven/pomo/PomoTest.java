package org.apache.maven.pomo;

import org.apache.maven.model.Model;
import org.apache.maven.reducto.Reducto;
import org.apache.maven.reducto.source.DirectoryJarDataSource;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * @author Jason van Zyl
 */
public class PomoTest
    extends PlexusTestCase
{
    public void testPomGenerationFromJarAnalysis()
        throws Exception
    {
        Reducto r = (Reducto) lookup( Reducto.ROLE );

        Map analyses =
            r.analyse( new DirectoryJarDataSource( new File( getBasedir(), "src/test/jar-sets/multiple" ) ) );

        Pomo p = (Pomo) lookup( Pomo.ROLE );

        PomInfoSource source = new SimplePomInfoSource( "4.0.0", "foo", "bar", "1.0" );

        Model m = p.generateModel( source, analyses );

        File f = new File( "target/pomo-test", "testPom.xml" );

        f.getParentFile().mkdirs();

        FileWriter w = new FileWriter( f );

        p.writeModel( w, m );
    }
}
