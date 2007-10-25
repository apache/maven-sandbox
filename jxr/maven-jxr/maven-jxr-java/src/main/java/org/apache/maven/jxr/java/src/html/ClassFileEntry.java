package org.apache.maven.jxr.java.src.html;

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
 * Class file entry used in Pass2 class.
 *
 * @author  <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version  $Id$
 */
class ClassFileEntry
{
    private String className;

    private String fileName;

    /**
     * Constructor ClassFile
     *
     * @param className
     * @param fileName
     */
    ClassFileEntry( String className, String fileName )
    {
        setClassName( className );
        setFileName( fileName );
    }

    String getClassName()
    {
        return className;
    }

    void setClassName( String className )
    {
        this.className = className;
    }

    String getFileName()
    {
        return fileName;
    }

    void setFileName( String fileName )
    {
        this.fileName = fileName;
    }
}
