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

import org.apache.maven.continuum.model.project.Project;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * Opens up a Project Details View in a new Edit Part for the user to update.
 * <p>
 * This should check permissions if the user is allowed to access/edit the
 * selected project or not.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class OpenProjectAction extends SelectionProviderAction {

    private Shell shell;


    protected OpenProjectAction(Shell shell, ISelectionProvider provider, String name) {
        super (provider, name);
        this.shell = shell;
    }


    /**
     * Connects to the selected Connection Profile and obtains a list of
     * {@link Project}s from the remote server.
     */
    @Override
    public void run() {
        super.run ();
        System.err.println ("Opening Project 'Unknown'");
        // TODO Auto-generated method stub
        System.err.println ("Opened!");

    }


    /**
     * Enable/Disable the state of this action based on the selection.
     * <p>
     * Say if a 'project' was selected in the view then we don't need the
     * Connect Action to be active.
     */
    @Override
    public void selectionChanged(ISelection selection) {
        // TODO Auto-generated method stub
        super.selectionChanged (selection);
    }

}
