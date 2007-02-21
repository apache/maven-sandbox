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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.continuum.Activator;
import org.apache.continuum.internal.model.BuildDefinitionAdapter;
import org.apache.continuum.internal.model.ProjectAdapter;
import org.apache.continuum.model.ConnectionProfile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Class that plugs in a Project List view into the workbench. The view shows
 * data obtained from the model.
 * <p>
 * <em>The sample creates a dummy model on the fly, but a real implementation would
 * connect to the model available either in this or another plug-in (e.g. the
 * workspace). The view is connected to the model using a content provider.
 * </em>
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ProjectsView extends ViewPart implements ITabbedPropertySheetPageContributor {

    private static final int DEFAULT_COLUMN_WIDTH = 200;

    private static final int MIN_COLUMN_WIDTH = 5;

    /**
     * Key to persist View's column widths.
     */
    private static final String TAG_COLUMN_WIDTH = "columnWidth";

    /**
     * number of columns.
     */
    protected int [] cols = new int[4];

    protected Tree treeTable;

    private ProjectListViewer tableViewer;

    // actions on a project/project-build
    protected List<Action> actions = new ArrayList<Action> ();

    protected MenuManager restartMenu;


    public ProjectsView() {
        super ();
    }


    @Override
    public void createPartControl(Composite parent) {
        treeTable = new Tree (parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
        treeTable.setHeaderVisible (true);
        treeTable.setLinesVisible (false);
        treeTable.setLayoutData (new GridData (GridData.FILL_BOTH));
        treeTable.setFont (parent.getFont ());

        // add columns
        TreeColumn c1 = new TreeColumn (treeTable, SWT.SINGLE);
        c1.setText ("Connection/Project");
        c1.setWidth (cols[0]);

        TreeColumn c2 = new TreeColumn (treeTable, SWT.SINGLE);
        c2.setText ("Status");
        c2.setWidth (cols[1]);

        TreeColumn c3 = new TreeColumn (treeTable, SWT.SINGLE);
        c3.setText ("Group Id");
        c3.setWidth (cols[2]);

        TreeColumn c4 = new TreeColumn (treeTable, SWT.SINGLE);
        c4.setText ("Artifact Id");
        c4.setWidth (cols[3]);

        tableViewer = new ProjectListViewer (this, treeTable);
        initializeActions (tableViewer);

        // setup listener and wire them to actions.
        treeTable.addSelectionListener (new SelectionAdapter () {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                super.widgetSelected (e);
            }


            @Override
            public void widgetDefaultSelected(SelectionEvent evt) {
                try {
                    TreeItem item = treeTable.getSelection ()[0];
                    Object data = item.getData ();
                    if (data instanceof BuildDefinitionAdapter)
                        return;
                    if (data instanceof ProjectAdapter) {
                        ProjectAdapter pa = (ProjectAdapter) data;
                        // TODO: Open Project details View/Editor.
                        System.err.println ("Opening Project Details Editor...");
                    }

                    if (data instanceof ConnectionProfile) {
                        ConnectionProfile cp = (ConnectionProfile) data;
                        // TODO: Open Connection Profile Editor.
                        System.err.println ("Opening Connection Profile Editor...");
                    }
                } catch (Exception e) {
                    // TODO: Better error logging
                    Activator.getDefault ().getLog ().log (new Status (IStatus.ERROR, Activator.PLUGIN_ID, -1, e.getLocalizedMessage (), e));
                }
            }

        });

        hookContextMenu ();

        getSite ().setSelectionProvider (tableViewer);

    }


    /**
     * Setup Context Menu.
     */
    private void hookContextMenu() {
        MenuManager menuManager = new MenuManager ("#PopupMenu");
        menuManager.setRemoveAllWhenShown (true);
        final Shell shell = treeTable.getShell ();
        menuManager.addMenuListener (new IMenuListener () {

            public void menuAboutToShow(IMenuManager mgr) {
                fillContextMenu (shell, mgr);
            }
        });
        Menu menu = menuManager.createContextMenu (tableViewer.getControl ());
        treeTable.setMenu (menu);
        getSite ().registerContextMenu (menuManager, tableViewer);
    }


    /**
     * Setup Context menu for this view.
     * <p>
     * Items in a context menu are determined by the target selection for the
     * context menu.
     * 
     * @param shell
     * @param menu
     */
    protected void fillContextMenu(Shell shell, IMenuManager menu) {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection ();
        if (!selection.isEmpty ()) {
            Iterator it = selection.iterator ();
            Object obj = it.next ();
            if (obj instanceof ProjectAdapter) {
                ProjectAdapter p = (ProjectAdapter) obj;
                // TODO Create Context menu for Project selection
            }
        }
        // If there was nothing under selection then create a context menu
        // with
        // 'New Connection' action.
        menu.add (new GroupMarker (IWorkbenchActionConstants.MB_ADDITIONS));

        Action newConnectionAction = new NewConnectionAction ();
        newConnectionAction.setToolTipText ("Create New Continuum Connection");
        newConnectionAction.setText ("New Connection");
        newConnectionAction.setImageDescriptor (PlatformUI.getWorkbench ().getSharedImages ().getImageDescriptor (ISharedImages.IMG_TOOL_NEW_WIZARD));
        menu.add (newConnectionAction);

        // TODO: Add other Context menus here.
    }


    /**
     * Sets up Actions for this View which are wired via listeners registered on
     * the View to perform actions.
     * 
     * @param tableViewer2
     */
    private void initializeActions(ISelectionProvider selectionProvider) {
        Action openAction = new OpenProjectAction (getSite ().getShell (), selectionProvider, "Open Project");
        openAction.setToolTipText ("Open Project");
        openAction.setText ("Open Project");
        // FIXME: Some dummy image.
        openAction.setImageDescriptor (PlatformUI.getWorkbench ().getSharedImages ().getImageDescriptor (ISharedImages.IMG_TOOL_NEW_WIZARD));

        // add to list of actions
        actions.add (openAction);
        // add actions to View's toolbar
        IContributionManager cm = getViewSite ().getActionBars ().getToolBarManager ();
        for (Iterator it = actions.iterator (); it.hasNext ();) {
            Action a = (Action) it.next ();
            cm.add (a);
        }
    }


    /**
     * TODO:
     */
    @Override
    public void setFocus() {
        if (treeTable != null)
            treeTable.setFocus ();
    }


    // ------------------------------------------------------------------------
    // For Managing state of the Project List View
    // ------------------------------------------------------------------------

    /**
     * Initialize the view and restore any previous UI state or setup with new
     * one.
     * 
     * @see {@link ViewPart#init(IViewSite, IMemento)}
     */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init (site, memento);
        for (int i = 0; i < cols.length; i++) {
            cols[i] = DEFAULT_COLUMN_WIDTH;
            if (memento != null) {
                Integer in = memento.getInteger (TAG_COLUMN_WIDTH + i);
                if (in != null && in.intValue () > MIN_COLUMN_WIDTH)
                    cols[i] = in.intValue ();
            }
        }
    }


    /**
     * Saves the state of this view when this view is closed.
     * 
     * @see {@link ViewPart#saveState(IMemento)}
     */
    @Override
    public void saveState(IMemento memento) {
        // super.saveState (memento);
        TreeColumn [] tc = treeTable.getColumns ();
        for (int i = 0; i < cols.length; i++) {
            int width = tc[i].getWidth ();
            if (width != 0)
                memento.putInteger (TAG_COLUMN_WIDTH + i, width);
        }
    }


    /**
     * Returns Worknbench part's Id.
     */
    public String getContributorId() {
        return getSite ().getId ();
    }


    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySheetPage.class)
            return new TabbedPropertySheetPage (this);
        return super.getAdapter (adapter);
    }
}
