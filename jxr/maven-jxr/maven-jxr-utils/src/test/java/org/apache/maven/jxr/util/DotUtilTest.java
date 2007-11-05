package org.apache.maven.jxr.util;

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

import org.apache.maven.jxr.util.DotUtil.DotNotPresentInPathException;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class DotUtilTest
    extends TestCase
{
    public void testDefaultExecuteDot()
        throws Exception
    {
        final String basedir = new File( "" ).getAbsolutePath();

        File in = new File( basedir, "src/test/resources/dot/target.dot" );
        File out = new File( basedir, "target/unit/dot-default/output.png" );

        try
        {
            DotUtil.executeDot( in, out );
            assertTrue( "DOT exists in the path", true );
        }
        catch ( DotNotPresentInPathException e )
        {
            assertTrue( "DOT doesnt exist in the path. Ignored test", true );
            return;
        }

        // Generated files
        assertTrue( out.exists() );
        assertTrue( out.length() > 0 );
    }

    public void testFormatExecuteDot()
        throws Exception
    {
        final String basedir = new File( "" ).getAbsolutePath();

        File in = new File( basedir, "src/test/resources/dot/target.dot" );
        File out = new File( basedir, "target/unit/dot-default/output.png" );

        String format = DotUtil.DEFAULT_OUTPUT_FORMAT;
        try
        {
            DotUtil.executeDot( in, format, out );
            assertTrue( "DOT exists in the path", true );
        }
        catch ( DotNotPresentInPathException e )
        {
            assertTrue( "DOT doesnt exist in the path. Ignored test", true );
            return;
        }

        // Generated files
        File generated = new File( out.getParentFile(), "output.png." + DotUtil.DEFAULT_OUTPUT_FORMAT );
        assertTrue( generated.exists() );
        assertTrue( generated.length() > 0 );
    }
}
