package org.apache.maven.shared.plugin.scanner;

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
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Use the builder pattern to configure and execute a DirectoryScanner. <code>iterateOn*</code> methods allow to iterate
 * on the scanner result
 *
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public class ScannerBuilderTest extends TestCase
{
    public void testScan()
    {
        File basedir = new File( System.getProperty( "basedir", "" ) );
        File root = new File( basedir.getAbsoluteFile(), "target/test-classes/scanner" );

        Iterator it = new ScannerBuilder( root )
            .include( "*.xml" )
            .exclude( "excluded.xml" )
            .scan()
            .iterateOnFiles();
        assertTrue( it.hasNext() );
        assertEquals( "included.xml", ( (File) it.next() ).getName() );
        assertFalse( it.hasNext() );
    }
}
