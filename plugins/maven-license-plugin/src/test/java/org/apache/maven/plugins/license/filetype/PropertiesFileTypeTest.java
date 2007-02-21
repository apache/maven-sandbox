package org.apache.maven.plugins.license.filetype;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugins.license.LicenseCheckException;

import java.io.File;
import java.io.IOException;

/**
 * PropertiesFileTypeTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class PropertiesFileTypeTest
    extends AbstractFileTypeTestCase
{
    public PropertiesFileTypeTest()
    {
        super( "properties" );
    }

    public void testCheck()
        throws IOException, LicenseCheckException
    {
        String license = getRawLicense();
        String licenses[] = new String[] { license };

        File unsetFile = getSourceFile( "inject-1.properties" );
        File expectedFile = getSourceFile( "inject-1-expected.properties" );

        assertTrue( filetype.hasLicense( expectedFile, licenses ) );
        assertFalse( filetype.hasLicense( unsetFile, licenses ) );
    }

    public void testInject1()
        throws Exception
    {
        File injectFile = getTestInjectFile( "inject-1.properties" );
        String rawLicense = getRawLicense();

        filetype.injectLicense( injectFile, rawLicense );

        String expectedContents = getTestFileContents( "inject-1-expected.properties" );
        String actualContents = getFileContents( injectFile );

        assertEquals( expectedContents, actualContents );
    }

}
