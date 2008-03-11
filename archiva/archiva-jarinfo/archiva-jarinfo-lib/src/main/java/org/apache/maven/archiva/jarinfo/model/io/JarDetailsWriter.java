package org.apache.maven.archiva.jarinfo.model.io;

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

import org.apache.commons.io.IOUtils;
import org.apache.maven.archiva.jarinfo.JarInfoException;
import org.apache.maven.archiva.jarinfo.model.JarDetails;
import org.apache.maven.archiva.jarinfo.model.xml.JarDetailsXmlSerializer;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * JarDetailsWriter - write the details out to XML 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JarDetailsWriter {
    
    public void write(JarDetails details, File outputFile)
        throws IOException, JarInfoException
    {
        FileWriter writer = null;
        try
        {
            writer = new FileWriter( outputFile );
            write(details, writer);
        }
        finally
        {
            IOUtils.closeQuietly( writer );
        }
    }
    
    public void write(JarDetails details, Writer writer)
        throws IOException, JarInfoException
    {
        Document doc = JarDetailsXmlSerializer.serialize( details );
        
        // Write it out to disk.
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" );
        XMLWriter xmlwriter = new XMLWriter(writer, outformat);
        xmlwriter.write(doc);
        xmlwriter.flush();
    }
}
