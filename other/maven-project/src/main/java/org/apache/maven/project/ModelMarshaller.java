package org.apache.maven.project;

import java.util.*;

public class ModelMarshaller {

    private final static int basePosition = ModelUri.BASE.getUri().length();

    public List<ModelProperty> marshallXmlToModelProperties(String xml, Collection<MarshalProperty> marshalProperties) {
        int state = 0;
        List<ModelProperty> modelProperties = new ArrayList<ModelProperty>();
         /*
        Stack x = new Stack();
        x.addAll(Arrays.asList(xml.getBytes()));
        while(!x.isEmpty()) {
            if(x.peek().equals("<")) {
                state = 1;
                x.pop();
                while(x.pop()) {

                }
            } else if {

            }
        }
        */
        return null;
    }

    private static class Tag {
        static final int START = 0;

        static final int END = 1;

        private String value;

        private int tagType;

        public int getTagType() {
            return tagType;
        }

        public void setTagType(int tagType) {
            this.tagType = tagType;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    public String unmarshalModelPropertiesToXml(List<ModelProperty> modelProperties) {
        StringBuffer sb = new StringBuffer();
        List<String> lastUriTags = new ArrayList<String>();
        for (ModelProperty mp : modelProperties) {
            String uri = mp.getUri();
            List<String> tagNames = getTagNamesFromUri(uri);
            if (lastUriTags.size() > tagNames.size()) {
                for (int i = lastUriTags.size() - 1; i >= tagNames.size(); i--) {
                    sb.append(toEndTag(lastUriTags.get(i - 1)));
                }
            }
            String tag = tagNames.get(tagNames.size() - 1);
            sb.append(toStartTag(tag));
            if (mp.getValue() != null) {
                sb.append(mp.getValue());
                sb.append(toEndTag(tag));
            }
            lastUriTags = tagNames;
        }
        for (int i = lastUriTags.size() - 2; i >= 0; i--) {
            sb.append(toEndTag(lastUriTags.get(i)));
        }
        return sb.toString();
    }

    private static List<String> getTagNamesFromUri(String uri) {
        List<String> methodNames = new ArrayList<String>();
        for (String name : uri.substring(basePosition).replace("#collection", "").split("/")) {
            methodNames.add(name);
        }
        return methodNames;
    }

    static String toStartTag(String value) {
        StringBuffer sb = new StringBuffer();
        sb.append("<" + value + ">\r\n");
        return sb.toString();
    }

    static String toEndTag(String value) {
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n</" + value + ">\r\n");
        return sb.toString();
    }
}
