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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * AbstractFileTypeTestCase 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractFileTypeTestCase
    extends PlexusTestCase
{
    private String roleHint;

    public AbstractFileTypeTestCase( String hint )
    {
        super();
        this.roleHint = hint;
    }

    protected AbstractFileType filetype;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        filetype = (AbstractFileType) lookup( AbstractFileType.ROLE, this.roleHint );
        assertNotNull( "Unable to find filetype for role-hint [" + this.roleHint + "]", filetype );
    }

    public String getFileContents( File file )
        throws IOException
    {
        FileReader freader = null;
        try
        {
            freader = new FileReader( file );
            String rawContents = IOUtil.toString( freader );
            return rawContents;
        }
        finally
        {
            IOUtil.close( freader );
        }
    }

    public String getRawLicense()
        throws IOException
    {
        File licenseFile = new File( "src/test/filetypes/LICENSE" );
        assertTrue( licenseFile.exists() );
        return getFileContents( licenseFile );
    }

    public File getSourceFile( String name )
    {
        File sourceFile = new File( "src/test/filetypes/" + roleHint + "/" + name );

        assertTrue( "Source test file [" + sourceFile.getAbsolutePath() + "] should exist.", sourceFile.exists() );
        
        return sourceFile;
    }

    public File getTestInjectFile( String name )
        throws IOException
    {
        File sourceFile = getSourceFile( name );

        File destDir = new File( "target/test-filetype-" + roleHint );

        if ( !destDir.exists() )
        {
            assertTrue( "Unable to make " + destDir.getAbsolutePath() + " directories.", destDir.mkdirs() );
        }

        FileUtils.copyFileToDirectory( sourceFile, destDir );

        File testFile = new File( destDir, sourceFile.getName() );

        assertTrue( "Test file [" + testFile.getAbsolutePath() + "] should exist.", testFile.exists() );

        return testFile;
    }

    public String getTestFileContents( String name )
        throws IOException
    {
        File sourceFile = new File( "src/test/filetypes/" + roleHint + "/" + name );

        assertTrue( "Source test file [" + sourceFile.getAbsolutePath() + "] should exist.", sourceFile.exists() );

        return getFileContents( sourceFile );
    }
}
