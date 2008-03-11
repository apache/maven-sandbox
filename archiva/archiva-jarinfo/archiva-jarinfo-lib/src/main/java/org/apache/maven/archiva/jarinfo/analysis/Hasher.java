package org.apache.maven.archiva.jarinfo.analysis;

import org.apache.maven.archiva.jarinfo.utils.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Hasher - simple hashing routines. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class Hasher
{
    protected final MessageDigest md;

    public static final String MD5 = "md5";

    public static final String SHA1 = "sha-1";

    private static final int BUFFER_SIZE = 32768;
    
    private String algorithm;

    public Hasher( String algorithm )
    {
        this.algorithm = algorithm;
        try
        {
            md = MessageDigest.getInstance( algorithm );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( "Unable to initialize MessageDigest algorithm " + algorithm + " : "
                + e.getMessage(), e );
        }
    }

    public Hasher update( InputStream stream )
        throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        int size = stream.read( buffer, 0, BUFFER_SIZE );
        while ( size >= 0 )
        {
            md.update( buffer, 0, size );
            size = stream.read( buffer, 0, BUFFER_SIZE );
        }

        return this;
    }

    public Hasher update( byte[] buffer, int offset, int size )
    {
        md.update( buffer, 0, size );
        return this;
    }

    public static void update( List<Hasher> hashers, InputStream stream )
        throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        int size = stream.read( buffer, 0, BUFFER_SIZE );
        while ( size >= 0 )
        {
            for ( Hasher hasher : hashers )
            {
                hasher.update( buffer, 0, size );
            }
            size = stream.read( buffer, 0, BUFFER_SIZE );
        }
    }

    public void reset()
    {
        md.reset();
    }

    public String getHash()
    {
        return Hex.encode( md.digest() );
    }

    public String getAlgorithm()
    {
        return algorithm;
    }

    public void setAlgorithm( String algorithm )
    {
        this.algorithm = algorithm;
    }
}
