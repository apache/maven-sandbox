package org.apache.maven.doxia.module.mediawiki.parser.state;

public interface StateFactory
{
    
    StateParser getState( CharSequence lookahead );

}
