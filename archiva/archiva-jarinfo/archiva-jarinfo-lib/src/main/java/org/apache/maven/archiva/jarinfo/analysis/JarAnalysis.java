package org.apache.maven.archiva.jarinfo.analysis;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.apache.commons.io.IOUtils;
import org.apache.maven.archiva.jarinfo.analysis.visitors.EntryClassAnalyzer;
import org.apache.maven.archiva.jarinfo.analysis.visitors.EntryHasher;
import org.apache.maven.archiva.jarinfo.analysis.visitors.EntryManifest;
import org.apache.maven.archiva.jarinfo.analysis.visitors.EntrySizer;
import org.apache.maven.archiva.jarinfo.analysis.visitors.IdentificationEmbeddedMavenProperties;
import org.apache.maven.archiva.jarinfo.analysis.visitors.IdentificationFilename;
import org.apache.maven.archiva.jarinfo.analysis.visitors.IdentificationTimestamps;
import org.apache.maven.archiva.jarinfo.model.EntryDetail;
import org.apache.maven.archiva.jarinfo.model.JarDetails;
import org.apache.maven.archiva.jarinfo.utils.EntryDetailComparator;
import org.apache.maven.archiva.jarinfo.utils.NaturalLanguageComparator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * JarAnalysis - takes a jar file, analyzes it, and creates a JarDetails model. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JarAnalysis
{
    private List<JarEntryVisitor> visitors;

    private boolean performInspection = true;

    /**
     * Setup the JarAnalysis engine.
     * 
     * @param performInspection true to perform inspection (and populate the {@link JarDetails#getInspectedIds()} field.
     */
    public JarAnalysis( boolean performInspection )
    {
        this.performInspection = performInspection;

        visitors = new ArrayList<JarEntryVisitor>();
        addDefaultVisitors();
    }

    public void addDefaultVisitors()
    {
        visitors.add( new EntryManifest() );
        visitors.add( new EntryHasher() );
        visitors.add( new EntrySizer() );
        visitors.add( new EntryClassAnalyzer( this.performInspection ) );
        visitors.add( new IdentificationEmbeddedMavenProperties( this.performInspection ) );

        if ( this.performInspection )
        {
            visitors.add( new IdentificationFilename() );
            visitors.add( new IdentificationTimestamps() );
        }
    }

    public JarDetails analyze( File file )
        throws IOException
    {
        JarDetails details = new JarDetails();

        details.getGenerator().setName( "archiva-jarinfo" );
        details.getGenerator().setVersion( getVersion() );
        details.getGenerator().setTimestamp( Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) ) );

        basics( file, details );
        hashcodes( details, file );

        JarFile jar = new JarFile( file );
        try
        {
            processEntries( details, jar );
        }
        finally
        {
            jar.close();
        }

        return details;
    }

    public String getVersion()
    {
        // TODO: Discover the version from the jar pom.properties.
        return "1.0";
    }

    /**
     * Populate the full file hashcodes
     * @throws IOException 
     */
    private void hashcodes( JarDetails details, File file )
        throws IOException
    {
        Hasher md5Hasher = new Hasher( Hasher.MD5 );
        Hasher sha1Hasher = new Hasher( Hasher.SHA1 );

        FileInputStream fileStream = null;
        try
        {
            fileStream = new FileInputStream( file );
            List<Hasher> fullHashers = new ArrayList<Hasher>();
            fullHashers.add( md5Hasher );
            fullHashers.add( sha1Hasher );

            // Read file once for all full hashers.
            Hasher.update( fullHashers, fileStream );

            details.setHash( md5Hasher.getAlgorithm(), md5Hasher.getHash() );
            details.setHash( sha1Hasher.getAlgorithm(), sha1Hasher.getHash() );
        }
        finally
        {
            IOUtils.closeQuietly( fileStream );
        }
    }

    /**
     * Populate the basics.
     */
    private void basics( File file, JarDetails details )
    {
        details.setFilename( file.getName() );
        details.setSize( file.length() );

        // Get file date in system timezone format.
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( file.lastModified() );

        // Adjust to UTC
        cal.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

        // Set to details obj
        details.setTimestamp( cal );
    }

    /**
     * Process the jar file entries
     */
    private void processEntries( JarDetails details, JarFile jar )
        throws IOException
    {
        // Entry names
        List<String> entryNames = new ArrayList<String>();

        // Gather up entry names, and sort.
        Enumeration<JarEntry> en = jar.entries();
        while ( en.hasMoreElements() )
        {
            JarEntry entry = en.nextElement();
            entryNames.add( entry.getName() );
        }

        Collections.sort( entryNames, new NaturalLanguageComparator() );

        // Process entries.
        for ( JarEntryVisitor visitor : visitors )
        {
            visitor.visitStart( details, jar );
        }

        for ( String name : entryNames )
        {
            JarEntry jarEntry = jar.getJarEntry( name );
            EntryDetail entry = new EntryDetail();
            entry.setName( jarEntry.getName() );

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis( jarEntry.getTime() );
            entry.setTimestamp( cal );
            entry.setDirectory( jarEntry.isDirectory() );

            for ( JarEntryVisitor visitor : visitors )
            {
                visitor.visitJarEntry( entry, jarEntry );
            }

            // Add entry.
            details.addEntry( entry );
        }

        Collections.sort( details.getEntries(), new EntryDetailComparator() );

        for ( JarEntryVisitor visitor : visitors )
        {
            visitor.visitFinished( details, jar );
        }
    }

    public List<JarEntryVisitor> getVisitors()
    {
        return visitors;
    }

    public void setVisitors( List<JarEntryVisitor> visitors )
    {
        this.visitors = visitors;
    }

    public boolean isPerformingInspection()
    {
        return performInspection;
    }

    /**
     * Sets the <code>performInspection</code> flag.
     * NOTE: Setting this value will reset the {@link #getVisitors()} to default values.
     * 
     * @param performInspection true to perform inspection (and populate the {@link JarDetails#getInspectedIds()} field.
     */
    public void setPerformInspection( boolean performInspection )
    {
        this.performInspection = performInspection;
        visitors.clear();
        addDefaultVisitors();
    }
}
