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


import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


/**
 * This is a stub test that demonstrates that something is here. It's not 
 * worthwhile to build a complex test to prove that the current javolution
 * is identical in behavior with the copy captured in plexus-utils.
 *
 */
public class FastMapTest extends Assert
{
    @SuppressWarnings( "rawtypes" )
    @Test
    public void simpleTest() {
        FastMap map = new FastMap();
        map.put( "red", "green" );
        map.put( "braised", "roasted" );
        assertEquals( 2, map.size() );
        Iterator it = map.entrySet().iterator();
        Object meo1 = it.next();
        Object meo2 = it.next();
        Map.Entry me = (Map.Entry)meo1;
        assertEquals( "red", me.getKey() );
        me = (Map.Entry)meo2;
        assertEquals( "braised", me.getKey() );
    }
    
}
