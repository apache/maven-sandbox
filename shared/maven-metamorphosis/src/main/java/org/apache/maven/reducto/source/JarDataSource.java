package org.apache.maven.reducto.source;

import java.util.Set;

/**
 * An abstract source of JARs. We could look in a directory, fetch a set from a network connection,
 * pull them out of some dependency source like a POM, or anywhere else.
 *
 * @author Jason van Zyl
 */
public interface JarDataSource
{
    Set retrieveJars()
        throws JarDataSourceRetrievalException;
}
