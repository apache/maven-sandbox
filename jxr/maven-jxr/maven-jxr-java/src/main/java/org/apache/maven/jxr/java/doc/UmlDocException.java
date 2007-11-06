package org.apache.maven.jxr.java.doc;

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
 * Signals an error when processing UmlDoc.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class UmlDocException
    extends Exception
{
    static final long serialVersionUID = -6738054815023953478L;

    /**
     * Constructs am exception with no descriptive information.
     */
    public UmlDocException()
    {
        super();
    }

    /**
     * Constructs an exception with the given descriptive message.
     *
     * @param message
     */
    public UmlDocException( String message )
    {
        super( message );
    }

    /**
     * Constructs an exception with the given message and exception as
     * a root cause.
     *
     * @param message
     * @param cause
     */
    public UmlDocException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
