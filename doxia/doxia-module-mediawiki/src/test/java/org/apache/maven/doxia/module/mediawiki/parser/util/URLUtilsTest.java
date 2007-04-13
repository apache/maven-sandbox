package org.apache.maven.doxia.module.mediawiki.parser.util;

import junit.framework.TestCase;

public class URLUtilsTest
    extends TestCase
{
    
    public void testIsAbsolute_RelativeURL_RootOriented()
    {
        assertFalse( URLUtils.isAbsolute( "/other.html" ) );
    }

    public void testIsAbsolute_RelativeURL_LocalOriented()
    {
        assertFalse( URLUtils.isAbsolute( "index.html" ) );
    }

    public void testIsAbsolute_RelativeURL_RootOrientedNoExtension()
    {
        assertFalse( URLUtils.isAbsolute( "/app" ) );
    }

    public void testIsAbsolute_RelativeURL_LocalOrientedNoExtension()
    {
        assertFalse( URLUtils.isAbsolute( "app" ) );
    }

    public void testIsAbsolute_AbsoluteURL()
    {
        assertTrue( URLUtils.isAbsolute( "http://www.google.com/other.html" ) );
    }

    public void testIsAbsolute_AbsoluteURL_NoExtension()
    {
        assertTrue( URLUtils.isAbsolute( "http://www.google.com/app" ) );
    }

    public void testIsAbsolute_MailtoURL()
    {
        assertTrue( URLUtils.isAbsolute( "mailto:nobody@nowhere.com" ) );
    }
    
    public void testAppendToBaseURL_BaseUrlWithTrailingSlash_PathWithLeadingSlash()
    {
        String base = "http://www.google.com/";
        String path = "/index.html";
        
        assertEquals( "http://www.google.com/index.html", URLUtils.appendToBaseURL( base, path ) );
    }

    public void testAppendToBaseURL_BaseUrlWithoutTrailingSlash_PathWithLeadingSlash()
    {
        String base = "http://www.google.com";
        String path = "/index.html";
        
        assertEquals( "http://www.google.com/index.html", URLUtils.appendToBaseURL( base, path ) );
    }

    public void testAppendToBaseURL_BaseUrlWithTrailingSlash_PathWithoutLeadingSlash()
    {
        String base = "http://www.google.com/";
        String path = "index.html";
        
        assertEquals( "http://www.google.com/index.html", URLUtils.appendToBaseURL( base, path ) );
    }

    public void testAppendToBaseURL_BaseUrlWithoutTrailingSlash_PathWithoutLeadingSlash()
    {
        String base = "http://www.google.com";
        String path = "index.html";
        
        assertEquals( "http://www.google.com/index.html", URLUtils.appendToBaseURL( base, path ) );
    }

    public void testAppendToBaseURL_NullBaseUrl()
    {
        String path = "index.html";
        
        assertEquals( path, URLUtils.appendToBaseURL( null, path ) );
    }

    public void testAppendToBaseURL_NullPath()
    {
        String base = "http://www.google.com";
        
        assertEquals( base, URLUtils.appendToBaseURL( base, null ) );
    }

    public void testAppendToBaseURL_RemoveSingleDotPathParts()
    {
        String base = "http://www.google.com/";
        String path = "./index.html";
        
        assertEquals( "http://www.google.com/index.html", URLUtils.appendToBaseURL( base, path ) );
    }

    public void testAppendToBaseURL_AdjustForDoubleDotPathParts()
    {
        String base = "http://www.google.com/app/";
        String path = "../index.html";
        
        assertEquals( "http://www.google.com/index.html", URLUtils.appendToBaseURL( base, path ) );
    }

}
