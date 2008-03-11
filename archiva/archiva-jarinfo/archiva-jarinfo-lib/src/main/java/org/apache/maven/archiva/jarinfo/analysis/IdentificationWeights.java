package org.apache.maven.archiva.jarinfo.analysis;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * IdentificationWeights 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class IdentificationWeights
{
    /* singleton */
    private static final IdentificationWeights _INSTANCE = new IdentificationWeights();

    public static IdentificationWeights getInstance()
    {
        return _INSTANCE;
    }

    private Map<String, Integer> weights = new HashMap<String, Integer>();

    /* TODO Load from resource.
     * TODO Allow overwrite from runtime.
     */

    public IdentificationWeights()
    {
        try
        {
            URL url = this.getClass().getResource( "idweights.properties" );
            if ( url == null )
            {
                // TODO log error.
                return;
            }
            Properties props = new Properties();
            props.load( url.openStream() );

            Iterator<?> it = props.keySet().iterator();
            while ( it.hasNext() )
            {
                String key = (String) it.next();
                Integer weight = toWeight( props.getProperty( key ) );
                if ( weight != null )
                {
                    weights.put( key, weight );
                }
            }
        }
        catch ( IOException e )
        {
            // TODO log error
        }
    }

    private Integer toWeight( String val )
    {
        try
        {
            return Integer.valueOf( val );
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

    public int getWeight( String key )
    {
        Integer weight = weights.get( key );
        if ( weight == null )
        {
            // TODO log complaint 
            return 1;
        }
        return weight.intValue();
    }
}
