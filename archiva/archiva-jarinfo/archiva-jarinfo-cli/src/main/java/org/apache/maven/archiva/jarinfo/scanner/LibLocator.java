package org.apache.maven.archiva.jarinfo.scanner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class LibLocator
{
    private static final String APP_HOME_PROPERTY = "app.home";

    /** Debug Flag. */
    public static boolean debug = false;

    private List<URL> classpathUrls;

    private File appHomeDir;

    /**
     * Create <code>LibLocator</code>
     */
    public LibLocator()
    {
        this.classpathUrls = new ArrayList<URL>();
        initAppHome();
        addLib( "lib" ); // default lib entry.
    }

    /**
     * Constructs a file path from a <code>file:</code> URI.
     *
     * <p>Will be an absolute path if the given URI is absolute.</p>
     *
     * <p>Swallows '%' that are not followed by two characters,
     * doesn't deal with non-ASCII characters.</p>
     *
     * @param uri the URI designating a file in the local filesystem.
     * @return the local file system path for the file.
     * @since Ant 1.6
     */
    public String fromURI( String uri )
    {
        URL url = null;
        try
        {
            url = new URL( uri );
        }
        catch ( MalformedURLException ignore )
        {
            /* ignore */
        }
        if ( url == null || !( "file".equals( url.getProtocol() ) ) )
        {
            throw new IllegalArgumentException( "Can only handle valid file: URIs" );
        }
        StringBuffer buf = new StringBuffer( url.getHost() );
        if ( buf.length() > 0 )
        {
            buf.insert( 0, File.separatorChar ).insert( 0, File.separatorChar );
        }

        String file = url.getFile();
        int queryPos = file.indexOf( '?' );
        buf.append( ( queryPos < 0 ) ? file : file.substring( 0, queryPos ) );

        String tmpUri = buf.toString().replace( '/', File.separatorChar );

        if ( File.pathSeparatorChar == ';' && uri.startsWith( "\\" ) && uri.length() > 2
            && Character.isLetter( uri.charAt( 1 ) ) && uri.lastIndexOf( ':' ) > -1 )
        {
            tmpUri = uri.substring( 1 );
        }

        String path = decodeUri( tmpUri );
        return path;
    }

    /**
     * Decodes an Uri with % characters.
     * @param uri String with the uri possibly containing % characters.
     * @return The decoded Uri
     */
    private String decodeUri( String uri )
    {
        if ( uri.indexOf( '%' ) == -1 )
        {
            return uri;
        }
        StringBuffer sb = new StringBuffer();
        CharacterIterator iter = new StringCharacterIterator( uri );
        for ( char c = iter.first(); c != CharacterIterator.DONE; c = iter.next() )
        {
            if ( c == '%' )
            {
                char c1 = iter.next();
                if ( c1 != CharacterIterator.DONE )
                {
                    int i1 = Character.digit( c1, 16 );
                    char c2 = iter.next();
                    if ( c2 != CharacterIterator.DONE )
                    {
                        int i2 = Character.digit( c2, 16 );
                        sb.append( (char) ( ( i1 << 4 ) + i2 ) );
                    }
                }
            }
            else
            {
                sb.append( c );
            }
        }
        String path = sb.toString();
        return path;
    }

    /**
     * Get the URLs being tracked.
     * 
     * @return the URL array.
     */
    public URL[] getUrls()
    {
        // now update the class.path property
        StringBuffer baseClassPath = new StringBuffer( System.getProperty( "java.class.path" ) );
        if ( baseClassPath.charAt( baseClassPath.length() - 1 ) == File.pathSeparatorChar )
        {
            baseClassPath.setLength( baseClassPath.length() - 1 );
        }

        URL[] jars = this.classpathUrls.toArray( new URL[0] );

        for ( int i = 0; i < jars.length; ++i )
        {
            baseClassPath.append( File.pathSeparatorChar );
            baseClassPath.append( fromURI( jars[i].toString() ) );
        }

        System.setProperty( "java.class.path", baseClassPath.toString() );

        return jars;
    }

    /**
     * Initialize the app.home system property.
     */
    private void initAppHome()
    {
        /* Try System Property */
        String sysPropHome = System.getProperty( APP_HOME_PROPERTY );
        if ( ( sysPropHome != null ) && ( sysPropHome.trim().length() > 0 ) )
        {
            debug( "Found System Property." );
            this.appHomeDir = new File( sysPropHome.trim() );
            return;
        }

        /* Attempt to figgure out the classpath entry that this object was loaded from. */
        String selfclass = "/" + this.getClass().getName();
        selfclass = selfclass.replace( '.', '/' ) + ".class";
        URL classURL = this.getClass().getResource( selfclass );
        if ( classURL != null )
        {
            debug( "Found Class URL: " + classURL.toExternalForm() );
            String protocol = classURL.getProtocol();

            if ( "jar".equals( protocol ) )
            {
                debug( "Found in jar source: " + classURL.toExternalForm() );
                String rawurl = classURL.toExternalForm();
                if ( !rawurl.startsWith( "jar:file:/" ) )
                {
                    throw new IllegalStateException( "Unknown jar file syntax: " + rawurl );
                }

                rawurl = rawurl.substring( "jar:file:".length() );
                while ( rawurl.startsWith( "//" ) )
                {
                    rawurl = rawurl.substring( 1 );
                }

                int idx = rawurl.indexOf( ".jar!/" );
                if ( idx > 0 )
                {
                    rawurl = rawurl.substring( 0, idx );
                    debug( "Raw url stripped of class: " + rawurl );
                }

                idx = rawurl.lastIndexOf( "/", idx );
                if ( idx > 0 )
                {
                    rawurl = rawurl.substring( 0, idx );
                    debug( "Raw url path stripped: " + rawurl );
                }

                debug( "Found in a jar file: " + rawurl );
                this.appHomeDir = new File( rawurl );
            }
            else if ( "file".equals( protocol ) )
            {
                debug( "Found as file source: " + classURL.toExternalForm() );
                String rawurl = classURL.toExternalForm().substring( 6 );
                debug( "rawurl: " + rawurl );
                debug( "Found in a classpath directory." );
                this.appHomeDir = new File( rawurl.substring( 0, rawurl.length() - selfclass.length() ) );
            }
            else
            {
                throw new IllegalStateException( "Only jar and file sources ares supported by Launcher: "
                    + classURL.toExternalForm() );
            }
        }

        if ( this.appHomeDir == null )
        {
            throw new IllegalStateException( "Unable to find app classes." );
        }

        System.setProperty( APP_HOME_PROPERTY, this.appHomeDir.getAbsolutePath() );
    }

    /**
     * Add a file to the underlying classpath urls.
     * 
     * @param file the file to add.
     */
    public void addFile( File file )
    {
        debug( ".addFile(" + file.getAbsolutePath() + ")" );
        try
        {
            addURL( file.toURL() );
        }
        catch ( MalformedURLException e )
        {
            System.err.println( "Unable to add URL to classloader for file " + file.getAbsolutePath() );
        }
    }

    /**
     * Add a lib directory.
     * 
     * @param lib the lib directory.
     */
    public void addLib( String lib )
    {
        debug( ".addLib((String)" + lib + ")" );
        File libFile = new File( lib );
        if ( libFile.isAbsolute() )
        {
            addLib( libFile );
        }
        else
        {
            addLib( new File( this.appHomeDir, lib ) );
        }
    }

    /**
     * Add all of the jar files in a lib.
     * 
     * @param lib the lib to add.
     */
    public void addLib( File lib )
    {
        debug( ".addLib((File)" + lib.getAbsolutePath() + ")" );
        if ( lib.isFile() )
        {
            addFile( lib );
        }
        else
        {
            addFiles( lib );
        }
    }

    /**
     * Add a URL to the list tracked by the ClassLoader.
     * 
     * @param url the url to add.
     */
    public void addURL( URL url )
    {
        if ( !this.classpathUrls.contains( url ) )
        {
            // Add to parent URL Set if not added.
            debug( "Added URL: " + url.toExternalForm() );
            this.classpathUrls.add( url );
        }
    }

    private void debug( String msg )
    {
        if ( debug )
        {
            System.out.println( "[DEBUG|LibLocator] " + msg );
        }
    }

    private void addFiles( File dir )
    {
        File files[] = dir.listFiles();

        if ( files == null )
        {
            debug( "No files in " + dir.getAbsolutePath() );
            return;
        }

        for ( int i = 0; i < files.length; i++ )
        {
            if ( files[i].getName().startsWith( "." ) || files[i].isHidden() )
            {
                continue; // skip hidden files/dirs
            }

            if ( files[i].isDirectory() )
            {
                addFiles( files[i] ); // dive into dir.
            }
            else
            {
                int idx = files[i].getName().lastIndexOf( "." );
                if ( idx > 0 )
                {
                    String ext = files[i].getName().substring( idx );
                    if ( ".jar".equalsIgnoreCase( ext ) )
                    {
                        addFile( files[i] );
                    }
                }
            }
        }
    }
}
