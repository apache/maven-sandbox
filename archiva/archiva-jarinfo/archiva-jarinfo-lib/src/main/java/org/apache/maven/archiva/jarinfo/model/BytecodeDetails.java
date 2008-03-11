package org.apache.maven.archiva.jarinfo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BytecodeDetails
{
    private Map<String, String> hashes = new HashMap<String, String>();

    private boolean debug = false;

    private String requiredJdk;

    private List<ClassDetail> classes = new ArrayList<ClassDetail>();

    public List<ClassDetail> getClasses()
    {
        return classes;
    }

    public String getRequiredJdk()
    {
        return requiredJdk;
    }

    public boolean hasDebug()
    {
        return debug;
    }

    public void addClass( ClassDetail detail )
    {
        this.classes.add( detail );
    }

    public void setClasses( List<ClassDetail> classes )
    {
        this.classes = classes;
    }

    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }

    public void setHash( String algorithm, String hash )
    {
        this.hashes.put( algorithm, hash );
    }

    public void setRequiredJdk( String requiredJdk )
    {
        this.requiredJdk = requiredJdk;
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
