package org.apache.maven.doxia.module.mediawiki.parser.testutil;

import org.apache.maven.doxia.module.mediawiki.parser.ParsingContext;
import org.easymock.MockControl;

public class ParserOptionsMockAndControl
    implements MockAndControl
{

    private MockControl control;

    private ParsingContext options;

    public ParserOptionsMockAndControl()
    {
        control = MockControl.createControl( ParsingContext.class );
        options = (ParsingContext) control.getMock();
    }

    public MockControl getControl()
    {
        return control;
    }

    public ParsingContext getOptions()
    {
        return options;
    }

    public void getBaseUrl( String returnValue )
    {
        options.getBaseUrl();
        control.setReturnValue( returnValue, MockControl.ZERO_OR_MORE );
    }

    public void getUrlSpaceToken( String returnValue )
    {
        options.getUrlSpaceToken();
        control.setReturnValue( returnValue, MockControl.ZERO_OR_MORE );
    }
    
    public void incrementLine()
    {
        options.incrementLine();
        control.setVoidCallable();
    }
    
    public void getLine( int line )
    {
        options.getLine();
        control.setReturnValue( line );
    }

}
