package org.apache.maven.report.downloads;

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

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.StringUtils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class DownloadLinksRenderer
    extends AbstractMavenReportRenderer
{
    protected I18N i18n;

    protected Locale locale;

    private String name;

    public DownloadLinksRenderer( Sink sink, I18N i18n, Locale locale )
    {
        super( sink );

        this.i18n = i18n;

        this.locale = locale;
    }

    @Override
    public String getTitle()
    {
        return format( "download-links", "title", getName() ) ;
    }

    protected String getI18nString( String key )
    {
        return getI18nString( "download-links", key );
    }

    protected String getI18nString( String section, String key )
    {
        return i18n.getString( "download-links-report", locale, "report." + section + '.' + key );
    }

    protected String format( String section, String key, Object arg1 )
    {
        return i18n.format( "download-links-report", locale, "report." + section + '.' + key, arg1 );
    }

    protected String format( String section, String key, Object arg1, Object arg2 )
    {
        return i18n.format( "download-links-report", locale, "report." + section + '.' + key, arg1, arg2 );
    }

    protected String format( String section, String key, Object[] args )
    {
        return i18n.format( "download-links-report", locale, "report." + section + '.' + key, args );
    }

    @Override
    protected void text( String text )
    {
        if ( StringUtils.isEmpty( text ) ) // Take care of spaces
        {
            sink.text( "-" );
        }
        else
        {
            // custombundle text with xml?
            String regex = "(.+?)<(\"[^\"]*\"|'[^']*'|[^'\">])*>(.+?)";
            if ( Pattern.matches( regex, text ) )
            {
                sink.rawText( text );
            }
            else
            {
                sink.text( text );
            }
        }
    }

    @Override
    protected void renderBody()
    {
        startSection( getTitle() );
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
