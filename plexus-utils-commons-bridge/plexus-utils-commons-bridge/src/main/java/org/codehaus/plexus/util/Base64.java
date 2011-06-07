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

@Deprecated
public class Base64
{
    public Base64()
    {
        super();
    }

    public static boolean isArrayByteBase64(byte[] arrayOctect)
    {
        return org.apache.commons.codec.binary.Base64.isArrayByteBase64( arrayOctect );
    }

    public static byte[] encodeBase64(byte[] binaryData)
    {
        return org.apache.commons.codec.binary.Base64.encodeBase64( binaryData );
    }

    public static byte[] encodeBase64Chunked(byte[] binaryData)
    {
        return org.apache.commons.codec.binary.Base64.encodeBase64Chunked( binaryData );
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked)
    {
        return org.apache.commons.codec.binary.Base64.encodeBase64( binaryData, isChunked );
    }

    public static byte[] decodeBase64(byte[] base64Data)
    {
        return org.apache.commons.codec.binary.Base64.decodeBase64( base64Data );
    }

    public byte[] encode(byte[] pArray)
    {
        return org.apache.commons.codec.binary.Base64.encodeBase64( pArray );
    }

    public byte[] decode(byte[] pArray)
    {
        return org.apache.commons.codec.binary.Base64.decodeBase64( pArray );
    }
}
