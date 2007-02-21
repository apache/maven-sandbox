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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Displays a Dialog to capture user input to create new Build definition.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class NewBuildDefinitionAction extends AbstractOpenDialogAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.continuum.ui.views.AbstractNewDialogAction#createDialog(org.eclipse.ui.IWorkbenchWindow)
     */
    @Override
    protected TitleAreaDialog createDialog(IWorkbenchWindow window) {

        return super.createDialog (window);
    }

}
