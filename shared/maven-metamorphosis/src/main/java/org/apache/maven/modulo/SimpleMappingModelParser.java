package org.apache.maven.modulo;

import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Jason van Zyl
 */
public class SimpleMappingModelParser
    implements MappingModelParser
{
    public MappingModel parse( File mapping )
        throws MappingModelParsingException
    {
        DefaultMappingModel model = new DefaultMappingModel();

        try
        {
            String line;

            BufferedReader br = new BufferedReader( new FileReader( mapping ) );

            while ( ( line = br.readLine() ) != null )
            {
                if ( line.startsWith( "//" ) || line.startsWith( "#" ) || line.trim().length() == 0 )
                {
                    continue;
                }

                if ( line.startsWith( "groupId" ) )
                {
                    model.setGroupId( StringUtils.split( line, ":")[1].trim() );
                }
                else if ( line.startsWith( "artifactId" ))
                {
                    model.setArtifactId( StringUtils.split( line, ":")[1].trim() );
                }
                else if ( line.startsWith( "version" ))
                {
                    model.setVersion( StringUtils.split( line, ":")[1].trim() );
                }
                else if ( line.startsWith( "packaging" ))
                {
                    model.setPackaging( StringUtils.split( line, ":")[1].trim() );
                }
                else if ( line.startsWith( "jarDir" ))
                {
                    model.addJarDirectory( parseResource( line ) );
                }
                else if ( line.startsWith( "resourceDir" ))
                {
                    model.addResourceDirectory( parseResource( line ) );
                }
                else if ( line.startsWith( "srcDir" ))
                {
                    model.setSourceDirectory( StringUtils.split( line, ":")[1].trim() );
                }
                else if ( line.startsWith( "testSrcDir" ))
                {
                    model.setTestSourceDirectory( StringUtils.split( line, ":")[1].trim() );
                }
            }
        }
        catch ( FileNotFoundException e )
        {
            throw new MappingModelParsingException( "Cannot find " + mapping + "." );

        }
        catch ( IOException e )
        {
            throw new MappingModelParsingException( "Error reading the mapping " + mapping + "." );
        }

        return model;
    }

    private Resource parseResource( String line )
    {
        String[] s = StringUtils.split( StringUtils.split( line, ":")[1], "," );

        Resource r = new Resource();

        if ( s.length == 1 )
        {
            r.setDirectory( s[0].trim() );
        }
        else if ( s.length == 2 )
        {
            r.setDirectory( s[0].trim() );

            r.setIncludes( s[1].trim() );
        }
        else if ( s.length == 3 )
        {
            r.setDirectory( s[0].trim() );

            r.setIncludes( s[1].trim() );

            r.setExcludse( s[2].trim() );
        }

        return r;
    }
}
