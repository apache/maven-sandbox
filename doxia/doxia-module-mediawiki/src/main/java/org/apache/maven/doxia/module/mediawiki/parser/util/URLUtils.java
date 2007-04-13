package org.apache.maven.doxia.module.mediawiki.parser.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

public final class URLUtils
{
    
    private URLUtils()
    {
    }
    
    public static boolean isAbsolute( String url )
    {
        return url.matches( "[a-zA-Z0-9]+:/?/?.+" );
    }
    
    public static String appendToBaseURL( String baseUrl, String path )
    {
        // null checks; don't try to concat if one or both is null.
        if ( path == null )
        {
            return baseUrl;
        }
        else if ( baseUrl == null )
        {
            return path;
        }
        
        boolean baseWithSlash = baseUrl.endsWith( "/" );
        boolean pathWithSlash = path.startsWith( "/" );
        
        // basic concat.
        String result = null;
        if ( baseWithSlash && !pathWithSlash )
        {
            result = baseUrl + path;
        }
        else if ( !baseWithSlash && pathWithSlash )
        {
            result = baseUrl + path;
        }
        else if ( baseWithSlash && pathWithSlash )
        {
            if ( path.length() > 1 )
            {
                result = baseUrl + path.substring( 1 );
            }
            else
            {
                result = baseUrl;
            }
        }
        else
        {
            result = baseUrl + "/" + path;
        }
        
        // '.' and '..' replacement...
        result = result.replaceAll( "\\/\\.\\/", "/" );
        result = adjustForDoubleDotPathParts( result );
        
        return result;
    }

    private static String adjustForDoubleDotPathParts( String input )
    {
        // trim off the protocol, save it for later.
        String proto = "";
        if ( isAbsolute( input ) )
        {
            int trimIdx = input.indexOf( ":" ) + 1;
            proto = input.substring( 0, trimIdx );
            input = input.substring( trimIdx );
            
            while ( input.startsWith( "/" ) )
            {
                proto += "/";
                
                if ( input.length() > 1 )
                {
                    input = input.substring( 1 );
                }
                else
                {
                    input = "";
                }
            }
        }
        
        LinkedList pathParts = new LinkedList();
        StringTokenizer tokens = new StringTokenizer( input, "/" );
        
        while( tokens.hasMoreTokens() )
        {
            String token = tokens.nextToken();
            
            if ( "..".equals( token.trim() ) )
            {
                pathParts.removeLast();
            }
            else
            {
                pathParts.add( token );
            }
        }
        
        StringBuffer resultBuffer = new StringBuffer();
        for ( Iterator it = pathParts.iterator(); it.hasNext(); )
        {
            String part = (String) it.next();
            
            resultBuffer.append( part );
            if ( it.hasNext() )
            {
                resultBuffer.append( "/" );
            }
        }
        
        return proto + resultBuffer.toString();
    }

}
