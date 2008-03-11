package org.apache.maven.archiva.jarinfo.utils;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

/**
 * NaturalLanguageComparator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class NaturalLanguageComparator
    implements Comparator<String>
{
    private Collator collator = Collator.getInstance();

    public int compare( String o1, String o2 )
    {
        CollationKey key1 = collator.getCollationKey( o1 );
        CollationKey key2 = collator.getCollationKey( o2 );

        return key1.compareTo( key2 );
    }

}
