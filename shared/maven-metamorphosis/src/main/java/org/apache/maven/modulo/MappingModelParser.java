package org.apache.maven.modulo;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public interface MappingModelParser
{
    MappingModel parse( File mapping )
        throws MappingModelParsingException;
}
