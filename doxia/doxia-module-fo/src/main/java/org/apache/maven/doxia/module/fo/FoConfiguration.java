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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


/**
 * A utility class to construct FO configuration parameters..
 */
public class FoConfiguration
{

    /** Holds the single attributes. */
    private StringBuffer buffer;

    /** The configuration instance. */
    private final XMLConfiguration config;

    /** The list of attribute sets. */
    private final List sets;

    // TODO: add constructor to override default configuration

    /** Constructor. */
    public FoConfiguration()
    {
        this.config = new XMLConfiguration();
        // necessary because some attributes contain commas:
        config.setDelimiterParsingDisabled( true );

        try
        {
            config.load( getClass().getResourceAsStream( "/fo-styles.xslt" ) );
        }
        catch ( ConfigurationException cex )
        {
            throw new RuntimeException( cex ); // TODO
        }

        this.sets = config.getList( "xsl:attribute-set[@name]" );
        reset();
    }

    /** Builds a list of attributes.
     * @param attributeId A unique id to identify the set of attributes.
     * This should correspond to the name of an attribute-set
     * defined in the configuration file.
     * @return A string that contains a list of attributes with
     * the values configured for the current builder. Returns the
     * empty string if attributeId is null or if attributeId
     * is not a valid identifier.
     */
    public String getAttributeSet( String attributeId )
    {
        if ( attributeId == null )
        {
            return "";
        }

        reset();
        addAttributes( attributeId );
        return buffer.toString();
    }

    private void addAttributes( String attributeId )
    {
        int index = sets.indexOf( attributeId );
        String keybase = "xsl:attribute-set(" + String.valueOf( index ) + ")";

        Object prop = config.getProperty( keybase + ".xsl:attribute" );
        if ( prop instanceof List )
        {
            List values = (List) prop;
            List keys = config.getList( keybase + ".xsl:attribute[@name]" );
            for ( int i = 0; i < values.size(); i++ )
            {
                buffer.append( " " + keys.get( i ) + "=\"" + values.get( i ) + "\"" );
            }
        }
        else if ( prop instanceof String )
        {
            String value = config.getString( keybase + ".xsl:attribute" );
            String key = config.getString( keybase + ".xsl:attribute[@name]" );
            buffer.append( " " + key + "=\"" + value + "\"" );
        }

        String extend = config.getString( keybase + "[@use-attribute-sets]" );
        if ( extend != null )
        {
            addAttributes( extend );
        }
    }

    private void reset()
    {
        this.buffer = new StringBuffer( 512 );
    }

}
