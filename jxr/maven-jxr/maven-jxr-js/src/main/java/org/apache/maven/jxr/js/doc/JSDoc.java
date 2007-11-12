package org.apache.maven.jxr.js.doc;

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

import java.io.IOException;

/**
 * Interface to generate documentation for <a href="http://developer.mozilla.org/en/docs/JavaScript">Javascript</a> files.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface JSDoc
{
    /** The Plexus lookup role. */
    String ROLE = JSDoc.class.getName();

    /**
     * Generate Javascript documentation
     *
     * @param jsDirectory the Javascript source directory, should be not null
     * @param destDir the destination directory, should be not null
     * @throws IOException if any
     */
    void generate( String jsDirectory, String destDir )
        throws IOException;
}
