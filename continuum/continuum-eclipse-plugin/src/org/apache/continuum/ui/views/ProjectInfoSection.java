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

import org.apache.continuum.internal.model.ProjectAdapter;
import org.apache.maven.continuum.model.project.Project;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Displays the section with Project details for a selected project.
 * <p>
 * TODO: Factor out Label Strings to use Message Bundle.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class ProjectInfoSection extends AbstractPropertySection {

    private CLabel labelGroupId;

    private CLabel labelArtifactId;

    private CLabel labelVersion;

    private CLabel labelTotalBuilds;

    private ProjectAdapter adapter;


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
     *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
     */
    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
        // TODO Auto-generated method stub
        super.createControls (parent, aTabbedPropertySheetPage);
        Composite composite = getWidgetFactory ().createFlatFormComposite (parent);
        FormData data = null;
        CLabel label = null;

        // Laying controls right-to-left
        data = new FormData ();
        data.left = new FormAttachment (0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment (75, 0);
        data.top = new FormAttachment (0, ITabbedPropertyConstants.VSPACE);
        labelGroupId = getWidgetFactory ().createCLabel (composite, "");
        labelGroupId.setLayoutData (data);

        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.right = new FormAttachment (labelGroupId, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment (labelGroupId, 0, SWT.CENTER);
        label = getWidgetFactory ().createCLabel (composite, "Group Id: ");
        label.setLayoutData (data);

        data = new FormData ();
        data.left = new FormAttachment (0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment (75, 0);
        data.top = new FormAttachment (labelGroupId, ITabbedPropertyConstants.VSPACE);
        labelArtifactId = getWidgetFactory ().createCLabel (composite, "");
        labelArtifactId.setLayoutData (data);

        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.right = new FormAttachment (labelArtifactId, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment (labelGroupId, ITabbedPropertyConstants.VSPACE);
        label = getWidgetFactory ().createCLabel (composite, "Artifact Id: ");
        label.setLayoutData (data);

        data = new FormData ();
        data.left = new FormAttachment (0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment (75, 0);
        data.top = new FormAttachment (labelArtifactId, ITabbedPropertyConstants.VSPACE);
        labelVersion = getWidgetFactory ().createCLabel (composite, "");
        labelVersion.setLayoutData (data);

        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.right = new FormAttachment (labelVersion, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment (labelArtifactId, ITabbedPropertyConstants.VSPACE);
        label = getWidgetFactory ().createCLabel (composite, "Version: ");
        label.setLayoutData (data);

        data = new FormData ();
        data.left = new FormAttachment (0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment (75, 0);
        data.top = new FormAttachment (labelVersion, ITabbedPropertyConstants.VSPACE);
        labelTotalBuilds = getWidgetFactory ().createCLabel (composite, "");
        labelTotalBuilds.setLayoutData (data);

        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.right = new FormAttachment (labelTotalBuilds, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment (labelVersion, ITabbedPropertyConstants.VSPACE);
        label = getWidgetFactory ().createCLabel (composite, "Total Builds: ");
        label.setLayoutData (data);

        // TODO Add Edit button that pops up the Edit Project Dialog.       
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput (part, selection);
        Assert.isTrue (selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement ();
        if (input instanceof ProjectAdapter) {
            this.adapter = (ProjectAdapter) input;
        }
        // ignore others
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
     */
    @Override
    public void refresh() {
        super.refresh ();
        adapter.getAdapter (IPropertySource2.class);
        Project p = adapter.getProject ();
        labelGroupId.setText (p.getGroupId ());
        labelArtifactId.setText (p.getArtifactId ());
        labelVersion.setText (p.getVersion ());
        labelTotalBuilds.setText (Integer.toString (p.getBuildNumber ()));
    }

}
