package org.apache.maven.plugin.plugit.tools;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class CodeGenerator
{
    private VelocityComponent velocity;

    private final File destDir;

    private final String destPackage;

    private final Map initialContext;

    public CodeGenerator( VelocityComponent velocity, File destDir, String destPackage, Map initialContext )
    {
        this.velocity = velocity;
        this.destDir = destDir;
        this.destPackage = destPackage;
        this.initialContext = initialContext;
    }

    public void generateCode( String className, String templateName )
        throws ToolException
    {
        generateCode( null, className, templateName );
    }

    public void generateCode( String pomPath, String className, String templateName )
        throws ToolException
    {
        File pkgDir = new File( destDir, destPackage.replace( '.', '/' ) );

        pkgDir.mkdirs();

        File classFile = new File( pkgDir, className + ".java" );

        Context ctx = initialContext == null ? new VelocityContext() : new VelocityContext( initialContext );

        ctx.put( "package", destPackage );
        ctx.put( "class", className );

        if ( pomPath != null )
        {
            ctx.put( "pomPath", pomPath );
        }

        Writer writer = null;
        try
        {
            writer = new BufferedWriter( new FileWriter( classFile ) );

            velocity.getEngine().mergeTemplate( templateName, ctx, writer );

            writer.flush();
        }
        catch ( ResourceNotFoundException e )
        {
            throw new ToolException( "Unable to locate Velocity template: " + templateName, e );
        }
        catch ( ParseErrorException e )
        {
            throw new ToolException( "Unable to parse Velocity template: " + templateName, e );
        }
        catch ( MethodInvocationException e )
        {
            throw new ToolException( "Error merging Velocity template: " + templateName, e );
        }
        catch ( IOException e )
        {
            throw new ToolException( "Error generating class to: " + classFile, e );
        }
        catch ( Exception e )
        {
            throw new ToolException( "Unknown error (probably from VelocityComponent)", e );
        }
        finally
        {
            IOUtil.close( writer );
        }
    }
}
