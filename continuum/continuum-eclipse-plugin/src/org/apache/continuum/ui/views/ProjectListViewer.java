/**
 *    Copyright 2006  <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.continuum.ui.views;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.continuum.Activator;
import org.apache.continuum.internal.ConnectionProfileManager;
import org.apache.continuum.internal.model.BuildDefinitionAdapter;
import org.apache.continuum.internal.model.ProjectAdapter;
import org.apache.continuum.model.ConnectionProfile;
import org.apache.maven.continuum.model.project.BuildDefinition;
import org.apache.maven.continuum.model.project.Project;
import org.apache.maven.continuum.rpc.ProjectsReader;
import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Controller entity of the MVC paradigm.
 * <p>
 * This renders the Project view.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class ProjectListViewer extends TreeViewer {

    /**
     * Array of Table headers for the Category Document view.
     */
    // private static ColumnHeaders [] headers = ColumnHeaders.values ();
    // View that is rendered by this viewer.
    private ProjectsView view;


    /**
     * Create an instance of table-based {@link ProjectListViewer}.
     * 
     * @param view
     *            {@link Composite} parent to use for creating this viewer.
     * @param treeTable
     *            set of SWT.* options that could be used for styling up this
     *            viewer.
     */
    public ProjectListViewer(ProjectsView view, Tree treeTable) {
        super (treeTable);
        this.view = view;
        setContentProvider (new ViewContentProvider ());
        setLabelProvider (new LabelProvider ());
        // No input initially.
        setInput ("ROOT");
        IActionBars actionBars = view.getViewSite ().getActionBars ();
        // FIXME: Add appropriate action handler here
        actionBars.setGlobalActionHandler (ActionFactory.DELETE.getId (), new NewConnectionAction ());
    }

    // ------------------------------------------------------------------------
    // Content Provider Implementation for the viewer.
    // ------------------------------------------------------------------------

    public class ViewContentProvider implements ITreeContentProvider {

        /**
         * TODO: Review this implementation.
         */
        public Object [] getElements(Object elt) {
            return getChildren (elt);
        }


        public void dispose() {
        // TODO Auto-generated method stub

        }


        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // TODO Auto-generated method stub
            System.err.println ("VieweContentProvider.inputChanged()");
        }


        /**
         * @param elt
         *            Parent Element.
         */
        public Object [] getChildren(Object elt) {
            if (elt instanceof ConnectionProfile) {
                ConnectionProfile cp = (ConnectionProfile) elt;
                // Obtain projects for this connection profile
                String url = cp.getConnectionUrl ();
                try {
                    ProjectsReader pr = new ProjectsReader (new URL (url));
                    Project [] projects = pr.readProjects ();
                    // return projects;
                    // Return Adaptable instances for Project
                    ProjectAdapter [] adapters = new ProjectAdapter[projects.length];
                    for (int i = 0; i < projects.length; i++) {
                        adapters[i] = new ProjectAdapter (projects[i]);
                    }
                    return adapters;
                } catch (MalformedURLException e) {
                    // TODO: better error reporting.
                    Activator.getDefault ().getLog ().log (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, e.getLocalizedMessage (), e));
                } catch (XmlRpcException e) {
                    // TODO: better error reporting.
                    Activator.getDefault ().getLog ().log (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, e.getLocalizedMessage (), e));
                } catch (IOException e) {
                    // TODO: better error reporting.
                    Activator.getDefault ().getLog ().log (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, e.getLocalizedMessage (), e));
                } catch (Exception e) {
                    // TODO: better error reporting.
                    Activator.getDefault ().getLog ().log (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, e.getLocalizedMessage (), e));
                }
            }

            if (elt instanceof ProjectAdapter) {
                List builds = ((ProjectAdapter) elt).getProject ().getBuildDefinitions ();
                // we return BuildAdapter[]
                BuildDefinitionAdapter [] ba = new BuildDefinitionAdapter[builds.size ()];
                for (int i = 0; i < ba.length; i++) {
                    ba[i] = new BuildDefinitionAdapter ((BuildDefinition) builds.get (i));
                }
                return ba;
            }

            // Else, we are creating view contents afresh
            System.err.println ("Loading Connection Profiles...");
            List<ConnectionProfile> list = new ArrayList<ConnectionProfile> ();
            try {
                list = ConnectionProfileManager.loadConnectionProfiles ();
            } catch (CoreException e) {
                // log and swallow for the moment
                Activator.getDefault ().getLog ().log (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, e.getLocalizedMessage (), e));
                // TODO: better Error reporting mechanism.
            }
            return list.toArray ();
        }


        public Object getParent(Object elt) {
            // TODO: Obtain parent node based class of passed in element.
            System.err.println ("VieweContentProvider.getParent()");
            return null;
        }


        /**
         * Determines if a node element in the viewer has children.
         * 
         * @return <code>true</code> if a selected node element had children.
         */
        public boolean hasChildren(Object elt) {
            if (elt instanceof ConnectionProfile) {
                ConnectionProfile cpd = (ConnectionProfile) elt;
                try {
                    // Obtain projects for this connection profile
                    String url = cpd.getConnectionUrl ();
                    ProjectsReader pr = new ProjectsReader (new URL (url));
                    Project [] projects = pr.readProjects ();
                    return (null != projects && projects.length > 0);
                } catch (Exception e) {
                    MessageDialog.openError (view.getSite ().getShell (), "Error", e.getMessage ());
                    return false;
                }
            }

            if (elt instanceof ProjectAdapter) {
                Project p = ((ProjectAdapter) elt).getProject ();
                return (null != p.getBuildDefinitions () && p.getBuildDefinitions ().size () > 0);
            }

            return false;
        }

    }

    // ------------------------------------------------------------------------
    // Label Provider Implementation for the viewer.
    // ------------------------------------------------------------------------

    public class LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            // TODO Return proper image.
            return null;
        }


        public String getColumnText(Object elt, int colIdx) {
            if (elt instanceof ConnectionProfile) {
                ConnectionProfile cpd = (ConnectionProfile) elt;
                if (colIdx == 0)
                    return cpd.getId ();
            }

            // FIXME : Build column labels appropriately, currently testing
            // only.
            if (elt instanceof ProjectAdapter) {
                Project p = ((ProjectAdapter) elt).getProject ();
                if (colIdx == 0)
                    return p.getName ();
                if (colIdx == 1)
                    return Integer.toString (p.getState ());
                if (colIdx == 2)
                    return p.getGroupId ();
            }

            if (elt instanceof BuildDefinitionAdapter) {
                BuildDefinition bd = ((BuildDefinitionAdapter) elt).getBuildDefinition ();
                if (colIdx == 0)
                    return Integer.toString (bd.getId ());
                if (colIdx == 1)
                    return bd.getGoals ();
                if (colIdx == 2)
                    return bd.getArguments ();
                if (colIdx == 3)
                    return (null != bd.getSchedule ()) ? bd.getSchedule ().getName () : "";
            }

            return "";
        }


        public void addListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub

        }


        public void dispose() {
        // TODO Auto-generated method stub

        }


        public boolean isLabelProperty(Object element, String property) {
            // TODO Auto-generated method stub
            return false;
        }


        public void removeListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub

        }

    }
}
