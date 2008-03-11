package org.apache.maven.archiva.jarinfo.model.xml;

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

import java.io.File;
import java.io.StringWriter;
import java.util.Calendar;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.maven.archiva.jarinfo.analysis.JarAnalysis;
import org.apache.maven.archiva.jarinfo.model.JarDetails;
import org.apache.maven.archiva.jarinfo.utils.Timestamp;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * JarDetailsXmlTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JarDetailsXmlSerializerTest
    extends TestCase
{
    public void testToXml()
        throws Exception
    {
        JarDetails details = new JarDetails();
        details.setFilename( "dummy-test.jar" );
        details.setSize( 54321 );

        Calendar cal = Calendar.getInstance();
        cal.set( 2007, Calendar.AUGUST, 22, 13, 44, 55 );
        details.setTimestamp( cal );

        assertDetails( "dummy-test.jardetails.xml", details );
    }

    public void testMysteryInspectionSerialize()
        throws Exception
    {
        JarAnalysis analysis = new JarAnalysis( true );
        File mysteryFile = new File( "src/test/jars", "mystery.jar" );
        JarDetails details = analysis.analyze( mysteryFile );
        // Workaround/Hack for timestamp change on svn checkout.
        details.setTimestamp( Timestamp.convert( "2007-12-08 15:28:21 UTC" ) );
        details.getGenerator().setTimestamp( Timestamp.convert( "2007-12-11 16:31:43 UTC" ) );

        assertDetails( "mystery.jar-details.xml", details );
    }

    public void testMavenSharedInspectionSerialize()
        throws Exception
    {
        JarAnalysis analysis = new JarAnalysis( true );
        File mavenSharedFile = new File( "src/test/jars", "maven-shared-jar-1.0.jar" );
        JarDetails details = analysis.analyze( mavenSharedFile );
        // Workaround/Hack for timestamp change on svn checkout.
        details.setTimestamp( Timestamp.convert( "2007-12-08 15:28:21 UTC" ) );
        details.getGenerator().setTimestamp( Timestamp.convert( "2007-12-11 16:31:43 UTC" ) );

        assertDetails( "maven-shared.jar-details.xml", details );
    }

    public void testJxrNoInspectionSerialize()
        throws Exception
    {
        // Set it up as no inspection.
        JarAnalysis analysis = new JarAnalysis( false );
        File jxrFile = new File( "src/test/jars", "jxr.jar" );
        JarDetails details = analysis.analyze( jxrFile );
        // Workaround/Hack for timestamp change on svn checkout.
        details.setTimestamp( Timestamp.convert( "2007-12-08 15:28:21 UTC" ) );
        details.getGenerator().setTimestamp( Timestamp.convert( "2007-12-11 16:31:43 UTC" ) );

        assertDetails( "jxr.jar-details.xml", details );
    }

    private void assertDetails( String expectedContentsFile, JarDetails details )
        throws Exception
    {
        Document doc = JarDetailsXmlSerializer.serialize( details );
        OutputFormat format = OutputFormat.createPrettyPrint();
        StringWriter str = new StringWriter();
        XMLWriter writer = new XMLWriter( str, format );
        writer.write( doc );

        String actualContents = str.toString();
        String expectedContents = FileUtils.readFileToString( new File( "src/test/resources/", expectedContentsFile ) );

        DetailedDiff diff = new DetailedDiff( new Diff( expectedContents, actualContents ) );
        if ( !diff.similar() )
        {
            assertEquals( expectedContents, actualContents );
        }
    }

}
