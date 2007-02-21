package org.apache.maven.modulo;

import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.util.Set;
import java.util.Iterator;

/**
 * @author Jason van Zyl
 */
public class SimpleMappingModelParserTest
    extends PlexusTestCase
{
    public void testModelParsing()
        throws Exception
    {
        MappingModelParser p = new SimpleMappingModelParser();

        MappingModel m = p.parse( new File( getBasedir(), "src/test/modules/basic/map.txt" ) );

        assertEquals( "no.bbs", m.getGroupId() );

        assertEquals( "bbs-artifact", m.getArtifactId() );

        assertEquals( "1.0-SNAPSHOT", m.getVersion() );

        assertEquals( "ejb", m.getPackaging() );

        assertEquals( "src", m.getSourceDirectory() );

        assertEquals( "unittest", m.getTestSourceDirectory() );

        Set jarDirectories = m.getJarDirectories();

        assertEquals( 1, jarDirectories.size() );

        Resource jd = (Resource) jarDirectories.iterator().next();

        assertEquals( "lib", jd.getDirectory() );

        Set resourceDirectories = m.getResourceDirectories();

        assertEquals( 3, resourceDirectories.size() );

        Iterator i = resourceDirectories.iterator();

        Resource r0 = (Resource) i.next();

        assertEquals( "src/META-INF", r0.getDirectory() );

        Resource r1 = (Resource) i.next();

        assertEquals( "etc", r1.getDirectory() );

        Resource r2 = (Resource) i.next();

        assertEquals( "src", r2.getDirectory() );
    }
}
