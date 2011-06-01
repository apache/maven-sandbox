/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.mae.prompt;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.apache.maven.mae.conf.MAEConfiguration;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MAEPromptTest
{
    
    @Test
    public void getSelectionWithDefaultReturnsDefaultWhenInputIsBlank()
        throws PromptException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream( "\n".getBytes() );
        
        MAEConfiguration config =
            new MAEConfiguration().withStandardIn( bais ).withStandardOut( new PrintStream( baos ) );
        
        List<String> selections = new ArrayList<String>();
        selections.add( "One" );
        selections.add( "Two" );
        
        int selection = new MAEPrompt( config ).getSelection( "Choose", selections, 1 );
        
        System.out.println( new String( baos.toByteArray() ) );
        
        assertThat( selection, equalTo( 1 ) );
    }

}
