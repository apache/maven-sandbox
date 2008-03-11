package org.apache.maven.archiva.jarinfo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDetail
{
    private Map<String, String> hashes = new HashMap<String, String>();

    private String name;

    private String targetJdk;

    private String classVersion;

    private boolean debug = false;

    private List<String> methods = new ArrayList<String>();

    private List<String> imports = new ArrayList<String>();
    
    public String getPackage()
    {
        String ret = name;
        int idx = ret.lastIndexOf( '.' );
        if ( idx >= 0 )
        {
            ret = ret.substring( 0, idx );
        }

        return ret;
    }

    public void addImport( String importName )
    {
        this.imports.add( importName );
    }

    public void addMethod( String method )
    {
        this.methods.add( method );
    }

    public String getClassVersion()
    {
        return classVersion;
    }

    

    public List<String> getImports()
    {
        return imports;
    }

    public List<String> getMethods()
    {
        return methods;
    }

    public String getName()
    {
        return name;
    }

    public String getTargetJdk()
    {
        return targetJdk;
    }

    public boolean hasDebug()
    {
        return debug;
    }

    public void setClassVersion( String classVersion )
    {
        this.classVersion = classVersion;
    }

    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }

    public void setHash( String algorithm, String hash )
    {
        this.hashes.put( algorithm, hash );
    }

    public void setImports( List<String> imports )
    {
        this.imports = imports;
    }

    public void setMethods( List<String> methods )
    {
        this.methods = methods;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setTargetJdk( String targetJdk )
    {
        this.targetJdk = targetJdk;
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
