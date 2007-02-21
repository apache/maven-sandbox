package org.apache.maven.plugins.grafo.gui.applets;

import org.apache.maven.plugins.grafo.gui.RadialGraphView;

import prefuse.util.ui.JPrefuseApplet;

/**
 * Applet that shows a radial graph from the xml input specified in the applet parameter
 * <code>datafile</code> and using as labels the field in the applet parameter <code>lable</code>.
 * 
 * <code>
 *   &lt;applet code="org/apache/maven/plugins/grafo/gui/applets/RadialGraphViewApplet.class"
 *          archive="maven-grafo-plugin-1.0-SNAPSHOT.jar,prefuse-beta-20060220.jar"
 *          width="800" height="500">
 *     &lt;param name=datafile value=graph.xml>
 *     &lt;param name=label value=label>
 *     If you can read this text, the applet is not working. Perhaps you don't
 *     have the Java 1.4.2 (or later) web plug-in installed?<br/>
 *     &lt;a href="http://java.com">Get Java here.</a>
 *   &lt;/applet>
 * </code>
 * 
 * @see org.apache.maven.plugins.grafo.gui.RadialGraphView
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class RadialGraphViewApplet
    extends JPrefuseApplet
{

    public void init()
    {
        String datafile = getParameter( "datafile" );
        String label = getParameter( "label" );

        if ( datafile == null || label == null )
        {
            datafile = "/socialnet.xml";
            label = "name";
        }

        this.setContentPane( RadialGraphView.demo( datafile, label ) );
    }

}
