package org.apache.maven.archiva.jarinfo.analysis.visitors;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
import org.apache.maven.archiva.jarinfo.analysis.Hasher;
import org.apache.maven.archiva.jarinfo.analysis.IdentificationWeights;
import org.apache.maven.archiva.jarinfo.analysis.JarEntryVisitor;
import org.apache.maven.archiva.jarinfo.model.ClassDetail;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.JarDetails;
import org.apache.maven.archiva.jarinfo.utils.EmptyUtils;

public class EntryClassAnalyzer
    extends AbstractJarEntryVisitor
    implements JarEntryVisitor
{
    private static final double JAVA_1_7_CLASS_VERSION = 51.0;

    private static final double JAVA_1_6_CLASS_VERSION = 50.0;

    private static final double JAVA_1_5_CLASS_VERSION = 49.0;

    private static final double JAVA_1_4_CLASS_VERSION = 47.0;

    private static final double JAVA_1_3_CLASS_VERSION = 46.0;

    private static final double JAVA_1_2_CLASS_VERSION = 45.65536;

    private static final double JAVA_1_1_CLASS_VERSION = 45.3;

    private boolean overallDebugPresent;

    private double overallClassVersion;

    private Set<String> packages = new HashSet<String>();
    
    private boolean performInspection = false;
    
    private Hasher classHasher = new Hasher( Hasher.SHA1 );
    
    public EntryClassAnalyzer(boolean performInspection )
    {
    	this.performInspection = performInspection;
    }

    public void visitStart( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitStart( details, jar );
        overallDebugPresent = false;
        overallClassVersion = 0.0;
        packages.clear();
    }

    public void visitJarEntry( EntryDetail entry, JarEntry jarEntry )
        throws IOException
    {
        if ( !jarEntry.getName().endsWith( ".class" ) )
        {
            return;
        }
        
        ClassParser classParser = new ClassParser( jar.getName(), entry.getName() );
        JavaClass javaClass = null;
        try
        {
            javaClass = classParser.parse();
        }
        catch ( ClassFormatException e )
        {
            System.out.println( "Failure on entry: " + jarEntry.getName() );
            e.printStackTrace();
            return;
        }

        ClassDetail classDetail = new ClassDetail();

        classHasher.reset();
        classHasher.update( jar.getInputStream( jarEntry ) );

        classDetail.setHash( classHasher.getAlgorithm(), classHasher.getHash() );
        classDetail.setName( javaClass.getClassName() );
        classDetail.setDebug( hasDebugSymbols( javaClass ) );

        if ( classDetail.hasDebug() )
        {
            overallDebugPresent = true;
        }

        double classVersion = javaClass.getMajor();
        if ( javaClass.getMinor() > 0 )
        {
            classVersion = classVersion + ( 1 / (double) javaClass.getMinor() );
        }

        if ( classVersion > overallClassVersion )
        {
            overallClassVersion = classVersion;
        }

        classDetail.setClassVersion( javaClass.getMajor() + "." + javaClass.getMinor() );

        Method[] methods = javaClass.getMethods();
        for ( Method method : methods )
        {
            classDetail.addMethod( method.getName() + method.getSignature() );
        }

        BCELImportVisitor importVisitor = new BCELImportVisitor( javaClass );
        DescendingVisitor descVisitor = new DescendingVisitor( javaClass, importVisitor );
        javaClass.accept( descVisitor );

        classDetail.getImports().addAll( importVisitor.getImports() );
        classDetail.setTargetJdk( toJDK( classVersion ) );

        details.getBytecode().addClass( classDetail );

        // Package to GroupId InspectedIds
        if(this.performInspection)
        {
	        String packageName = javaClass.getPackageName();
	        packages.add( packageName );
	        int weight = IdentificationWeights.getInstance().getWeight( "packages.groupId" );
	        details.getInspectedIds().addGroupId( packageName, weight, "class.packages" );
        }
    }

    public void visitFinished( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitFinished( details, jar );
        details.getBytecode().setDebug( overallDebugPresent );
        details.getBytecode().setRequiredJdk( toJDK( overallClassVersion ) );

        if(this.performInspection)
        {
	        // Determine common groupId.
	        String commonPackage = null;
	        for ( String packageName : packages )
	        {
	            if ( commonPackage == null )
	            {
	                commonPackage = packageName;
	                continue;
	            }
	
	            commonPackage = overlap( commonPackage, packageName );
	            if ( commonPackage.endsWith( "." ) )
	            {
	                commonPackage.substring( 0, commonPackage.length() - 1 );
	            }
	        }
	
	        if ( !EmptyUtils.isEmpty( commonPackage ) )
	        {
	            int weight = IdentificationWeights.getInstance().getWeight( "packages.groupId.common" );
	            details.getInspectedIds().addGroupId( commonPackage, weight, "class.packages.common" );
	        }
        }
    }

    private String overlap( String str1, String str2 )
    {
        if ( EmptyUtils.isEmpty( str1 ) || EmptyUtils.isEmpty( str2 ) )
        {
            return "";
        }

        StringBuffer ret = new StringBuffer();

        int len = Math.min( str1.length(), str2.length() );
        for ( int i = 0; i < len; i++ )
        {
            char c1 = str1.charAt( i );
            char c2 = str2.charAt( i );
            if ( c1 != c2 )
            {
                break;
            }
            ret.append( c1 );
        }

        return ret.toString();
    }

    private boolean hasDebugSymbols( JavaClass javaClass )
    {
        boolean ret = false;
        Method[] methods = javaClass.getMethods();
        for ( int i = 0; i < methods.length; i++ )
        {
            LineNumberTable linenumbers = methods[i].getLineNumberTable();
            if ( linenumbers != null && linenumbers.getLength() > 0 )
            {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private String toJDK( double classVersion )
    {
        if ( classVersion >= JAVA_1_7_CLASS_VERSION )
        {
            return "1.7";
        }
        else if ( classVersion >= JAVA_1_6_CLASS_VERSION )
        {
            return "1.6";
        }
        else if ( classVersion >= JAVA_1_5_CLASS_VERSION )
        {
            return "1.5";
        }
        else if ( classVersion >= JAVA_1_4_CLASS_VERSION )
        {
            return "1.4";
        }
        else if ( classVersion >= JAVA_1_3_CLASS_VERSION )
        {
            return "1.3";
        }
        else if ( classVersion >= JAVA_1_2_CLASS_VERSION )
        {
            return "1.2";
        }
        else if ( classVersion >= JAVA_1_1_CLASS_VERSION )
        {
            return "1.1";
        }
        else if ( classVersion > 0 )
        {
            return "1.0";
        }

        return null;
    }

}
