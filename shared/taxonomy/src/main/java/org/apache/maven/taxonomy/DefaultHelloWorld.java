package org.apache.maven.taxonomy;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

import java.util.Map;

/**
 *
 */
public class DefaultHelloWorld
    extends ActionSupport
{
    public String execute()
        throws Exception
    {
        return SUCCESS;
    }
}
