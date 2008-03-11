package org.apache.maven.archiva.jarinfo.utils;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

import org.apache.maven.archiva.jarinfo.model.IdValue;

public class IdValueComparator
    implements Comparator<IdValue>
{
    private Collator collator = Collator.getInstance();

    public int compare( IdValue o1, IdValue o2 )
    {
        if ( o1.getWeight() < o2.getWeight() )
        {
            return 1;
        }

        if ( o1.getWeight() > o2.getWeight() )
        {
            return -1;
        }

        CollationKey key1 = collator.getCollationKey( o1.getValue() );
        CollationKey key2 = collator.getCollationKey( o2.getValue() );

        return key1.compareTo( key2 );
    }

}
