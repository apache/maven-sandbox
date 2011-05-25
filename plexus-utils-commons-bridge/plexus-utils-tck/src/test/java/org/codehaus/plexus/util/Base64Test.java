package org.codehaus.plexus.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static org.apache.maven.tck.TckMatchers.hasDefaultConstructor;
import static org.apache.maven.tck.TckMatchers.isFinalClass;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class Base64Test {


    @Test
    public void isNotUtilityClass()
        throws Exception
    {
        assertThat( Base64.class, allOf( hasDefaultConstructor(), not( isFinalClass() ) ) );
    }

    @Test
    public void encode()
        throws Exception
    {
        assertEquals("dGVzdA==", new Base64().encode("test".getBytes()));
    }

    @Test
    public void decode()
        throws Exception
    {
        assertEquals( "test", new Base64().decode("dGVzdA==".getBytes()) );
    }

    @Test
    public void encodeBase64()
            throws Exception
    {
        assertEquals( "dGVzdA==", Base64.encodeBase64( "test".getBytes() ) );
        assertEquals( "dGVzdA==", Base64.encodeBase64( "test".getBytes(), false ) );
    }

    @Test
    public void encodeBase64Chunked()
            throws Exception
    {
        assertEquals(
                "c29tZSBsb25nIGxvbmcgbG9uZyBsb25nIGxvbmcgbG9uZyBsb25nIGxvbmcgbG9uZyBsb25nIGxv\r\n" +
                "bmcgbG9uZyBsb25nIGxvbmcgdGV4dA==\r\n",
                Base64.encodeBase64(
                "some long long long long long long long long long long long long long long text"
                        .getBytes(), true ) );
    }

    @Test
    public void decodeBase64()
        throws Exception
    {
        assertEquals( "test", Base64.decodeBase64( "dGVzdA==".getBytes() ) );
    }

    @Test
    public void isArrayByteBase64()
        throws Exception
    {
        String valid = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        assertTrue( Base64.isArrayByteBase64( valid.getBytes() ) );
    }

    private static void assertEquals( String expected , byte[] actual )
    {
        Assert.assertEquals(expected, new String(actual));
    }
}
