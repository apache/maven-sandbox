package org.apache.maven.doxia.module.mediawiki.parser;

public interface ParsingContext
{

    String getUrlSpaceToken();

    String getBaseUrl();

    void incrementLine();
    
    int getLine();

}
