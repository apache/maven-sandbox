package org.apache.maven.plugins.license;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.license.LicenseUtils;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * AbstractLicenseMojo 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractLicenseMojo
    extends AbstractMojo
{
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The Maven Settings.
     *
     * @parameter default-value="${settings}"
     * @required
     * @readonly
     */
    protected Settings settings;

    /**
     * The list of fileSets to process, in addition to the default directories.
     *
     * @parameter
     */
    protected List filesets;

    /**
     * @component role="org.apache.maven.plugins.license.filetype.AbstractFileType"
     */
    protected List filetypeHandlers;

    /**
     * @component
     */
    protected LicenseUtils licenseUtils;

    /** 
     * Finds and retrieves included and excluded files, and handles their 
     * deletion
     */
    protected FileSetManager fileSetManager;

    /**
     * Sets whether the plugin runs in verbose mode.
     *
     * @parameter expression="${license.verbose}" default-value="false"
     */
    protected boolean verbose;

    public void ensureDefaultFilesets()
    {
        if ( filesets == null )
        {
            filesets = new ArrayList();
        }

        Fileset rootFileset = new Fileset();
        rootFileset.setDirectory( project.getBasedir().getAbsolutePath() );
        rootFileset.addInclude( "pom.xml" );

        filesets.add( rootFileset );

        Fileset srcFileset = new Fileset();
        srcFileset.setDirectory( "src" );
        srcFileset.addInclude( "**/*.java" );
        srcFileset.addInclude( "**/*.xml" );
        srcFileset.addInclude( "**/*.properties" );
        srcFileset.addInclude( "**/*.html" );
        srcFileset.addInclude( "**/*.htm" );

        filesets.add( srcFileset );
    }

    public Fileset getFilesetForDirectory( String directory )
    {
        if ( ( filesets != null ) && ( !filesets.isEmpty() ) )
        {
            Iterator it = filesets.iterator();
            while ( it.hasNext() )
            {
                Fileset fset = (Fileset) it.next();
                if ( StringUtils.equals( directory, fset.getDirectory() ) )
                {
                    return fset;
                }
            }
        }

        Fileset fset = new Fileset();
        fset.setDirectory( directory );
        return fset;
    }

}
