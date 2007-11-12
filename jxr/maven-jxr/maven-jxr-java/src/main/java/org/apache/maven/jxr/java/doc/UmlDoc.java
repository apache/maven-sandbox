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

import java.io.File;
import java.io.IOException;

/**
 * Interface to generate <a href="http://www.uml.org/>UML</a> class diagram.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface UmlDoc
{
    /** The Plexus lookup role. */
    String ROLE = UmlDoc.class.getName();

    /**
     * Generate the UML class diagram as image.
     * <br/>
     * The image format is based on the out extension, or <a href="http://www.w3.org/Graphics/SVG/">SVG</a> if none.
     *
     * @param srcDir the source directory, should be not null
     * @param srcEncoding the source encoding, could be null
     * @param out the generated class diagram file, should be not null
     * @param outEncoding the output encoding, could be null
     * @throws IOException if any
     * @throws UmlDocException if any
     */
    void generate( File srcDir, File out )
        throws IOException, UmlDocException;

    /**
     * Generate the UML class diagram as image.
     * <br/>
     * The image format is based on the out extension, or <a href="http://www.w3.org/Graphics/SVG/">SVG</a> if none.
     *
     * @param srcDir the source directory, should be not null
     * @param srcEncoding the source encoding, could be null
     * @param out the generated class diagram file, should be not null
     * @param outEncoding the output encoding, could be null
     * @throws IOException if any
     * @throws UmlDocException if any
     */
    void generate( File srcDir, String srcEncoding, File out, String outEncoding )
        throws IOException, UmlDocException;

    /**
     * Generate the UML class diagram as image.
     * <br/>
     * The image format is based on the out extension, or <a href="http://www.w3.org/Graphics/SVG/">SVG</a> if none.
     *
     * @param graphExecutable the graph executable to use, for instance <a href="http://www.graphviz.org/">Graphviz</a>, should be not null.
     * @param srcDir the source directory, should be not null
     * @param srcEncoding the source encoding, could be null
     * @param out the generated class diagram file, should be not null
     * @param outEncoding the output encoding, could be null
     * @throws IOException if any
     * @throws UmlDocException if any
     */
    void generate( File graphExecutable, File srcDir, String srcEncoding, File out, String outEncoding )
        throws IOException, UmlDocException;

    /**
     * Setter for the UML class diagram label
     *
     * @param diagramLabel the UML class diagram label to set
     */
    public void setDiagramLabel( String diagramLabel );

    /**
     * Setter for the javadoc relative path.
     * <br/>
     * Will be used only if the format of the generated UML class image supports links, like
     * <a href="http://www.w3.org/Graphics/SVG/">SVG</a>.
     *
     * @param javadocPath the relative path or URI to the generated javadoc directory
     */
    public void setJavadocPath( String javadocPath );

    /**
     * Setter for the java source cross relative path.
     * <br/>
     * Will be used only if the format of the generated UML class image supports links, like
     * <a href="http://www.w3.org/Graphics/SVG/">SVG</a>.
     *
     * @param javasrcPath the relative path or URI to the generated java source cross directory
     */
    public void setJavasrcPath( String javasrcPath );

    /**
     * Specify the visibility members to include in the UML class diagram. Possible values are:
     * <ul>
     * <li>public: shows only public classes and members</li>
     * <li>protected: shows only public and protected classes and members</li>
     * <li>package: shows all classes and members not marked private</li>
     * <li>private: shows all classes and members</li>
     * <li>"" (i.e. empty): nothing</li>
     * </ul>
     *
     * @param show the show to set
     */
    public void setShow( String show );
}
