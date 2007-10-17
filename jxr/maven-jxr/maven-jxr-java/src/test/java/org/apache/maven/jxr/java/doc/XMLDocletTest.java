package org.apache.maven.jxr.java.doc;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import junit.framework.TestCase;

import com.sun.tools.javadoc.Main;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class XMLDocletTest
    extends TestCase
{
    /**
     * Call Javadoc tool with XML doclet.
     *
     * @throws Exception if any
     */
    public void testDefaultExecute()
        throws Exception
    {
        final String basedir = new File( "" ).getAbsolutePath();

        File outputXML = new File( basedir, "target/unit/xmldoclet-default/javadoc.xml" );

        // test phase is after compile phase, so we are sure that classes dir exists
        // TODO wrap syso and syserr
        String[] args = {
            "-package",
            "-sourcepath",
            new File( basedir, "src/test/resources/javasrc" ).getAbsolutePath(),
            "-doclet",
            "org.apache.maven.jxr.java.doc.XMLDoclet",
            "-docletpath",
            new File( basedir, "target/classes" ).getAbsolutePath(),
            "-o",
            outputXML.getAbsolutePath(),
            "test.packA",
            "test.packB",
            "test.packC",
            "test.packD",
            "test.packE",
            "test.packF",
            "test.packG.a",
            "test.packG.b" };

        Main.execute( "javadoc", XMLDoclet.class.getName(), args );

        // Generated files
        assertTrue( outputXML.exists() );
        assertTrue( outputXML.length() > 0 );
        File dtd = new File( basedir, "target/unit/xmldoclet-default/" + XMLDoclet.XMLDOCLET_DTD );
        assertTrue( dtd.exists() );
        assertTrue( dtd.length() > 0 );
    }
}
