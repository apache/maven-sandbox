package org.apache.maven.shared.artifact.tools.resolve;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.model.Dependency;
import org.apache.maven.shared.tools.easymock.MockManager;
import org.easymock.MockControl;

import java.util.List;

import junit.framework.TestCase;

public class ArtifactQueryTest
    extends TestCase
{

    public void testCreateQueryFromDependencyWithVersion()
        throws InvalidArtifactSpecificationException
    {
        Dependency dep = new Dependency();

        dep.setGroupId( "group" );
        dep.setArtifactId( "artifact" );
        dep.setVersion( "1.0" );

        ArtifactQuery query = new ArtifactQuery( dep );

        MockManager mm = new MockManager();

        MockControl ctl = MockControl.createControl( ArtifactHandlerManager.class );

        mm.add( ctl );

        ArtifactHandlerManager mgr = (ArtifactHandlerManager) ctl.getMock();

        mgr.getArtifactHandler( "jar" );

        MockControl handlerCtl = MockControl.createControl( ArtifactHandler.class );

        mm.add( handlerCtl );

        ArtifactHandler handler = (ArtifactHandler) handlerCtl.getMock();

        handler.getClassifier();
        handlerCtl.setReturnValue( null );

        ctl.setReturnValue( handler );

        mm.replayAll();

        Artifact artifact = query.createArtifact( mgr );

        assertEquals( dep.getGroupId(), artifact.getGroupId() );
        assertEquals( dep.getArtifactId(), artifact.getArtifactId() );
        assertEquals( dep.getType(), artifact.getType() );
        assertEquals( dep.getVersion(), artifact.getVersion() );

        mm.verifyAll();
    }

    public void testCreateQueryFromDependencyWithVersionRange()
        throws InvalidArtifactSpecificationException
    {
        Dependency dep = new Dependency();

        dep.setGroupId( "group" );
        dep.setArtifactId( "artifact" );
        dep.setVersion( "[1.0,2.0)" );

        ArtifactQuery query = new ArtifactQuery( dep );

        MockManager mm = new MockManager();

        MockControl ctl = MockControl.createControl( ArtifactHandlerManager.class );

        mm.add( ctl );

        ArtifactHandlerManager mgr = (ArtifactHandlerManager) ctl.getMock();

        mgr.getArtifactHandler( "jar" );

        MockControl handlerCtl = MockControl.createControl( ArtifactHandler.class );

        mm.add( handlerCtl );

        ArtifactHandler handler = (ArtifactHandler) handlerCtl.getMock();

        handler.getClassifier();
        handlerCtl.setReturnValue( null );

        ctl.setReturnValue( handler );

        mm.replayAll();

        Artifact artifact = query.createArtifact( mgr );

        assertEquals( dep.getGroupId(), artifact.getGroupId() );
        assertEquals( dep.getArtifactId(), artifact.getArtifactId() );
        assertEquals( dep.getType(), artifact.getType() );
        assertEquals( dep.getVersion(), String.valueOf( artifact.getVersionRange() ) );

        mm.verifyAll();
    }

    public void testCreateArtifactWithVersionRange()
        throws InvalidArtifactSpecificationException
    {
        ArtifactQuery query = new ArtifactQuery( "group", "artifact" );
        query.setVersionRangeSpec( "[2.0,2.1)" );

        MockManager mm = new MockManager();

        MockControl ctl = MockControl.createControl( ArtifactHandlerManager.class );

        mm.add( ctl );

        ArtifactHandlerManager mgr = (ArtifactHandlerManager) ctl.getMock();

        mgr.getArtifactHandler( "jar" );

        MockControl handlerCtl = MockControl.createControl( ArtifactHandler.class );

        mm.add( handlerCtl );

        ArtifactHandler handler = (ArtifactHandler) handlerCtl.getMock();

        handler.getClassifier();
        handlerCtl.setReturnValue( null );

        ctl.setReturnValue( handler );

        mm.replayAll();

        Artifact artifact = query.createArtifact( mgr );

        List restrictions = artifact.getVersionRange().getRestrictions();

        Restriction restriction = (Restriction) restrictions.get( 0 );

        assertEquals( "2.0", String.valueOf( restriction.getLowerBound() ) );
        assertTrue( restriction.isLowerBoundInclusive() );
        assertEquals( "2.1", String.valueOf( restriction.getUpperBound() ) );
        assertFalse( restriction.isUpperBoundInclusive() );

        mm.verifyAll();
    }

    public void testCreateArtifactWithVersion()
        throws InvalidArtifactSpecificationException
    {
        ArtifactQuery query = new ArtifactQuery( "group", "artifact" );
        query.setVersion( "2.0" );

        MockManager mm = new MockManager();

        MockControl ctl = MockControl.createControl( ArtifactHandlerManager.class );

        mm.add( ctl );

        ArtifactHandlerManager mgr = (ArtifactHandlerManager) ctl.getMock();

        mgr.getArtifactHandler( "jar" );

        MockControl handlerCtl = MockControl.createControl( ArtifactHandler.class );

        mm.add( handlerCtl );

        ArtifactHandler handler = (ArtifactHandler) handlerCtl.getMock();

        handler.getClassifier();
        handlerCtl.setReturnValue( null );

        ctl.setReturnValue( handler );

        mm.replayAll();

        Artifact artifact = query.createArtifact( mgr );

        assertEquals( "2.0", artifact.getVersion() );

        mm.verifyAll();
    }

    public void testCopyWithVersionRange()
        throws InvalidArtifactSpecificationException
    {
        ArtifactQuery query = new ArtifactQuery( "group", "artifact" );
        query.setVersionRangeSpec( "[2.0,2.1)" );

        ArtifactQuery copy = query.copy();

        MockManager mm = new MockManager();

        MockControl ctl = MockControl.createControl( ArtifactHandlerManager.class );

        mm.add( ctl );

        ArtifactHandlerManager mgr = (ArtifactHandlerManager) ctl.getMock();

        mgr.getArtifactHandler( "jar" );

        MockControl handlerCtl = MockControl.createControl( ArtifactHandler.class );

        mm.add( handlerCtl );

        ArtifactHandler handler = (ArtifactHandler) handlerCtl.getMock();

        handler.getClassifier();
        handlerCtl.setReturnValue( null, MockControl.ONE_OR_MORE );

        ctl.setReturnValue( handler, MockControl.ONE_OR_MORE );

        mm.replayAll();

        Artifact artifact = query.createArtifact( mgr );
        Artifact copied = copy.createArtifact( mgr );

        assertEquals( artifact.getGroupId(), copied.getGroupId() );
        assertEquals( artifact.getArtifactId(), copied.getArtifactId() );
        assertEquals( artifact.getType(), copied.getType() );
        assertEquals( String.valueOf( artifact.getVersionRange() ), String.valueOf( copied.getVersionRange() ) );

        mm.verifyAll();
    }

    public void testCopyWithVersion()
        throws InvalidArtifactSpecificationException
    {
        ArtifactQuery query = new ArtifactQuery( "group", "artifact" );
        query.setVersion( "2.0" );

        ArtifactQuery copy = query.copy();

        MockManager mm = new MockManager();

        MockControl ctl = MockControl.createControl( ArtifactHandlerManager.class );

        mm.add( ctl );

        ArtifactHandlerManager mgr = (ArtifactHandlerManager) ctl.getMock();

        mgr.getArtifactHandler( "jar" );

        MockControl handlerCtl = MockControl.createControl( ArtifactHandler.class );

        mm.add( handlerCtl );

        ArtifactHandler handler = (ArtifactHandler) handlerCtl.getMock();

        handler.getClassifier();
        handlerCtl.setReturnValue( null, MockControl.ONE_OR_MORE );

        ctl.setReturnValue( handler, MockControl.ONE_OR_MORE );

        mm.replayAll();

        Artifact artifact = query.createArtifact( mgr );
        Artifact copied = copy.createArtifact( mgr );

        assertEquals( artifact.getId(), copied.getId() );

        mm.verifyAll();
    }

}
