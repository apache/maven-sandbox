package org.apache.maven.shared.profiles;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.apache.maven.context.BuildContextManager;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.profiles.activation.ProfileActivator;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.build.ProjectBuildCache;
import org.apache.maven.project.build.ProjectBuildContext;
import org.apache.maven.project.build.model.ModelLineage;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

/**
 * @plexus.component role="org.apache.maven.profiles.activation.ProfileActivator"
 *                   role-hint="modelProperty"
 *                   instantiation-strategy="per-lookup"
 *
 * @author jdcasey
 */
public class ModelPropertyProfileActivator
    implements ProfileActivator, LogEnabled
{

    /**
     * Configured as part of the CustomActivator's setup of this ProfileActivator, before the
     * CustomActivator delegates the profile-activation process to it. This IS a required element,
     * and it can be reversed (negated) using a '!' prefix. Reversing the name means one of two things:
     * <br/>
     * <ul>
     *   <li>If the value configuration is null, make sure the property doesn't exist in the lineage.</li>
     *   <li>If the value configuration does exist, make sure the retrieved value doesn't match it.</li>
     * </ul>
     *
     * @plexus.configuration
     */
    private String name;

    /**
     * Configured as part of the CustomActivator's setup of this ProfileActivator, before the
     * CustomActivator delegates the profile-activation process to it. This is NOT a required element,
     * and it can be reversed (negated) using a '!' prefix.
     *
     * @plexus.configuration
     */
    private String value;

    /**
     * @plexus.requirement
     */
    private BuildContextManager buildContextManager;

    // initialized by the container, or lazily.
    private Logger logger;

    public ModelPropertyProfileActivator()
    {
        // provided for Plexus activation
    }

    protected ModelPropertyProfileActivator( String name, BuildContextManager buildContextManager )
    {
        this.name = name;
        this.buildContextManager = buildContextManager;
    }

    protected ModelPropertyProfileActivator( String name, String value, BuildContextManager buildContextManager )
    {
        this.name = name;
        this.value = value;
        this.buildContextManager = buildContextManager;
    }

    public boolean canDetermineActivation( Profile profile )
    {
        ProjectBuildContext projectContext = ProjectBuildContext.getProjectBuildContext( buildContextManager, false );

        if ( checkConfigurationSanity() && ( projectContext != null ) )
        {
            return projectContext.getModelLineage() != null;
        }

        return false;
    }

    private boolean checkConfigurationSanity()
    {
        return name != null;
    }

    public boolean isActive( Profile profile )
    {
        // currently, just make sure the name configuration is set.
        if ( !checkConfigurationSanity() )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Skipping profile: " + profile.getId() + ". Reason: modelProperty activation is missing 'name' configuration." );
            }
            return false;
        }

        // using the project cache to speed things up; if the project has been built already,
        // we don't have to search the entire model lineage for it...parents should be
        // built ahead of children, so this could be a time-saver.
        ProjectBuildCache projectCache = ProjectBuildCache.read( buildContextManager );

        ProjectBuildContext projectContext = ProjectBuildContext.getProjectBuildContext( buildContextManager, false );

        if ( projectContext == null )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Skipping profile: " + profile.getId() + ". Reason: projectContext is missing." );
            }

            return false;
        }

        ModelLineage lineage = projectContext.getModelLineage();

        if ( lineage == null )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Skipping profile: " + profile.getId() + ". Reason: model lineage is missing." );
            }

            return false;
        }

        String originatingId = lineage.getOriginatingModel().getId();

        String propertyName = name;
        boolean reverse = false;

        if ( propertyName.startsWith( "!" ) )
        {
            reverse = true;
            propertyName = propertyName.substring( 1 );
        }

        String checkValue = value;
        if ( ( checkValue != null ) && checkValue.startsWith( "!" ) )
        {
            reverse = true;
            checkValue = checkValue.substring( 1 );
        }

        boolean matches = false;

        // iterate through the Model instances that will eventually be calculated as one
        // inheritance-assembled Model, and see if we can activate the profile based on properties
        // found within one of them. NOTE: iteration starts with the child POM, and goes back through
        // the ancestry.
        for ( Iterator it = lineage.modelIterator(); it.hasNext(); )
        {
            Model model = (Model) it.next();
            MavenProject project = projectCache.getCachedProject( model.getGroupId(), model.getArtifactId(), model.getVersion() );
            File file = lineage.getFile( model );

            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Searching: " + model.getId() + "\nfrom file: " + file + "\nfor property: " + propertyName + "\nhaving value: " + checkValue + " (if null, only checking for property presence)." );
            }

            boolean fromProject = false;
            Properties properties;
            if ( project != null )
            {
                properties = project.getProperties();
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Found MavenProject instance for: " + model.getId() + "; no need to traverse entire lineage from this point back." );
                }
            }
            else
            {
                properties = model.getProperties();
            }

            if ( properties == null )
            {
                if ( fromProject )
                {
                    break;
                }
                else
                {
                    if ( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( "no properties here. continuing down the lineage." );
                    }
                    continue;
                }
            }

            String retrievedValue = properties.getProperty( propertyName );

            if ( value != null )
            {
                // local-most values win, so if the retrievedValue != null in the current POM, NEVER
                // look in the parent POM for a match.
                // If the retrievedValue == null, though, we need to stop looking for a match here.
                if ( retrievedValue == null )
                {
                    if ( fromProject )
                    {
                        break;
                    }
                    else
                    {
                        if ( getLogger().isDebugEnabled() )
                        {
                            getLogger().debug( "property not found here. continuing down the lineage." );
                        }
                        continue;
                    }
                }

                matches = checkValue.equals( retrievedValue );

                // if we get here, retrievedValue != null, and we're looking at the local-most POM, so:
                //
                // if startsWith '!' (reverse == true) and values don't match (match == false), return true
                // if NOT startsWith '!' (reverse == false) and values DO match (match == true), return true
                // else return false
                if ( reverse != matches )
                {
                    if ( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( "Searching for property-value match: matches: " + matches + "; reversed: " + reverse + "; profile: " + profile.getId() + " should be activated." );
                    }
                    break;
                }
            }
            // if the value is not specified, then we have to search the entire ancestry before we
            // can say for certain that a property is missing.
            else
            {
                // if startsWith '!' (reverse == true) and retrievedValue == null (true), return true
                // if NOT startsWith '!' (reverse == false) and NOT retrievedValue == null (false), return true
                matches = retrievedValue != null;

                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Searching for property presence: matches: " + matches + "; reversed: " + reverse + "; stopping lineage search." );
                }

                if ( matches )
                {
                    break;
                }
            }

            // if we can't definitely say we're activating the profile, go to the next model in the
            // lineage, and try again.
        }

        // if we get to the end of the lineage without activating the profile, return false.
        boolean result = reverse != matches;

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "As a result of modelProperty activation in profile: " + profile.getId() + " of POM: " + originatingId + ", profile is: " + ( result ? "ACTIVE" : "INACTIVE") );
        }

        return result;
    }

    protected Logger getLogger()
    {
        if ( logger == null )
        {
            logger = new ConsoleLogger( Logger.LEVEL_DEBUG, "ModelPropertyProfileActivator:internal" );
        }

        return logger;
    }

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

}
