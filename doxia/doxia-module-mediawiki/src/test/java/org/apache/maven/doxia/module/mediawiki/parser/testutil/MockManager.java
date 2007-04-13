package org.apache.maven.doxia.module.mediawiki.parser.testutil;

import org.easymock.MockControl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MockManager
{
    
    private Set controls = new HashSet();
    
    public void register( MockControl control )
    {
        controls.add( control );
    }
    
    public void register( MockAndControl mac )
    {
        controls.add( mac.getControl() );
    }
    
    public void replayAll()
    {
        for ( Iterator it = controls.iterator(); it.hasNext(); )
        {
            MockControl control = (MockControl) it.next();
            
            control.replay();
        }
    }
    
    public void resetAll()
    {
        for ( Iterator it = controls.iterator(); it.hasNext(); )
        {
            MockControl control = (MockControl) it.next();
            
            control.reset();
        }
    }

    public void verifyAll()
    {
        for ( Iterator it = controls.iterator(); it.hasNext(); )
        {
            MockControl control = (MockControl) it.next();
            
            control.verify();
        }
    }

}
