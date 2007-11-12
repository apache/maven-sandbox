package org.apache.maven.jxr.java.src;

import java.io.File;
import java.io.IOException;

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
 * Interface to generate Java cross references.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface JavaSrc
{
    /** The Plexus lookup role. */
    String ROLE = JavaSrc.class.getName();

    /**
     * Generate Java cross references with default options.
     *
     * @param srcDir the java source directory, should be not null
     * @param destDir the output directory, should be not null
     * @throws IOException if any
     * @see #generate(File, File, JavaSrcOptions)
     */
    void generate( File srcDir, File destDir )
        throws IOException;

    /**
     * Generate Java cross references.
     *
     * @param srcDir the java source directory, should be not null
     * @param destDir the output directory, should be not null
     * @param options a set of options, should be not null
     * @throws IOException if any
     */
    void generate( File srcDir, File destDir, JavaSrcOptions options )
        throws IOException;
}
