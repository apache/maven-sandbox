/*
 * Copyright 2010 Red Hat, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.maven.mae.boot.log;

import org.apache.maven.repository.ArtifactTransferEvent;
import org.apache.maven.repository.ArtifactTransferListener;
import org.apache.maven.repository.ArtifactTransferResource;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InteractiveTransferListener
    implements ArtifactTransferListener
{

    private final Map<ArtifactTransferResource, Long> downloads =
        new ConcurrentHashMap<ArtifactTransferResource, Long>();

    private int lastLength;

    protected PrintStream out;

    private boolean showChecksumEvents;

    public InteractiveTransferListener( final PrintStream out )
    {
        this.out = out;
    }

    protected void doProgress( final ArtifactTransferEvent transferEvent )
    {
        final ArtifactTransferResource resource = transferEvent.getResource();
        downloads.put( resource, Long.valueOf( transferEvent.getTransferredBytes() ) );

        final StringBuilder buffer = new StringBuilder( 64 );

        for ( final Map.Entry<ArtifactTransferResource, Long> entry : downloads.entrySet() )
        {
            final long total = entry.getKey().getContentLength();
            final long complete = entry.getValue().longValue();

            buffer.append( getStatus( complete, total ) ).append( "  " );
        }

        final int pad = lastLength - buffer.length();
        lastLength = buffer.length();
        pad( buffer, pad );
        buffer.append( '\r' );

        out.print( buffer );
    }

    private String getStatus( final long complete, final long total )
    {
        if ( total >= 1024 )
        {
            return toKB( complete ) + "/" + toKB( total ) + " KB ";
        }
        else if ( total >= 0 )
        {
            return complete + "/" + total + " B ";
        }
        else if ( complete >= 1024 )
        {
            return toKB( complete ) + " KB ";
        }
        else
        {
            return complete + " B ";
        }
    }

    private void pad( final StringBuilder buffer, int spaces )
    {
        final String block = "                                        ";
        while ( spaces > 0 )
        {
            final int n = Math.min( spaces, block.length() );
            buffer.append( block, 0, n );
            spaces -= n;
        }
    }

    public void transferCompleted( final ArtifactTransferEvent transferEvent )
    {
        downloads.remove( transferEvent.getResource() );

        final StringBuilder buffer = new StringBuilder( 64 );
        pad( buffer, lastLength );
        buffer.append( '\r' );
        out.print( buffer );

        if ( !showEvent( transferEvent ) )
        {
            return;
        }

        doCompleted( transferEvent );
    }

    protected boolean showEvent( final ArtifactTransferEvent event )
    {
        if ( event.getResource() == null )
        {
            return true;
        }

        final String resource = event.getResource().getName();

        if ( resource == null || resource.trim().length() == 0 )
        {
            return true;
        }

        if ( resource.endsWith( ".sha1" ) || resource.endsWith( ".md5" ) )
        {
            return showChecksumEvents;
        }

        return true;
    }

    public void transferInitiated( final ArtifactTransferEvent transferEvent )
    {
        if ( !showEvent( transferEvent ) )
        {
            return;
        }

        doInitiated( transferEvent );
    }

    protected void doInitiated( final ArtifactTransferEvent transferEvent )
    {
        final String message =
            transferEvent.getRequestType() == ArtifactTransferEvent.REQUEST_PUT ? "Uploading" : "Downloading";

        out.println( message + ": " + transferEvent.getResource().getUrl() );
    }

    public void transferStarted( final ArtifactTransferEvent transferEvent )
    {
        if ( !showEvent( transferEvent ) )
        {
            return;
        }

        doStarted( transferEvent );
    }

    protected void doStarted( final ArtifactTransferEvent transferEvent )
    {
        // to be overriden by sub classes
    }

    public void transferProgress( final ArtifactTransferEvent transferEvent )
    {
        if ( !showEvent( transferEvent ) )
        {
            return;
        }

        doProgress( transferEvent );
    }

    protected void doCompleted( final ArtifactTransferEvent transferEvent )
    {
        final ArtifactTransferResource artifact = transferEvent.getResource();
        final long contentLength = transferEvent.getTransferredBytes();
        if ( contentLength >= 0 )
        {
            final String type =
                ( transferEvent.getRequestType() == ArtifactTransferEvent.REQUEST_PUT ? "Uploaded" : "Downloaded" );
            final String len = contentLength >= 1024 ? toKB( contentLength ) + " KB" : contentLength + " B";

            String throughput = "";
            final long duration = System.currentTimeMillis() - artifact.getTransferStartTime();
            if ( duration > 0 )
            {
                final DecimalFormat format = new DecimalFormat( "0.0", new DecimalFormatSymbols( Locale.ENGLISH ) );
                final double kbPerSec = ( contentLength / 1024.0 ) / ( duration / 1000.0 );
                throughput = " at " + format.format( kbPerSec ) + " KB/sec";
            }

            out.println( type + ": " + artifact.getUrl() + " (" + len + throughput + ")" );
        }
    }

    protected long toKB( final long bytes )
    {
        return ( bytes + 1023 ) / 1024;
    }

    public boolean isShowChecksumEvents()
    {
        return showChecksumEvents;
    }

    public void setShowChecksumEvents( final boolean showChecksumEvents )
    {
        this.showChecksumEvents = showChecksumEvents;
    }

}
