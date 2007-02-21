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
package org.apache.continuum.ui.wizard;

import org.apache.continuum.model.ConnectionProfile;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * WizardPage implementation that captures the information from the user
 * required to create a Continuum Connection Profile.
 * <p>
 * TODO: Review access on setters.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @since 1.0
 */
public class ConnectionProfilePage extends WizardPage {

    /**
     * Data model that holds Connection Profile preferences.
     */
    private ConnectionProfile connectionProfile;

    private Text profileName;

    private Text serverLocation;

    private Text username;

    private Text password;


    protected ConnectionProfilePage(String pageName, ConnectionProfile connectionProfile) {
        super (pageName);
        this.connectionProfile = connectionProfile;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        FormLayout layout = new FormLayout ();

        Composite container = new Composite (parent, SWT.NULL);
        container.setLayout (layout);

        FormData data = new FormData ();
        data.top = new FormAttachment (10, 10);
        data.width = 100;
        Label label = new Label (container, SWT.NULL);
        label.setText ("Profile Name: ");
        label.setLayoutData (data);

        data = new FormData ();
        data.top = new FormAttachment (10, 10);
        data.left = new FormAttachment (label, 10);
        data.width = 300;
        profileName = new Text (container, SWT.BORDER);
        profileName.setLayoutData (data);

        data = new FormData ();
        data.top = new FormAttachment (profileName, 10);
        data.width = 100;
        label = new Label (container, SWT.NULL);
        label.setText ("Server Location: ");
        label.setLayoutData (data);

        data = new FormData ();
        data.top = new FormAttachment (profileName, 10);
        data.left = new FormAttachment (label, 10);
        data.width = 300;
        serverLocation = new Text (container, SWT.BORDER);
        serverLocation.setLayoutData (data);

        data = new FormData ();
        data.top = new FormAttachment (serverLocation, 10);
        data.width = 100;
        label = new Label (container, SWT.NULL);
        label.setText ("Username: ");
        label.setLayoutData (data);

        data = new FormData ();
        data.top = new FormAttachment (serverLocation, 10);
        data.left = new FormAttachment (label, 10);
        data.width = 300;
        username = new Text (container, SWT.BORDER);
        username.setLayoutData (data);

        data = new FormData ();
        data.top = new FormAttachment (username, 10);
        data.width = 100;
        label = new Label (container, SWT.NULL);
        label.setText ("Password: ");
        label.setLayoutData (data);

        data = new FormData ();
        data.top = new FormAttachment (username, 10);
        data.left = new FormAttachment (label, 10);
        data.width = 300;
        password = new Text (container, SWT.BORDER | SWT.PASSWORD);
        password.setLayoutData (data);

        // populate the form with initial values from the model.
        updateFormFieldValues ();

        setControl (container);
    }


    /**
     * Populates the form fields from the Model instance.
     */
    private void updateFormFieldValues() {
        profileName.setText (null == connectionProfile.getLabel () ? "" : connectionProfile.getLabel ());
        serverLocation.setText (null == connectionProfile.getConnectionUrl () ? "" : connectionProfile.getConnectionUrl ());
        username.setText (null == connectionProfile.getUsername () ? "" : connectionProfile.getUsername ());
        password.setText (null == connectionProfile.getPassword () ? "" : connectionProfile.getPassword ());
    }


    /**
     * Obtains the values from the Form fields and updates the model instance.
     */
    private void updateModel() {
        connectionProfile.setId (getProfileName ());
        connectionProfile.setLabel (getProfileName ());
        connectionProfile.setConnectionUrl (getServerLocation ());
        connectionProfile.setUsername (getUsername ());
        connectionProfile.setPassword (getPassword ());
    }


    /**
     * Validates the form fields for valid values and other constraints (if
     * any).
     * 
     * @return <code>true</code> if validation succeeded, else
     *         <code>false</code> to indicate a validation failure.
     */
    public boolean validate() {
        if (profileName.getText ().trim ().equals (""))
            return false;
        if (serverLocation.getText ().trim ().equals (""))
            return false;

        return true;
    }


    /**
     * Cleanup!
     */
    @Override
    public void dispose() {
        super.dispose ();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#getControl()
     */
    @Override
    public Control getControl() {
        return super.getControl ();
    }


    /**
     * This is called when the wizard finishes off walking-thru the user.
     */
    public void finish() {
        // populate the connection profile model
        updateModel ();
    }


    /**
     * @return the connectionProfileData
     */
    public ConnectionProfile getConnectionProfileData() {
        return connectionProfile;
    }


    /**
     * @param connectionProfile
     *            the connectionProfileData to set
     * @deprecated <em>Experimental</em>
     */
    @Deprecated
    public void setConnectionProfileData(ConnectionProfile connectionProfile) {
        this.connectionProfile = connectionProfile;
    }


    /**
     * @return the password
     */
    public String getPassword() {
        return password.getText ();
    }


    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String v) {
        password.setText (v);
    }


    /**
     * @return the profileName
     */
    public String getProfileName() {
        return profileName.getText ();
    }


    /**
     * @param v
     *            the profileName to set
     */
    public void setProfileName(String v) {
        profileName.setText (v);
    }


    /**
     * @return the serverLocation
     */
    public String getServerLocation() {
        return serverLocation.getText ();
    }


    /**
     * @param v
     *            the serverLocation to set
     */
    public void setServerLocation(String v) {
        serverLocation.setText (v);
    }


    /**
     * @return the username
     */
    public String getUsername() {
        return username.getText ();
    }


    /**
     * @param v
     *            the username to set
     */
    public void setUsername(String v) {
        username.setText (v);
    }

}
