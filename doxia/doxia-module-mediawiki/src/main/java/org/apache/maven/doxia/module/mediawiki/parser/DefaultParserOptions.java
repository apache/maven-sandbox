package org.apache.maven.doxia.module.mediawiki.parser;

public class DefaultParserOptions
    implements ParsingContext
{
    
    private String baseUrl;
    private String urlSpaceToken;
    private int line = 0;
    
    public DefaultParserOptions setBaseUrl( String baseUrl )
    {
        this.baseUrl = baseUrl;
        return this;
    }

    public DefaultParserOptions setUrlSpaceToken( String urlSpaceToken )
    {
        this.urlSpaceToken = urlSpaceToken;
        return this;
    }
    
    public void incrementLine()
    {
        line++;
    }
    
    public int getLine()
    {
        return line;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public String getUrlSpaceToken()
    {
        return urlSpaceToken;
    }

}
