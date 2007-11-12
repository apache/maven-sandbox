package org.apache.maven.jxr.ant.doc;

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

import java.io.File;
import java.io.IOException;

/**
 * Interface to generate documentation (images and HTML) from an <a href="http://ant.apache.org/">Ant</a> buildfile.
 * The image/HTML shows the Ant targets dependency.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface AntDoc
{
    /** The Plexus lookup role. */
    String ROLE = AntDoc.class.getName();

    /**
     * Generate documentation from an <a href="http://ant.apache.org/">Ant</a> buildfile.
     *
     * @param antFile the Ant file, typically <code>build.xml</code>, should be not null.
     * @param destDir the destination directory of the generated documentation, should be not null
     * @throws IOException if any
     * @throws AntDocException if any
     */
    void generate( File antFile, File destDir )
        throws IOException, AntDocException;

    /**
     * Generate documentation from an <a href="http://ant.apache.org/">Ant</a> buildfile.
     *
     * @param graphExecutable the graph executable to use, for instance <a href="http://www.graphviz.org/">Graphviz</a>, should be not null.
     * @param antFile the Ant file, typically <code>build.xml</code>, should be not null.
     * @param destDir the destination directory of the generated documentation, should be not null
     * @throws IOException if any
     * @throws AntDocException if any
     */
    void generate( File graphExecutable, File antFile, File destDir )
        throws IOException, AntDocException;
}
