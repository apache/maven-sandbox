package org.apache.maven.dynaweb.display.filter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RequestDumpFilter
    implements Filter
{
    
    private Logger log = LogManager.getLogger( RequestDumpFilter.class.getName() );
    
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
        throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        log.info( "Request URI: " + httpRequest.getRequestURI() );
        log.info( "Servlet Path: " + httpRequest.getServletPath() );
        log.info( "Path Info: " + httpRequest.getPathInfo() );
        
        chain.doFilter( httpRequest, response );
    }

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
    }

    public void destroy()
    {
    }

}
