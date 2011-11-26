package org.apache.maven.plugins.patchtracker;
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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.patchtracker.tracking.PatchTracker;
import org.apache.maven.plugins.patchtracker.tracking.PatchTrackerRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.diff.DiffScmResult;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Olivier Lamy
 */
public abstract class AbstractPatchMojo
    extends AbstractMojo
    implements Contextualizable
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
     * @parameter expression="${scm.providerType}" default-value=""
     */
    protected String providerType = "";

    /**
     * @parameter default-value="${settings}"
     * @required
     * @readonly
     */
    protected Settings settings;


    /**
     * if user/password are stored in your settings.xml in a server
     *
     * @parameter expression="${patch.serverId}" default-value=""
     */
    protected String serverId;

    /**
     * if path tracker url is not stored in the pom.
     * <b>For jira, url must include project key!: http://jira.codehaus.org/browse/MNG</b>
     *
     * @parameter expression="${patch.serverUrl}" default-value=""
     */
    protected String serverUrl;

    /**
     * @parameter expression="${patch.user}" default-value=""
     */
    protected String user;

    /**
     * @parameter expression="${patch.password}" default-value=""
     */
    protected String password;

    /**
     * @parameter expression="${patch.system}" default-value=""
     */
    protected String system;

    /**
     * @parameter expression="${patch.summary}" default-value=""
     */
    protected String summary;

    /**
     * @parameter expression="${patch.description}" default-value=""
     */
    protected String description;


    /**
     * the type of the patch tracker entry to load: default 1 for jira bug
     *
     * @parameter expression="${patch.patchType}" default-value="1"
     */
    protected String patchType;

    /**
     * the priority of the patch tracker entry to load: default 3 for jira major
     *
     * @parameter expression="${patch.priority}" default-value="3"
     */
    protected String patchPriority;


    /**
     * Component used to prompt for input.
     *
     * @component
     */
    protected Prompter prompter;

    protected PlexusContainer plexusContainer;

    /**
     * @component
     */
    protected ScmManager scmManager;


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

    protected PatchTrackerRequest buidPatchTrackerRequest( boolean creation )
        throws MojoExecutionException
    {
        try
        {
            PatchTrackerRequest patchTrackerRequest =
                new PatchTrackerRequest().setUrl( getPatchTrackerUrl() ).setUserName(
                    getPatchTrackerUsername() ).setPassword( getPatchTrackerPassword() ).setPatchType(
                    patchType ).setPatchPriority( patchPriority );

            return creation ? patchTrackerRequest.setSummary( getPatchTrackerSummary() ).setDescription(
                getPatchTrackerDescription() ) : patchTrackerRequest;


        }
        catch ( PrompterException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }


    protected String getPatchTrackerUrl()
        throws PrompterException, MojoExecutionException
    {
        String value = project.getIssueManagement() == null ? "" : project.getIssueManagement().getUrl();

        // cli must win !
        if ( StringUtils.isNotEmpty( serverUrl ) )
        {
            value = serverUrl;
        }

        return getValue( value, "path tracker url ? (http://jira.codehaus.org/browse/MNG)", null, true,
                         "you must configure a patch system or at least use interactive mode", value, false );
    }

    protected String getPatchTrackerSummary()
        throws PrompterException, MojoExecutionException
    {
        String value = summary;

        return getValue( value, "patch summary ? (wonderfull patch to fix ....) ", Collections.<String>emptyList(),
                         true, "you must configure a patch summary or at least use interactive mode", null, false );
    }

    protected String getPatchTrackerDescription()
        throws PrompterException, MojoExecutionException
    {
        String value = description;

        return getValue( value, "patch description ?(this patch fix this very annoying issue ....) ", null, false,
                         "you must configure a patch summary or at least use interactive mode", null, false );
    }

    protected String getServerId()
    {
        String serverIdFromPom = (String) project.getProperties().get( "patch.tracker.serverId" );
        if ( StringUtils.isNotEmpty( serverIdFromPom ) )
        {
            return serverIdFromPom;
        }
        return serverId;
    }

    protected String getPatchTrackerUsername()
        throws PrompterException, MojoExecutionException
    {
        String value = null;

        if ( StringUtils.isNotEmpty( getServerId() ) )
        {
            Server server = getServer( getServerId() );
            if ( server == null )
            {
                getLog().warn( "no server found in your settings.xml with id:" + getServerId() );
            }
            else
            {
                value = server.getUsername();
            }

        }

        // cli must win !
        if ( StringUtils.isNotEmpty( user ) )
        {
            value = user;
        }

        return getValue( value, "patch tracker username ?", null, true,
                         "you must configure a user for your patch tracker or at least use interactive mode", value,
                         false );
    }

    protected String getPatchTrackerPassword()
        throws PrompterException, MojoExecutionException
    {
        String value = null;

        if ( StringUtils.isNotEmpty( getServerId() ) )
        {
            Server server = getServer( getServerId() );
            if ( server == null )
            {
                getLog().warn( "no server found in your settings.xml with id:" + getServerId() );
            }
            else
            {
                value = server.getPassword();
            }

        }

        // cli must win !
        if ( StringUtils.isNotEmpty( password ) )
        {
            value = password;
        }

        return getValue( value, "patch tracker password ?", null, true,
                         "you must configure a password for your patch tracker or at least use interactive mode", value,
                         true );
    }

    protected String getPatchTrackerSystem()
        throws MojoExecutionException
    {
        String value = project.getIssueManagement() == null ? "" : project.getIssueManagement().getSystem();

        // cli must win !
        if ( StringUtils.isNotEmpty( system ) )
        {
            value = system;
        }

        try
        {
            return getValue( value, "path tracker system id ?", Arrays.asList( "jira" ), true,
                             "you must configure a patch system or at least use interactive mode", "jira", false );
        }
        catch ( PrompterException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    protected String getValue( String currentValue, String message, List<String> possibleValues, boolean mandatory,
                               String errorMessage, String defaultValue, boolean passwordPrompt )
        throws PrompterException, MojoExecutionException
    {
        boolean loadFromPrompt = false;
        String value = currentValue;
        if ( mandatory && StringUtils.isEmpty( value ) )
        {
            if ( settings.isInteractiveMode() )
            {

                getLog().debug( "1st prompt message " + message + ", defaultValue " + defaultValue + ", possibleValues"
                                    + possibleValues );
                if ( passwordPrompt )
                {
                    value = prompter.promptForPassword( message );
                }
                else
                {
                    value = ( possibleValues == null || possibleValues.isEmpty() )
                        ? prompter.prompt( message, defaultValue )
                        : prompter.prompt( message, possibleValues, defaultValue );
                }
                loadFromPrompt = StringUtils.isNotBlank( value );
            }
            else
            {
                getLog().error( errorMessage );
                throw new MojoExecutionException( errorMessage );
            }
            if ( StringUtils.isEmpty( value ) )
            {
                getLog().error( errorMessage );
                throw new MojoExecutionException( errorMessage );
            }
        }

        if ( settings.isInteractiveMode() && !loadFromPrompt )
        {
            getLog().debug( "2nd prompt message " + message + ", defaultValue " + defaultValue + ", possibleValues"
                                + possibleValues );
            if ( passwordPrompt )
            {
                value = prompter.promptForPassword( message );
            }
            else
            {
                value = ( possibleValues == null || possibleValues.isEmpty() ) ? ( StringUtils.isEmpty( defaultValue )
                    ? prompter.prompt( message )
                    : prompter.prompt( message, defaultValue ) )
                    : ( StringUtils.isEmpty( defaultValue )
                        ? prompter.prompt( message, possibleValues )
                        : prompter.prompt( message, possibleValues, defaultValue ) );
            }
            if ( StringUtils.isEmpty( value ) && mandatory )
            {
                getLog().error( errorMessage );
                throw new MojoExecutionException( errorMessage );
            }

        }
        return value;
    }


    protected Server getServer( String id )
    {
        if ( StringUtils.isEmpty( id ) )
        {
            return null;
        }
        return settings.getServer( id );
    }

    protected PatchTracker getPatchTracker()
        throws MojoExecutionException, ComponentLookupException
    {
        String system = getPatchTrackerSystem();

        getLog().debug( "patch tracker system:" + system );

        return (PatchTracker) plexusContainer.lookup( PatchTracker.class.getName(), system );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        this.plexusContainer = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
