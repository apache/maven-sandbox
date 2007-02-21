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

import org.apache.continuum.ui.wizard.CreateConnectionProfileWizard;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * TODO: Review and factor out common view elements and actions to common
 * packages.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class NewConnectionAction extends AbstractOpenDialogAction {

    public NewConnectionAction() {
        super ();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.apache.continuum.ui.views.AbstractNewDialogAction#createDialog(org.eclipse.ui.IWorkbenchWindow)
     */
    @Override
    protected TitleAreaDialog createDialog(IWorkbenchWindow window) {
        ISelection selection = window.getSelectionService ().getSelection ();
        IStructuredSelection structuredSelection = StructuredSelection.EMPTY;
        if (selection instanceof IStructuredSelection)
            structuredSelection = (IStructuredSelection) selection;
        IWorkbench workbench = window.getWorkbench ();
        // Create the New Connection Wizard dialog and return for display.
        CreateConnectionProfileWizard wizard = new CreateConnectionProfileWizard ();
        wizard.init (workbench, structuredSelection);
        WizardDialog newDlg = new WizardDialog (workbench.getActiveWorkbenchWindow ().getShell (), wizard);
        return newDlg;
    }

}
