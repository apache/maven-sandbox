package org.apache.maven.doxia.editor.model;

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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DoxiaAttribute
{
    public final static String TYPE = "DOXIA_TYPE";

    public final static DoxiaAttribute TITLE = new DoxiaAttribute( "TITLE" );
    public final static DoxiaAttribute AUTHOR = new DoxiaAttribute( "AUTHOR" );
    public final static DoxiaAttribute DATE = new DoxiaAttribute( "DATE" );
    public final static DoxiaAttribute SECTION_1 = new DoxiaAttribute( "SECTION_1" );
    public final static DoxiaAttribute SECTION_2 = new DoxiaAttribute( "SECTION_2" );
    public final static DoxiaAttribute SECTION_3 = new DoxiaAttribute( "SECTION_3" );
    public final static DoxiaAttribute SECTION_4 = new DoxiaAttribute( "SECTION_4" );
    public final static DoxiaAttribute SECTION_5 = new DoxiaAttribute( "SECTION_5" );
    public final static DoxiaAttribute TEXT = new DoxiaAttribute( "TEXT" );
    public final static DoxiaAttribute PARAGRAPH_SEPARATOR = new DoxiaAttribute( "PARAGRAPH_SEPARATOR" );

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private final String value;

    public DoxiaAttribute( String value )
    {
        this.value = value;
    }

    public static String getType()
    {
        return TYPE;
    }

    public String toString()
    {
        return "DoxiaAttribute: " + value;
    }
}
