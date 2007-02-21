package org.apache.maven.dynaweb.cms.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class HttpCMSRequest
    implements CMSRequest
{
    
    private final HttpServletRequest request;

    public HttpCMSRequest( HttpServletRequest request )
    {
        this.request = request;
    }

    public List<String> getPath()
    {
        String servletPath = request.getServletPath();
        
        String[] pathElements = servletPath.split( "\\/" );
        
        List<String> path = new ArrayList<String>( Arrays.asList( pathElements ) );
        
        if ( servletPath.endsWith( "/" ) )
        {
            path.add( "index.html" );
        }
        
        for ( Iterator<String> pathIterator = path.iterator(); pathIterator.hasNext(); )
        {
            String pathElement = pathIterator.next();
            
            if ( pathElement == null || pathElement.length() < 1 )
            {
                pathIterator.remove();
            }
        }
        
        return path;
    }

}
