package org.apache.maven.project.builder;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.model.InputStreamDomainModel;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;


import java.io.*;
import java.util.Arrays;

/**
 * Provides a wrapper for the maven model.
 */
public final class PomClassicDomainModel implements InputStreamDomainModel {

    private byte[] inputStream;

    /**
     * Constructor
     *
     * @param model maven model
     */
    public PomClassicDomainModel(Model model) throws IOException {
        if (model == null) {
            throw new IllegalArgumentException("model: null");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new OutputStreamWriter(baos), model);
        inputStream = baos.toByteArray();
    }

    public PomClassicDomainModel(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream: null");
        }
        this.inputStream = IOUtil.toByteArray(inputStream);
    }

    /**
     * Returns maven model
     *
     * @return maven model
     */
    public Model getModel() throws IOException {
        try {
            return new MavenXpp3Reader().read(new StringReader(new String(inputStream)));
        }
        catch (XmlPullParserException e) {
            throw new IOException(e);
        }
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(Arrays.copyOf(inputStream, inputStream.length));
    }
}
