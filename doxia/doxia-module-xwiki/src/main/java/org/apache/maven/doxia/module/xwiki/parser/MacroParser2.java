package org.apache.maven.doxia.module.xwiki.parser;

import java.util.regex.Pattern;

public class MacroParser2
{
    private static final Pattern MACRO_PATTERN = Pattern.compile( "(?:(.*)(?::)?)?(.*)?(?:/)}(?:(.*)\\{(?:\\/)\1})?" );
/*
    public int parse( String input, int position, List blocks )
        throws ParseException
    {
        Matcher m = MACRO_PATTERN.matcher(input.substring(position));
        m.find();
        Map parameters = new HashMap();
        if (m.group(2) != null && m.group(2).length() > 0) {
           StringTokenizer st = new StringTokenizer(m.group(2), "|");
           while (st.hasMoreTokens()) {
               String param = st.nextToken();
               StringTokenizer st2 = new StringTokenizer(param, "=");
               parameters.put(st2.nextToken(), st2.nextToken());
           }
        }

        return position + m.
    }*/
}
