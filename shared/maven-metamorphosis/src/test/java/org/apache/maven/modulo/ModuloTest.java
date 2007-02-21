package org.apache.maven.modulo;

import org.codehaus.plexus.PlexusTestCase;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ModuloTest
    extends PlexusTestCase
{
    public void testSimpleProjectConversionUsingASimpleMappingModel()
        throws Exception
    {
        Modulo m = (Modulo) lookup( Modulo.ROLE );

        assertNotNull( m );

        File sourceDirectory = new File( getBasedir(), "src/test/modules/basic" );

        File mappingFile = new File( sourceDirectory, "map.txt" );

        File outputDirectory = new File( getBasedir(), "target/basic-conversion" );

        m.convert( sourceDirectory, new SimpleMappingModelSource( mappingFile ), outputDirectory );
    }
}
