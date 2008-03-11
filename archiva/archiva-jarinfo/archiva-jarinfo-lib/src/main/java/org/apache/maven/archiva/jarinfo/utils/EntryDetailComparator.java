package org.apache.maven.archiva.jarinfo.utils;

import org.apache.maven.archiva.jarinfo.model.EntryDetail;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

/**
 * EntryDetailComparator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EntryDetailComparator
    implements Comparator<EntryDetail>
{
    private Collator collator = Collator.getInstance();

    public int compare( EntryDetail o1, EntryDetail o2 )
    {
        if( o1.isDirectory() && !o2.isDirectory() )
        {
            return -1;
        }
        
        if( !o1.isDirectory() && o2.isDirectory() )
        {
            return 1;
        }

        CollationKey key1 = collator.getCollationKey( o1.getName() );
        CollationKey key2 = collator.getCollationKey( o2.getName() );
        
        return key1.compareTo(key2);
    }

}
