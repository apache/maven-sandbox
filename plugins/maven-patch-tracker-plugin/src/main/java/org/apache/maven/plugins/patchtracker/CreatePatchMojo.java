package org.apache.maven.plugins.patchtracker;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.diff.DiffScmResult;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;

import java.io.File;

/**
 * Goal which create a diff/patch file from the current project and create an issue in the project
 * with attaching the created patch file
 *
 * @goal create
 * @aggregator
 */
public class CreatePatchMojo
    extends AbstractMojo
{
    /**
     * The Maven Project Object.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter default-value="${basedir}"
     * @required
     * @readonly
     */
    protected File baseDir;

    /**
     * @component
     */
    protected ScmManager scmManager;

    /**
     * @parameter expression="${scm.providerType}" default-value=""
     */
    protected String providerType = "";

    public void execute()
        throws MojoExecutionException
    {
        // TODO do a status before and complains if some files in to be added status ?

        String patchContent = getPatchContent();



    }


    protected String getPatchContent()
        throws MojoExecutionException
    {
        try
        {
            ScmRepository scmRepository = scmManager.makeScmRepository( project.getScm().getConnection() );

            ScmProvider provider = scmManager.getProviderByType( scmRepository.getProvider() );

            getLog().debug( "scm.providerType:" + providerType );
            if ( StringUtils.isNotEmpty( providerType ) )
            {
                provider = scmManager.getProviderByType( providerType );
            }

            DiffScmResult diffScmResult = provider.diff( scmRepository, new ScmFileSet( baseDir ), "", "" );
            getLog().debug( diffScmResult.getPatch() );

            return diffScmResult.getPatch();

        }
        catch ( ScmRepositoryException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( NoSuchScmProviderException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( ScmException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }
}
