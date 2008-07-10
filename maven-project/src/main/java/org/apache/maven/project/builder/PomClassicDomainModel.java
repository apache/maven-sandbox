package org.apache.maven.project.builder;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.model.InputStreamDomainModel;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;

/**
 * Provides a wrapper for the maven model.
 */
public final class PomClassicDomainModel implements InputStreamDomainModel {

    private byte[] inputStream;

    private String eventHistory;

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
        inputStream = removeIllegalCharacters(baos.toByteArray());
    }

    public PomClassicDomainModel(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream: null");
        }
        this.inputStream = removeIllegalCharacters(IOUtil.toByteArray(inputStream));
    }

    public boolean matchesParent(Parent parent) {
        Model model;
        try {
            model = getModel();
        } catch (IOException e) {
            return false;
        }

        String groupId = (model.getGroupId() == null) ? model.getParent().getGroupId() : model.getGroupId();
        String artifactId = (model.getArtifactId() == null) ? model.getParent().getArtifactId() : model.getArtifactId();
        String version = (model.getVersion() == null) ? model.getParent().getVersion() : model.getVersion();

        return (parent.getGroupId().equals(groupId) && parent.getArtifactId().equals(artifactId)
                && parent.getVersion().equals(version));
    }

    public String asString() {
        return new String(inputStream);
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
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    public InputStream getInputStream() {
        byte[] copy = new byte[inputStream.length];
        System.arraycopy(inputStream, 0, copy, 0, inputStream.length);
        return new ByteArrayInputStream(copy);
    }

    public String getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(String eventHistory) {
        this.eventHistory = eventHistory;
    }

    //TODO: Workaround
    private byte[] removeIllegalCharacters(byte[] bytes) {
        return new String(bytes).replaceAll("&oslash;", "").replaceAll("&(?![a-zA-Z]{1,8};)", "&amp;").getBytes();
    }
}
