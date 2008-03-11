package org.apache.maven.archiva.jarinfo.utils;

import java.util.Collection;
import java.util.Map;

/**
 * EmptyUtils 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EmptyUtils
{
    public static boolean isEmpty( String text )
    {
        if ( text == null )
        {
            return true;
        }

        return ( text.trim().length() <= 0 );
    }

    public static boolean isEmpty( Collection<?> coll )
    {
        if ( coll == null )
        {
            return true;
        }

        return ( coll.size() <= 0 );
    }

    public static boolean isEmpty( Map<?, ?> map )
    {
        if ( map == null )
        {
            return true;
        }

        return ( map.size() <= 0 );
    }
}
