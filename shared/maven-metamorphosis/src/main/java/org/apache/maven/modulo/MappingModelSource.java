package org.apache.maven.modulo;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public interface MappingModelSource
{
    MappingModel getMappingModel()
        throws MappingModelRetrievalException;    
}
