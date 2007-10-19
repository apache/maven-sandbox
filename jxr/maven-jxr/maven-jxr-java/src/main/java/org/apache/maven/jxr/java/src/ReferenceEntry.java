package org.apache.maven.jxr.java.src;

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

import java.util.StringTokenizer;

/**
 * An entry in the references.txt file used in Pass2 class.
 *
 * @author  <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version  $Id$
 */
class ReferenceEntry
{
    private String referentFileClass;

    private String referentClass;

    private String referentType;

    private String referentTag;

    private String referringPackage;

    private String referringClass;

    private String referringMethod;

    private String referringFile;

    private String referringLineNumber;

    /**
     * Constructor Reference
     *
     * @param line
     */
    ReferenceEntry( String line )
    {
        StringTokenizer st = new StringTokenizer( line, "|" );

        setReferentFileClass( st.nextToken() );
        setReferentClass( st.nextToken() );
        setReferentTag( st.nextToken() );
        setReferentType( st.nextToken() );
        setReferringPackage( st.nextToken() );
        setReferringClass( st.nextToken() );
        setReferringMethod( st.nextToken() );
        setReferringFile( st.nextToken() );
        setReferringLineNumber( st.nextToken() );
    }

    String getReferentFileClass()
    {
        return referentFileClass;
    }

    void setReferentFileClass( String referentFileClass )
    {
        this.referentFileClass = referentFileClass;
    }

    String getReferentClass()
    {
        return referentClass;
    }

    void setReferentClass( String referentClass )
    {
        this.referentClass = referentClass;
    }

    String getReferentType()
    {
        return referentType;
    }

    void setReferentType( String referentType )
    {
        this.referentType = referentType;
    }

    String getReferentTag()
    {
        return referentTag;
    }

    void setReferentTag( String referentTag )
    {
        this.referentTag = referentTag;
    }

    String getReferringPackage()
    {
        return referringPackage;
    }

    void setReferringPackage( String referringPackage )
    {
        this.referringPackage = referringPackage;
    }

    String getReferringClass()
    {
        return referringClass;
    }

    void setReferringClass( String referringClass )
    {
        this.referringClass = referringClass;
    }

    String getReferringMethod()
    {
        return referringMethod;
    }

    void setReferringMethod( String referringMethod )
    {
        this.referringMethod = referringMethod;
    }

    String getReferringFile()
    {
        return referringFile;
    }

    void setReferringFile( String referringFile )
    {
        this.referringFile = referringFile;
    }

    String getReferringLineNumber()
    {
        return referringLineNumber;
    }

    void setReferringLineNumber( String referringLineNumber )
    {
        this.referringLineNumber = referringLineNumber;
    }
}
