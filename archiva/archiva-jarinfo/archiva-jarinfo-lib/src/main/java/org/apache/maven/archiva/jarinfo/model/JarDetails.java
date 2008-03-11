package org.apache.maven.archiva.jarinfo.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JarDetails
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @todo manifest?
 */
public class JarDetails
{
    private Generator generator = new Generator();

    private BytecodeDetails bytecode = new BytecodeDetails();

    private List<EntryDetail> entries = new ArrayList<EntryDetail>();

    private String filename;

    private Map<String, String> hashes = new HashMap<String, String>();

    private InspectedIds inspectedIds = new InspectedIds();

    private AssignedId assignedId = new AssignedId();

    private boolean sealed;

    private long size;

    private long sizeUncompressed;

    private Calendar timestamp;

    public void addEntry( EntryDetail entry )
    {
        this.entries.add( entry );
    }

    public BytecodeDetails getBytecode()
    {
        return bytecode;
    }

    public List<EntryDetail> getEntries()
    {
        return entries;
    }

    public String getFilename()
    {
        return filename;
    }

    public InspectedIds getInspectedIds()
    {
        return inspectedIds;
    }

    public long getSize()
    {
        return size;
    }

    public long getSizeUncompressed()
    {
        return sizeUncompressed;
    }

    public Calendar getTimestamp()
    {
        return timestamp;
    }

    public boolean isSealed()
    {
        return sealed;
    }

    public void setBytecode( BytecodeDetails bytecode )
    {
        this.bytecode = bytecode;
    }

    public void setEntries( List<EntryDetail> entries )
    {
        this.entries = entries;
    }

    public void setFilename( String filename )
    {
        this.filename = filename;
    }

    public void setInspectedIds( InspectedIds inspectedIds )
    {
        this.inspectedIds = inspectedIds;
    }

    public Map<String, String> getHashes()
    {
        return hashes;
    }

    public void setHash( String algorithm, String hash )
    {
        this.hashes.put( algorithm, hash );
    }

    public void setHashes( Map<String, String> hashes )
    {
        this.hashes = hashes;
    }

    public void setSealed( boolean sealed )
    {
        this.sealed = sealed;
    }

    public void setSize( long l )
    {
        this.size = l;
    }

    public void setSizeUncompressed( long sizeUncompressed )
    {
        this.sizeUncompressed = sizeUncompressed;
    }

    public void setTimestamp( Calendar date )
    {
        this.timestamp = date;
    }

    public Generator getGenerator()
    {
        return generator;
    }

    public void setGenerator( Generator generator )
    {
        this.generator = generator;
    }

    public AssignedId getAssignedId()
    {
        return assignedId;
    }

    public void setAssignedId( AssignedId assignedId )
    {
        this.assignedId = assignedId;
    }
}
