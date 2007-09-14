package org.apache.maven.shared.profiles;

import org.apache.maven.context.BuildContextManager;
import org.apache.maven.context.DefaultBuildContextManager;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.build.ProjectBuildContext;
import org.apache.maven.project.build.model.DefaultModelLineage;
import org.apache.maven.project.build.model.ModelLineage;

import java.util.Properties;

import junit.framework.TestCase;

public class ModelPropertyProfileActivatorTest
    extends TestCase
{

    private BuildContextManager buildContextManager;

    public void setUp() throws Exception
    {
        super.setUp();

        buildContextManager = new DefaultBuildContextManager();
    }

    public void testCanDetermineActivation_ShouldReturnFalseWhenNameNotSet()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        assertFalse( new ModelPropertyProfileActivator( null, "value", buildContextManager ).canDetermineActivation( profile ) );
    }

    public void testCanDetermineActivation_ShouldReturnFalseWhenLineageNotPresent()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        assertFalse( new ModelPropertyProfileActivator( "name", buildContextManager ).canDetermineActivation( profile ) );
    }

    public void testCanDetermineActivation_ShouldReturnTrueWhenNameSetAndLineagePresent()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( new Model(), null, null );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );
        context.setModelLineage( lineage );
        context.store( buildContextManager );

        assertTrue( new ModelPropertyProfileActivator( "name", buildContextManager ).canDetermineActivation( profile ) );
    }

    public void testIsActive_ShouldReturnFalseWhenNameNotSet()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        assertFalse( new ModelPropertyProfileActivator( null, "value", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnFalseWhenProjectContextIsMissing()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        assertFalse( new ModelPropertyProfileActivator( "name", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnFalseWhenModelLineageMissingFromProjectContext()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );
        context.store( buildContextManager );

        assertFalse( new ModelPropertyProfileActivator( "name", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnFalseWhenNoModelsInLineageContainProperties()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( new Model(), null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertFalse( new ModelPropertyProfileActivator( "name", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnTrueWhenModelPropertyNamePresentAndValueNotConfigured()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        Model model = new Model();

        Properties props = new Properties();
        props.setProperty( "name", "value" );

        model.setProperties( props );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( model, null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertTrue( new ModelPropertyProfileActivator( "name", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnFalseWhenModelPropertyNamePresentValueNotConfigedAndNameConfigNegated()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        Model model = new Model();

        Properties props = new Properties();
        props.setProperty( "name", "value" );

        model.setProperties( props );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( model, null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertFalse( new ModelPropertyProfileActivator( "!name", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnTrueWhenModelPropertyNameMissingValueNotConfigedAndNameConfigNegated()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        Model model = new Model();

        Properties props = new Properties();

        model.setProperties( props );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( model, null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertTrue( new ModelPropertyProfileActivator( "!name", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnTrueWhenNameAndValueMatch()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        Model model = new Model();

        Properties props = new Properties();
        props.setProperty( "name", "value" );

        model.setProperties( props );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( model, null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertTrue( new ModelPropertyProfileActivator( "name", "value", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnTrueWhenNameAndValueMatchInParentModel()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        Model model = new Model();

        Model parentModel = new Model();

        Properties props = new Properties();
        props.setProperty( "name", "value" );

        parentModel.setProperties( props );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( model, null, null );
        lineage.addParent( parentModel, null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertTrue( new ModelPropertyProfileActivator( "name", "value", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnFalseWhenNameMatchesAndValueDoesntMatch()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        Model model = new Model();

        Properties props = new Properties();
        props.setProperty( "name", "value1" );

        model.setProperties( props );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( model, null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertFalse( new ModelPropertyProfileActivator( "name", "value", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnTrueWhenNameNegatedAndValueDoesntMatch()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        Model model = new Model();

        Properties props = new Properties();
        props.setProperty( "name", "value1" );

        model.setProperties( props );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( model, null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertTrue( new ModelPropertyProfileActivator( "!name", "value", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnTrueWhenNameNegatedAndValueDoesntMatchButValueInParentDoesMatch()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        Model model = new Model();

        Properties props = new Properties();
        props.setProperty( "name", "value1" );

        model.setProperties( props );

        Model parent = new Model();

        Properties parentProps = new Properties();
        parentProps.setProperty( "name", "value" );

        parent.setProperties( parentProps );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( model, null, null );
        lineage.addParent( parent, null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertTrue( new ModelPropertyProfileActivator( "!name", "value", buildContextManager ).isActive( profile ) );
    }

    public void testIsActive_ShouldReturnFalseWhenNameNegatedAndParentHasValue()
    {
        Profile profile = new Profile();
        profile.setId( "test-profile" );

        ProjectBuildContext context = ProjectBuildContext.getProjectBuildContext( buildContextManager, true );

        Model model = new Model();

        Properties props = new Properties();

        model.setProperties( props );

        Model parent = new Model();

        Properties parentProps = new Properties();
        parentProps.setProperty( "name", "value" );

        parent.setProperties( parentProps );

        ModelLineage lineage = new DefaultModelLineage();
        lineage.setOrigin( model, null, null );
        lineage.addParent( parent, null, null );

        context.setModelLineage( lineage );

        context.store( buildContextManager );

        assertFalse( new ModelPropertyProfileActivator( "!name", buildContextManager ).isActive( profile ) );
    }

}
