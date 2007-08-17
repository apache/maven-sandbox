package org.apache.maven.doxia.module.fo;

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

/**
 * <code>FO Sink</code> Test utilities.
 */
public class FoTestUtils
{

    /**
     * Converts an FO file to a PDF file using FOP.
     * @param fo the FO file.
     * @param pdf the target PDF file.
     * @param resourceDir The base directory for relative path resolution.
     * If null, defaults to the parent directory of fo.
     * @throws IOException In case of an I/O problem.
     * @throws FOPException In case of a FOP problem.
     * @throws TransformerException In case of a transformer problem.
     * @throws TransformerConfigurationException As above.
     */
    public static void convertFO2PDF( File fo, File pdf, String resourceDir )
        throws IOException, FOPException, TransformerConfigurationException, TransformerException
    {

        FopFactory fopFactory = FopFactory.newInstance();
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        foUserAgent.setBaseURL( getBaseURL( fo, resourceDir ) );
        OutputStream out = null;

        try
        {
            out = new BufferedOutputStream( new FileOutputStream( pdf ) );
            Fop fop = fopFactory.newFop( MimeConstants.MIME_PDF, foUserAgent, out );

            Source src = new StreamSource( fo);
            Result res = new SAXResult( fop.getDefaultHandler() );

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity transformer
            transformer.transform(src, res);
        }
        finally
        {
            if ( out != null )
            {
                out.close();
            }
        }
    }

    private static String getBaseURL( File fo, String resourceDir )
    {
        String url = null;
        if ( resourceDir == null )
        {
            url = "file:///" + fo.getParent() + "/";
        }
        else
        {
            url = "file:///" + resourceDir + "/";
        }
        return url;
    }

}
