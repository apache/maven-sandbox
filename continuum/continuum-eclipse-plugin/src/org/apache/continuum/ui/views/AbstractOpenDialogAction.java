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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Common base class for all action that need to invoke a dialog.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public abstract class AbstractOpenDialogAction extends Action {

    public AbstractOpenDialogAction() {
        super ();
    }


    /**
     * Invokes a dialog.
     */
    public void run() {
        IWorkbench workbench = PlatformUI.getWorkbench ();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow ();
        Dialog newDlg = createDialog (window);
        newDlg.open ();
    }


    /**
     * Creates an empty {@link TitleAreaDialog} by default. Subclasses should
     * override and provide their own implementation of the dialogs to display.
     * 
     * @param window
     *            parent for the dialog
     * @return newly created dialog.
     */
    protected TitleAreaDialog createDialog(IWorkbenchWindow window) {
        return new TitleAreaDialog (window.getPartService ().getActivePart ().getSite ().getShell ());
    }

}
