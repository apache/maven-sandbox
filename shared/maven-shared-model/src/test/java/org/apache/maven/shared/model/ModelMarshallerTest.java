package org.apache.maven.shared.model;


import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelMarshallerTest {

    @Test
    public void unmarshalWithEmptyCollectionTags() throws IOException {
        List<ModelProperty> modelProperties = Arrays.asList(
                new ModelProperty("http://apache.org/maven/project", null),
                new ModelProperty("http://apache.org/maven/project/dependencies#collection", null)
        );
        String xml = ModelMarshaller.unmarshalModelPropertiesToXml(modelProperties, "http://apache.org/maven");
        System.out.println("COMPLETE:" + xml); //TODO: Verify proper xml
    }

    @Test
    public void unmarshalWithSingleProperty() throws IOException {
        List<ModelProperty> modelProperties = Arrays.asList(
                new ModelProperty("http://apache.org/maven/project", null),
                new ModelProperty("http://apache.org/maven/project/modelVersion", "4.0.0")
        );
        String xml = ModelMarshaller.unmarshalModelPropertiesToXml(modelProperties, "http://apache.org/maven");
        System.out.println("COMPLETE:" + xml); //TODO: Verify proper xml
    }
    @Test
    public void unmarshalWithEmptyTags112() throws IOException {
 /*       List<ModelProperty> modelProperties = Arrays.asList(
                new ModelProperty("http://apache.org/maven/project", null),
                new ModelProperty("http://apache.org/maven/project/developers#collection", null),
                new ModelProperty("http://apache.org/maven/project/developers#collection/developer", null),
                new ModelProperty("http://apache.org/maven/project/developers#collection/developer/organization", null),
                new ModelProperty("http://apache.org/maven/project/modelVersion", "4.0.0")
        );    */
         List<ModelProperty> modelProperties = ModelMarshaller.marshallXmlToModelProperties(
                new FileInputStream("C:\\Documents and Settings\\sisbell\\.m2\\repository\\org\\mortbay\\jetty\\project\\6.1.5\\project-6.1.5.pom"),
                "http://apache.org/maven", null);

        String xml = ModelMarshaller.unmarshalModelPropertiesToXml(modelProperties, "http://apache.org/maven");
        System.out.println("COMPLETE:" + xml); //TODO: Verify proper xml
    }

    @Test
    public void unmarshalWithEmptyTags111() throws IOException {
 /*       List<ModelProperty> modelProperties = Arrays.asList(
                new ModelProperty("http://apache.org/maven/project", null),
                new ModelProperty("http://apache.org/maven/project/developers#collection", null),
                new ModelProperty("http://apache.org/maven/project/developers#collection/developer", null),
                new ModelProperty("http://apache.org/maven/project/developers#collection/developer/organization", null),
                new ModelProperty("http://apache.org/maven/project/modelVersion", "4.0.0")
        );    */
         List<ModelProperty> modelProperties = ModelMarshaller.marshallXmlToModelProperties(
                new ByteArrayInputStream("<project><S></S><version>1.2</version><developers><developer><organization></organization></developer></developers><modelVersion>4</modelVersion></project>".getBytes()),
                "http://apache.org/maven", null);

        String xml = ModelMarshaller.unmarshalModelPropertiesToXml(modelProperties, "http://apache.org/maven");
        System.out.println("COMPLETE:" + xml); //TODO: Verify proper xml
    }

    @Test
    public void marshal() throws IOException {
        List<ModelProperty> modelProperties = ModelMarshaller.marshallXmlToModelProperties(
                new ByteArrayInputStream("<project><version>1.1</version></project>".getBytes()),
                "http://apache.org/maven", null);

        assertEquals(2, modelProperties.size());
        assertEquals("http://apache.org/maven/project", modelProperties.get(0).getUri());
        assertEquals("http://apache.org/maven/project/version", modelProperties.get(1).getUri());
        assertEquals("1.1", modelProperties.get(1).getValue());
    }

    /*
    @Test(expected = IllegalArgumentException.class)
    public void unmarshalWithBadBaseUri() throws IOException, XmlPullParserException {
        List<ModelProperty> modelProperties = Arrays.asList(
                new ModelProperty("http://apache.org/maven/project", null),
                new ModelProperty("http://apache.org/maven/project/version", "1.1")
        );

        ModelMarshaller.unmarshalModelPropertiesToXml(modelProperties, "http://apache.org");
    }
     */
    @Test(expected = IllegalArgumentException.class)
    public void unmarshalWithNullBaseUri() throws IOException {
        List<ModelProperty> modelProperties = Arrays.asList(
                new ModelProperty("http://apache.org/maven/project", null)
        );

        ModelMarshaller.unmarshalModelPropertiesToXml(modelProperties, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unmarshalWithEmptyBaseUri() throws IOException {
        List<ModelProperty> modelProperties = Arrays.asList(
                new ModelProperty("http://apache.org/maven/project", null)
        );

        ModelMarshaller.unmarshalModelPropertiesToXml(modelProperties, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unmarshalWithEmptyModelProperties() throws IOException  {
        ModelMarshaller.unmarshalModelPropertiesToXml(new ArrayList<ModelProperty>(), "http://apache.org/maven/project");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unmarshalWithNullModelProperties() throws IOException {
        ModelMarshaller.unmarshalModelPropertiesToXml(null, "http://apache.org/maven/project");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unmarshalWithIncorrectModelPropertyUri() throws IOException  {
        List<ModelProperty> modelProperties = Arrays.asList(
                new ModelProperty("http://apache.org/maven/project", null),
                new ModelProperty("http://bogus.org/maven", "1.1")
        );

        ModelMarshaller.unmarshalModelPropertiesToXml(modelProperties, "http://apache.org/maven");
    }
}
