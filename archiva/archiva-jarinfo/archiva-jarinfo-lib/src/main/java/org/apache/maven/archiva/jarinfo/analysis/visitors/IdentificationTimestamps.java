package org.apache.maven.archiva.jarinfo.analysis.visitors;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.archiva.jarinfo.analysis.IdentificationWeights;
import org.apache.maven.archiva.jarinfo.analysis.JarEntryVisitor;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.JarDetails;

public class IdentificationTimestamps
    extends AbstractJarEntryVisitor
    implements JarEntryVisitor
{
    private long maxTimestamp;

    @Override
    public void visitStart( JarDetails details, JarFile jar )
        throws IOException
    {
        super.visitStart( details, jar );
        maxTimestamp = 0;
    }

    public void visitJarEntry( EntryDetail entry, JarEntry jarEntry )
        throws IOException
    {
        maxTimestamp = Math.max( maxTimestamp, jarEntry.getTime() );

        int weight = IdentificationWeights.getInstance().getWeight( "timestamp.version" );
        details.getInspectedIds().addVersion( toTimestamp( jarEntry.getTime() ), weight, "timestamps" );
    }

    @Override
    public void visitFinished( JarDetails finishDetails, JarFile finishJar )
        throws IOException
    {
        super.visitFinished( finishDetails, finishJar );
        int weight = IdentificationWeights.getInstance().getWeight( "timestamp.version.max" );
        details.getInspectedIds().addVersion( toTimestamp( maxTimestamp ), weight, "timestamps.max" );
    }

    private String toTimestamp( long timestamp )
    {
        SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd", Locale.US );
        return format.format( new Date( timestamp ) );
    }

}
