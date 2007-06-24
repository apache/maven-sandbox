package org.apache.maven.doxia.module.fo;

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

import junit.framework.TestCase;

/** FoConfiguration tests. */
public class FoConfigurationTest extends TestCase
{

    /** Tests the getAttributes( String ) method. */
    public void testGetAttributes()
    {
        FoConfiguration attributes = new FoConfiguration();

        assertEquals(
            "Null attribute ID should return empty string!",
            "", attributes.getAttributeSet( null ) );

        assertEquals(
            "Non existent attribute ID should return empty string!",
            "", attributes.getAttributeSet( "a.dummy.attribute" ) );

        assertEquals(
            "Wrong attributes returned!",
            " font-size=\"10pt\" font-family=\"monospace\"",
            attributes.getAttributeSet( "body.pre" ) );
    }

}
