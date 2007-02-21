package org.apache.maven.issue.jira.project;

import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public class CsvFileProjectRecordSource
    implements ProjectRecordSource
{
    private File source;

    private Map variables;

    public CsvFileProjectRecordSource( File source )
    {
        this.source = source;

        variables = new HashMap();
    }

    public Iterator getRecords()
        throws ProjectRecordRetrievalException
    {
        try
        {
            return new RecordIterator( source );
        }
        catch ( FileNotFoundException e )
        {
            throw new ProjectRecordRetrievalException( "Cannot find source file: " + source );
        }
    }

    class RecordIterator
        implements Iterator
    {
        private BufferedReader reader;

        private String line;

        public RecordIterator( File source )
            throws FileNotFoundException
        {
            reader = new BufferedReader( new FileReader( source ) );
        }

        public boolean hasNext()
        {
            try
            {
                while ( ( line = reader.readLine() ) != null )
                {
                    if ( line.indexOf( "=" ) > 0 )
                    {
                        String[] s = StringUtils.split( line, "=" );

                        String key = s[0].trim();

                        String value = s[1].trim();

                        variables.put( key, value );

                        continue;
                    }
                    else if ( line.startsWith( "#" ) || line.trim().length() == 0 )
                    {
                        continue;
                    }

                    break;
                }
            }
            catch ( IOException e )
            {
                // do nothing
            }

            return line != null;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public Object next()
        {
            String[] s = StringUtils.split( StringUtils.interpolate( line, variables ), "," );

            return new ProjectRecord( s[0], s[1], s[2], s[3], s[4], s[5] );
        }
    }
}

