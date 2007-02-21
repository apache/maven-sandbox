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

import org.apache.maven.continuum.model.project.BuildDefinition;
import org.apache.maven.continuum.model.project.Project;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Accepts {@link Project} instance and creates a dialog around it for
 * edit/update.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class EditBuildDefinitionDialog extends AbstractEditDialog {

    /**
     * Project instance to edit and update.
     */
    private BuildDefinition buildDefinition;

    private ArrayList defaultValues = null;


    /**
     * @param shell
     * @param buildDefinition
     *            {@link Project} instance to edit/update.
     */
    public EditBuildDefinitionDialog(IShellProvider shell, BuildDefinition buildDefinition) {
        this (shell.getShell (), buildDefinition);
    }


    /**
     * @param parentShell
     * @param project
     *            {@link Project} instance to edit/update.
     */
    public EditBuildDefinitionDialog(Shell parentShell, BuildDefinition buildDefinition) {
        super (parentShell);
        this.buildDefinition = buildDefinition;
        this.defaultValues = getDefaultValues (buildDefinition);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.apache.continuum.ui.views.AbstractDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea (parent);
        // Add UI elements.
        createDialogContents (composite, "Build Definition Details");
        return composite;
    }


    /**
     * Creates labels and controls for the Dialog box.
     * 
     * @param composite
     * @param groupTitle
     */
    private void createDialogContents(Composite composite, String groupTitle) {
        Group group = createGroup (composite, groupTitle, 4);
        List subGroupList = new ArrayList ();
        for (Iterator i = defaultValues.iterator (); i.hasNext ();) {
            DefaultValue defaultValue = (DefaultValue) i.next ();
            Label label = null;
            Class setType = defaultValue.method.getParameterTypes ()[0];

            // based on setType, layout the labels and respective controls.
            if (setType.equals (String.class)) {
                label = createLabel (group, defaultValue.label);
                defaultValue.widget = createTextField (group);
                // ((Text) defaultValue.widget).setText
                // (defaultValue.method.getName ().substring (3));
                ((Text) defaultValue.widget).setText ("");
            } else if (setType.equals (int.class)) {
                label = createLabel (group, defaultValue.label);
                defaultValue.widget = createTextField (group);
                ((Text) defaultValue.widget).setText ("");
            } else if (setType.equals (float.class)) {
                label = createLabel (group, defaultValue.label);
                defaultValue.widget = createTextField (group);
                ((Text) defaultValue.widget).setText ("");
            } else if (setType.equals (Boolean.class)) {
                label = createLabel (group, defaultValue.label);
                defaultValue.widget = createButton (group, "", SWT.CHECK | SWT.LEFT);
            } else if (setType.equals (List.class)) {
                // Group subGroup = createGroup (group, defaultValue.label, 2);
                subGroupList.add (defaultValue);
            }
        }
        // render subgroups here with lookup Buttons.
        for (Iterator it = subGroupList.iterator (); it.hasNext ();) {
            DefaultValue defaultValue = (DefaultValue) it.next ();
            defaultValue.widget = null;
            Label label = createLabel (group, defaultValue.label);
            defaultValue.widget = createButton (group, " View ", SWT.PUSH | SWT.CENTER);
        }
    }

}
