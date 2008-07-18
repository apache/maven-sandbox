package org.apache.maven.project.builder;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.model.InputStreamDomainModel;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;

/**
 * Provides a wrapper for the maven model.
 */
public final class
        PomClassicDomainModel implements InputStreamDomainModel {

    private byte[] inputBytes;

    private String eventHistory;

    private Model model;


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
        Writer out = WriterFactory.newXmlWriter( baos );
        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write( out, model );
        out.close();
        inputBytes = removeIllegalCharacters(baos.toByteArray());
    }

    public PomClassicDomainModel(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream: null");
        }
        this.inputBytes = removeIllegalCharacters(IOUtil.toByteArray(inputStream));
    }

    public boolean matchesModel(Model a) {
        Model model;
        try {
            model = getModel();
        } catch (IOException e) {
            return false;
        }

        String groupId = (model.getGroupId() == null) ? model.getParent().getGroupId() : model.getGroupId();
        String artifactId = (model.getArtifactId() == null) ? model.getParent().getArtifactId() : model.getArtifactId();
        String version = (model.getVersion() == null) ? model.getParent().getVersion() : model.getVersion();

        String aGroupId = (a.getGroupId() == null) ? a.getParent().getGroupId() : a.getGroupId();
        String aArtifactId = (a.getArtifactId() == null) ? a.getParent().getArtifactId() : a.getArtifactId();
        String aVersion = (a.getVersion() == null) ? a.getParent().getVersion() : a.getVersion();

        return groupId.equals(aGroupId) && artifactId.equals(aArtifactId) && version.equals(aVersion);
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
        try
        {
            return IOUtil.toString( ReaderFactory.newXmlReader( new ByteArrayInputStream( inputBytes ) ) );
        }
        catch ( IOException ioe )
        {
            // should not occur: everything is in-memory
            return "";
        }
    }

    /**
     * Returns maven model
     *
     * @return maven model
     */
    public Model getModel() throws IOException {
        if(model != null) {
            return model;
        }
        try {                                                                
            return new MavenXpp3Reader().read( ReaderFactory.newXmlReader(new ByteArrayInputStream( inputBytes )) );
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    public InputStream getInputStream() {
        byte[] copy = new byte[inputBytes.length];
        System.arraycopy(inputBytes, 0, copy, 0, inputBytes.length);
        return new ByteArrayInputStream(copy);
    }

    public String getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(String eventHistory) {
        if(eventHistory == null) {
            throw new IllegalArgumentException("eventHistory: null");
        }
        //System.out.println(eventHistory);
        this.eventHistory = eventHistory;
    }

    public boolean equals(Object o) {
        return o instanceof PomClassicDomainModel && this.asString().equals(((PomClassicDomainModel) o).asString());
    }

    //TODO: Workaround
    private byte[] removeIllegalCharacters(byte[] bytes) {
        // what is it supposed to do? which are the illegal characters to remove?
        // for encoding support, new String(bytes) and String.getBytes() should not be used
        return new String(bytes).replaceAll("&oslash;", "").replaceAll("&(?![a-zA-Z]{1,8};)", "&amp;").getBytes();
    }
}
