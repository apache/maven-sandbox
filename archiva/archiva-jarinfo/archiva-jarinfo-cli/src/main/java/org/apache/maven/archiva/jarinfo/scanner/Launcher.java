package org.apache.maven.archiva.jarinfo.scanner;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Launcher
{
    private boolean debug;

    private String mainClassName;

    /**
     * Main Launcher.
     */
    public Launcher( String mainClass )
    {
        super();
        this.debug = false;
        this.mainClassName = mainClass;
    }

    /**
     * Launcher the code and start mergere-discovery.
     * 
     * @param args the command line arguments.
     */
    public void bootstrap( String[] args )
    {
        List<String> argList = new ArrayList<String>();
        List<String> libList = new ArrayList<String>();

        /* Add args to argList */
        try
        {
            for ( int i = 0; i < args.length; i++ )
            {
                if ( "--lib-debug".equals( args[i] ) )
                {
                    this.debug = true;
                    LibLocator.debug = true;
                }
                else if ( "--lib".equals( args[i] ) )
                {
                    i++;
                    libList.add( args[i] );
                }
                else
                {
                    argList.add( args[i] );
                }
            }
        }
        catch ( ArrayIndexOutOfBoundsException e )
        {
            System.err.println( "Unable to load lib." );
            System.exit( 2 );
        }

        /* Create locator */
        LibLocator locator = new LibLocator();

        /* Add command line libs */
        for ( String lib : libList )
        {
            locator.addLib( lib );
        }

        /* Load class */
        try
        {
            URLClassLoader loader = new URLClassLoader( locator.getUrls() );
            Thread.currentThread().setContextClassLoader( loader );

            debug( "Attempting to load " + this.mainClassName );
            Class<?> mainClass = loader.loadClass( this.mainClassName );

            debug( "Attempting to create a new instance of " + mainClass );
            Object objmain = mainClass.newInstance();

            debug( "Attempting to find main method of " + mainClass );
            Class<?> argClass = Array.newInstance( String.class, 0 ).getClass();
            Method mainMethod = mainClass.getMethod( "main", new Class[] { argClass } );

            debug( "Attempting to execute main method of " + mainClass );
            String arguments[] = argList.toArray( new String[0] );
            mainMethod.invoke( objmain, new Object[] { arguments } );

        }
        catch ( ClassNotFoundException e )
        {
            error( "failed to load " + mainClassName + ".", e );
        }
        catch ( InstantiationException e )
        {
            error( "failed to instantiate " + mainClassName + ".", e );
        }
        catch ( IllegalAccessException e )
        {
            error( "failed to access " + mainClassName + ".", e );
        }
        catch ( SecurityException e )
        {
            error( "denied access to load " + mainClassName + ".", e );
        }
        catch ( IllegalArgumentException e )
        {
            error( "passed invalid arguments to " + mainClassName + "#main(String[]).", e );
        }
        catch ( NoSuchMethodException e )
        {
            error( "unable find method " + mainClassName + "#main(String[]).", e );
        }
        catch ( InvocationTargetException e )
        {
            if ( e.getCause() == null )
            {
                error( "unable execute " + mainClassName + "#main(String[]).", e );
            }
            else
            {
                error( "Unable to run app.", e.getCause() );
            }
        }
    }

    private void error( String msg, Throwable toss )
    {
        System.err.println( "[ERROR] " + Launcher.class.getName() + "\n[ERROR]   " + msg );
        toss.printStackTrace( System.err );
    }

    private void debug( String msg )
    {
        if ( debug )
        {
            System.out.println( "[DEBUG] " + msg );
        }
    }
}