package org.apache.maven.modulo;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class SimpleMappingModelSource
    implements MappingModelSource
{
    private File mappingFile;

    public SimpleMappingModelSource( File mappingFile )
    {
        this.mappingFile = mappingFile;
    }

    public MappingModel getMappingModel()
        throws MappingModelRetrievalException
    {
        MappingModelParser p = new SimpleMappingModelParser();

        try
        {
            return p.parse( mappingFile );
        }
        catch ( MappingModelParsingException e )
        {
            throw new MappingModelRetrievalException( "Error parsing mapping file " + mappingFile + "." );
        }
    }
}
