package org.apache.maven.archiva.jarinfo.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EntryDetail
{
    private String name;

    private Map<String, String> hashes = new HashMap<String, String>();

    private Calendar timestamp;

    private long size;

    private boolean directory;

    public Calendar getTimestamp()
    {
        return timestamp;
    }

    public String getName()
    {
        return name;
    }

    public long getSize()
    {
        return size;
    }

    public boolean isDirectory()
    {
        return directory;
    }

    public void setTimestamp( Calendar date )
    {
        this.timestamp = date;
    }

    public void setDirectory( boolean directory )
    {
        this.directory = directory;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setSize( long size )
    {
        this.size = size;
    }

    public void setHash( String algorithm, String hash )
    {
        this.hashes.put( algorithm, hash );
    }

    public Map<String, String> getHashes()
    {
        return hashes;
    }

    public void setHashes( Map<String, String> hashes )
    {
        this.hashes = hashes;
    }
}
