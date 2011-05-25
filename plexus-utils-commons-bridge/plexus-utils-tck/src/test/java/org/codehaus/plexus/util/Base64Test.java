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

import org.junit.Test;

import static org.apache.maven.tck.TckMatchers.hasDefaultConstructor;
import static org.apache.maven.tck.TckMatchers.isFinalClass;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class Base64Test
{


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
        assertThat( new String( new Base64().encode( "test".getBytes() ) ), is( "dGVzdA==" ) );
    }

    @Test
    public void decode()
        throws Exception
    {
        assertThat( new String( new Base64().decode( "dGVzdA==".getBytes() ) ), is( "test" ) );
    }

    @Test
    public void encodeBase64()
        throws Exception
    {
        assertThat( new String( Base64.encodeBase64( "test".getBytes() ) ), is( "dGVzdA==" ) );
        assertThat( new String( Base64.encodeBase64( "test".getBytes(), false ) ), is( "dGVzdA==" ) );
    }

    @Test
    public void encodeBase64Chunked()
        throws Exception
    {
        assertThat( new String( Base64.encodeBase64(
            "some long long long long long long long long long long long long long long text".getBytes(), true ) ),
                    is( "c29tZSBsb25nIGxvbmcgbG9uZyBsb25nIGxvbmcgbG9uZyBsb25nIGxvbmcgbG9uZyBsb25nIGxv\r\n"
                            + "bmcgbG9uZyBsb25nIGxvbmcgdGV4dA==\r\n" ) );
    }

    @Test
    public void decodeBase64()
        throws Exception
    {
        assertThat( new String( Base64.decodeBase64( "dGVzdA==".getBytes() ) ), is( "test" ) );
    }

    @Test
    public void isArrayByteBase64()
        throws Exception
    {
        String valid = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        assertThat( Base64.isArrayByteBase64( valid.getBytes() ), is( true ) );
    }

}
