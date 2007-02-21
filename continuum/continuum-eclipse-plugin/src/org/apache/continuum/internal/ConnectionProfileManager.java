/**
 *    Copyright 2006  <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.continuum.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.continuum.Activator;
import org.apache.continuum.model.ConnectionProfile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Continuum's Connection profile manager.
 * <p>
 * This should handle:<br>
 * <ul>
 * <li>Loading up already created Connection Profiles</li>
 * <li>Create and save new/existing Connection Profiles</li>
 * </ul>
 * TODO: Add method to save a list of {@link {@link ConnectionProfile}}.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class ConnectionProfileManager {

    private static final String ATTR_PASSWORD = "password";

    private static final String ATTR_USERNAME = "username";

    private static final String ATTR_URL = "url";

    private static final String ATTR_NAME = "name";

    private static final String ATTR_ID = "id";

    private static final String ELT_PROFILE = "profile";

    private static final String ELT_CONTINUUM_PROFILES = "continuum-profiles";

    /**
     * File name where Continuum Connection Profiles are stored.
     */
    private static final String DATA_PROFILES = "ContinuumProfiles.dat";

    /**
     * Path to location of Connection profiles.
     */
    private static IPath storageLocation = Activator.getDefault ().getStateLocation ().append (DATA_PROFILES);

    /**
     * XML Document builder to create new XML document or parse an existing one.
     */
    private static DocumentBuilder documentBuilder;

    /**
     * Factory that creates and returns an instance of a {@link DocumentBuilder}
     * implementation.
     */
    private static DocumentBuilderFactory documentBuilderFactory;

    private static Transformer transformer;

    /**
     * Factory to create an instance of XML Transformer
     */
    private static TransformerFactory transFactory;


    /**
     * Loads up all the Continuum Connection profiles from the specified file.
     * 
     * @return {@link List} of {@link ConnectionProfile} instances that were
     *         parsed from the XML store.
     */
    public static List<ConnectionProfile> loadConnectionProfiles() throws CoreException {
        Assert.isNotNull (storageLocation);
        List<ConnectionProfile> list = new ArrayList<ConnectionProfile> ();
        File file = storageLocation.toFile ();
        try {
            // return empty list if no file existed.
            if (!file.exists ()) {
                Activator.getDefault ().getLog ().log (new Status (IStatus.INFO, Activator.PLUGIN_ID, 0, "No Continuum Connection profiles were found to be loaded.", null));
                return list;
            }
            InputStream is = new FileInputStream (file);
            InputSource source = new InputSource (is);
            Document document = getDocumentBuilder ().parse (source);

            // obtain profiles from xml
            NodeList nl = document.getElementsByTagName (ELT_PROFILE);
            for (int i = 0; i < nl.getLength (); i++) {
                Node node = nl.item (i);
                if (!(node instanceof Element))
                    continue;
                // If appropriate element found, then create a ConnectionProfile
                Element elt = (Element) node;
                ConnectionProfile cp = new ConnectionProfile ();
                cp.setId (elt.getAttribute (ATTR_ID));
                cp.setLabel (elt.getAttribute (ATTR_NAME));
                cp.setUsername (elt.getAttribute (ATTR_USERNAME));
                cp.setPassword (elt.getAttribute (ATTR_PASSWORD));
                cp.setConnectionUrl (elt.getAttribute (ATTR_URL));
                list.add (cp);
            }
            Activator.getDefault ().getLog ().log (new Status (IStatus.INFO, Activator.PLUGIN_ID, 0, "Loaded '" + list.size () + "' Continuum Connection profiles.", null));
        } catch (FileNotFoundException e) {
            throw new CoreException (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, "Encounterd FileNotFoundException.", e));//$NON-NLS-1$
        } catch (IOException e) {
            throw new CoreException (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, "Encounterd IOException.", e));//$NON-NLS-1$
        } catch (SAXException e) {
            throw new CoreException (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, "Encounterd SAXException.", e));//$NON-NLS-1$
        }
        return list;
    }


    /**
     * Serializes the Connection Profile to a XML-based persistence store.
     * 
     * @param cp
     *            {@link {@link ConnectionProfile}} instance to serialize.
     */
    public static void saveConnectionProfile(ConnectionProfile cp) throws CoreException {
        Writer writer = null;
        try {
            Assert.isNotNull (storageLocation);
            Document document = null;
            Element rootElt = null;
            File file = storageLocation.toFile ();
            if (!file.exists ()) {
                file.createNewFile ();
                document = getDocumentBuilder ().newDocument ();
            } else {
                document = getDocumentBuilder ().parse (file);
                // Attempt to obtain the root element if the file is existing.
                NodeList nl = document.getElementsByTagName (ELT_CONTINUUM_PROFILES);
                for (int i = 0; i < nl.getLength (); i++) {
                    Node node = nl.item (i);
                    if (!(node instanceof Element))
                        continue;
                    // else, there should be only one Root element.
                    rootElt = (Element) node;
                    break;
                }
            }

            // create root element if it wasn't found.
            if (null == rootElt) {
                rootElt = document.createElement (ELT_CONTINUUM_PROFILES);
                document.appendChild (rootElt);
            }

            OutputStream out = new FileOutputStream (file);
            OutputStreamWriter osWriter = new OutputStreamWriter (out, "UTF8");
            writer = new BufferedWriter (osWriter);

            // write out profiles here
            Element eltProfile = document.createElement (ELT_PROFILE);
            eltProfile.setAttribute (ATTR_ID, cp.getId ());
            eltProfile.setAttribute (ATTR_NAME, cp.getLabel ());
            eltProfile.setAttribute (ATTR_URL, cp.getConnectionUrl ());
            eltProfile.setAttribute (ATTR_USERNAME, cp.getUsername ());
            eltProfile.setAttribute (ATTR_PASSWORD, cp.getPassword ());

            rootElt.appendChild (eltProfile);

            DOMSource domSource = new DOMSource (document);
            StreamResult result = new StreamResult (osWriter);
            getTransformer ().transform (domSource, result);

            Activator.getDefault ().getLog ().log (new Status (IStatus.INFO, Activator.PLUGIN_ID, 0, "Profile saved to : " + file.getAbsolutePath (), null));
        } catch (DOMException e) {
            throw new CoreException (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, "Encountered DOMException.", e));//$NON-NLS-1$
        } catch (IOException e) {
            throw new CoreException (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, "Encounterd IOException.", e));//$NON-NLS-1$
        } catch (TransformerException e) {
            throw new CoreException (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, "Encountered TransformerException.", e));//$NON-NLS-1$
        } catch (SAXException e) {
            throw new CoreException (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, "Encountered SAXException. Unable to parse profiles.", e));//$NON-NLS-1$
        } finally {
            if (null != writer)
                try {
                    writer.close ();
                } catch (IOException e) {
                    // swallow
                }
        }
    }


    /**
     * Internal method to get a handle to an XML document builder.
     * 
     * @return
     */
    private static DocumentBuilder getDocumentBuilder() {
        if (documentBuilder == null) {
            documentBuilderFactory = DocumentBuilderFactory.newInstance ();
            documentBuilderFactory.setNamespaceAware (true);
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder ();
            } catch (ParserConfigurationException e) {
                e.printStackTrace ();
                Activator.getDefault ().getLog ().log (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, "Unable to obtain DocumentBuilder from DocumentBuilderFactory instance ", e));
            }
        }
        return documentBuilder;
    }


    /**
     * Internal method to get a handle to an XML document transformer.
     * 
     * @return
     */
    private static Transformer getTransformer() {
        if (transformer == null) {
            transFactory = TransformerFactory.newInstance ();
            try {
                transformer = transFactory.newTransformer ();
            } catch (TransformerConfigurationException e) {
                Activator.getDefault ().getLog ().log (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, "Unable to obtain Transformer from TransformerFactory instance ", e));
            }
        }
        return transformer;
    }

}
